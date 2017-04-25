/*
 * GPLv3 License
 *  Copyright (c) SikuliCef by waicool20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.waicool20.sikulicef.util

import org.cef.CefApp
import org.cef.CefSettings
import org.cef.handler.CefAppHandlerAdapter
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission


object CefAppLoader {
    val CODE_SOURCE: String = javaClass.protectionDomain.codeSource.location.toURI().path
    val CEF_DIR: Path = Paths.get(System.getProperty("user.home")).resolve(".cef")
    val LIB_DIR: Path = if (CODE_SOURCE.endsWith(".jar")) CEF_DIR else Paths.get("src/resources")

    private val MAC_FRAMEWORK_DIR: Path by lazy { LIB_DIR.resolve("jcef_app.app/Contents/Frameworks/Chromium Embedded Framework.framework/") }
    private val MAC_HELPER: Path by lazy { LIB_DIR.resolve("jcef_app.app/Contents/Frameworks/jcef Helper.app/Contents/MacOS/jcef Helper") }

    private val JVM: Path by lazy { Paths.get(System.getProperty("java.home")) }
    private val FAKE_JVM: Path by lazy { CEF_DIR.resolve("fakejvm") }
    private val FAKE_JVM_BIN: Path by lazy { FAKE_JVM.resolve("bin") }

    private val BINARIES by lazy { listOf("icudtl.dat", "natives_blob.bin", "snapshot_blob.bin") }

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        loadBinaries()
        loadLibraries()
    }

    private fun loadBinaries() {
        // Hack for icudtl.dat discovery on linux
        if (SystemUtils.isLinux()) {
            Files.createDirectories(FAKE_JVM_BIN)
            unpack(JVM.resolve("bin/java"), FAKE_JVM_BIN.resolve("java"))
            BINARIES.forEach { unpack(javaClass.classLoader.getResourceAsStream("java-cef-res/binaries/$it"), FAKE_JVM_BIN.resolve(it)) }
            FAKE_JVM.resolve("lib").let {
                if (Files.notExists(it)) {
                    val target = JVM.resolve("lib")
                    logger.debug("[LINK] $it to $target")
                    Files.createSymbolicLink(it, target)
                }
            }
            if (System.getenv("fakeJvm").isNullOrEmpty()) {
                val java = FAKE_JVM_BIN.resolve("java")
                logger.debug("Not running under fake JVM")
                logger.debug("Main class: ${SystemUtils.mainClassName}")
                logger.debug("Fake JVM path: $java")
                val cp = (ClassLoader.getSystemClassLoader() as URLClassLoader).urLs.map { it.toString().replace("file:", "") }.joinToString(":")
                with(ProcessBuilder(java.toString(), "-cp", cp, SystemUtils.mainClassName)) {
                    inheritIO()
                    environment().put("fakeJvm", FAKE_JVM.toString())
                    logger.debug("Launching new instance with fake JVM")
                    start().waitFor()
                    System.exit(0)
                }
            }
            logger.debug("Running under fake JVM")
        }
    }

    private fun loadLibraries() {
        if (CODE_SOURCE.endsWith(".jar")) {
            val jarURI = URI.create("jar:file:$CODE_SOURCE")
            val env = mapOf(
                    "create" to "false",
                    "encoding" to "UTF-8"
            )
            Files.createDirectories(LIB_DIR)
            (FileSystems.newFileSystem(jarURI, env)).use { fs ->
                Files.walk(fs.getPath("/java-cef-res"))
                        .filter { !it.startsWith("/java-cef-res/binaries/") }
                        .filter { it.nameCount > 1 }
                        .map { it to LIB_DIR.resolve((if (it.nameCount > 2) it.subpath(1, it.nameCount) else it.fileName).toString().replace(".jarpak", ".jar")) }
                        .forEach {
                            unpack(it.first, it.second)
                            if (!SystemUtils.isWindows()) {
                                val perms = Files.getPosixFilePermissions(it.second).toMutableSet()
                                perms.addAll(listOf(PosixFilePermission.OWNER_EXECUTE))
                                Files.setPosixFilePermissions(it.second, perms)
                            }
                        }
            }
        }

        Files.walk(LIB_DIR)
                .filter {
                    when {
                        SystemUtils.isLinux() -> it.toString().endsWith(".so")
                        SystemUtils.isMac() -> it.toString().endsWith(".dll")
                        SystemUtils.isWindows() -> it.toString().endsWith(".dylib")
                        else -> false
                    }
                }
                .peek { SystemUtils.loadLibrary(it) } // Add to library path first
                .forEach {
                    logger.debug("Loading library: $it")
                    try {
                        // Try loading it through System if possible, otherwise ignore and let CefApp take care of it
                        System.load(it.toAbsolutePath().toString())
                    } catch (e: UnsatisfiedLinkError) {
                        // Ignore
                    }
                }
        Files.walk(LIB_DIR).filter { it.toString().endsWith(".jar") }.forEach {
            logger.debug("Loading jar library $it")
            SystemUtils.loadJarLibrary(it)
        }
        if (SystemUtils.isMac()) {
            MAC_FRAMEWORK_DIR.resolve("Chromium Embedded Framework").let {
                logger.debug("Loading library: $it")
                System.load(it.toString())
            }

        }
    }

    private fun <T> unpack(source: T, target: Path) {
        if (Files.notExists(target)) {
            when (source) {
                is Path -> {
                    logger.debug("[COPY] $source to $target")
                    Files.copy(source, target)
                }
                is InputStream -> {
                    logger.debug("[UNPACKED] $target")
                    Files.copy(source, target)
                }
                else -> throw IllegalArgumentException()
            }

        }
    }

    fun load(args: Array<String> = arrayOf<String>(), cefSettings: CefSettings = CefSettings()): CefApp {
        val arguments = args.toMutableList()
        if (SystemUtils.isMac()) {
            arguments.removeIf { it.startsWith("--framework-dir-path") || it.startsWith("--browser-subprocess-path") }
            arguments.add("--framework-dir-path=$MAC_FRAMEWORK_DIR")
            arguments.add("--browser-subprocess-path=$MAC_HELPER")
        }
        logger.debug("Using arguments: $arguments")
        CefApp.addAppHandler(object : CefAppHandlerAdapter(arguments.toTypedArray()) {
            override fun stateHasChanged(state: CefApp.CefAppState) {
                logger.debug("CefApp state: $state")
                if (state == CefApp.CefAppState.TERMINATED) System.exit(0)
            }
        })
        return CefApp.getInstance(arguments.toTypedArray(), cefSettings)
    }
}

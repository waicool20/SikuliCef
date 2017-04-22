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

import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission


object CefResourceLoader {
    fun load() {
        val codeSource = javaClass.protectionDomain.codeSource.location.toURI().path
        val libDir = if (codeSource.endsWith(".jar")) Files.createTempDirectory("java-cef-res") else Paths.get("")
        if (codeSource.endsWith(".jar")) {
            val jarURI = URI.create("jar:file:$codeSource")
            val env = mapOf(
                    "create" to "false",
                    "encoding" to "UTF-8"
            )
            if (Files.notExists(libDir)) Files.createDirectories(libDir)

            (FileSystems.newFileSystem(jarURI, env)).use { fs ->
                Files.walk(fs.getPath("/java-cef-res"))
                        .filter { it.nameCount > 1 }
                        .map { it to libDir.resolve((if (it.nameCount > 2) it.subpath(1, it.nameCount) else it.fileName).toString().replace(".jarpak", ".jar")) }
                        .forEach {
                            Files.copy(it.first, it.second)
                            if (!SystemUtils.isWindows()) {
                                val perms = Files.getPosixFilePermissions(it.second).toMutableSet()
                                perms.addAll(listOf(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE))
                                Files.setPosixFilePermissions(it.second, perms)
                            }
                        }
            }
            Runtime.getRuntime().addShutdownHook(Thread {
                Files.walk(libDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(Files::delete)
            })
        }

        Files.walk(libDir)
                .filter { it.toString().matches(".*\\.(so|dll|dylib)".toRegex()) }
                .peek { SystemUtils.loadLibrary(it) } // Add to library path first
                .forEach {
                    try {
                        // Try loading it through System if possible, otherwise ignore and let CefApp take care of it
                        System.load(it.toAbsolutePath().toString())
                    } catch (e: UnsatisfiedLinkError) {
                        // Ignore
                    }
                }
        Files.walk(libDir)
                .filter { it.toString().endsWith(".jar") }
                .forEach {
                    SystemUtils.loadJarLibrary(it)
                }

        if (SystemUtils.isMac()) {
            System.load(libDir.resolve("jcef_app.app/Contents/Frameworks/Chromium Embedded Framework.framework/Chromium Embedded Framework").toString())
        }
    }
}

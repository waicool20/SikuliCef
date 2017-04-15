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
import java.nio.file.attribute.PosixFilePermission


object CefResourceLoader {
    fun load() {
        val jarPath = javaClass.protectionDomain.codeSource.location.toURI().path
        val jarURI = URI.create("jar:file:$jarPath")
        val env = mapOf(
                "create" to "false",
                "encoding" to "UTF-8"
        )
        val libDir = Files.createTempDirectory("java-cef-res")
        if (Files.notExists(libDir)) Files.createDirectories(libDir)

        (FileSystems.newFileSystem(jarURI, env)).use { fs ->
            Files.walk(fs.getPath("/java-cef-res"))
                    .filter { it.nameCount > 1 }
                    .map { it to libDir.resolve((if (it.nameCount > 2) it.subpath(1, it.nameCount) else it.fileName).toString()) }
                    .forEach {
                        Files.copy(it.first, it.second)
                        val perms = Files.getPosixFilePermissions(it.second).toMutableSet()
                        perms.addAll(listOf(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE))
                        Files.setPosixFilePermissions(it.second, perms)
                    }
        }

        Files.walk(libDir)
                .filter { it.toString().endsWith(".so") || it.toString().endsWith(".dll") }
                .forEach {
                    SystemUtils.loadLibrary(it)
                }

        Runtime.getRuntime().addShutdownHook(Thread {
            Files.walk(libDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(Files::delete)
        })
    }
}

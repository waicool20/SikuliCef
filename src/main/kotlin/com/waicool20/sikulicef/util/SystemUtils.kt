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

import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path

object SystemUtils {
    fun loadLibrary(path: Path) = loadLibrary(listOf(path))

    fun loadLibrary(paths: List<Path>) {
        val separator = if (isWindows()) ";" else ":"
        val currentLibs = System.getProperty("java.library.path").split(separator).toMutableSet()
        val addLibs = paths
                .map(Path::toAbsolutePath)
                .map(Path::getParent)
                .map(Path::toString)
                .distinct()
        currentLibs.addAll(addLibs)

        System.setProperty("java.library.path", currentLibs.joinToString(separator))
        with(ClassLoader::class.java.getDeclaredField("sys_paths")) {
            isAccessible = true
            set(null, null)
        }
    }

    fun loadJarLibrary(jar: Path) {
        val loader = ClassLoader.getSystemClassLoader() as URLClassLoader
        val url = jar.toUri().toURL()

        try {
            if (!loader.urLs.contains(url)) {
                val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
                method.isAccessible = true
                method.invoke(loader, url)
            }
        } catch (e: Exception) {
            println("Exception occured while trying to add Jar library")
        }
    }

    fun isWindows() = System.getProperty("os.name").toLowerCase().contains("win")
    fun isLinux() = System.getProperty("os.name").toLowerCase().contains("linux")
    fun isMac() = System.getProperty("os.name").toLowerCase().contains("mac")
}




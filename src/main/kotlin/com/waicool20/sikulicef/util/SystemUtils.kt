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

import java.nio.file.Path

object SystemUtils {
    fun loadLibrary(path: Path) = loadLibrary(listOf(path))

    fun loadLibrary(paths: List<Path>) {
        val currentLibs = System.getProperty("java.library.path").split(":").toMutableSet()
        val addLibs = paths
                .map(Path::toAbsolutePath)
                .map(Path::getParent)
                .map(Path::toString)
                .distinct()
        currentLibs.addAll(addLibs)

        System.setProperty("java.library.path", currentLibs.joinToString(":"))
        with (ClassLoader::class.java.getDeclaredField("sys_paths")) {
            isAccessible = true
            set(null, null)
        }
    }
}




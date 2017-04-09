package com.waicool20.sikulixcef.util

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




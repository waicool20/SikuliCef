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

package com.waicool20.sikulicef.tests

import com.waicool20.sikulicef.util.CefAppLoader
import com.waicool20.sikulicef.util.getSource
import com.waicool20.sikulicef.util.waitForLoadComplete
import com.waicool20.sikulicef.wrappers.CefScreen
import org.cef.CefApp
import org.sikuli.script.ImagePath
import org.sikuli.script.Pattern
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.system.measureTimeMillis

private val logger = LoggerFactory.getLogger("SikuliCefTest")

fun main(args: Array<String>) {
    val argsList = arrayOf(
            *args,
            "--ppapi-flash-path=${Paths.get("/usr/lib/PepperFlash/libpepflashplayer.so")}",
            "--ppapi-flash-version=25.0.0.148",
            "--disable-overlay-scrollbar",
            "--hide-scrollbars",
            "--off-screen-frame-rate=60",
            "--disable-gpu",
            "--disable-gpu-compositing",
            "--enable-begin-frame-scheduling"
    )

    val cefApp = CefAppLoader.load(argsList)
    val client = cefApp.createClient()
    val browser = client.createBrowser(initTestPageResources().resolve("index.html").toUri().toString(), true, true)
    val screen = CefScreen(browser)
    val mainFrame = JFrame()
    mainFrame.contentPane.add(screen.uiComponent, BorderLayout.CENTER)
    mainFrame.title = "SikuliCefTest"
    mainFrame.size = Dimension(800, 600)
    mainFrame.minimumSize = mainFrame.size
    mainFrame.isVisible = true

    mainFrame.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(event: WindowEvent?) {
            CefApp.getInstance().dispose()
            mainFrame.dispose()
        }
    })

    Thread {
        ImagePath.add(ClassLoader.getSystemClassLoader().getResource("images"))

        /* Test Mouse */
        measureTimeMillis { browser.waitForLoadComplete() }.let {
            logger.debug("Browser loading complete, took $it ms")
        }

        logger.debug("--------------------------------------------")
        logger.debug("Testing mouse")

        var match = screen.exists(Pattern("menu.png").exact(), 10.0)
        if (match != null) {
            logger.debug("Match: $match")
            logger.debug("Clicking $match")
            match.click()
            "<div id=\"mouseTest\">(.*?)</div>".toRegex().find(browser.getSource())?.groupValues?.get(1)?.let {
                if (it == "Clicked") {
                    logger.debug("Mouse test: PASS")
                } else {
                    logger.debug("Mouse failed to click target")
                    return@Thread
                }
            }
        } else {
            logger.error("Match not found for mouse test!")
            return@Thread
        }

        logger.debug("--------------------------------------------")
        logger.debug("Testing keyboard")

        /* Keyboard Test */
        TimeUnit.MILLISECONDS.sleep(500)
        match = screen.exists(Pattern("searchbar.png").exact(), 10.0)
        if (match != null) {
            logger.debug("Match: $match")
            val typeTest = "Hello SikuliCef"
            logger.debug("Attempting to type \"$typeTest\"")
            match.type(match, typeTest)
            "<div id=\"typeTest\">(.*?)</div>".toRegex().find(browser.getSource())?.groupValues?.get(1)?.let {
                logger.debug("Text in search box: $it")
                if (it == typeTest) {
                    logger.debug("Keyboard test: PASS")
                } else {
                    logger.error("String to type didn't match string found in search box!")
                    return@Thread
                }
            }
        } else {
            logger.error("Match not found for keyboard test!")
            return@Thread
        }

        logger.debug("--------------------------------------------")
        Paths.get("screenshot.png").toFile().let {
            logger.debug("Saving screenshot to ${it.absolutePath}")
            ImageIO.write(browser.currentFrameBuffer, "png", it)
        }

        logger.debug("--------------------------------------------")
        logger.debug("Test complete")
    }.start()
}

private val testPageResources = listOf("test-page.css", "index.html", "bootstrap.min.css", "bootstrap.min.js", "jquery-3.2.1.min.js")

private fun initTestPageResources(): Path {
    val tmpDir = Files.createTempDirectory("cef-test")
    logger.debug("Loading test resources at $tmpDir")
    testPageResources.forEach {
        Files.copy(ClassLoader.getSystemClassLoader().getResourceAsStream("test-page/$it"), tmpDir.resolve(it.substringAfter("/")))
    }
    Runtime.getRuntime().addShutdownHook(Thread {
        tmpDir.toFile().deleteRecursively()
    })
    return tmpDir
}

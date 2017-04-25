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
import com.waicool20.sikulicef.wrappers.CefScreen
import org.cef.CefApp
import org.sikuli.script.ImagePath
import org.sikuli.script.Key
import org.sikuli.script.Pattern
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.swing.JFrame

fun main(args: Array<String>) {
    val argsList = arrayOf(
            *args,
            "--ppapi-flash-path=${Paths.get("/usr/lib/PepperFlash/libpepflashplayer.so")}",
            "--ppapi-flash-version=25.0.0.148",
            "--disable-overlay-scrollbar",
            "--hide-scrollbars"
    )

    val cefApp = CefAppLoader.load(argsList)
    val client = cefApp.createClient()
    val browser = client.createBrowser("http://www.google.com", true, false)
    val screen = CefScreen(browser)
    val mainFrame = JFrame()
    mainFrame.contentPane.add(screen.uiComponent, BorderLayout.CENTER)
    mainFrame.size = Dimension(800, 600)
    mainFrame.isVisible = true

    mainFrame.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(event: WindowEvent?) {
            CefApp.getInstance().dispose()
            mainFrame.dispose()
        }
    })

    println(ClassLoader.getSystemClassLoader().getResource("images"))
    Thread {
        ImagePath.add(ClassLoader.getSystemClassLoader().getResource("images"))
        while (browser.isLoading) {
        }
        println("Browsers finished loading")

        /* Keyboard Test */
        var match = screen.exists(Pattern("searchbar.png").exact(), 10.0)
        println("Match: $match")
        println("Attempting to type \"Hello SikuliCef\"")
        match?.highlight()
        match?.click()
        match?.type("Hello SikuliCef")
        match?.type(Key.ENTER)
        match?.highlight()

        /* Test Mouse */
        while (browser.isLoading) {
        }
        println("Browsers finished loading")
        match = screen.exists(Pattern("mic.png").exact(), 10.0)
        println("Match: $match")
        println("Sleeping for 2 seconds then clicking")
        TimeUnit.SECONDS.sleep(2)
        println("Clicking")
        match?.click()

        ImageIO.write(browser.currentFrameBuffer, "png", Paths.get("screenshot.png").toFile())
    }.start()
}

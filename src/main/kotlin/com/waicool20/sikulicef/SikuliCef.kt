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

package com.waicool20.sikulicef

import com.waicool20.sikulicef.util.CefResourceLoader
import org.cef.CefApp
import org.cef.browser.CefBrowser
import org.cef.handler.CefAppHandlerAdapter
import org.cef.handler.CefLoadHandlerAdapter
import org.sikuli.script.ImagePath
import org.sikuli.script.Key
import org.sikuli.script.Pattern
import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel

fun main(args: Array<String>) {
    CefResourceLoader.load()

    val argsList = arrayOf(
            *args,
            "--ppapi-flash-path=${Paths.get("/usr/lib/PepperFlash/libpepflashplayer.so")}",
            "--ppapi-flash-version=25.0.0.148",
            "--disable-overlay-scrollbar",
            "--hide-scrollbars"
    )
    CefApp.addAppHandler(object : CefAppHandlerAdapter(argsList) {
        override fun stateHasChanged(state: CefApp.CefAppState) {
            if (state == CefApp.CefAppState.TERMINATED)
                System.exit(0)
        }
    })

    val cefApp = CefApp.getInstance(argsList)
    val client = cefApp.createClient()

    client.addLoadHandler(object : CefLoadHandlerAdapter() {
        override fun onLoadEnd(browser: CefBrowser?, frameIdentifier: Int, statusCode: Int) {
            super.onLoadStart(browser, frameIdentifier)
            browser?.executeJavaScript("document.body.style.overflow = \"hidden\"", "", 0)
        }
    })

    val browser = client.createBrowser("http://www.google.com", true, false)
    val browserUI = browser.uiComponent
    val mainFrame = JFrame()
    val panel = JPanel(BorderLayout())
    panel.add(browserUI)
    mainFrame.contentPane.add(panel, BorderLayout.CENTER)
    mainFrame.setSize(800, 600)
    mainFrame.isVisible = true

    mainFrame.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(event: WindowEvent?) {
            CefApp.getInstance().dispose()
            mainFrame.dispose()
        }
    })

    Thread {
        ImagePath.add(ClassLoader.getSystemClassLoader().getResource("images"))
        while (browser.isLoading) {
        }
        println("Browsers finished loading")
        val screen = CefScreen(browser)

        /* Keyboard Test */
        var match = screen.exists(Pattern("searchbar.png").exact(), 10.0)
        println("Match: $match")
        println("Attempting to type \"Hello SikuliCef\"")
        match.click()
        match.type("Hello SikuliCef")
        match.type(Key.ENTER)

        /* Test Mouse */
        TimeUnit.SECONDS.sleep(1)
        while (browser.isLoading) {
        }
        println("Browsers finished loading")
        match = screen.exists(Pattern("mic.png").exact(), 10.0)
        println("Match: $match")
        println("Sleeping for 2 seconds then clicking")
        TimeUnit.SECONDS.sleep(2)
        println("Clicking")
        match.click()

        ImageIO.write(browser.currentFrameBuffer, "png", Paths.get("screenshot.png").toFile())
    }.start()

}

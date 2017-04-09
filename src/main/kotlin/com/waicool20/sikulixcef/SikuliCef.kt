package com.waicool20.sikulixcef

import com.waicool20.sikulixcef.util.SystemUtils
import org.cef.CefApp
import org.cef.OS
import org.cef.browser.CefBrowser
import org.cef.handler.CefLoadHandlerAdapter
import org.sikuli.script.ImagePath
import org.sikuli.script.Pattern
import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    ImagePath.add(ClassLoader.getSystemClassLoader().getResource("images"))

    Files.walk(Paths.get("java-cef-framebuffer/binary_distrib").toAbsolutePath(), Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)
            .filter { it.toString().endsWith(".so") || it.toString().endsWith(".dll") }
            .forEach {
                SystemUtils.loadLibrary(it)
            }

    val argsList = arrayOf(
            *args,
            "--ppapi-flash-path='${Paths.get("/usr/lib/PepperFlash/libpepflashplayer.so")}'",
            "--disable-overlay-scrollbar",
            "--hide-scrollbars"
    )

    val cefApp = CefApp.getInstance(argsList)
    val client = cefApp.createClient()

    client.addLoadHandler(object : CefLoadHandlerAdapter() {
        override fun onLoadEnd(browser: CefBrowser?, frameIdentifier: Int, statusCode: Int) {
            super.onLoadStart(browser, frameIdentifier)
            browser?.executeJavaScript("document.body.style.overflow = \"hidden\"", "", 0)
        }
    })

    val browser = client.createBrowser("http://www.google.com", OS.isLinux(), false)
    val browserUI = browser.uiComponent
    val mainFrame = JFrame()
    mainFrame.contentPane.add(browserUI, BorderLayout.CENTER)
    mainFrame.setSize(800, 600)
    mainFrame.isVisible = true

    mainFrame.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(event: WindowEvent?) {
            super.windowClosing(event)
            SwingUtilities.invokeLater {
                cefApp.dispose()
                mainFrame.dispose()
            }
        }

        override fun windowClosed(event: WindowEvent?) {
            super.windowClosed(event)
            System.exit(0)
        }
    })

    Thread {
        println("Sleeping for 4 seconds to wait for browser")
        TimeUnit.SECONDS.sleep(4)
        println("Done waiting, checking for google image")
        val screen = CefScreen(browser)
        val match = screen.exists(Pattern("searchbar.png").exact(), 10.0)
        println("Match: $match")
        ImageIO.write(browser.currentFrameBuffer, "png", Paths.get("screenshot.png").toFile())

    }.start()

}

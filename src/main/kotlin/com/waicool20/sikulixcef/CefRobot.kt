package com.waicool20.sikulixcef

import org.sikuli.script.IRobot
import org.sikuli.script.IScreen
import org.sikuli.script.Location
import org.sikuli.script.ScreenImage
import java.awt.Color
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit

class CefRobot(val screen: CefScreen): IRobot {
    override fun captureScreen(screenRect: Rectangle): ScreenImage = screen.capture(screenRect)

    override fun getScreen(): IScreen = screen

    override fun cleanup() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun waitForIdle() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun setAutoDelay(ms: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun smoothMove(dest: Location?) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun smoothMove(src: Location?, dest: Location?, ms: Long) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun pressModifiers(modifiers: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseWheel(wheelAmt: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun getColorAt(x: Int, y: Int): Color {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyDown(keys: String?) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyDown(code: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun clickEnds() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun clickStarts() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseDown(buttons: Int) {
        println("Mouse Down!")
        with (screen.browser.uiComponent) {
            this.dispatchEvent(java.awt.event.MouseEvent(
                    this, java.awt.event.MouseEvent.MOUSE_PRESSED,
                    5,
                    0,
                    551,
                    468,
                    551,
                    468,
                    1,
                    false,
                    buttons
            ))
        }
    }

    override fun typeEnds() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun typeChar(character: Char, mode: IRobot.KeyMode?) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun isRemote(): Boolean {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun releaseModifiers(modifiers: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun typeKey(key: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun typeStarts() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyUp(keys: String?) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyUp(code: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyUp() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseMove(x: Int, y: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseReset() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun delay(ms: Int) {
        TimeUnit.MILLISECONDS.sleep(ms.toLong())
    }

    override fun mouseUp(buttons: Int): Int {
        println("Mouse Up!")
        with (screen.browser.uiComponent) {
            this.dispatchEvent(java.awt.event.MouseEvent(
                    this, java.awt.event.MouseEvent.MOUSE_RELEASED,
                    5,
                    0,
                    551,
                    468,
                    551,
                    468,
                    1,
                    false,
                    buttons
            ))
        }
        return 0
    }

}



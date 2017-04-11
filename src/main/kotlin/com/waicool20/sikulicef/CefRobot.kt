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

import org.sikuli.basics.AnimatorOutQuarticEase
import org.sikuli.basics.AnimatorTimeBased
import org.sikuli.basics.Settings
import org.sikuli.script.IRobot
import org.sikuli.script.IScreen
import org.sikuli.script.Location
import org.sikuli.script.ScreenImage
import java.awt.Color
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.event.MouseWheelEvent
import java.util.concurrent.TimeUnit

class CefRobot(val screen: CefScreen) : IRobot {
    private var currentMouseX = 0
    private var currentMouseY = 0
    private var heldButtons = 0
    private var autoDelay = 100

    init {
        with(screen.browser.uiComponent) {
            screen.browser.uiComponent.addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseMoved(event: MouseEvent) {
                    if (currentMouseX != event.x) currentMouseX = event.x
                    if (currentMouseY != event.y) currentMouseY = event.y
                }
            })
        }
    }

    //<editor-fold desc="Mouse Actions">
    override fun mouseUp(buttons: Int): Int {
        if (buttons == 0) {
            generateMouseEvent(MouseEvent.MOUSE_RELEASED, heldButtons)
            heldButtons = 0
        } else {
            generateMouseEvent(MouseEvent.MOUSE_RELEASED, buttons)
            heldButtons = heldButtons.and(buttons.inv())
        }
        return heldButtons
    }

    override fun mouseDown(buttons: Int) {
        heldButtons = if (heldButtons == 0) buttons else heldButtons.or(buttons)
        generateMouseEvent(MouseEvent.MOUSE_PRESSED, buttons)
    }

    override fun mouseMove(x: Int, y: Int) {
        currentMouseX = x
        currentMouseY = y
        generateMouseEvent(MouseEvent.MOUSE_MOVED, MouseEvent.NOBUTTON)
    }

    override fun mouseWheel(wheelAmt: Int) =
            screen.browser.uiComponent.dispatchEvent(MouseWheelEvent(
                    screen.browser.uiComponent,
                    System.currentTimeMillis().toInt(),
                    System.currentTimeMillis(),
                    0,
                    currentMouseX,
                    currentMouseY,
                    1,
                    false,
                    MouseWheelEvent.WHEEL_UNIT_SCROLL,
                    wheelAmt,
                    wheelAmt
            ))


    override fun smoothMove(dest: Location) = smoothMove(getCurrentMouseLocation(), dest, (Settings.MoveMouseDelay * 1000).toLong())

    override fun smoothMove(src: Location, dest: Location, ms: Long) {
        if (ms < 1) {
            mouseMove(dest.x, dest.y)
            return
        }
        val aniX = AnimatorTimeBased(AnimatorOutQuarticEase(src.x.toFloat(), dest.x.toFloat(), ms))
        val aniY = AnimatorTimeBased(AnimatorOutQuarticEase(src.y.toFloat(), dest.y.toFloat(), ms))
        while (aniX.running()) {
            val x = aniX.step()
            val y = aniY.step()
            mouseMove(x.toInt(), y.toInt())
            delay(10)
        }
    }

    override fun mouseReset() {
        mouseMove(0, 0)
        mouseUp(MouseEvent.BUTTON1_MASK
                .and(MouseEvent.BUTTON2_MASK)
                .and(MouseEvent.BUTTON3_MASK)
        )
    }
    //</editor-fold>

    //<editor-fold desc="Keyboard Actions">
    override fun pressModifiers(modifiers: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun releaseModifiers(modifiers: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyDown(keys: String?) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyDown(code: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun typeEnds() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun typeChar(character: Char, mode: IRobot.KeyMode?) {
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
    //</editor-fold>

    //<editor-fold desc="Screen">
    override fun captureScreen(screenRect: Rectangle): ScreenImage = screen.capture(screenRect)

    override fun getScreen(): IScreen = screen
    override fun getColorAt(x: Int, y: Int) = Color(screen.browser.currentFrameBuffer.getRGB(x, y))
    //</editor-fold>

    //<editor-fold desc="Misc">
    override fun setAutoDelay(ms: Int) {
        autoDelay = ms
    }

    override fun isRemote() = false

    override fun delay(ms: Int) = TimeUnit.MILLISECONDS.sleep(ms.toLong())

    override fun waitForIdle() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }
    //</editor-fold>

    //<editor-fold desc="Stuff that does Nothing">
    override fun cleanup() = Unit

    override fun clickEnds() = Unit
    override fun clickStarts() = Unit
    //</editor-fold>

    private fun generateMouseEvent(mouseEvent: Int, buttons: Int) =
            screen.browser.uiComponent.dispatchEvent(MouseEvent(
                    screen.browser.uiComponent.parent,
                    mouseEvent,
                    System.currentTimeMillis(),
                    0,
                    currentMouseX,
                    currentMouseY,
                    currentMouseX,
                    currentMouseY,
                    1,
                    false,
                    buttons
            ))


    fun getCurrentMouseLocation() = Location(currentMouseX, currentMouseY)
}



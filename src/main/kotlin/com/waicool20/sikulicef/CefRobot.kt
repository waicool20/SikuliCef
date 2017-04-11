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
import org.sikuli.script.*
import java.awt.Color
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.event.MouseWheelEvent
import java.util.concurrent.TimeUnit
import javax.swing.KeyStroke


class CefRobot(val screen: CefScreen) : IRobot {
    private var currentMouseX = 0
    private var currentMouseY = 0
    private var heldButtons = 0
    private val heldKeys = mutableSetOf<Int>()
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
        generateMouseEvent(MouseEvent.MOUSE_PRESSED, heldButtons)
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
        if (modifiers and KeyModifier.SHIFT != 0) keyDown(KeyEvent.VK_SHIFT)
        if (modifiers and KeyModifier.CTRL != 0) keyDown(KeyEvent.VK_CONTROL)
        if (modifiers and KeyModifier.ALT != 0) keyDown(KeyEvent.VK_ALT)
        if (modifiers and KeyModifier.META != 0 || modifiers and KeyModifier.WIN != 0) {
            if (Settings.isWindows()) {
                keyDown(KeyEvent.VK_WINDOWS)
            } else {
                keyDown(KeyEvent.VK_META)
            }
        }
    }

    override fun releaseModifiers(modifiers: Int) {
        if (modifiers and KeyModifier.SHIFT != 0) keyUp(KeyEvent.VK_SHIFT)
        if (modifiers and KeyModifier.CTRL != 0) keyUp(KeyEvent.VK_CONTROL)
        if (modifiers and KeyModifier.ALT != 0) keyUp(KeyEvent.VK_ALT)
        if (modifiers and KeyModifier.META != 0 || modifiers and KeyModifier.WIN != 0) {
            if (Settings.isWindows()) {
                keyUp(KeyEvent.VK_WINDOWS)
            } else {
                keyUp(KeyEvent.VK_META)
            }
        }
    }

    override fun keyDown(keys: String) = keys.toCharArray().map(Char::toInt).forEach {
        keyDown(KeyEvent.getExtendedKeyCodeForChar(it))
    }

    override fun keyDown(code: Int) {
        if (!heldKeys.contains(code)) {
            heldKeys.add(code)
            generateKeyEvent(KeyEvent.KEY_PRESSED, code)
        }
    }

    override fun typeChar(character: Char, mode: IRobot.KeyMode) = when (mode) {
        IRobot.KeyMode.PRESS_ONLY -> keyDown(character.toInt())
        IRobot.KeyMode.PRESS_RELEASE -> typeKey(character.toInt())
        IRobot.KeyMode.RELEASE_ONLY -> keyUp(character.toInt())
    }

    override fun typeKey(key: Int) {
        generateKeyEvent(KeyEvent.KEY_TYPED, key)
    }

    override fun keyUp() = heldKeys.forEach { keyUp(it) }
    override fun keyUp(keys: String) = keys.toCharArray().map(Char::toInt).forEach {
        keyUp(KeyEvent.getExtendedKeyCodeForChar(it))
    }

    override fun keyUp(code: Int) {
        if (heldKeys.contains(code)) {
            generateKeyEvent(KeyEvent.KEY_RELEASED, code)
            heldKeys.remove(code)
        }
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

    //</editor-fold>

    //<editor-fold desc="Stuff that does Nothing">
    override fun cleanup() = Unit

    override fun clickStarts() = Unit
    override fun clickEnds() = Unit
    override fun typeStarts() = Unit
    override fun typeEnds() = Unit
    override fun waitForIdle() = Unit
    //</editor-fold>

    private fun generateMouseEvent(mouseEvent: Int, buttons: Int) =
            screen.browser.uiComponent.dispatchEvent(MouseEvent(
                    screen.browser.uiComponent.parent,
                    mouseEvent,
                    System.currentTimeMillis(),
                    buttons,
                    currentMouseX,
                    currentMouseY,
                    currentMouseX,
                    currentMouseY,
                    1,
                    false,
                    MouseEvent.NOBUTTON
            ))

    private fun generateKeyEvent(keyEvent: Int, keyCode: Int) {
        screen.browser.uiComponent.requestFocus()
        screen.browser.uiComponent.dispatchEvent(KeyEvent(
                screen.browser.uiComponent,
                keyEvent,
                System.currentTimeMillis(),
                0,
                KeyStroke.getKeyStroke(keyCode.toChar()).keyCode,
                if (keyEvent == KeyEvent.KEY_TYPED) keyCode.toChar() else KeyEvent.CHAR_UNDEFINED
        ))
    }

    fun getCurrentMouseLocation() = Location(currentMouseX, currentMouseY)
}



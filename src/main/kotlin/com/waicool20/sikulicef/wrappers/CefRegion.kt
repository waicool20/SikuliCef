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

package com.waicool20.sikulicef.wrappers

import com.waicool20.sikulicef.graphical.CefHighlighter
import com.waicool20.sikulicef.input.CefKeyBoard
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.sikuli.basics.Settings
import org.sikuli.script.*
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.util.concurrent.TimeUnit

open class CefRegion(xPos: Int, yPos: Int, width: Int, height: Int, screen: IScreen? = CefScreen.getScreen()) : Region() {
    constructor(region: Region, screen: IScreen? = CefScreen.getScreen()) : this(region.x, region.y, region.w, region.h, screen)
    constructor(rect: Rectangle, screen: IScreen? = CefScreen.getScreen()) : this(rect.x, rect.y, rect.width, rect.height, screen)

    init {
        x = xPos
        y = yPos
        w = width
        h = height
        if (screen != null) this.screen = screen
    }

    private val mouse by lazy { (this.screen as CefScreen).mouse }
    private val keyboard by lazy { (this.screen as CefScreen).keyboard }

    override fun setLocation(loc: Location): CefRegion {
        x = loc.x
        y = loc.y
        return this
    }

    override fun setROI() = setROI(screen.bounds)
    override fun setROI(rect: Rectangle) = with(rect) { setROI(x, y, width, height) }
    override fun setROI(region: Region) = with(region) { setROI(x, y, w, h) }
    override fun setROI(X: Int, Y: Int, W: Int, H: Int) {
        x = X
        y = Y
        w = if (W > 1) W else 1
        h = if (H > 1) H else 1
    }

    override fun getCenter() = Location(x + (w / 2), y + (h / 2))
    override fun getTopLeft() = Location(x, y)
    override fun getTopRight() = Location(x + w - 1, y)
    override fun getBottomLeft() = Location(x, y + h - 1)
    override fun getBottomRight() = Location(x + w - 1, y + h - 1)

    override fun getLastMatch() = super.getLastMatch()?.let(::CefMatch)
    override fun getLastMatches() =
            super.getLastMatches().asSequence().toList().map(::CefMatch).iterator()

    override fun offset(loc: Location?) = CefRegion(super.offset(loc), screen)
    override fun above() = CefRegion(super.above(), screen)
    override fun below() = CefRegion(super.below(), screen)
    override fun left() = CefRegion(super.left(), screen)
    override fun right() = CefRegion(super.right(), screen)

    override fun above(height: Int) = CefRegion(super.above(height), screen)
    override fun below(height: Int) = CefRegion(super.below(height), screen)
    override fun left(width: Int) = CefRegion(super.left(width), screen)
    override fun right(width: Int) = CefRegion(super.right(width), screen)

    /* Search operations */

    override fun <PSI : Any?> find(target: PSI) = super.find(target)?.let(::CefMatch)
    override fun <PSI : Any?> findAll(target: PSI) =
            super.findAll(target).asSequence().toList().map(::CefMatch).iterator()

    override fun <PSI : Any?> wait(target: PSI) = super.wait(target).let(::CefMatch)
    override fun <PSI : Any?> wait(target: PSI, timeout: Double) = super.wait(target, timeout)?.let(::CefMatch)

    override fun <PSI : Any?> exists(target: PSI) = super.exists(target)?.let(::CefMatch)
    override fun <PSI : Any?> exists(target: PSI, timeout: Double) = super.exists(target, timeout)?.let(::CefMatch)

    //<editor-fold desc="Mouse and KeyboardActions">
    override fun click(): Int = click(center, 0)

    override fun <PFRML : Any?> click(target: PFRML): Int = click(target, 0)
    override fun <PFRML : Any?> click(target: PFRML, modifiers: Int): Int =
            try {
                getLocationFromTarget(target)?.let {
                    mouse.click(it, Mouse.LEFT, modifiers)
                    1
                } ?: 0
            } catch (e: FindFailed) {
                0
            }


    override fun doubleClick(): Int = doubleClick(center, 0)
    override fun <PFRML : Any?> doubleClick(target: PFRML): Int = doubleClick(target, 0)
    override fun <PFRML : Any?> doubleClick(target: PFRML, modifiers: Int): Int =
            try {
                getLocationFromTarget(target)?.let {
                    mouse.doubleClick(it, Mouse.LEFT, modifiers)
                    1
                } ?: 0
            } catch (e: FindFailed) {
                0
            }


    override fun rightClick(): Int = rightClick(center, 0)
    override fun <PFRML : Any?> rightClick(target: PFRML): Int = rightClick(target, 0)
    override fun <PFRML : Any?> rightClick(target: PFRML, modifiers: Int): Int =
            try {
                getLocationFromTarget(target)?.let {
                    mouse.click(it, Mouse.RIGHT, modifiers)
                    1
                } ?: 0
            } catch (e: FindFailed) {
                0
            }

    override fun hover(): Int = hover(center)
    override fun <PFRML : Any?> hover(target: PFRML): Int =
            try {
                getLocationFromTarget(target)?.let {
                    mouse.moveTo(it)
                    1
                } ?: 0
            } catch (e: FindFailed) {
                0
            }

    override fun <PFRML : Any?> dragDrop(target: PFRML): Int = dragDrop(lastMatch, target)

    override fun <PFRML : Any?> dragDrop(t1: PFRML, t2: PFRML): Int =
            getLocationFromTarget(t1)?.let { l1 -> getLocationFromTarget(t2)?.let { mouse.dragDrop(l1, it) } } ?: 0

    override fun <PFRML : Any?> drag(target: PFRML): Int = getLocationFromTarget(target)?.let { mouse.drag(it) } ?: 0

    override fun <PFRML : Any?> dropAt(target: PFRML): Int = getLocationFromTarget(target)?.let { mouse.dropAt(it) } ?: 0

    override fun type(text: String): Int = type(text, 0)
    override fun type(text: String, modifiers: String): Int = type(text, CefKeyBoard.parseModifiers(modifiers))
    override fun type(text: String, modifiers: Int): Int {
        keyboard.type(null, text, modifiers)
        return 1
    }

    override fun <PFRML : Any?> type(target: PFRML, text: String): Int = type(target, text, 0)
    override fun <PFRML : Any?> type(target: PFRML, text: String, modifiers: String): Int =
            type(target, text, CefKeyBoard.parseModifiers(modifiers))

    override fun <PFRML : Any?> type(target: PFRML, text: String, modifiers: Int): Int =
            try {
                keyboard.type(getLocationFromTarget(target), text, modifiers)
                1
            } catch (e: FindFailed) {
                0
            }


    override fun paste(text: String): Int = paste(null, text)

    override fun <PFRML : Any?> paste(target: PFRML, text: String): Int {
        if (text.isEmpty() || (target != null && click(target) == 1)) return 1
        (screen as CefScreen).clipboard = text
        keyDown(Key.getHotkeyModifier())
        keyDown(KeyEvent.VK_V)
        keyUp(KeyEvent.VK_V)
        keyUp(Key.getHotkeyModifier())
        return 0
    }
    //</editor-fold>

    //<editor-fold desc="Low-level Mouse and Keyboard Actions">
    override fun mouseDown(buttons: Int) = mouse.mouseDown(buttons)

    override fun mouseUp() {
        mouse.mouseUp()
    }

    override fun mouseUp(buttons: Int) {
        mouse.mouseUp(buttons)
    }

    override fun mouseMove(): Int = if (lastMatch != null) mouseMove(lastMatch) else 0
    override fun mouseMove(xoff: Int, yoff: Int): Int = mouseMove(Location(x + xoff, y + yoff))
    override fun <PFRML : Any?> mouseMove(target: PFRML): Int =
            try {
                getLocationFromTarget(target)?.let {
                    mouse.moveTo(it)
                    1
                } ?: 0
            } catch (e: FindFailed) {
                0
            }

    override fun wheel(direction: Int, steps: Int): Int = wheel(mouse.getCurrentMouseLocation(), direction, steps)
    override fun <PFRML : Any?> wheel(target: PFRML, direction: Int, steps: Int): Int = wheel(target, direction, steps, Mouse.WHEEL_STEP_DELAY)
    override fun <PFRML : Any?> wheel(target: PFRML, direction: Int, steps: Int, stepDelay: Int): Int =
            getLocationFromTarget(target)?.let {
                mouse.spinWheel(it, direction, steps, stepDelay)
                1
            } ?: 0

    override fun keyDown(keycode: Int) = keyboard.keyDown(keycode)
    override fun keyDown(keys: String) = keyboard.keyDown(keys)

    override fun keyUp() = keyboard.keyUp()
    override fun keyUp(keycode: Int) = keyboard.keyUp(keycode)
    override fun keyUp(keys: String) = keyboard.keyUp(keys)
    //</editor-fold>

    //<editor-fold desc="Highlight Action">
    private val highlighter by lazy { CefHighlighter(this) }

    override fun highlight(): Region = highlight("")
    override fun highlight(color: String): Region {
        highlighter.setColor(color)
        highlighter.toggle()
        return this
    }

    override fun highlight(secs: Int): Region = highlight(secs, "")
    override fun highlight(secs: Float): Region = highlight(secs, "")
    override fun highlight(secs: Int, color: String): Region = highlight(secs.toFloat(), color)

    override fun highlight(secs: Float, color: String): Region {
        highlight(color)
        async(CommonPool) {
            delay((if (secs < 0.1) Settings.SlowMotionDelay else secs * 1000L).toLong(),
                    TimeUnit.MILLISECONDS)
            highlight()
        }
        return this
    }

    //</editor-fold>

    override fun <PSIMRL> getLocationFromTarget(target: PSIMRL): Location? = when (target) {
        is Pattern, is String, is Image -> find(target)?.target
        is Match -> target.target
        is CefRegion -> target.center
        is Region -> target.center
        is Location -> target
        else -> throw FindFailed("")
    }?.setOtherScreen(screen)
}

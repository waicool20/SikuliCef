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

package com.waicool20.sikulicef.input

import org.sikuli.basics.Settings
import org.sikuli.script.Location
import org.sikuli.script.Mouse


class CefMouse(val robot: CefRobot) {
    fun click(location: Location, buttons: Int, modifiers: Int) = synchronized(this) {
        moveTo(location)
        val pause = if (Settings.ClickDelay > 1) 1 else (Settings.ClickDelay * 1000).toInt()
        robot.pressModifiers(modifiers)
        robot.mouseDown(buttons)
        robot.delay(pause)
        robot.mouseUp(buttons)
        robot.releaseModifiers(modifiers)
        Settings.ClickDelay = 0.0
    }

    fun doubleClick(location: Location, buttons: Int, modifiers: Int) = synchronized(this) {
        repeat(2) {
            click(location, buttons, modifiers)
        }
    }

    fun moveTo(location: Location) = synchronized(this) { robot.smoothMove(location) }

    /* Low level actions */

    fun mouseDown(buttons: Int) = synchronized(this) { robot.mouseDown(buttons) }

    fun mouseUp(buttons: Int = 0): Int = synchronized(this) { robot.mouseUp(buttons) }

    fun spinWheel(location: Location, direction: Int, steps: Int, stepDelay: Int) = synchronized(this) {
        moveTo(location)
        repeat(steps) {
            robot.mouseWheel(if (direction < 0) -1 else 1)
            robot.delay(stepDelay)
        }
    }

    fun drag(location: Location, resetDelays: Boolean = true) = synchronized(this) {
        moveTo(location)
        robot.delay((Settings.DelayBeforeMouseDown * 1000).toInt())
        mouseDown(Mouse.LEFT)
        robot.delay((if (Settings.DelayBeforeDrag < 0) Settings.DelayAfterDrag else Settings.DelayBeforeDrag).toInt() * 1000)
        if (resetDelays) resetDragDelays()
        1
    }

    fun dropAt(location: Location, resetDelays: Boolean = true) = synchronized(this) {
        moveTo(location)
        robot.delay((Settings.DelayBeforeDrop * 1000).toInt())
        mouseUp(Mouse.LEFT)
        if (resetDelays) resetDragDelays()
        1
    }

    fun dragDrop(loc1: Location, loc2: Location) = synchronized(this) {
        drag(loc1, false)
        dropAt(loc2)
        1
    }

    private fun resetDragDelays() {
        Settings.DelayBeforeMouseDown = Settings.DelayValue
        Settings.DelayAfterDrag = Settings.DelayValue
        Settings.DelayBeforeDrag = -Settings.DelayValue
        Settings.DelayBeforeDrop = Settings.DelayValue
    }

    fun getCurrentMouseLocation() = robot.getCurrentMouseLocation()

}

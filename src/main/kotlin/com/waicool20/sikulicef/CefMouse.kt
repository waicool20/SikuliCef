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

import org.sikuli.basics.Settings
import org.sikuli.script.Location


class CefMouse(val robot: CefRobot) {

    fun click(location: Location, buttons: Int, modifiers: Int) = synchronized(robot) {
        moveTo(location)
        val pause = if (Settings.ClickDelay > 1) 1 else (Settings.ClickDelay * 1000).toInt()
        //robot.pressModifiers(modifiers) // TODO Implement Key modifiers
        robot.mouseDown(buttons)
        robot.delay(pause)
        robot.mouseUp(buttons)
        //robot.releaseModifiers(modifiers) // TODO Implement Key modifiers
        Settings.ClickDelay = 0.0
    }

    fun doubleClick(location: Location, buttons: Int, modifiers: Int) = synchronized(robot) {
        repeat(2) {
            click(location, buttons, modifiers)
            robot.delay(10)
        }
    }

    fun moveTo(location: Location) = synchronized(robot) {
        robot.smoothMove(location)
    }

    /* Low level actions */

    fun mouseDown(buttons: Int) = synchronized(robot) {
        robot.mouseDown(buttons)
    }

    fun mouseUp(buttons: Int = 0): Int = synchronized(robot) {
        robot.mouseUp(buttons)
    }

    fun spinWheel(location: Location, direction: Int, steps: Int, stepDelay: Int) = synchronized(robot) {
        moveTo(location)
        repeat(steps) {
            robot.mouseWheel(if (direction < 0 ) -1 else 1)
            robot.delay(stepDelay)
        }
    }

    fun getCurrentMouseLocation() = robot.getCurrentMouseLocation()

}

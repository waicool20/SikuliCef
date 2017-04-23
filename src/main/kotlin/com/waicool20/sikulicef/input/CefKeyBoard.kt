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
import org.sikuli.script.Key
import org.sikuli.script.KeyModifier
import org.sikuli.script.Location


class CefKeyBoard(val robot: CefRobot) {

    companion object {
        fun parseModifiers(modifiers: String): Int {
            var mods = 0
            modifiers.toCharArray().forEach {
                mods = mods.or(when (it) {
                    Key.C_CTRL -> KeyModifier.CTRL
                    Key.C_ALT -> KeyModifier.ALT
                    Key.C_SHIFT -> KeyModifier.SHIFT
                    Key.C_META -> KeyModifier.META
                    Key.C_ALTGR -> KeyModifier.ALTGR
                    Key.C_WIN -> KeyModifier.WIN
                    else -> 0
                })
            }
            return mods
        }
    }

    fun type(location: Location?, text: String, modifiers: Int) = synchronized(this) {
        if (location != null) robot.screen.click(location)
        val pause = if (Settings.TypeDelay > 1) 1 else (Settings.TypeDelay * 1000).toInt()
        robot.pressModifiers(modifiers)
        text.toCharArray().map(Char::toInt).forEach {
            robot.typeKey(it)
            robot.delay(if (pause < 80) 80 else pause)
        }
        Settings.TypeDelay = 0.0
    }

    fun keyUp() = synchronized(this) { robot.keyUp() }
    fun keyUp(keycode: Int) = synchronized(this) { robot.keyUp(keycode) }
    fun keyUp(keys: String) = synchronized(this) { robot.keyUp(keys) }

    fun keyDown(keycode: Int) = synchronized(this) { robot.keyDown(keycode) }
    fun keyDown(keys: String) = synchronized(this) { robot.keyDown(keys) }
}

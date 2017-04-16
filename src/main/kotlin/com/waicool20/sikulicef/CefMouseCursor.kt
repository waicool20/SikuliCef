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

import java.awt.Color
import java.awt.Graphics
import java.awt.Window
import java.awt.event.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.SwingUtilities

class CefMouseCursor(val robot: CefRobot, val frame: JFrame) : Window(null) {
    private val image = ImageIO.read(javaClass.classLoader.getResource("images/cursor.png"))

    init {
        updateBounds()
        isAlwaysOnTop = true
        background = Color(0, 0, 0, 0)
        isVisible = true

        frame.addWindowStateListener { isVisible = it.newState == 0 }
        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(event: ComponentEvent) {
                updateBounds()
            }
        })
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowDeactivated(event: WindowEvent) {
                isVisible = false
            }

            override fun windowActivated(event: WindowEvent) {
                isVisible = true
            }

            override fun windowClosing(event: WindowEvent) = SwingUtilities.invokeLater { dispose() }
        })
        robot.screen.browser.uiComponent.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(event: MouseEvent) = updateBounds()
        })
        robot.screen.browser.uiComponent.addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(event: MouseEvent) = updateBounds()
            override fun mouseExited(event: MouseEvent) = updateBounds()
        })
    }

    override fun paint(graphics: Graphics) {
        graphics.drawImage(image, 0, 0, this)
    }

    override fun update(graphics: Graphics) = paint(graphics)

    fun updateBounds() {
        val mouseLocation = robot.getCurrentMouseLocation()
        val frameX = frame.contentPane.locationOnScreen.x
        val frameY = frame.contentPane.locationOnScreen.y
        var newX = frameX + mouseLocation.x + 2
        var newY = frameY + mouseLocation.y + 2

        if (newX > frameX + frame.contentPane.width) newX = frameX + frame.contentPane.width
        if (newY > frameY + frame.contentPane.height) newY = frameY + frame.contentPane.height

        SwingUtilities.invokeLater {
            setBounds(newX, newY, image.width, image.height)
        }
    }
}

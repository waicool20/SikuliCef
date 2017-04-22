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

import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.imageio.ImageIO

class CefMouseCursor(screen: CefScreen) : CefOverlay(screen) {
    private val image = ImageIO.read(javaClass.classLoader.getResource("images/cursor.png"))

    init {
        updateBounds()
        with(screen.browser.uiComponent) {
            addMouseMotionListener(object : MouseMotionListener {
                override fun mouseDragged(event: MouseEvent) = updateBounds()
                override fun mouseMoved(event: MouseEvent) = updateBounds()
            })
            addMouseListener(object : MouseListener {
                override fun mouseReleased(event: MouseEvent) = updateBounds()
                override fun mouseClicked(event: MouseEvent) = updateBounds()
                override fun mousePressed(event: MouseEvent) = updateBounds()
                override fun mouseEntered(event: MouseEvent) = updateBounds()
                override fun mouseExited(event: MouseEvent) = updateBounds()
            })
        }
    }

    override fun paintContent(graphics: Graphics2D) {
        graphics.drawImage(image, 0, 0, this)
    }

    private fun updateBounds() {
        val mouseLocation = (screen.robot as CefRobot).getCurrentMouseLocation()
        bounds = Rectangle(mouseLocation.x + 1, mouseLocation.y + 1, image.width, image.height)
    }
}

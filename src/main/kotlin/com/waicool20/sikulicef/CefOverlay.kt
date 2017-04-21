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

import java.awt.Graphics
import java.awt.event.*
import java.awt.image.RasterFormatException
import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLEventListener
import javax.media.opengl.awt.GLCanvas


open class CefOverlay(val screen: CefScreen) : GLCanvas(), GLEventListener {

    private val browserUI = screen.browser.uiComponent as GLCanvas

    init {
        initialize()
    }

    private fun initialize() {
        browserUI.addGLEventListener(this)
        addMouseListener(object : MouseListener {
            override fun mouseReleased(event: MouseEvent?) = updateMouse(event)
            override fun mouseEntered(event: MouseEvent?) = updateMouse(event)
            override fun mouseClicked(event: MouseEvent?) = updateMouse(event)
            override fun mouseExited(event: MouseEvent?) = updateMouse(event)
            override fun mousePressed(event: MouseEvent?) = updateMouse(event)
        })
        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseMoved(event: MouseEvent?) = updateMouse(event)
            override fun mouseDragged(event: MouseEvent?) = updateMouse(event)
        })
        addMouseWheelListener { browserUI.dispatchEvent(it) }
        addKeyListener(object : KeyListener {
            override fun keyTyped(event: KeyEvent?) = browserUI.dispatchEvent(event)
            override fun keyPressed(event: KeyEvent?) = browserUI.dispatchEvent(event)
            override fun keyReleased(event: KeyEvent?) = browserUI.dispatchEvent(event)
        })
    }

    private fun updateMouse(event: MouseEvent?) {
        if (event == null) return
        browserUI.dispatchEvent(MouseEvent(this, event.id, event.`when`, event.modifiers,
                event.x + x, event.y + y, event.clickCount, event.isPopupTrigger, event.button))
        cursor = browserUI.cursor
    }

    override fun paint(graphics: Graphics) {
        val image = try {
            (screen.browser.currentFrameBuffer ?: return).let {
                val w = if (x + width > it.width) it.width - x else width
                val h = if (y + height > it.height) it.height - y else height
                it.getSubimage(x, y, w, h)
            }
        } catch (e: RasterFormatException) {
            graphics.clearRect(x, y, width, height)
            return
        }
        paintContent(image.createGraphics())
        graphics.drawImage(image, 0, 0, this)
    }

    open fun paintContent(graphics: Graphics) = Unit

    override fun reshape(p0: GLAutoDrawable?, p1: Int, p2: Int, p3: Int, p4: Int) {
        repaint()
    }

    override fun display(p0: GLAutoDrawable?) {
        repaint()
    }

    override fun init(p0: GLAutoDrawable?) {
        repaint()
    }

    override fun dispose(p0: GLAutoDrawable?) {
        repaint()
    }
}

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

package com.waicool20.sikulicef.graphical

import com.waicool20.sikulicef.wrappers.CefRegion
import com.waicool20.sikulicef.wrappers.CefScreen
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import javax.swing.JLayeredPane
import javax.swing.SwingUtilities


class CefHighlighter(region: CefRegion, color: String? = "RED") : CefOverlay(region.screen as CefScreen) {
    private var showing = false
    private var color = parseColor(color)

    init {
        setBounds(region.x, region.y, region.w, region.h)
        SwingUtilities.invokeLater {
            (region.screen as CefScreen).uiComponent.add(this, Integer(JLayeredPane.DRAG_LAYER - 10))
            repaint()
        }
    }

    override fun paintContent(graphics: Graphics2D) {
        if (showing) {
            graphics.color = Color(color.red, color.green, color.blue, 128)
            graphics.stroke = BasicStroke(4f)
            graphics.drawRect(0, 0, width, height)
        }
    }

    fun toggle() {
        showing = !showing
        repaint()
    }

    fun setColor(color: String?) {
        this.color = parseColor(color)
        repaint()
    }

    private fun parseColor(color: String?): Color {
        if (color == null) return Color.RED
        try {
            if (color.startsWith("#")) {
                if (color.length > 7) {
                    if (color.length == 10) {
                        val cR = Integer.decode(color.substring(1, 4))
                        val cG = Integer.decode(color.substring(4, 7))
                        val cB = Integer.decode(color.substring(7, 10))
                        return Color(cR, cG, cB)
                    }
                } else {
                    return Color(Integer.decode(color))
                }
            } else {
                return Class.forName("java.awt.Color").getField(color.replace(" ", "_").toUpperCase()).get(null) as Color
            }
        } catch (e: Exception) {
            return Color.RED
        }
        return Color.RED
    }
}

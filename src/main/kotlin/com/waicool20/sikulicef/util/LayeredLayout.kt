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

package com.waicool20.sikulicef.util

import com.waicool20.sikulicef.CefOverlay
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager


class LayeredLayout(val target: Container) : LayoutManager {

    override fun layoutContainer(container: Container?) {
        container?.components?.forEach {
            if (it is CefOverlay) {
                it.setBounds(it.x, it.y, it.width, it.height)
            } else {
                it.setBounds(0, 0, target.width, target.height)
            }
        }
    }

    override fun preferredLayoutSize(container: Container?): Dimension = minimumLayoutSize(container)

    override fun minimumLayoutSize(container: Container?): Dimension = Dimension(500, 500)

    override fun addLayoutComponent(name: String, component: Component) = Unit

    override fun removeLayoutComponent(p0: Component?) = Unit
}

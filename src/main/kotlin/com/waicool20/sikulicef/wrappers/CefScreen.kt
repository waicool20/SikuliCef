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

import com.waicool20.sikulicef.graphical.CefMouseCursor
import com.waicool20.sikulicef.input.CefKeyBoard
import com.waicool20.sikulicef.input.CefMouse
import com.waicool20.sikulicef.input.CefRobot
import com.waicool20.sikulicef.util.LayeredLayout
import org.cef.browser.CefBrowser
import org.sikuli.basics.Settings
import org.sikuli.script.*
import org.sikuli.util.ScreenHighlighter
import java.awt.Rectangle
import javax.media.opengl.awt.GLCanvas
import javax.swing.JLayeredPane

/**
 * Represents a [org.sikuli.script.IScreen] containing the visible viewport of the CefBrowser
 */
class CefScreen private constructor(val browser: CefBrowser) : CefRegion(
        browser.uiComponent.bounds.x,
        browser.uiComponent.bounds.y,
        browser.uiComponent.bounds.width,
        browser.uiComponent.bounds.height
), IScreen {

    companion object Factory {
        private val screens = mutableMapOf<Int, CefScreen>()
        /**
         * Get CefScreen bound to [org.cef.browser.CefBrowser] identifier
         * @param identifier Identifier from [org.cef.browser.CefBrowser.getIdentifier]
         */
        fun getScreen(identifier: Int = 0): CefScreen? = screens[identifier]

        /**
         * Get CefScreen bound to [org.cef.browser.CefBrowser] identifier or returns new CefScreen instance if not found
         * @param browser [org.cef.browser.CefBrowser] instance
         * @throws IllegalArgumentException Thrown if CefBrowser is not rendered using OSR mode
         */
        fun getScreen(browser: CefBrowser): CefScreen {
            getScreen(browser.identifier)?.let { return it }
            if (browser.uiComponent !is GLCanvas) throw IllegalArgumentException("CefScreen only supports OSR browser")
            return CefScreen(browser)
        }
    }

    init {
        screens.putIfAbsent(getIdentifier(), this)
        screen = this
        isVirtual = true
        setOtherScreen(this)
    }

    private val robot = CefRobot(this)

    /**
     * [com.waicool20.sikulicef.input.CefMouse] instance bound to this screen
     */
    val mouse = CefMouse(robot)

    /**
     * [com.waicool20.sikulicef.input.CefKeyBoard] instance bound to this screen
     */
    val keyboard = CefKeyBoard(robot)

    /**
     * [javax.swing.JLayeredPane] containing the [org.cef.browser.CefBrowser] UI Component and
     * software rendering of [com.waicool20.sikulicef.input.CefMouse]
     */
    val uiComponent = run {
        val layeredPane = JLayeredPane()
        layeredPane.layout = LayeredLayout(layeredPane)
        layeredPane.add(CefMouseCursor(this), Integer(JLayeredPane.DRAG_LAYER))
        layeredPane.add(browser.uiComponent)
        layeredPane
    }

    /**
     * Clipboard of this screen
     */
    var clipboard = ""

    /**
     * Gets the unique identifier of this screen
     */
    fun getIdentifier() = browser.identifier

    override fun getScreen(): IScreen = this

    override fun showTarget(location: Location) = showTarget(location, Settings.SlowMotionDelay)

    private fun showTarget(location: Location, seconds: Float) {
        if (Settings.isShowActions()) {
            val overlay = ScreenHighlighter(this, null)
            overlay.showTarget(location, seconds)
        }
    }

    override fun getIdFromPoint(srcx: Int, srcy: Int) = 0

    override fun getRobot(): IRobot = robot

    override fun userCapture(string: String?): ScreenImage {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun getID() = 0

    override fun getBounds(): Rectangle = browser.uiComponent.bounds

    override fun capture(): ScreenImage = capture(rect)

    override fun capture(x: Int, y: Int, w: Int, h: Int): ScreenImage = capture(Rectangle(x, y, w, h))

    override fun capture(rect: Rectangle): ScreenImage = getCurrentFrameScreenImage().getSub(rect)

    override fun capture(reg: Region): ScreenImage = with(reg) { capture(x, y, w, h) }

    override fun getLastScreenImageFromScreen(): ScreenImage = lastScreenImage

    private fun getCurrentFrameScreenImage(): ScreenImage {
        while (browser.currentFrameBuffer == null) {
        }
        with(browser.currentFrameBuffer) {
            return ScreenImage(Rectangle(0, 0, width, height), this)
        }
    }
}

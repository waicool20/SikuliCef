package com.waicool20.sikulixcef

import org.sikuli.script.IScreen
import org.sikuli.script.Location
import org.sikuli.script.Match
import org.sikuli.script.Region
import java.awt.Rectangle

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

    private fun getRobot() = screen.robot

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

    override fun getCenter() = Location((x + w) / 2, (y + h) / 2)
    override fun getTopLeft() = Location(x, y)
    override fun getTopRight() = Location(x + w - 1, y)
    override fun getBottomLeft() = Location(x, y + h - 1)
    override fun getBottomRight() = Location(x + w - 1, y + h - 1)

    override fun getLastMatch(): Match? = CefMatch(super.getLastMatch())
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

    override fun <PSI : Any?> find(target: PSI) = CefMatch(super.find(target))
    override fun <PSI : Any?> findAll(target: PSI) =
            super.findAll(target).asSequence().toList().map(::CefMatch).iterator()

    override fun <PSI : Any?> wait(target: PSI) = CefMatch(super.wait(target))
    override fun <PSI : Any?> wait(target: PSI, timeout: Double) = CefMatch(super.wait(target, timeout))

    override fun <PSI : Any?> exists(target: PSI) = CefMatch(super.exists(target))
    override fun <PSI : Any?> exists(target: PSI, timeout: Double) = CefMatch(super.exists(target, timeout))

    //<editor-fold desc="Mouse and Keyboard Actions">
    override fun click(): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> click(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> click(target: PFRML, modifiers: Int?): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun doubleClick(): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> doubleClick(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> doubleClick(target: PFRML, modifiers: Int?): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun rightClick(): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> rightClick(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> rightClick(target: PFRML, modifiers: Int?): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun hover(): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> hover(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> dragDrop(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> dragDrop(t1: PFRML, t2: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> drag(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> dropAt(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun type(text: String): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun paste(text: String): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> paste(target: PFRML, text: String): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }
    //</editor-fold>

    //<editor-fold desc="Low-level Mouse and Keyboard Actions">
    override fun mouseDown(buttons: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseUp() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseUp(buttons: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseMove(): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun mouseMove(xoff: Int, yoff: Int): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun <PFRML : Any?> mouseMove(target: PFRML): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun wheel(direction: Int, steps: Int): Int {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyDown(keycode: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyDown(keys: String) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyUp() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyUp(keycode: Int) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun keyUp(keys: String) {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }
    //</editor-fold>

    //<editor-fold desc="Highlight Action">
    override fun highlight(): Region {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun highlight(secs: Float): Region {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun highlight(secs: Int): Region {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun highlight(color: String): Region {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun highlight(secs: Float, color: String): Region {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    override fun highlight(secs: Int, color: String): Region {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }
    //</editor-fold>
}

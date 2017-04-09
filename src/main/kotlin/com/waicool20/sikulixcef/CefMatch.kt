package com.waicool20.sikulixcef

import org.sikuli.script.Location
import org.sikuli.script.Match
import org.sikuli.script.Region
import java.awt.Rectangle


class CefMatch(match: Match) : Match(match) {
    private val region = CefRegion(match, match.screen)

    override fun setLocation(loc: Location) = region.setLocation(loc)

    override fun setROI() = region.setROI()
    override fun setROI(rect: Rectangle) = region.setROI(rect)
    override fun setROI(region: Region) = region.setROI(region)
    override fun setROI(X: Int, Y: Int, W: Int, H: Int) = region.setROI(X, Y, W, H)

    override fun getCenter() = region.center
    override fun getTopLeft() = region.topLeft
    override fun getTopRight() = region.topRight
    override fun getBottomLeft() = region.bottomLeft
    override fun getBottomRight() = region.bottomRight

    override fun getLastMatch() = region.lastMatch
    override fun getLastMatches() = region.lastMatches

    override fun offset(loc: Location?) = region.offset(loc)
    override fun above() = region.above()
    override fun below() = region.below()
    override fun left() = region.left()
    override fun right() = region.right()

    override fun above(height: Int) = region.above(height)
    override fun below(height: Int) = region.below(height)
    override fun left(width: Int) = region.left(width)
    override fun right(width: Int) = region.right(width)

    /* Search operations */

    override fun <PSI : Any?> find(target: PSI) = region.find(target)
    override fun <PSI : Any?> findAll(target: PSI) = region.findAll(target)

    override fun <PSI : Any?> wait(target: PSI) = region.wait(target)
    override fun <PSI : Any?> wait(target: PSI, timeout: Double) = region.wait(target, timeout)

    override fun <PSI : Any?> exists(target: PSI) = region.exists(target)
    override fun <PSI : Any?> exists(target: PSI, timeout: Double) = region.exists(target, timeout)

    //<editor-fold desc="Mouse and Keyboard Actions">
    override fun click() = region.click()

    override fun <PFRML : Any?> click(target: PFRML) = region.click(target)
    override fun <PFRML : Any?> click(target: PFRML, modifiers: Int?) = region.click(target, modifiers)

    override fun doubleClick() = region.doubleClick()
    override fun <PFRML : Any?> doubleClick(target: PFRML) = region.doubleClick(target)
    override fun <PFRML : Any?> doubleClick(target: PFRML, modifiers: Int?) = region.doubleClick(target, modifiers)

    override fun rightClick() = region.rightClick()
    override fun <PFRML : Any?> rightClick(target: PFRML) = region.rightClick(target)
    override fun <PFRML : Any?> rightClick(target: PFRML, modifiers: Int?) = region.rightClick(target, modifiers)

    override fun hover() = region.hover()
    override fun <PFRML : Any?> hover(target: PFRML) = region.hover(target)

    override fun <PFRML : Any?> dragDrop(target: PFRML) = region.dragDrop(target)
    override fun <PFRML : Any?> dragDrop(t1: PFRML, t2: PFRML) = region.dragDrop(t1, t2)

    override fun <PFRML : Any?> drag(target: PFRML) = region.drag(target)

    override fun <PFRML : Any?> dropAt(target: PFRML) = region.dropAt(target)

    override fun type(text: String) = region.type(text)

    override fun paste(text: String) = region.paste(text)
    override fun <PFRML : Any?> paste(target: PFRML, text: String) = region.paste(target, text)
    //</editor-fold>

    //<editor-fold desc="Low-level Mouse and Keyboard Actions">
    override fun mouseDown(buttons: Int) = region.mouseDown(buttons)

    override fun mouseUp() = region.mouseUp()
    override fun mouseUp(buttons: Int) = region.mouseUp(buttons)

    override fun mouseMove() = region.mouseMove()
    override fun mouseMove(xoff: Int, yoff: Int) = region.mouseMove(xoff, yoff)
    override fun <PFRML : Any?> mouseMove(target: PFRML) = region.mouseMove(target)

    override fun wheel(direction: Int, steps: Int) = region.wheel(direction, steps)

    override fun keyDown(keycode: Int) = region.keyDown(keycode)
    override fun keyDown(keys: String) = region.keyDown(keys)

    override fun keyUp() = region.keyUp()
    override fun keyUp(keycode: Int) = region.keyUp(keycode)
    override fun keyUp(keys: String) = region.keyUp(keys)
    //</editor-fold>

    //<editor-fold desc="Highlight Action">
    override fun highlight() = region.highlight()
    override fun highlight(secs: Float) = region.highlight(secs)
    override fun highlight(secs: Int) = region.highlight(secs)
    override fun highlight(color: String) = region.highlight(color)
    override fun highlight(secs: Float, color: String) = region.highlight(secs, color)
    override fun highlight(secs: Int, color: String) = region.highlight(secs, color)
    //</editor-fold>
}

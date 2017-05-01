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

import org.cef.browser.CefBrowser
import java.util.concurrent.TimeUnit

fun CefBrowser.waitForLoadComplete() {
    while (isLoading) {
    }
}

fun CefBrowser.getSource(): String {
    var source = ""
    var done = false
    getSource {
        source = it
        done = true
    }
    do {
        TimeUnit.MILLISECONDS.sleep(1)
    } while (!done)
    return source
}



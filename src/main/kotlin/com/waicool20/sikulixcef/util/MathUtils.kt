/*
 * GPLv3 License
 *  Copyright (c) sikulix-cef by waicool20
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

package com.waicool20.kcsubswitcher

import java.util.*

fun Random.nextInt(min: Int, max: Int) = nextInt(max - min + 1) + min
fun Random.nextLong(min: Long, max: Long) = (nextDouble() * (max - min)).toLong() + min
fun Random.nextSign() = if (nextInt(100) > 50) 1 else -1


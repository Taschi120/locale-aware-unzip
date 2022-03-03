package de.taschi.unzip

//    Locale Aware Unzipper
//    Copyright (C) 2022 S. Hillebrand <dev@hit-to-key.net>
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

import kotlin.math.min

class UnzipProgress {
    private var count = 1
    private var processedCount = 0

    @Synchronized
    fun getMaxCount(): Int {
        return maxOf(count, 1)
    }

    @Synchronized
    fun setMaxCount(value: Int) {
        count = value
        processedCount = min(count, processedCount)
    }

    @Synchronized
    fun getProcessedCount(): Int {
        return clamp(0, processedCount, count)
    }

    @Synchronized
    fun incrementProcessedCount() {
        processedCount++
    }

    fun clamp(min: Int, value: Int, max: Int): Int {
        if (value < min) return min
        if (value > max) return max
        return value
    }

}
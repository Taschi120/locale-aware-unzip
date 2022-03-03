package de.taschi.unzip

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
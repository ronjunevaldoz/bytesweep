package io.github.ronjunevaldoz.bytesweep.ui.util

import kotlin.math.abs

/** Format a byte count into a human-readable string, e.g. 1536 -> "1.5 KB". */
fun formatSize(bytes: Long): String {
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var unit = 0
    while (abs(value) >= 1024.0 && unit < units.lastIndex) {
        value /= 1024.0
        unit++
    }
    return if (unit == 0) {
        "${bytes} ${units[unit]}"
    } else {
        val rounded = (value * 10).toLong() / 10.0
        "$rounded ${units[unit]}"
    }
}

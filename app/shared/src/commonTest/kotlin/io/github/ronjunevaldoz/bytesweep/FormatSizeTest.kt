package io.github.ronjunevaldoz.bytesweep

import io.github.ronjunevaldoz.bytesweep.ui.util.formatSize
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatSizeTest {

    @Test
    fun formatsBytes() {
        assertEquals("512 B", formatSize(512))
    }

    @Test
    fun formatsKilobytes() {
        assertEquals("1.5 KB", formatSize(1536))
    }

    @Test
    fun formatsMegabytes() {
        assertEquals("1.0 MB", formatSize(1024L * 1024))
    }

    @Test
    fun formatsGigabytes() {
        assertEquals("2.0 GB", formatSize(2L * 1024 * 1024 * 1024))
    }
}

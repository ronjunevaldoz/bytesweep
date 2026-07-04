package io.github.ronjunevaldoz.bytesweep.model

/**
 * The kind of a scanned item. Junk buckets (cache/temp/logs) come first, then real
 * file-type categories used to organize the rest of storage. Display order follows the
 * declaration order.
 */
enum class JunkCategory(val label: String) {
    CACHE("App cache"),
    TEMP("Temporary files"),
    LOGS("Log files"),
    DOCUMENT("Documents"),
    IMAGE("Images"),
    AUDIO("Audio & music"),
    VIDEO("Video"),
    ARCHIVE("Archives"),
    MODEL("AI models"),
    CODE("Code & data"),
    LARGE_FILE("Large files"),
    OTHER("Other"),
}

/** A single removable item discovered during a scan. */
data class JunkItem(
    val id: String,
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val category: JunkCategory,
    val selected: Boolean = true,
)

/** The full outcome of a storage scan. */
data class ScanResult(
    val items: List<JunkItem>,
) {
    val totalBytes: Long get() = items.sumOf { it.sizeBytes }
}

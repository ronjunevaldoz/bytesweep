package io.github.ronjunevaldoz.bytesweep.model

/** A category of reclaimable junk surfaced by a [io.github.ronjunevaldoz.bytesweep.domain.StorageScanner]. */
enum class JunkCategory(val label: String) {
    CACHE("App cache"),
    TEMP("Temporary files"),
    LOGS("Log files"),
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

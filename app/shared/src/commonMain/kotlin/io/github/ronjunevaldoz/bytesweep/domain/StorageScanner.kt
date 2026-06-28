package io.github.ronjunevaldoz.bytesweep.domain

import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult

/**
 * Platform abstraction over the local filesystem / app storage.
 *
 * Each target (Android, Desktop/JVM, iOS, Web) provides its own implementation,
 * wired through the platform Koin module. Web targets return a representative
 * dataset since browsers cannot inspect the host filesystem.
 */
interface StorageScanner {

    /** Scan reclaimable junk (caches, temp, logs, large files). */
    suspend fun scan(): ScanResult

    /**
     * Delete the given items. Returns the number of bytes actually reclaimed.
     * Implementations must only delete items they themselves surfaced.
     */
    suspend fun clean(items: List<JunkItem>): Long
}

package io.github.ronjunevaldoz.bytesweep.domain

import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult

/**
 * Lets the user pick a real directory, scans it (recursively), and can delete selected
 * files back within that same directory. Distinct from [StorageScanner], which scans
 * fixed system locations. Desktop and Chromium browsers support this; other platforms
 * report [isSupported] = false.
 */
interface FolderScanner {
    val isSupported: Boolean

    /** Whether deletion of scanned items is available (real user files — always confirm first). */
    val canDelete: Boolean

    /** Opens a folder chooser and scans the selection. Returns null if cancelled/unsupported. */
    suspend fun pickAndScan(): ScanResult?

    /** Deletes the given items from the last-scanned folder. Returns bytes reclaimed. */
    suspend fun delete(items: List<JunkItem>): Long
}

/** Fallback for platforms without a native folder chooser (iOS, Firefox/Safari web). */
class UnsupportedFolderScanner : FolderScanner {
    override val isSupported: Boolean = false
    override val canDelete: Boolean = false
    override suspend fun pickAndScan(): ScanResult? = null
    override suspend fun delete(items: List<JunkItem>): Long = 0L
}

package io.github.ronjunevaldoz.bytesweep.domain

import io.github.ronjunevaldoz.bytesweep.model.ScanResult

/**
 * Lets the user pick a real directory and scans it. Distinct from [StorageScanner], which
 * scans fixed system locations (caches/temp). Currently a Desktop capability; sandboxed
 * platforms report [isSupported] = false.
 */
interface FolderScanner {
    val isSupported: Boolean

    /** Opens a folder chooser and scans the selection. Returns null if cancelled/unsupported. */
    suspend fun pickAndScan(): ScanResult?
}

/** Fallback for platforms without a native folder chooser (Android, iOS, Web). */
class UnsupportedFolderScanner : FolderScanner {
    override val isSupported: Boolean = false
    override suspend fun pickAndScan(): ScanResult? = null
}

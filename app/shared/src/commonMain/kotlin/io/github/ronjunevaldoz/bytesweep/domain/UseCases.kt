package io.github.ronjunevaldoz.bytesweep.domain

import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult

/** Scan local storage for reclaimable junk. */
class ScanStorageUseCase(private val scanner: StorageScanner) {
    suspend operator fun invoke(): ScanResult = scanner.scan()
}

/** Delete the selected junk items, returning the bytes reclaimed. */
class CleanJunkUseCase(private val scanner: StorageScanner) {
    suspend operator fun invoke(items: List<JunkItem>): Long =
        if (items.isEmpty()) 0L else scanner.clean(items)
}

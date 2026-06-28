package io.github.ronjunevaldoz.bytesweep.data

import io.github.ronjunevaldoz.bytesweep.domain.StorageScanner
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult
import kotlinx.coroutines.delay

/**
 * Scanner used on platforms with no host-filesystem access (Web/JS/WasmJs).
 * Returns a representative dataset and simulates cleaning so the UI is fully
 * functional in the browser sandbox.
 */
class DemoStorageScanner : StorageScanner {

    override suspend fun scan(): ScanResult {
        delay(600) // simulate work
        val items = listOf(
            JunkItem("demo-cache", "Browser cache", "/web/cache", 64_000_000, JunkCategory.CACHE),
            JunkItem("demo-sw", "Service worker temp", "/web/sw", 18_400_000, JunkCategory.TEMP),
            JunkItem("demo-logs", "Console logs", "/web/logs", 900_000, JunkCategory.LOGS),
            JunkItem("demo-map", "cached-bundle.js.map", "/web/maps", 42_000_000, JunkCategory.LARGE_FILE),
        )
        return ScanResult(items)
    }

    override suspend fun clean(items: List<JunkItem>): Long {
        delay(400)
        return items.sumOf { it.sizeBytes } // simulated reclaim
    }
}

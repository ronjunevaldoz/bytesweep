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
            JunkItem("demo-cache", "Browser cache", "/Library/Caches/browser", 64_000_000, JunkCategory.CACHE),
            JunkItem("demo-temp", "Service worker temp", "/tmp/sw", 18_400_000, JunkCategory.TEMP),
            JunkItem("demo-log", "app.log", "/logs/app.log", 900_000, JunkCategory.LOGS),
            JunkItem("demo-doc", "Q4-report.pdf", "/Documents/Q4-report.pdf", 4_200_000, JunkCategory.DOCUMENT),
            JunkItem("demo-img", "screenshot.png", "/Pictures/screenshot.png", 1_800_000, JunkCategory.IMAGE),
            JunkItem("demo-audio", "podcast-ep-12.mp3", "/Music/podcast-ep-12.mp3", 58_000_000, JunkCategory.AUDIO),
            JunkItem("demo-video", "trip.mp4", "/Movies/trip.mp4", 512_000_000, JunkCategory.VIDEO),
            JunkItem("demo-archive", "old-backup.zip", "/Downloads/old-backup.zip", 256_000_000, JunkCategory.ARCHIVE),
            JunkItem("demo-model", "llama-3-8b.Q4_K_M.gguf", "/models/llama-3-8b.Q4_K_M.gguf", 4_920_000_000, JunkCategory.MODEL),
        )
        return ScanResult(items)
    }

    override suspend fun clean(items: List<JunkItem>): Long {
        delay(400)
        return items.sumOf { it.sizeBytes } // simulated reclaim
    }
}

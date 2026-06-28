package io.github.ronjunevaldoz.bytesweep.data

import android.content.Context
import io.github.ronjunevaldoz.bytesweep.domain.StorageScanner
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Scans the app's internal and external cache directories. */
class AndroidStorageScanner(private val context: Context) : StorageScanner {

    override suspend fun scan(): ScanResult = withContext(Dispatchers.IO) {
        val roots = listOfNotNull(context.cacheDir, context.externalCacheDir, context.codeCacheDir)
        val items = roots.flatMap { scanTopLevel(it) }
            .sortedByDescending { it.sizeBytes }
            .take(50)
        ScanResult(items)
    }

    override suspend fun clean(items: List<JunkItem>): Long = withContext(Dispatchers.IO) {
        var reclaimed = 0L
        for (item in items) {
            val file = File(item.path)
            if (file.exists() && file.deleteRecursively()) reclaimed += item.sizeBytes
        }
        reclaimed
    }

    private fun scanTopLevel(dir: File): List<JunkItem> {
        val children = dir.listFiles() ?: return emptyList()
        return children.mapNotNull { child ->
            val size = sizeOf(child)
            if (size <= 0) return@mapNotNull null
            val cat = when {
                child.isFile && child.extension.equals("log", ignoreCase = true) -> JunkCategory.LOGS
                child.isFile && size > 100_000_000 -> JunkCategory.LARGE_FILE
                else -> JunkCategory.CACHE
            }
            JunkItem(
                id = child.absolutePath,
                name = child.name,
                path = child.absolutePath,
                sizeBytes = size,
                category = cat,
            )
        }
    }

    private fun sizeOf(file: File): Long =
        if (file.isDirectory) {
            file.walkBottomUp().filter { it.isFile }.sumOf { it.length() }
        } else {
            file.length()
        }
}

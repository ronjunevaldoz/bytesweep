package io.github.ronjunevaldoz.bytesweep.data

import io.github.ronjunevaldoz.bytesweep.domain.StorageScanner
import io.github.ronjunevaldoz.bytesweep.model.FileClassifier
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSFileType
import platform.Foundation.NSFileTypeDirectory
import platform.Foundation.NSNumber
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask

/** Scans the iOS Caches and temporary directories via NSFileManager. */
@OptIn(ExperimentalForeignApi::class)
class IosStorageScanner : StorageScanner {

    private val fm = NSFileManager.defaultManager

    @Suppress("UNCHECKED_CAST")
    override suspend fun scan(): ScanResult = withContext(Dispatchers.Default) {
        val roots = buildList {
            val caches = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
                .firstOrNull() as? String
            if (caches != null) add(caches to JunkCategory.CACHE)
            add(NSTemporaryDirectory() to JunkCategory.TEMP)
        }
        val items = roots.flatMap { (path, cat) -> scanTopLevel(path, cat) }
            .sortedByDescending { it.sizeBytes }
            .take(50)
        ScanResult(items)
    }

    override suspend fun clean(items: List<JunkItem>): Long = withContext(Dispatchers.Default) {
        var reclaimed = 0L
        for (item in items) {
            if (fm.removeItemAtPath(item.path, error = null)) reclaimed += item.sizeBytes
        }
        reclaimed
    }

    @Suppress("UNCHECKED_CAST")
    private fun scanTopLevel(dirPath: String, category: JunkCategory): List<JunkItem> {
        val names = fm.contentsOfDirectoryAtPath(dirPath, error = null) as? List<String> ?: return emptyList()
        return names.mapNotNull { name ->
            val full = "$dirPath/$name"
            val size = sizeOf(full)
            if (size <= 0) return@mapNotNull null
            val cat = FileClassifier.classify(name, size, fallback = category)
            JunkItem(id = full, name = name, path = full, sizeBytes = size, category = cat)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun sizeOf(path: String): Long {
        val attrs = fm.attributesOfItemAtPath(path, error = null) ?: return 0L
        val isDir = (attrs[NSFileType] as? String) == NSFileTypeDirectory
        if (!isDir) return (attrs[NSFileSize] as? NSNumber)?.longLongValue ?: 0L

        var total = 0L
        val enumerator = fm.enumeratorAtPath(path)
        while (true) {
            val child = enumerator?.nextObject() as? String ?: break
            val childAttrs = fm.attributesOfItemAtPath("$path/$child", error = null) ?: continue
            total += (childAttrs[NSFileSize] as? NSNumber)?.longLongValue ?: 0L
        }
        return total
    }
}

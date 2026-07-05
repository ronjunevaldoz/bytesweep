package io.github.ronjunevaldoz.bytesweep.data

import io.github.ronjunevaldoz.bytesweep.domain.FolderScanner
import io.github.ronjunevaldoz.bytesweep.model.FileClassifier
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.Promise

/** Real, user-consented folder scan + delete on the web via the File System Access API (Chromium). */
class WebFolderScanner : FolderScanner {

    private val supported: Boolean =
        js("typeof window !== 'undefined' && typeof window.showDirectoryPicker === 'function'") as Boolean

    override val isSupported: Boolean = supported
    override val canDelete: Boolean = supported

    override suspend fun pickAndScan(): ScanResult? {
        // Promise chains only (js() rejects async/await). Recurses, stashes the root
        // handle on globalThis, returns a JSON array string, or null if cancelled.
        val promise: Promise<String?> = js(
            """(function() {
                if (typeof window === 'undefined' || !window.showDirectoryPicker) return Promise.resolve(null);
                return window.showDirectoryPicker().then(function(root) {
                    globalThis.__bsRoot = root;
                    var out = [];
                    function walk(dir, prefix, depth) {
                        if (depth > 8 || out.length > 3000) return Promise.resolve();
                        var it = dir.entries();
                        function step() {
                            return it.next().then(function(res) {
                                if (res.done) return;
                                var name = res.value[0], entry = res.value[1];
                                var path = prefix ? (prefix + '/' + name) : name;
                                if (entry.kind === 'file') {
                                    return entry.getFile().then(function(f) { out.push({ path: path, size: f.size }); return step(); }, function() { return step(); });
                                } else if (entry.kind === 'directory') {
                                    return walk(entry, path, depth + 1).then(function() { return step(); }, function() { return step(); });
                                }
                                return step();
                            });
                        }
                        return step();
                    }
                    return walk(root, '', 0).then(function() { return JSON.stringify(out); });
                }).catch(function(e) { return null; });
            })()""",
        )
        val json = promise.await() ?: return null
        return ScanResult(webEntriesToItems(json))
    }

    override suspend fun delete(items: List<JunkItem>): Long {
        if (items.isEmpty()) return 0L
        ensureDeleteFn()
        val pathsJson = Json.encodeToString(items.map { it.path })
        val fn = js("window.__bsDelete")
        val promise: Promise<String?> = fn(pathsJson).unsafeCast<Promise<String?>>()
        val deletedJson = promise.await() ?: return 0L
        val deleted = Json.decodeFromString<List<String>>(deletedJson).toSet()
        return items.filter { it.path in deleted }.sumOf { it.sizeBytes }
    }

    private fun ensureDeleteFn() {
        js(
            """if (!window.__bsDelete) { window.__bsDelete = function(pathsJson) {
                var root = globalThis.__bsRoot;
                if (!root) return Promise.resolve('[]');
                var paths = JSON.parse(pathsJson);
                var deleted = [];
                function parentOf(segs, idx, handle) {
                    if (idx >= segs.length - 1) return Promise.resolve(handle);
                    return handle.getDirectoryHandle(segs[idx]).then(function(sub) { return parentOf(segs, idx + 1, sub); });
                }
                function delOne(i) {
                    if (i >= paths.length) return JSON.stringify(deleted);
                    var p = paths[i];
                    var segs = p.split('/');
                    var leaf = segs[segs.length - 1];
                    return parentOf(segs, 0, root).then(function(parent) {
                        return parent.removeEntry(leaf, { recursive: true }).then(function() { deleted.push(p); return delOne(i + 1); }, function() { return delOne(i + 1); });
                    }, function() { return delOne(i + 1); });
                }
                return Promise.resolve(delOne(0));
            }; }""",
        )
    }
}

@Serializable
internal data class WebEntry(val path: String, val size: Long)

internal fun webEntriesToItems(json: String): List<JunkItem> =
    Json.decodeFromString<List<WebEntry>>(json)
        .filter { it.size > 0 }
        .map {
            val name = it.path.substringAfterLast('/')
            JunkItem(
                id = it.path,
                name = name,
                path = it.path,
                sizeBytes = it.size,
                category = FileClassifier.classify(name, it.size, JunkCategory.OTHER),
                selected = false,
            )
        }
        .sortedByDescending { it.sizeBytes }
        .take(500)

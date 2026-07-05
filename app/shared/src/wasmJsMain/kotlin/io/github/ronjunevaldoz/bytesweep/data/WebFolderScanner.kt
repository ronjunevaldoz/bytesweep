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

/** true only where the File System Access API exists (Chromium: Chrome/Edge). */
@JsFun("() => (typeof window !== 'undefined' && typeof window.showDirectoryPicker === 'function')")
private external fun supportsDirectoryPicker(): Boolean

/**
 * Opens the directory picker, reads each file's size, and returns a JSON array string
 * of {name,size}. Returns null if the user cancels. All async work happens in JS so we
 * never drive the async iterator from Wasm.
 */
@JsFun(
    """() => {
        if (typeof window === 'undefined' || !window.showDirectoryPicker) return Promise.resolve(null);
        return window.showDirectoryPicker().then(function(handle) {
            var out = [];
            var it = handle.entries();
            function step() {
                return it.next().then(function(res) {
                    if (res.done) return JSON.stringify(out);
                    var name = res.value[0], entry = res.value[1];
                    if (entry.kind === 'file') {
                        return entry.getFile().then(function(f) { out.push({ name: name, size: f.size }); return step(); });
                    }
                    return step();
                });
            }
            return step();
        }).catch(function(e) { return null; });
    }""",
)
private external fun pickAndListJson(): Promise<JsString?>

/** Real, user-consented folder scan on the web via the File System Access API. */
class WebFolderScanner : FolderScanner {

    override val isSupported: Boolean = supportsDirectoryPicker()

    override suspend fun pickAndScan(): ScanResult? {
        val json = pickAndListJson().await() ?: return null
        return ScanResult(webEntriesToItems(json.toString()))
    }
}

@Serializable
internal data class WebEntry(val name: String, val size: Long)

internal fun webEntriesToItems(json: String): List<JunkItem> =
    Json.decodeFromString<List<WebEntry>>(json)
        .filter { it.size > 0 }
        .map {
            JunkItem(
                id = it.name,
                name = it.name,
                path = it.name,
                sizeBytes = it.size,
                category = FileClassifier.classify(it.name, it.size, JunkCategory.OTHER),
                selected = false,
            )
        }
        .sortedByDescending { it.sizeBytes }
        .take(200)

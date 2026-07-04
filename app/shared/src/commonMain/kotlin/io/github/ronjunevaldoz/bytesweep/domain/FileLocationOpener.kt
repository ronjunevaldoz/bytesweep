package io.github.ronjunevaldoz.bytesweep.domain

import io.github.ronjunevaldoz.bytesweep.model.JunkItem

/**
 * Reveals a scanned item in the platform's file manager. Support is platform-specific:
 * Desktop reveals the file in Finder/Explorer/Files; Android is best-effort; iOS and Web
 * are sandboxed and report [isSupported] = false so the UI hides the action.
 */
interface FileLocationOpener {
    /** Whether this platform can open file locations at all. */
    val isSupported: Boolean

    /** Opens/reveals the item's location. Returns false if it couldn't be opened. */
    suspend fun open(item: JunkItem): Boolean
}

/** Fallback for sandboxed platforms (iOS, Web) where filesystem reveal isn't possible. */
class UnsupportedFileLocationOpener : FileLocationOpener {
    override val isSupported: Boolean = false
    override suspend fun open(item: JunkItem): Boolean = false
}

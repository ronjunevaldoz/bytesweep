package io.github.ronjunevaldoz.bytesweep.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import io.github.ronjunevaldoz.bytesweep.domain.FileLocationOpener
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Best-effort reveal on Android: asks a file-manager app to view the containing folder.
 * Not all devices ship a handler, so [open] returns false gracefully when none is found.
 */
class AndroidFileLocationOpener(private val context: Context) : FileLocationOpener {

    override val isSupported: Boolean = true

    override suspend fun open(item: JunkItem): Boolean = withContext(Dispatchers.IO) {
        val parent = File(item.path).parentFile ?: return@withContext false
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(parent), "resource/folder")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching {
            context.startActivity(intent)
            true
        }.getOrDefault(false)
    }
}

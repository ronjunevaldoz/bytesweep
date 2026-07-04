package io.github.ronjunevaldoz.bytesweep.data

import io.github.ronjunevaldoz.bytesweep.domain.FileLocationOpener
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Reveals the file in the OS file manager (Finder / Explorer / default file browser). */
class DesktopFileLocationOpener : FileLocationOpener {

    override val isSupported: Boolean = true

    override suspend fun open(item: JunkItem): Boolean = withContext(Dispatchers.IO) {
        val file = File(item.path)
        val os = System.getProperty("os.name").orEmpty().lowercase()
        runCatching {
            when {
                os.contains("mac") ->
                    ProcessBuilder("open", "-R", file.absolutePath).start()
                os.contains("win") ->
                    ProcessBuilder("explorer.exe", "/select,", file.absolutePath).start()
                else ->
                    ProcessBuilder("xdg-open", (file.parentFile ?: file).absolutePath).start()
            }
            true
        }.getOrDefault(false)
    }
}

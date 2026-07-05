package io.github.ronjunevaldoz.bytesweep.data

import io.github.ronjunevaldoz.bytesweep.domain.FolderScanner
import io.github.ronjunevaldoz.bytesweep.model.FileClassifier
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

/**
 * Opens a native folder chooser and scans the selected directory's top-level entries,
 * classifying each by file type. This is a real scan of user-chosen files (unlike the
 * fixed cache/temp scan of [DesktopStorageScanner]).
 */
class DesktopFolderScanner : FolderScanner {

    override val isSupported: Boolean = true
    override val canDelete: Boolean = true

    override suspend fun pickAndScan(): ScanResult? = withContext(Dispatchers.IO) {
        val dir = chooseDirectory() ?: return@withContext null
        val items = scanDirectory(dir)
        ScanResult(items)
    }

    override suspend fun delete(items: List<JunkItem>): Long = withContext(Dispatchers.IO) {
        var reclaimed = 0L
        for (item in items) {
            val file = File(item.path)
            if (file.exists() && file.deleteRecursively()) reclaimed += item.sizeBytes
        }
        reclaimed
    }

    /** Shows a DIRECTORIES_ONLY chooser on the EDT and returns the selection. */
    private fun chooseDirectory(): File? {
        var selected: File? = null
        val show = Runnable {
            val chooser = JFileChooser().apply {
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                dialogTitle = "Choose a folder to scan"
            }
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selected = chooser.selectedFile
            }
        }
        if (SwingUtilities.isEventDispatchThread()) show.run() else SwingUtilities.invokeAndWait(show)
        return selected
    }

    private fun scanDirectory(dir: File): List<JunkItem> {
        val children = dir.listFiles() ?: return emptyList()
        return children.mapNotNull { child ->
            val size = sizeOf(child)
            if (size <= 0) return@mapNotNull null
            JunkItem(
                id = child.absolutePath,
                name = child.name,
                path = child.absolutePath,
                sizeBytes = size,
                category = FileClassifier.classify(child.name, size, JunkCategory.OTHER),
                selected = false, // user-chosen folders: don't pre-select for deletion
            )
        }.sortedByDescending { it.sizeBytes }.take(200)
    }

    private fun sizeOf(file: File): Long =
        if (file.isDirectory) {
            file.walkBottomUp().filter { it.isFile }.sumOf { it.length() }
        } else {
            file.length()
        }
}

package io.github.ronjunevaldoz.bytesweep

import io.github.ronjunevaldoz.bytesweep.model.FileClassifier
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import kotlin.test.Test
import kotlin.test.assertEquals

class FileClassifierTest {

    @Test
    fun classifiesByExtension() {
        assertEquals(JunkCategory.DOCUMENT, FileClassifier.classify("report.pdf", 1000))
        assertEquals(JunkCategory.IMAGE, FileClassifier.classify("photo.PNG", 1000))
        assertEquals(JunkCategory.AUDIO, FileClassifier.classify("song.mp3", 1000))
        assertEquals(JunkCategory.VIDEO, FileClassifier.classify("clip.mp4", 1000))
        assertEquals(JunkCategory.ARCHIVE, FileClassifier.classify("backup.zip", 1000))
        assertEquals(JunkCategory.CODE, FileClassifier.classify("Main.kt", 1000))
    }

    @Test
    fun classifiesHuggingFaceModels() {
        assertEquals(JunkCategory.MODEL, FileClassifier.classify("llama-3-8b.Q4_K_M.gguf", 5_000_000_000))
        assertEquals(JunkCategory.MODEL, FileClassifier.classify("model.safetensors", 1_000))
        assertEquals(JunkCategory.MODEL, FileClassifier.classify("pytorch_model.bin", 1_000))
    }

    @Test
    fun fallsBackForUnknownExtensions() {
        assertEquals(JunkCategory.CACHE, FileClassifier.classify("blob", 1000, fallback = JunkCategory.CACHE))
    }

    @Test
    fun largeUnknownBecomesLargeFile() {
        assertEquals(JunkCategory.LARGE_FILE, FileClassifier.classify("blob", 200_000_000))
    }
}

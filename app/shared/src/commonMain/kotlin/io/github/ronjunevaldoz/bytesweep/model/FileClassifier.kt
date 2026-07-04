package io.github.ronjunevaldoz.bytesweep.model

/**
 * Classifies a file into a [JunkCategory] by extension, so every platform scanner
 * organizes storage the same way. Known file types win; otherwise the scanner's
 * [fallback] (the nature of the directory being scanned, e.g. a cache dir) is used,
 * or [JunkCategory.LARGE_FILE] for big unknowns.
 */
object FileClassifier {

    private val documents = setOf(
        "pdf", "doc", "docx", "txt", "md", "rtf", "odt", "pages",
        "xls", "xlsx", "csv", "tsv", "ppt", "pptx", "key", "numbers", "epub",
    )
    private val images = setOf("png", "jpg", "jpeg", "gif", "webp", "bmp", "svg", "heic", "heif", "tiff", "ico")
    private val audio = setOf("mp3", "wav", "flac", "aac", "m4a", "ogg", "opus", "aiff", "wma", "mid")
    private val video = setOf("mp4", "mov", "mkv", "avi", "webm", "m4v", "flv", "wmv", "mpg", "mpeg")
    private val archives = setOf("zip", "tar", "gz", "tgz", "bz2", "xz", "zst", "7z", "rar", "dmg", "iso", "pkg")

    /** ML / Hugging Face model weights and checkpoints — usually the largest items on disk. */
    private val models = setOf(
        "gguf", "ggml", "safetensors", "bin", "pt", "pth", "onnx",
        "ckpt", "h5", "pb", "tflite", "mlmodel", "mlpackage", "npz", "npy", "pkl",
    )
    private val code = setOf(
        "kt", "kts", "java", "js", "mjs", "ts", "tsx", "py", "rb", "go", "rs",
        "c", "h", "cpp", "hpp", "cc", "swift", "sh", "json", "xml", "yaml", "yml", "toml", "sql",
    )
    private val logExt = setOf("log", "log1", "log2")
    private val tempExt = setOf("tmp", "temp", "part", "crdownload", "download", "bak", "old", "swp", "~")

    private const val LARGE_THRESHOLD = 100_000_000L // 100 MB

    fun classify(name: String, sizeBytes: Long, fallback: JunkCategory = JunkCategory.OTHER): JunkCategory {
        val ext = name.substringAfterLast('.', "").lowercase()
        return when (ext) {
            in models -> JunkCategory.MODEL
            in documents -> JunkCategory.DOCUMENT
            in images -> JunkCategory.IMAGE
            in audio -> JunkCategory.AUDIO
            in video -> JunkCategory.VIDEO
            in archives -> JunkCategory.ARCHIVE
            in code -> JunkCategory.CODE
            in logExt -> JunkCategory.LOGS
            in tempExt -> JunkCategory.TEMP
            else -> if (sizeBytes > LARGE_THRESHOLD) JunkCategory.LARGE_FILE else fallback
        }
    }
}

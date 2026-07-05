package io.github.ronjunevaldoz.bytesweep.di

import io.github.ronjunevaldoz.bytesweep.data.DemoStorageScanner
import io.github.ronjunevaldoz.bytesweep.data.WebFolderScanner
import io.github.ronjunevaldoz.bytesweep.domain.FileLocationOpener
import io.github.ronjunevaldoz.bytesweep.domain.FolderScanner
import io.github.ronjunevaldoz.bytesweep.domain.StorageScanner
import io.github.ronjunevaldoz.bytesweep.domain.UnsupportedFileLocationOpener
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<StorageScanner> { DemoStorageScanner() }
    single<FileLocationOpener> { UnsupportedFileLocationOpener() }
    single<FolderScanner> { WebFolderScanner() }
}

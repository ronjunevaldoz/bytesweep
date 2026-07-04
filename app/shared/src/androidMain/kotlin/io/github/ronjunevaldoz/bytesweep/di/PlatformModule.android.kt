package io.github.ronjunevaldoz.bytesweep.di

import io.github.ronjunevaldoz.bytesweep.data.AndroidFileLocationOpener
import io.github.ronjunevaldoz.bytesweep.data.AndroidStorageScanner
import io.github.ronjunevaldoz.bytesweep.domain.FileLocationOpener
import io.github.ronjunevaldoz.bytesweep.domain.StorageScanner
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<StorageScanner> { AndroidStorageScanner(AppContextHolder.context) }
    single<FileLocationOpener> { AndroidFileLocationOpener(AppContextHolder.context) }
}

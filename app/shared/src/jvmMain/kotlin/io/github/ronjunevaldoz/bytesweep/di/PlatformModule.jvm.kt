package io.github.ronjunevaldoz.bytesweep.di

import io.github.ronjunevaldoz.bytesweep.data.DesktopStorageScanner
import io.github.ronjunevaldoz.bytesweep.domain.StorageScanner
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<StorageScanner> { DesktopStorageScanner() }
}

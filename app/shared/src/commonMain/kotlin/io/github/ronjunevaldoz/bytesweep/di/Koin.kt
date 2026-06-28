package io.github.ronjunevaldoz.bytesweep.di

import io.github.ronjunevaldoz.bytesweep.domain.CleanJunkUseCase
import io.github.ronjunevaldoz.bytesweep.domain.ScanStorageUseCase
import io.github.ronjunevaldoz.bytesweep.presenter.ScannerViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/** Common bindings shared across every platform. */
val appModule = module {
    singleOf(::ScanStorageUseCase)
    singleOf(::CleanJunkUseCase)
    viewModelOf(::ScannerViewModel)
}

/** Each platform binds its own [io.github.ronjunevaldoz.bytesweep.domain.StorageScanner]. */
expect fun platformModule(): Module

/** Call once at application start on every platform. */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModule, platformModule())
    }
}

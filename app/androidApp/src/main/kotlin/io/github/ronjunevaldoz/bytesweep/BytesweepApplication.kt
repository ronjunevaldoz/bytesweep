package io.github.ronjunevaldoz.bytesweep

import android.app.Application
import io.github.ronjunevaldoz.bytesweep.di.AppContextHolder
import io.github.ronjunevaldoz.bytesweep.di.initKoin

class BytesweepApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.context = applicationContext
        initKoin()
    }
}

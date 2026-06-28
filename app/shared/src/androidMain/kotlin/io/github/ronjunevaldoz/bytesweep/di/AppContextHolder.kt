package io.github.ronjunevaldoz.bytesweep.di

import android.content.Context

/**
 * Holds the application [Context] so the Android platform module can build a
 * scanner without depending on koin-android. Set this once from your
 * Application/Activity before calling [initKoin].
 */
object AppContextHolder {
    lateinit var context: Context
}

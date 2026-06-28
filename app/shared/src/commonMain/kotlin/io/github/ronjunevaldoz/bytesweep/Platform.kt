package io.github.ronjunevaldoz.bytesweep

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
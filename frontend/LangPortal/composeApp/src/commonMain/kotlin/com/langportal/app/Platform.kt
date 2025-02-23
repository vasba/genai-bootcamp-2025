package com.langportal.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
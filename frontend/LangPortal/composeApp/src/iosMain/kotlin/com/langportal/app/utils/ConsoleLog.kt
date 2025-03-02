package com.langportal.app
import platform.Foundation.NSLog

actual fun consoleLog(message: String) {
    NSLog("LangPortal: %@", message)
}

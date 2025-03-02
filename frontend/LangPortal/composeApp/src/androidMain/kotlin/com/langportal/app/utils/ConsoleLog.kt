package com.langportal.app

import android.util.Log

actual fun consoleLog(message: String) {
    Log.d("LangPortal", message)
}

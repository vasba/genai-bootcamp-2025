package com.langportal.app.api

expect suspend fun fetchJson(url: String): String
expect suspend fun postJson(url: String, body: String): String
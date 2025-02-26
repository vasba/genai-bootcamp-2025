package com.langportal.app.api

import com.langportal.app.model.DashboardData
import kotlinx.serialization.json.Json

expect class DashboardApi() {
    suspend fun getDashboardData(): Result<DashboardData>
}
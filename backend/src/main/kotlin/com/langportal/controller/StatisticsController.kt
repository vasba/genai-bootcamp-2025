package com.langportal.controller

import com.langportal.dto.ReviewStatsDTO
import com.langportal.service.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/statistics")
class StatisticsController(private val statisticsService: StatisticsService) {

    @GetMapping("/sessions/{sessionId}")
    fun getSessionStatistics(@PathVariable sessionId: Long): ResponseEntity<ReviewStatsDTO> {
        val stats = statisticsService.getSessionStatistics(sessionId)
        return ResponseEntity.ok(ReviewStatsDTO(
            totalReviews = stats.totalReviews,
            correctReviews = stats.correctReviews,
            accuracy = stats.accuracy
        ))
    }

    @GetMapping("/groups/{groupId}")
    fun getGroupStatistics(@PathVariable groupId: Long): ResponseEntity<ReviewStatsDTO> {
        val stats = statisticsService.getGroupStatistics(groupId)
        return ResponseEntity.ok(ReviewStatsDTO(
            totalReviews = stats.totalReviews,
            correctReviews = stats.correctReviews,
            accuracy = stats.accuracy
        ))
    }
}
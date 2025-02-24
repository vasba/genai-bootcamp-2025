package com.langportal.controller

import com.langportal.service.ReviewStatistics
import com.langportal.service.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/statistics")
class StatisticsController(private val statisticsService: StatisticsService) {

    @GetMapping("/sessions/{sessionId}")
    fun getSessionStatistics(@PathVariable sessionId: Long): ResponseEntity<ReviewStatistics> {
        return try {
            val stats = statisticsService.getSessionStatistics(sessionId)
            ResponseEntity.ok(stats)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/groups/{groupId}")
    fun getGroupStatistics(@PathVariable groupId: Long): ResponseEntity<ReviewStatistics> {
        return try {
            val stats = statisticsService.getGroupStatistics(groupId)
            ResponseEntity.ok(stats)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/words")
    fun getWordStatistics(): ResponseEntity<ReviewStatistics> {
        val stats = statisticsService.getWordStatistics()
        return ResponseEntity.ok(stats)
    }
}

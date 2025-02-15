package com.langportal.controller

import com.langportal.model.StudyActivity
import com.langportal.service.StudyActivityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/activities")
class StudyActivityController(
    private val studyActivityService: StudyActivityService,
) {
    @GetMapping
    fun getAllActivities(): ResponseEntity<List<StudyActivity>> = ResponseEntity.ok(studyActivityService.getAllActivities())

    @GetMapping("/{id}")
    fun getActivityById(
        @PathVariable id: Long,
    ): ResponseEntity<StudyActivity> = ResponseEntity.ok(studyActivityService.getActivityById(id))

    @DeleteMapping("/{id}")
    fun deleteActivity(
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        studyActivityService.deleteActivity(id)
        return ResponseEntity.ok().build()
    }
}

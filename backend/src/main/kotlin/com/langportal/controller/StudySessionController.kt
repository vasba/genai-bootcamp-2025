package com.langportal.controller

import com.langportal.dto.*
import com.langportal.dto.response.ApiResponse
import com.langportal.mapper.ModelMapper
import com.langportal.model.StudySession
import com.langportal.service.StudySessionService
import com.langportal.service.WordReviewService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/study-sessions")
@CrossOrigin
class StudySessionController(
    private val studySessionService: StudySessionService,
    private val wordReviewService: WordReviewService,
    private val modelMapper: ModelMapper,
) {
    data class CreateSessionRequest(
        val groupId: Long,
        val studyActivityId: Long,
    )

    @PostMapping
    fun createSession(
        @RequestBody request: CreateSessionRequest,
    ): ResponseEntity<ApiResponse<StudySessionDTO>> {
        val session = studySessionService.createStudySession(request.studyActivityId, request.groupId)
        return ResponseEntity.ok(ApiResponse.success(modelMapper.toStudySessionDTO(session)))
    }

    @PostMapping("/{id}/review")
    fun addReview(
        @PathVariable id: Long,
        @RequestBody review: WordReviewRequestDTO,
    ): ResponseEntity<ApiResponse<Boolean>> {
        val reviewItem = wordReviewService.createWordReview(id, review.wordId, review.correct)
        return ResponseEntity.ok(ApiResponse.success(true))
    }

    @GetMapping("/{id}/reviews")
    fun getSessionReviews(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<List<WordReviewItemDTO>>> {
        val reviews = wordReviewService.getSessionReviews(id)
        return ResponseEntity.ok(
            ApiResponse.success(
                reviews.map { modelMapper.toWordReviewItemDTO(it) },
            ),
        )
    }

    @GetMapping
    fun getAllSessions(pageable: Pageable): ResponseEntity<Page<StudySessionDTO>> {
        val sessions = studySessionService.getAllStudySessions(pageable)
        return ResponseEntity.ok(sessions.map { modelMapper.toStudySessionDTO(it) })
    }

    @GetMapping("/by-activity")
    fun getSessionsByActivity(
        @RequestParam activityId: Long,
        pageable: Pageable,
    ): ResponseEntity<Page<StudySessionDTO>> {
        val sessions = studySessionService.getStudySessionsByActivity(activityId, pageable)
        return ResponseEntity.ok(sessions.map { modelMapper.toStudySessionDTO(it) })
    }

    @GetMapping("/{id}")
    fun getSessionById(
        @PathVariable id: Long,
    ): ResponseEntity<StudySessionDTO> {
        val session = studySessionService.getStudySessionById(id)
        return ResponseEntity.ok(modelMapper.toStudySessionDTO(session))
    }

    @PutMapping("/{id}")
    fun updateSession(
        @PathVariable id: Long,
        @RequestBody session: StudySession,
    ): ResponseEntity<StudySessionDTO> {
        val updatedSession = studySessionService.updateStudySession(id, session.endTime!!)
        return ResponseEntity.ok(modelMapper.toStudySessionDTO(updatedSession))
    }

    @GetMapping("/{id}/review-items")
    fun getSessionReviewItems(
        @PathVariable id: Long,
    ): ResponseEntity<List<WordReviewItemDTO>> {
        val session = studySessionService.getStudySessionById(id)
        return ResponseEntity.ok(
            session.reviewItems.map { modelMapper.toWordReviewItemDTO(it) },
        )
    }

    @GetMapping("/last")
    fun getLastStudySession(): ResponseEntity<StudySessionDTO?> {
        val session = studySessionService.getLastStudySession()
        return ResponseEntity.ok(session?.let { modelMapper.toStudySessionDTO(it) })
    }
}

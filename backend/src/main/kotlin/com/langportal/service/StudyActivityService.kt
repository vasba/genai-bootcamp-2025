package com.langportal.service

import com.langportal.model.StudyActivity
import com.langportal.repository.StudyActivityRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudyActivityService(private val studyActivityRepository: StudyActivityRepository) {
    
    @Transactional(readOnly = true)
    fun getAllActivities(): List<StudyActivity> {
        return studyActivityRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun getActivityById(id: Long): StudyActivity {
        return studyActivityRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Activity not found with id: $id") }
    }

    @Transactional
    fun deleteActivity(id: Long) {
        if (!studyActivityRepository.existsById(id)) {
            throw EntityNotFoundException("Activity not found with id: $id")
        }
        studyActivityRepository.deleteById(id)
    }
}
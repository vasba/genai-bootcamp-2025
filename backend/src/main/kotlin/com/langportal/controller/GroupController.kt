package com.langportal.controller

import com.langportal.dto.GroupDTO
import com.langportal.dto.ReviewStatsDTO
import com.langportal.mapper.ModelMapper
import com.langportal.model.Group
import com.langportal.service.GroupService
import com.langportal.service.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController(
    private val groupService: GroupService,
    private val statisticsService: StatisticsService,
    private val modelMapper: ModelMapper,
) {
    @GetMapping
    fun getAllGroups(): ResponseEntity<List<GroupDTO>> {
        val groups = groupService.getAllGroups()
        return ResponseEntity.ok(groups.map { modelMapper.toGroupWithoutWords(it) })
    }

    @GetMapping("/{id}")
    fun getGroupById(
        @PathVariable id: Long,
    ): ResponseEntity<GroupDTO> {
        val group = groupService.getGroup(id)
        return ResponseEntity.ok(modelMapper.toGroupDTO(group))
    }

    @PostMapping
    fun createGroup(
        @RequestBody group: String,
    ): ResponseEntity<GroupDTO> {
        val createdGroup = groupService.createGroup(group)
        return ResponseEntity.ok(modelMapper.toGroupDTO(createdGroup))
    }

    @PutMapping("/{id}")
    fun updateGroup(
        @PathVariable id: Long,
        @RequestBody group: Group,
    ): ResponseEntity<GroupDTO> {
        val updatedGroup = groupService.updateGroup(id, group)
        return ResponseEntity.ok(modelMapper.toGroupDTO(updatedGroup))
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        groupService.deleteGroup(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}/stats")
    fun getGroupStatistics(
        @PathVariable id: Long,
    ): ResponseEntity<ReviewStatsDTO> {
        val stats = statisticsService.getGroupStatistics(id)
        return ResponseEntity.ok(
            ReviewStatsDTO(
                totalReviews = stats.totalReviews,
                correctReviews = stats.correctReviews,
                accuracy = stats.accuracy,
            ),
        )
    }
}

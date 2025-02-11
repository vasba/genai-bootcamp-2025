package com.langportal.service

import com.langportal.model.Group
import com.langportal.model.Word
import com.langportal.repository.GroupRepository
import com.langportal.repository.WordRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val wordRepository: WordRepository
) {
    @Transactional(readOnly = true)
    fun getGroups(pageable: Pageable): Page<Group> {
        return groupRepository.findAll(pageable)
    }
    
    @Transactional(readOnly = true)
    fun getAllGroups(): List<Group> {
        return groupRepository.findAll()
    }
    
    @Transactional(readOnly = true)
    fun getGroup(id: Long): Group {
        return groupRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Group not found with id: $id") }
    }
    
    @Transactional(readOnly = true)
    fun getGroupWithWords(id: Long): Group {
        return groupRepository.findByIdWithWords(id)
            ?: throw EntityNotFoundException("Group not found with id: $id")
    }
    
    @Transactional
    fun createGroup(name: String): Group {
        val group = Group(name = name)
        return groupRepository.save(group)
    }
    
    @Transactional
    fun addWordToGroup(groupId: Long, wordId: Long): Group {
        val group = getGroupWithWords(groupId)
        val word = wordRepository.findById(wordId)
            .orElseThrow { EntityNotFoundException("Word not found with id: $wordId") }
        
        group.words.add(word)
        group.wordsCount = group.words.size
        return groupRepository.save(group)
    }
    
    @Transactional
    fun removeWordFromGroup(groupId: Long, wordId: Long): Group {
        val group = getGroupWithWords(groupId)
        val word = wordRepository.findById(wordId)
            .orElseThrow { EntityNotFoundException("Word not found with id: $wordId") }
        
        group.words.remove(word)
        group.wordsCount = group.words.size
        return groupRepository.save(group)
    }

    @Transactional
    fun deleteGroup(id: Long) {
        if (!groupRepository.existsById(id)) {
            throw EntityNotFoundException("Group not found with id: $id")
        }
        groupRepository.deleteById(id)
    }

    @Transactional
    fun updateGroup(id: Long, updatedGroup: Group): Group {
        val existingGroup = groupRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Group not found with id: $id") }
        
        existingGroup.name = updatedGroup.name
        existingGroup.description = updatedGroup.description
        
        return groupRepository.save(existingGroup)
    }

    @Transactional
    fun addWordsToGroup(id: Long, words: List<Long>): Group {
        val group = groupRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Group not found with id: $id") }
        // Words handling logic here
        return groupRepository.save(group)
    }
}
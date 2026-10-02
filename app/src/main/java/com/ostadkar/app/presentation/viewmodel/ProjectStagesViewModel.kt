package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.ProjectStage
import com.ostadkar.app.domain.repository.ProjectStageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectStagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stageRepository: ProjectStageRepository
) : ViewModel() {

    val projectId: Long = savedStateHandle["projectId"] ?: 0L

    val stages: StateFlow<List<ProjectStage>> = stageRepository
        .observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cycleStatus(stage: ProjectStage) {
        val next = when (stage.status) {
            "not_started" -> "in_progress"
            "in_progress" -> "completed"
            else -> "not_started"
        }
        viewModelScope.launch {
            stageRepository.save(
                stage.copy(
                    status = next,
                    completedAt = if (next == "completed") System.currentTimeMillis() else null
                )
            )
        }
    }
}

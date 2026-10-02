package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.FinancialSummary
import com.ostadkar.app.domain.model.Project
import com.ostadkar.app.domain.repository.DashboardRepository
import com.ostadkar.app.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    projectRepository: ProjectRepository,
    dashboardRepository: DashboardRepository
) : ViewModel() {

    private val projectId: Long = savedStateHandle["projectId"] ?: 0L

    val project: StateFlow<Project?> = projectRepository
        .observeById(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val financial: StateFlow<FinancialSummary> = dashboardRepository
        .observeProjectFinancial(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())
}

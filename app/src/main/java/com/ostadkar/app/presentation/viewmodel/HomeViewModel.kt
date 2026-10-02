package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.HomeDashboard
import com.ostadkar.app.domain.model.Project
import com.ostadkar.app.domain.repository.DashboardRepository
import com.ostadkar.app.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dashboardRepository: DashboardRepository,
    projectRepository: ProjectRepository
) : ViewModel() {

    val dashboard: StateFlow<HomeDashboard> = dashboardRepository
        .observeHomeDashboard()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeDashboard()
        )

    val recentProjects: StateFlow<List<Project>> = projectRepository
        .observeActive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

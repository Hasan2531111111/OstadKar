package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.HomeDashboard
import com.ostadkar.app.domain.model.Project
import com.ostadkar.app.domain.repository.DashboardRepository
import com.ostadkar.app.domain.repository.ProjectRepository
import com.ostadkar.app.domain.repository.UserProfile
import com.ostadkar.app.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dashboardRepository: DashboardRepository,
    projectRepository: ProjectRepository,
    userProfileRepository: UserProfileRepository
) : ViewModel() {

    val dashboard: StateFlow<HomeDashboard> = dashboardRepository
        .observeHomeDashboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeDashboard())

    val recentProjects: StateFlow<List<Project>> = projectRepository
        .observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<UserProfile> = userProfileRepository
        .observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())
}

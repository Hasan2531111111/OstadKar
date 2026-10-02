package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.HomeDashboard
import com.ostadkar.app.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    dashboardRepository: DashboardRepository
) : ViewModel() {
    val dashboard: StateFlow<HomeDashboard> = dashboardRepository
        .observeHomeDashboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeDashboard())
}

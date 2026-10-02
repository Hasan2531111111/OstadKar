package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.WorkItem
import com.ostadkar.app.domain.repository.WorkItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkItemsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workItemRepository: WorkItemRepository
) : ViewModel() {

    val projectId: Long = savedStateHandle["projectId"] ?: 0L

    val workItems: StateFlow<List<WorkItem>> = workItemRepository
        .observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val total: StateFlow<Long> = workItemRepository
        .observeProjectTotal(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            workItemRepository.delete(id)
        }
    }
}

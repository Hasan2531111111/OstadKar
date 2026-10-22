package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.data.backup.BackupExporter
import com.ostadkar.app.data.backup.BackupSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class BackupUiState(
    val isWorking: Boolean = false,
    val lastFile: File? = null,
    val lastSectionTitle: String? = null,
    val error: String? = null
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val exporter: BackupExporter
) : ViewModel() {

    private val _state = MutableStateFlow(BackupUiState())
    val state: StateFlow<BackupUiState> = _state.asStateFlow()

    fun export(section: BackupSection) {
        viewModelScope.launch {
            _state.value = BackupUiState(isWorking = true)
            try {
                val file = exporter.export(section)
                _state.value = BackupUiState(
                    isWorking = false,
                    lastFile = file,
                    lastSectionTitle = section.titleFa
                )
            } catch (e: Exception) {
                _state.value = BackupUiState(
                    isWorking = false,
                    error = e.message ?: "خطا در تهیه پشتیبان"
                )
            }
        }
    }

    fun clearShare() {
        _state.value = _state.value.copy(lastFile = null, lastSectionTitle = null, error = null)
    }
}

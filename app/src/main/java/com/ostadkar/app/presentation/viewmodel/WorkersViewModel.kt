package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.Worker
import com.ostadkar.app.domain.repository.WorkerRepository
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerFormState(
    val show: Boolean = false,
    val editingId: Long? = null,
    val name: String = "",
    val mobile: String = "",
    val type: String = "worker",
    val specialty: String = "",
    val defaultDailyWage: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkersViewModel @Inject constructor(
    private val workerRepository: WorkerRepository
) : ViewModel() {
    val workers: StateFlow<List<Worker>> = workerRepository
        .observeAllActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(WorkerFormState())
    val form: StateFlow<WorkerFormState> = _form.asStateFlow()

    fun showForm() { _form.value = WorkerFormState(show = true) }
    fun hideForm() { _form.value = WorkerFormState(show = false) }

    fun editWorker(worker: Worker) {
        _form.value = WorkerFormState(
            show = true,
            editingId = worker.id,
            name = worker.name,
            mobile = worker.mobile,
            type = worker.type,
            specialty = worker.specialty,
            defaultDailyWage = if (worker.defaultDailyWage > 0)
                MoneyFormatter.formatAmountInput(worker.defaultDailyWage.toString()) else "",
            notes = worker.notes
        )
    }

    fun updateName(v: String) { _form.value = _form.value.copy(name = v, error = null) }
    fun updateMobile(v: String) { _form.value = _form.value.copy(mobile = v.filter { it.isDigit() || it in '۰'..'۹' }) }
    fun updateType(v: String) { _form.value = _form.value.copy(type = v) }
    fun updateSpecialty(v: String) { _form.value = _form.value.copy(specialty = v) }
    fun updateWage(v: String) { _form.value = _form.value.copy(defaultDailyWage = MoneyFormatter.formatAmountInput(v)) }
    fun updateNotes(v: String) { _form.value = _form.value.copy(notes = v) }

    fun save() {
        val f = _form.value
        if (f.name.isBlank()) {
            _form.value = f.copy(error = "نام کارگر الزامی است")
            return
        }
        viewModelScope.launch {
            _form.value = f.copy(isSaving = true)
            workerRepository.save(
                Worker(
                    id = f.editingId ?: 0L,
                    name = f.name.trim(),
                    mobile = f.mobile.trim(),
                    type = f.type,
                    specialty = f.specialty.trim(),
                    defaultDailyWage = MoneyFormatter.parseAmount(f.defaultDailyWage),
                    notes = f.notes.trim(),
                    isActive = true
                )
            )
            _form.value = WorkerFormState(show = false)
        }
    }

    fun deleteWorker(id: Long) {
        viewModelScope.launch {
            workerRepository.delete(id)
            if (_form.value.editingId == id) hideForm()
        }
    }
}

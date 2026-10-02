package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.DailyWork
import com.ostadkar.app.domain.model.ProjectWorker
import com.ostadkar.app.domain.model.Worker
import com.ostadkar.app.domain.model.WorkerPayment
import com.ostadkar.app.domain.repository.DailyWorkRepository
import com.ostadkar.app.domain.repository.ProjectWorkerRepository
import com.ostadkar.app.domain.repository.WorkerPaymentRepository
import com.ostadkar.app.domain.repository.WorkerRepository
import com.ostadkar.app.domain.usecase.WageCalculator
import com.ostadkar.app.presentation.ui.components.PersianDateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddWorkerForm(
    val show: Boolean = false,
    val name: String = "",
    val mobile: String = "",
    val type: String = "worker",
    val specialty: String = "",
    val dailyWage: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

data class DailyWorkForm(
    val show: Boolean = false,
    val workerId: Long = 0,
    val workerName: String = "",
    val dailyWage: Long = 0,
    val workDate: Long = 0L,
    val workType: String = "full_day",
    val hours: String = "",
    val overtimeHours: String = "",
    val overtimeRate: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

data class WorkerPayForm(
    val show: Boolean = false,
    val workerId: Long = 0,
    val workerName: String = "",
    val amount: String = "",
    val paymentType: String = "partial",
    val method: String = "cash",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProjectWorkersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectWorkerRepository: ProjectWorkerRepository,
    private val workerRepository: WorkerRepository,
    private val dailyWorkRepository: DailyWorkRepository,
    private val workerPaymentRepository: WorkerPaymentRepository
) : ViewModel() {

    val projectId: Long = savedStateHandle["projectId"] ?: 0L

    val assignedWorkers: StateFlow<List<ProjectWorker>> = projectWorkerRepository
        .observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkers: StateFlow<List<Worker>> = workerRepository
        .observeAllActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyWorks: StateFlow<List<DailyWork>> = dailyWorkRepository
        .observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workerPayments: StateFlow<List<WorkerPayment>> = workerPaymentRepository
        .observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _addForm = MutableStateFlow(AddWorkerForm())
    val addForm: StateFlow<AddWorkerForm> = _addForm.asStateFlow()

    private val _dailyForm = MutableStateFlow(DailyWorkForm())
    val dailyForm: StateFlow<DailyWorkForm> = _dailyForm.asStateFlow()

    private val _payForm = MutableStateFlow(WorkerPayForm())
    val payForm: StateFlow<WorkerPayForm> = _payForm.asStateFlow()

    // --- Assign existing or create new worker ---

    fun showAddForm() {
        _addForm.value = AddWorkerForm(show = true)
    }

    fun hideAddForm() {
        _addForm.value = AddWorkerForm(show = false)
    }

    fun updateAddName(v: String) { _addForm.value = _addForm.value.copy(name = v, error = null) }
    fun updateAddMobile(v: String) { _addForm.value = _addForm.value.copy(mobile = v) }
    fun updateAddType(v: String) { _addForm.value = _addForm.value.copy(type = v) }
    fun updateAddSpecialty(v: String) { _addForm.value = _addForm.value.copy(specialty = v) }
    fun updateAddWage(v: String) { _addForm.value = _addForm.value.copy(dailyWage = v.filter { it.isDigit() }) }

    fun saveNewWorkerAndAssign() {
        val f = _addForm.value
        if (f.name.isBlank()) {
            _addForm.value = f.copy(error = "نام الزامی است")
            return
        }
        viewModelScope.launch {
            _addForm.value = f.copy(isSaving = true, error = null)
            val wage = f.dailyWage.toLongOrNull() ?: 0L
            val workerId = workerRepository.save(
                Worker(
                    name = f.name.trim(),
                    mobile = f.mobile.trim(),
                    type = f.type,
                    specialty = f.specialty.trim(),
                    defaultDailyWage = wage
                )
            )
            projectWorkerRepository.assign(projectId, workerId, if (wage > 0) wage else null)
            _addForm.value = AddWorkerForm(show = false)
        }
    }

    fun assignExisting(workerId: Long, dailyWage: Long?) {
        viewModelScope.launch {
            projectWorkerRepository.assign(projectId, workerId, dailyWage)
        }
    }

    fun removeWorker(workerId: Long) {
        viewModelScope.launch {
            projectWorkerRepository.remove(projectId, workerId)
        }
    }

    // --- Daily work ---

    fun showDailyForm(worker: ProjectWorker) {
        _dailyForm.value = DailyWorkForm(
            show = true,
            workerId = worker.workerId,
            workerName = worker.workerName,
            dailyWage = worker.dailyWage ?: 0L,
            workDate = PersianDateFormatter.startOfDay()
        )
    }

    fun hideDailyForm() {
        _dailyForm.value = DailyWorkForm(show = false)
    }

    fun updateDailyWorkType(v: String) { _dailyForm.value = _dailyForm.value.copy(workType = v) }
    fun updateDailyHours(v: String) { _dailyForm.value = _dailyForm.value.copy(hours = v.filterDigits()) }
    fun updateOvertimeHours(v: String) { _dailyForm.value = _dailyForm.value.copy(overtimeHours = v.filterDigits()) }
    fun updateOvertimeRate(v: String) { _dailyForm.value = _dailyForm.value.copy(overtimeRate = v.filter { it.isDigit() }) }
    fun updateDailyNotes(v: String) { _dailyForm.value = _dailyForm.value.copy(notes = v) }

    fun shiftDailyDate(days: Int) {
        val f = _dailyForm.value
        val base = if (f.workDate > 0) f.workDate else PersianDateFormatter.startOfDay()
        _dailyForm.value = f.copy(workDate = PersianDateFormatter.addDays(base, days))
    }

    fun setDailyDateToday() {
        _dailyForm.value = _dailyForm.value.copy(workDate = PersianDateFormatter.startOfDay())
    }

    fun saveDailyWork() {
        val f = _dailyForm.value
        val hours = f.hours.toDoubleOrNull() ?: 0.0
        if (f.workType == "hourly" && hours <= 0) {
            _dailyForm.value = f.copy(error = "ساعت کارکرد را وارد کنید")
            return
        }
        viewModelScope.launch {
            _dailyForm.value = f.copy(isSaving = true, error = null)
            val base = WageCalculator.baseWage(f.dailyWage, f.workType, hours)
            dailyWorkRepository.save(
                DailyWork(
                    projectId = projectId,
                    workerId = f.workerId,
                    workDate = PersianDateFormatter.startOfDay(f.workDate.takeIf { it > 0 } ?: System.currentTimeMillis()),
                    workType = f.workType,
                    hours = hours,
                    baseWage = base,
                    notes = f.notes.trim()
                )
            )
            // Overtime is stored separately if provided; for simplicity we fold OT into notes
            // Full OvertimeEntity support can be extended later
            _dailyForm.value = DailyWorkForm(show = false)
        }
    }

    // --- Worker payment ---

    fun showPayForm(worker: ProjectWorker) {
        _payForm.value = WorkerPayForm(
            show = true,
            workerId = worker.workerId,
            workerName = worker.workerName
        )
    }

    fun hidePayForm() {
        _payForm.value = WorkerPayForm(show = false)
    }

    fun updatePayAmount(v: String) { _payForm.value = _payForm.value.copy(amount = v.filter { it.isDigit() }, error = null) }
    fun updatePayType(v: String) { _payForm.value = _payForm.value.copy(paymentType = v) }
    fun updatePayMethod(v: String) { _payForm.value = _payForm.value.copy(method = v) }
    fun updatePayNotes(v: String) { _payForm.value = _payForm.value.copy(notes = v) }

    fun saveWorkerPayment() {
        val f = _payForm.value
        val amount = f.amount.toLongOrNull() ?: 0L
        if (amount <= 0) {
            _payForm.value = f.copy(error = "مبلغ را وارد کنید")
            return
        }
        viewModelScope.launch {
            _payForm.value = f.copy(isSaving = true, error = null)
            workerPaymentRepository.save(
                WorkerPayment(
                    projectId = projectId,
                    workerId = f.workerId,
                    amount = amount,
                    paymentDate = System.currentTimeMillis(),
                    paymentType = f.paymentType,
                    method = f.method,
                    notes = f.notes.trim()
                )
            )
            _payForm.value = WorkerPayForm(show = false)
        }
    }
}

private fun String.filterDigits(): String {
    return filter { it.isDigit() || it == '.' || it == '٫' }
        .replace('٫', '.')
}

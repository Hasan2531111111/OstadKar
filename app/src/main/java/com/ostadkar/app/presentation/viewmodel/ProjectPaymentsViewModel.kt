package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.CustomerPayment
import com.ostadkar.app.domain.repository.CustomerPaymentRepository
import com.ostadkar.app.domain.repository.WorkItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentFormState(
    val amount: String = "",
    val method: String = "cash",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false
)

@HiltViewModel
class ProjectPaymentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val paymentRepository: CustomerPaymentRepository,
    workItemRepository: WorkItemRepository
) : ViewModel() {

    val projectId: Long = savedStateHandle["projectId"] ?: 0L

    val payments: StateFlow<List<CustomerPayment>> = paymentRepository
        .observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receivedTotal: StateFlow<Long> = paymentRepository
        .observeProjectTotal(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val workTotal: StateFlow<Long> = workItemRepository
        .observeProjectTotal(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _form = MutableStateFlow(PaymentFormState())
    val form: StateFlow<PaymentFormState> = _form.asStateFlow()

    fun showForm() {
        _form.value = PaymentFormState(showForm = true)
    }

    fun hideForm() {
        _form.value = PaymentFormState(showForm = false)
    }

    fun updateAmount(v: String) {
        _form.value = _form.value.copy(amount = v.filter { it.isDigit() }, error = null)
    }

    fun updateMethod(method: String) {
        _form.value = _form.value.copy(method = method)
    }

    fun updateNotes(notes: String) {
        _form.value = _form.value.copy(notes = notes)
    }

    fun savePayment() {
        val f = _form.value
        val amount = f.amount.toLongOrNull() ?: 0L
        if (amount <= 0) {
            _form.value = f.copy(error = "مبلغ را وارد کنید")
            return
        }
        viewModelScope.launch {
            _form.value = f.copy(isSaving = true, error = null)
            paymentRepository.save(
                CustomerPayment(
                    projectId = projectId,
                    amount = amount,
                    paymentDate = System.currentTimeMillis(),
                    method = f.method,
                    notes = f.notes.trim()
                )
            )
            _form.value = PaymentFormState(showForm = false)
        }
    }
}

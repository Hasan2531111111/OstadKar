package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.CustomerPayment
import com.ostadkar.app.domain.repository.CustomerPaymentRepository
import com.ostadkar.app.domain.repository.WorkItemRepository
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentFormState(
    val amount: String = "",
    val method: String = "cash",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false,
    val editingId: Long? = null,
    val paymentDate: Long = System.currentTimeMillis()
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

    fun editPayment(payment: CustomerPayment) {
        _form.value = PaymentFormState(
            showForm = true,
            editingId = payment.id,
            amount = MoneyFormatter.formatAmountInput(payment.amount.toString()),
            method = payment.method,
            notes = payment.notes,
            paymentDate = payment.paymentDate
        )
    }

    fun hideForm() {
        _form.value = PaymentFormState(showForm = false)
    }

    fun updateAmount(v: String) {
        _form.value = _form.value.copy(amount = MoneyFormatter.formatAmountInput(v), error = null)
    }

    fun updateMethod(method: String) {
        _form.value = _form.value.copy(method = method)
    }

    fun updateNotes(notes: String) {
        _form.value = _form.value.copy(notes = notes)
    }

    fun savePayment() {
        val f = _form.value
        val amount = MoneyFormatter.parseAmount(f.amount)
        if (amount <= 0) {
            _form.value = f.copy(error = "مبلغ را وارد کنید")
            return
        }
        viewModelScope.launch {
            _form.value = f.copy(isSaving = true, error = null)
            paymentRepository.save(
                CustomerPayment(
                    id = f.editingId ?: 0L,
                    projectId = projectId,
                    amount = amount,
                    paymentDate = f.paymentDate,
                    method = f.method,
                    notes = f.notes.trim()
                )
            )
            _form.value = PaymentFormState(showForm = false)
        }
    }

    fun deletePayment(id: Long) {
        viewModelScope.launch {
            paymentRepository.delete(id)
            if (_form.value.editingId == id) hideForm()
        }
    }
}

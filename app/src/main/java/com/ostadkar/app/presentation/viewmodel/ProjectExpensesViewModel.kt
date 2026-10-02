package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.ProjectExpense
import com.ostadkar.app.domain.repository.ProjectExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExpenseFormState(
    val show: Boolean = false,
    val title: String = "",
    val amount: String = "",
    val category: String = "materials",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProjectExpensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expenseRepository: ProjectExpenseRepository
) : ViewModel() {

    val projectId: Long = savedStateHandle["projectId"] ?: 0L

    val expenses: StateFlow<List<ProjectExpense>> = expenseRepository
        .observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val total: StateFlow<Long> = expenseRepository
        .observeProjectTotal(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _form = MutableStateFlow(ExpenseFormState())
    val form: StateFlow<ExpenseFormState> = _form.asStateFlow()

    fun showForm() { _form.value = ExpenseFormState(show = true) }
    fun hideForm() { _form.value = ExpenseFormState(show = false) }
    fun updateTitle(v: String) { _form.value = _form.value.copy(title = v, error = null) }
    fun updateAmount(v: String) { _form.value = _form.value.copy(amount = v.filter { it.isDigit() }) }
    fun updateCategory(v: String) { _form.value = _form.value.copy(category = v) }
    fun updateNotes(v: String) { _form.value = _form.value.copy(notes = v) }

    fun save() {
        val f = _form.value
        if (f.title.isBlank()) {
            _form.value = f.copy(error = "عنوان هزینه الزامی است")
            return
        }
        val amount = f.amount.toLongOrNull() ?: 0L
        if (amount <= 0) {
            _form.value = f.copy(error = "مبلغ را وارد کنید")
            return
        }
        viewModelScope.launch {
            _form.value = f.copy(isSaving = true)
            expenseRepository.save(
                ProjectExpense(
                    projectId = projectId,
                    category = f.category,
                    title = f.title.trim(),
                    amount = amount,
                    expenseDate = System.currentTimeMillis(),
                    notes = f.notes.trim()
                )
            )
            _form.value = ExpenseFormState(show = false)
        }
    }
}

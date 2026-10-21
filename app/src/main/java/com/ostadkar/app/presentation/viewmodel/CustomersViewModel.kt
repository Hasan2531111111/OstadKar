package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.Customer
import com.ostadkar.app.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerFormState(
    val show: Boolean = false,
    val editingId: Long? = null,
    val name: String = "",
    val mobile: String = "",
    val address: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    val customers: StateFlow<List<Customer>> = customerRepository
        .observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(CustomerFormState())
    val form: StateFlow<CustomerFormState> = _form.asStateFlow()

    fun showForm() { _form.value = CustomerFormState(show = true) }
    fun hideForm() { _form.value = CustomerFormState(show = false) }

    fun editCustomer(c: Customer) {
        _form.value = CustomerFormState(
            show = true,
            editingId = c.id,
            name = c.name,
            mobile = c.mobile,
            address = c.address,
            notes = c.notes
        )
    }

    fun updateName(v: String) { _form.value = _form.value.copy(name = v, error = null) }
    fun updateMobile(v: String) { _form.value = _form.value.copy(mobile = v) }
    fun updateAddress(v: String) { _form.value = _form.value.copy(address = v) }
    fun updateNotes(v: String) { _form.value = _form.value.copy(notes = v) }

    fun save() {
        val f = _form.value
        if (f.name.isBlank()) {
            _form.value = f.copy(error = "نام کارفرما الزامی است")
            return
        }
        viewModelScope.launch {
            _form.value = f.copy(isSaving = true)
            val existing = f.editingId?.let { id -> customers.value.find { it.id == id } }
            customerRepository.save(
                Customer(
                    id = f.editingId ?: 0L,
                    name = f.name.trim(),
                    mobile = f.mobile.trim(),
                    address = f.address.trim(),
                    notes = f.notes.trim(),
                    createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            _form.value = CustomerFormState(show = false)
        }
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            customerRepository.delete(id)
            if (_form.value.editingId == id) hideForm()
        }
    }
}

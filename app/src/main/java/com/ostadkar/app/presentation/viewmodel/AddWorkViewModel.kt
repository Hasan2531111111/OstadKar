package com.ostadkar.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.WorkItem
import com.ostadkar.app.domain.model.WorkLocation
import com.ostadkar.app.domain.model.WorkType
import com.ostadkar.app.domain.repository.WorkItemRepository
import com.ostadkar.app.domain.repository.WorkLocationRepository
import com.ostadkar.app.domain.repository.WorkTypeRepository
import com.ostadkar.app.domain.usecase.PriceCalculator
import com.ostadkar.app.domain.usecase.QuantityCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AddWorkStep {
    SELECT_TYPE,
    SELECT_LOCATION,
    MEASUREMENT_AND_PRICE
}

data class AddWorkUiState(
    val step: AddWorkStep = AddWorkStep.SELECT_TYPE,
    val selectedType: WorkType? = null,
    val selectedLocation: WorkLocation? = null,
    val customTypeName: String = "",
    val customLocationName: String = "",
    val unit: String = "square_meter",
    val unitCustomName: String = "",
    val length: String = "",
    val width: String = "",
    val height: String = "",
    val manualQuantity: String = "",
    val unitPrice: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddWorkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workTypeRepository: WorkTypeRepository,
    private val workLocationRepository: WorkLocationRepository,
    private val workItemRepository: WorkItemRepository
) : ViewModel() {

    val projectId: Long = savedStateHandle["projectId"] ?: 0L

    val workTypes: StateFlow<List<WorkType>> = workTypeRepository
        .observeAllActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(AddWorkUiState())
    val uiState: StateFlow<AddWorkUiState> = _uiState.asStateFlow()

    val locations: StateFlow<List<WorkLocation>> = _uiState
        .flatMapLatest { state ->
            val typeId = state.selectedType?.id
            if (typeId != null && typeId > 0) {
                workLocationRepository.observeByWorkType(typeId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectType(type: WorkType) {
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            customTypeName = "",
            selectedLocation = null,
            customLocationName = "",
            step = AddWorkStep.SELECT_LOCATION,
            error = null
        )
    }

    fun selectCustomType() {
        _uiState.value = _uiState.value.copy(
            selectedType = null,
            step = AddWorkStep.SELECT_LOCATION,
            error = null
        )
    }

    fun updateCustomTypeName(name: String) {
        _uiState.value = _uiState.value.copy(customTypeName = name, error = null)
    }

    fun selectLocation(location: WorkLocation) {
        _uiState.value = _uiState.value.copy(
            selectedLocation = location,
            customLocationName = "",
            step = AddWorkStep.MEASUREMENT_AND_PRICE,
            error = null
        )
    }

    fun selectCustomLocation() {
        _uiState.value = _uiState.value.copy(
            selectedLocation = null,
            step = AddWorkStep.MEASUREMENT_AND_PRICE,
            error = null
        )
    }

    fun updateCustomLocationName(name: String) {
        _uiState.value = _uiState.value.copy(customLocationName = name, error = null)
    }

    fun updateUnit(unit: String) {
        _uiState.value = _uiState.value.copy(unit = unit)
    }

    fun updateUnitCustomName(name: String) {
        _uiState.value = _uiState.value.copy(unitCustomName = name)
    }

    fun updateLength(v: String) {
        _uiState.value = _uiState.value.copy(length = v.filterDigits())
    }

    fun updateWidth(v: String) {
        _uiState.value = _uiState.value.copy(width = v.filterDigits())
    }

    fun updateHeight(v: String) {
        _uiState.value = _uiState.value.copy(height = v.filterDigits())
    }

    fun updateManualQuantity(v: String) {
        _uiState.value = _uiState.value.copy(manualQuantity = v.filterDigits())
    }

    fun updateUnitPrice(v: String) {
        _uiState.value = _uiState.value.copy(unitPrice = v.filterDigitsInt())
    }

    fun updateNotes(v: String) {
        _uiState.value = _uiState.value.copy(notes = v)
    }

    fun goBack() {
        val current = _uiState.value
        when (current.step) {
            AddWorkStep.SELECT_LOCATION -> {
                _uiState.value = current.copy(
                    step = AddWorkStep.SELECT_TYPE,
                    selectedLocation = null,
                    customLocationName = ""
                )
            }
            AddWorkStep.MEASUREMENT_AND_PRICE -> {
                _uiState.value = current.copy(step = AddWorkStep.SELECT_LOCATION)
            }
            else -> {}
        }
    }

    fun calculatedQuantity(): Double {
        val s = _uiState.value
        return QuantityCalculator.calculate(
            unit = s.unit,
            length = s.length.toDoubleOrNull(),
            width = s.width.toDoubleOrNull(),
            height = s.height.toDoubleOrNull(),
            manualQuantity = s.manualQuantity.toDoubleOrNull()
        )
    }

    fun calculatedTotal(): Long {
        val qty = calculatedQuantity()
        val price = _uiState.value.unitPrice.toLongOrNull() ?: 0L
        return PriceCalculator.totalPrice(qty, price)
    }

    fun save() {
        val s = _uiState.value
        viewModelScope.launch {
            // Validation
            val typeName = when {
                s.selectedType != null -> s.selectedType.nameFa
                s.customTypeName.isNotBlank() -> s.customTypeName.trim()
                else -> {
                    _uiState.value = s.copy(error = "نوع کار را انتخاب یا وارد کنید")
                    return@launch
                }
            }
            val locationName = when {
                s.selectedLocation != null -> s.selectedLocation.nameFa
                s.customLocationName.isNotBlank() -> s.customLocationName.trim()
                else -> {
                    _uiState.value = s.copy(error = "محل اجرا را انتخاب یا وارد کنید")
                    return@launch
                }
            }

            val qty = calculatedQuantity()
            if (qty <= 0) {
                _uiState.value = s.copy(error = "مقدار/متراژ باید بیشتر از صفر باشد")
                return@launch
            }
            val unitPrice = s.unitPrice.toLongOrNull() ?: 0L
            if (unitPrice <= 0) {
                _uiState.value = s.copy(error = "قیمت واحد را وارد کنید")
                return@launch
            }

            _uiState.value = s.copy(isSaving = true, error = null)

            // Create custom type/location if needed
            var typeId = s.selectedType?.id
            if (typeId == null && s.customTypeName.isNotBlank()) {
                typeId = workTypeRepository.addCustom(s.customTypeName.trim())
            }

            var locationId = s.selectedLocation?.id
            if (locationId == null && s.customLocationName.isNotBlank() && typeId != null) {
                locationId = workLocationRepository.addCustom(typeId, s.customLocationName.trim())
            }

            val total = PriceCalculator.totalPrice(qty, unitPrice)

            workItemRepository.save(
                WorkItem(
                    projectId = projectId,
                    workTypeId = typeId,
                    workTypeName = typeName,
                    workLocationId = locationId,
                    workLocationName = locationName,
                    unit = s.unit,
                    unitCustomName = if (s.unit == "custom") s.unitCustomName.ifBlank { null } else null,
                    length = s.length.toDoubleOrNull(),
                    width = s.width.toDoubleOrNull(),
                    height = s.height.toDoubleOrNull(),
                    quantity = qty,
                    unitPrice = unitPrice,
                    totalPrice = total,
                    notes = s.notes.trim()
                )
            )

            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }

    private fun String.filterDigits(): String {
        return filter { it.isDigit() || it == '.' || it == '/' }
            .replace('/', '.')
    }

    private fun String.filterDigitsInt(): String {
        return filter { it.isDigit() }
    }
}

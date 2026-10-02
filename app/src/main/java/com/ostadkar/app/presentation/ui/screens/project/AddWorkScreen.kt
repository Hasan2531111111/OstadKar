package com.ostadkar.app.presentation.ui.screens.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.components.UnitLabels
import com.ostadkar.app.presentation.viewmodel.AddWorkStep
import com.ostadkar.app.presentation.viewmodel.AddWorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddWorkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val workTypes by viewModel.workTypes.collectAsState()
    val locations by viewModel.locations.collectAsState()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    val title = when (uiState.step) {
        AddWorkStep.SELECT_TYPE -> "انتخاب نوع کار"
        AddWorkStep.SELECT_LOCATION -> "انتخاب محل اجرا"
        AddWorkStep.MEASUREMENT_AND_PRICE -> "متراژ و قیمت"
    }

    Scaffold(
        topBar = {
            OstadTopBar(
                title = title,
                onBack = {
                    when (uiState.step) {
                        AddWorkStep.SELECT_TYPE -> onBack()
                        else -> viewModel.goBack()
                    }
                }
            )
        }
    ) { padding ->
        when (uiState.step) {
            AddWorkStep.SELECT_TYPE -> SelectTypeStep(
                workTypes = workTypes,
                customName = uiState.customTypeName,
                onSelect = viewModel::selectType,
                onCustomNameChange = viewModel::updateCustomTypeName,
                onCustomContinue = {
                    if (uiState.customTypeName.isNotBlank()) {
                        viewModel.selectCustomType()
                    }
                },
                modifier = Modifier.padding(padding)
            )
            AddWorkStep.SELECT_LOCATION -> SelectLocationStep(
                locations = locations,
                typeName = uiState.selectedType?.nameFa ?: uiState.customTypeName,
                customName = uiState.customLocationName,
                onSelect = viewModel::selectLocation,
                onCustomNameChange = viewModel::updateCustomLocationName,
                onCustomContinue = {
                    if (uiState.customLocationName.isNotBlank()) {
                        viewModel.selectCustomLocation()
                    }
                },
                modifier = Modifier.padding(padding)
            )
            AddWorkStep.MEASUREMENT_AND_PRICE -> MeasurementStep(
                uiState = uiState,
                calculatedQty = viewModel.calculatedQuantity(),
                calculatedTotal = viewModel.calculatedTotal(),
                onUnitChange = viewModel::updateUnit,
                onUnitCustomName = viewModel::updateUnitCustomName,
                onLength = viewModel::updateLength,
                onWidth = viewModel::updateWidth,
                onHeight = viewModel::updateHeight,
                onManualQty = viewModel::updateManualQuantity,
                onUnitPrice = viewModel::updateUnitPrice,
                onNotes = viewModel::updateNotes,
                onSave = viewModel::save,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun SelectTypeStep(
    workTypes: List<com.ostadkar.app.domain.model.WorkType>,
    customName: String,
    onSelect: (com.ostadkar.app.domain.model.WorkType) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCustomContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(workTypes, key = { it.id }) { type ->
            SelectableCard(
                title = type.nameFa,
                onClick = { onSelect(type) }
            )
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(
                            text = "  کار سفارشی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customName,
                        onValueChange = onCustomNameChange,
                        label = { Text("نام کار سفارشی") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (customName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onCustomContinue,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ادامه")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectLocationStep(
    locations: List<com.ostadkar.app.domain.model.WorkLocation>,
    typeName: String,
    customName: String,
    onSelect: (com.ostadkar.app.domain.model.WorkLocation) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCustomContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "نوع کار: $typeName",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        items(locations, key = { it.id }) { loc ->
            SelectableCard(
                title = loc.nameFa,
                onClick = { onSelect(loc) }
            )
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(
                            text = "  مورد دلخواه",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customName,
                        onValueChange = onCustomNameChange,
                        label = { Text("نام محل اجرا") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (customName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onCustomContinue,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ادامه")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MeasurementStep(
    uiState: com.ostadkar.app.presentation.viewmodel.AddWorkUiState,
    calculatedQty: Double,
    calculatedTotal: Long,
    onUnitChange: (String) -> Unit,
    onUnitCustomName: (String) -> Unit,
    onLength: (String) -> Unit,
    onWidth: (String) -> Unit,
    onHeight: (String) -> Unit,
    onManualQty: (String) -> Unit,
    onUnitPrice: (String) -> Unit,
    onNotes: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeName = uiState.selectedType?.nameFa ?: uiState.customTypeName
    val locName = uiState.selectedLocation?.nameFa ?: uiState.customLocationName

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "$typeName — $locName",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Text("واحد اندازه‌گیری", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val units = listOf(
                    "square_meter" to "متر مربع",
                    "linear_meter" to "متر طول",
                    "piece" to "عدد",
                    "day" to "روز",
                    "hour" to "ساعت",
                    "project" to "پروژه‌ای",
                    "custom" to "سفارشی"
                )
                units.forEach { (code, label) ->
                    FilterChip(
                        selected = uiState.unit == code,
                        onClick = { onUnitChange(code) },
                        label = { Text(label) }
                    )
                }
            }
        }

        if (uiState.unit == "custom") {
            item {
                OutlinedTextField(
                    value = uiState.unitCustomName,
                    onValueChange = onUnitCustomName,
                    label = { Text("نام واحد سفارشی") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        if (uiState.unit == "square_meter") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.length,
                        onValueChange = onLength,
                        label = { Text("طول (متر)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = uiState.width.ifBlank { uiState.height },
                        onValueChange = {
                            onWidth(it)
                            onHeight(it)
                        },
                        label = { Text("عرض / ارتفاع") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        if (uiState.unit == "linear_meter") {
            item {
                OutlinedTextField(
                    value = uiState.length,
                    onValueChange = onLength,
                    label = { Text("طول (متر)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = uiState.manualQuantity,
                onValueChange = onManualQty,
                label = {
                    Text(
                        if (uiState.unit == "square_meter" || uiState.unit == "linear_meter")
                            "مقدار دستی (اختیاری)"
                        else
                            "مقدار *"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مقدار محاسبه‌شده",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${MoneyFormatter.toPersianDigits(calculatedQty)} ${UnitLabels.label(uiState.unit, uiState.unitCustomName)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = uiState.unitPrice,
                onValueChange = onUnitPrice,
                label = { Text("قیمت واحد (تومان) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مبلغ کل",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = MoneyFormatter.format(calculatedTotal),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotes,
                label = { Text("توضیحات (اختیاری)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (uiState.error != null) {
            item {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Button(
                onClick = onSave,
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (uiState.isSaving) "در حال ذخیره..." else "ذخیره آیتم کار",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun SelectableCard(
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

package com.ostadkar.app.presentation.ui.screens.worker

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.domain.model.ProjectWorker
import com.ostadkar.app.presentation.ui.components.DailyWorkTypeLabels
import com.ostadkar.app.presentation.ui.components.EmptyState
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.components.PaymentMethodLabels
import com.ostadkar.app.presentation.ui.components.WorkerTypeLabels
import com.ostadkar.app.presentation.viewmodel.AddWorkerForm
import com.ostadkar.app.presentation.viewmodel.DailyWorkForm
import com.ostadkar.app.presentation.viewmodel.ProjectWorkersViewModel
import com.ostadkar.app.presentation.viewmodel.WorkerPayForm

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectWorkersScreen(
    onBack: () -> Unit,
    viewModel: ProjectWorkersViewModel = hiltViewModel()
) {
    val workers by viewModel.assignedWorkers.collectAsState()
    val dailyWorks by viewModel.dailyWorks.collectAsState()
    val payments by viewModel.workerPayments.collectAsState()
    val addForm by viewModel.addForm.collectAsState()
    val dailyForm by viewModel.dailyForm.collectAsState()
    val payForm by viewModel.payForm.collectAsState()

    Scaffold(
        topBar = { OstadTopBar(title = "کارگران و استادکاران", onBack = onBack) },
        floatingActionButton = {
            if (!addForm.show && !dailyForm.show && !payForm.show) {
                FloatingActionButton(
                    onClick = { viewModel.showAddForm() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "افزودن نفر")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (addForm.show) {
                item {
                    AddWorkerFormCard(
                        form = addForm,
                        onName = viewModel::updateAddName,
                        onMobile = viewModel::updateAddMobile,
                        onType = viewModel::updateAddType,
                        onSpecialty = viewModel::updateAddSpecialty,
                        onWage = viewModel::updateAddWage,
                        onSave = viewModel::saveNewWorkerAndAssign,
                        onCancel = viewModel::hideAddForm
                    )
                }
            }

            if (dailyForm.show) {
                item {
                    DailyWorkFormCard(
                        form = dailyForm,
                        onWorkType = viewModel::updateDailyWorkType,
                        onHours = viewModel::updateDailyHours,
                        onOtHours = viewModel::updateOvertimeHours,
                        onOtRate = viewModel::updateOvertimeRate,
                        onNotes = viewModel::updateDailyNotes,
                        onSave = viewModel::saveDailyWork,
                        onCancel = viewModel::hideDailyForm
                    )
                }
            }

            if (payForm.show) {
                item {
                    WorkerPayFormCard(
                        form = payForm,
                        onAmount = viewModel::updatePayAmount,
                        onType = viewModel::updatePayType,
                        onMethod = viewModel::updatePayMethod,
                        onNotes = viewModel::updatePayNotes,
                        onSave = viewModel::saveWorkerPayment,
                        onCancel = viewModel::hidePayForm
                    )
                }
            }

            if (workers.isEmpty() && !addForm.show) {
                item {
                    EmptyState(message = "هنوز کسی به این پروژه اضافه نشده.\nبا دکمه + نفر جدید اضافه کنید.")
                }
            }

            items(workers, key = { it.id }) { worker ->
                WorkerCard(
                    worker = worker,
                    onDailyWork = { viewModel.showDailyForm(worker) },
                    onPay = { viewModel.showPayForm(worker) },
                    onRemove = { viewModel.removeWorker(worker.workerId) }
                )
            }

            if (dailyWorks.isNotEmpty()) {
                item {
                    Text(
                        text = "کارکردهای ثبت‌شده",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(dailyWorks, key = { "dw_${it.id}" }) { dw ->
                    val name = workers.find { it.workerId == dw.workerId }?.workerName ?: "—"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${DailyWorkTypeLabels.label(dw.workType)} — ${MoneyFormatter.format(dw.baseWage)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (payments.isNotEmpty()) {
                item {
                    Text(
                        text = "پرداخت‌های دستمزد",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(payments, key = { "pay_${it.id}" }) { p ->
                    val name = workers.find { it.workerId == p.workerId }?.workerName ?: "—"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${MoneyFormatter.format(p.amount)} — ${PaymentMethodLabels.label(p.method)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun WorkerCard(
    worker: ProjectWorker,
    onDailyWork: () -> Unit,
    onPay: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = worker.workerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = WorkerTypeLabels.label(worker.workerType),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val wage = worker.dailyWage
                    if (wage != null && wage > 0) {
                        Text(
                            text = "دستمزد روزانه: ${MoneyFormatter.format(wage)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDailyWork) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("ثبت کارکرد")
                }
                TextButton(onClick = onPay) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("پرداخت")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddWorkerFormCard(
    form: AddWorkerForm,
    onName: (String) -> Unit,
    onMobile: (String) -> Unit,
    onType: (String) -> Unit,
    onSpecialty: (String) -> Unit,
    onWage: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("افزودن نفر", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = form.name,
                onValueChange = onName,
                label = { Text("نام *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = form.mobile,
                onValueChange = onMobile,
                label = { Text("شماره تماس") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Text("نوع", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("master" to "استادکار", "worker" to "کارگر").forEach { (code, label) ->
                    FilterChip(
                        selected = form.type == code,
                        onClick = { onType(code) },
                        label = { Text(label) }
                    )
                }
            }
            OutlinedTextField(
                value = form.specialty,
                onValueChange = onSpecialty,
                label = { Text("تخصص (مثلاً کاشی‌کار)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = form.dailyWage,
                onValueChange = onWage,
                label = { Text("دستمزد روزانه (تومان)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            if (form.error != null) {
                Text(form.error!!, color = MaterialTheme.colorScheme.error)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSave, enabled = !form.isSaving, modifier = Modifier.weight(1f)) {
                    Text("ذخیره و افزودن")
                }
                TextButton(onClick = onCancel) { Text("انصراف") }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DailyWorkFormCard(
    form: DailyWorkForm,
    onWorkType: (String) -> Unit,
    onHours: (String) -> Unit,
    onOtHours: (String) -> Unit,
    onOtRate: (String) -> Unit,
    onNotes: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "ثبت کارکرد — ${form.workerName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("نوع کارکرد", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "full_day" to "روز کامل",
                    "half_day" to "نیم‌روز",
                    "hourly" to "ساعتی",
                    "absent" to "غیبت",
                    "leave" to "مرخصی"
                ).forEach { (code, label) ->
                    FilterChip(
                        selected = form.workType == code,
                        onClick = { onWorkType(code) },
                        label = { Text(label) }
                    )
                }
            }
            if (form.workType == "hourly") {
                OutlinedTextField(
                    value = form.hours,
                    onValueChange = onHours,
                    label = { Text("تعداد ساعت") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            OutlinedTextField(
                value = form.overtimeHours,
                onValueChange = onOtHours,
                label = { Text("ساعت اضافه‌کاری (اختیاری)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = form.overtimeRate,
                onValueChange = onOtRate,
                label = { Text("مبلغ هر ساعت اضافه‌کاری") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = form.notes,
                onValueChange = onNotes,
                label = { Text("توضیحات") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            if (form.error != null) {
                Text(form.error!!, color = MaterialTheme.colorScheme.error)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSave, enabled = !form.isSaving, modifier = Modifier.weight(1f)) {
                    Text("ثبت کارکرد")
                }
                TextButton(onClick = onCancel) { Text("انصراف") }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkerPayFormCard(
    form: WorkerPayForm,
    onAmount: (String) -> Unit,
    onType: (String) -> Unit,
    onMethod: (String) -> Unit,
    onNotes: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "پرداخت دستمزد — ${form.workerName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = form.amount,
                onValueChange = onAmount,
                label = { Text("مبلغ (تومان) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            Text("نوع پرداخت", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "full" to "پرداخت کامل",
                    "advance" to "علی‌الحساب",
                    "partial" to "بخشی",
                    "settlement" to "تسویه"
                ).forEach { (code, label) ->
                    FilterChip(
                        selected = form.paymentType == code,
                        onClick = { onType(code) },
                        label = { Text(label) }
                    )
                }
            }
            Text("روش پرداخت", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "cash" to "نقدی",
                    "card_reader" to "کارت‌خوان",
                    "card_to_card" to "کارت به کارت",
                    "bank_transfer" to "انتقال بانکی",
                    "other" to "سایر"
                ).forEach { (code, label) ->
                    FilterChip(
                        selected = form.method == code,
                        onClick = { onMethod(code) },
                        label = { Text(label) }
                    )
                }
            }
            OutlinedTextField(
                value = form.notes,
                onValueChange = onNotes,
                label = { Text("توضیحات") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            if (form.error != null) {
                Text(form.error!!, color = MaterialTheme.colorScheme.error)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSave, enabled = !form.isSaving, modifier = Modifier.weight(1f)) {
                    Text("ثبت پرداخت")
                }
                TextButton(onClick = onCancel) { Text("انصراف") }
            }
        }
    }
}

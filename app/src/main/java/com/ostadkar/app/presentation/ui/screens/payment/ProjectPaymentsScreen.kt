package com.ostadkar.app.presentation.ui.screens.payment

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.presentation.ui.components.EmptyState
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.components.PaymentMethodLabels
import com.ostadkar.app.presentation.ui.components.SummaryCard
import com.ostadkar.app.presentation.ui.theme.CreditBlue
import com.ostadkar.app.presentation.ui.theme.ProfitGreen
import com.ostadkar.app.presentation.viewmodel.ProjectPaymentsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectPaymentsScreen(
    onBack: () -> Unit,
    viewModel: ProjectPaymentsViewModel = hiltViewModel()
) {
    val payments by viewModel.payments.collectAsState()
    val received by viewModel.receivedTotal.collectAsState()
    val workTotal by viewModel.workTotal.collectAsState()
    val form by viewModel.form.collectAsState()
    val remaining = workTotal - received

    Scaffold(
        topBar = { OstadTopBar(title = "پرداخت‌های مشتری", onBack = onBack) },
        floatingActionButton = {
            if (!form.showForm) {
                FloatingActionButton(
                    onClick = { viewModel.showForm() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "ثبت پرداخت")
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
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "مبلغ کل کار",
                        value = MoneyFormatter.formatCompact(workTotal),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "دریافت‌شده",
                        value = MoneyFormatter.formatCompact(received),
                        valueColor = ProfitGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                SummaryCard(
                    title = "باقی‌مانده (طلب)",
                    value = MoneyFormatter.format(remaining),
                    valueColor = CreditBlue
                )
            }

            if (form.showForm) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ثبت پرداخت جدید",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = form.amount,
                                onValueChange = viewModel::updateAmount,
                                label = { Text("مبلغ (تومان)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("روش پرداخت", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.height(6.dp))
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
                                        onClick = { viewModel.updateMethod(code) },
                                        label = { Text(label) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = form.notes,
                                onValueChange = viewModel::updateNotes,
                                label = { Text("توضیحات") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            if (form.error != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(form.error!!, color = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = viewModel::savePayment,
                                    enabled = !form.isSaving,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (form.isSaving) "..." else "ذخیره")
                                }
                                TextButton(onClick = viewModel::hideForm) {
                                    Text("انصراف")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "لیست پرداخت‌ها",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (payments.isEmpty()) {
                item {
                    EmptyState(message = "هنوز پرداختی ثبت نشده است.")
                }
            } else {
                items(payments, key = { it.id }) { payment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = MoneyFormatter.format(payment.amount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ProfitGreen
                            )
                            Text(
                                text = PaymentMethodLabels.label(payment.method),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDate(payment.paymentDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (payment.notes.isNotBlank()) {
                                Text(
                                    text = payment.notes,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

private fun formatDate(millis: Long): String {
    return try {
        SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale("fa", "IR")).format(Date(millis))
    } catch (_: Exception) {
        millis.toString()
    }
}

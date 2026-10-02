package com.ostadkar.app.presentation.ui.screens.project

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
import com.ostadkar.app.presentation.ui.components.SummaryCard
import com.ostadkar.app.presentation.ui.theme.DebtOrange
import com.ostadkar.app.presentation.viewmodel.ProjectExpensesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectExpensesScreen(
    onBack: () -> Unit,
    viewModel: ProjectExpensesViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val total by viewModel.total.collectAsState()
    val form by viewModel.form.collectAsState()

    Scaffold(
        topBar = { OstadTopBar(title = "هزینه‌های پروژه", onBack = onBack) },
        floatingActionButton = {
            if (!form.show) {
                FloatingActionButton(
                    onClick = { viewModel.showForm() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "افزودن هزینه")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SummaryCard(
                    title = "جمع هزینه‌ها",
                    value = MoneyFormatter.format(total),
                    valueColor = DebtOrange
                )
            }

            if (form.show) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("ثبت هزینه", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            OutlinedTextField(
                                value = form.title,
                                onValueChange = viewModel::updateTitle,
                                label = { Text("عنوان *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = form.amount,
                                onValueChange = viewModel::updateAmount,
                                label = { Text("مبلغ (تومان) *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Text("دسته", style = MaterialTheme.typography.labelLarge)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(
                                    "materials" to "مصالح",
                                    "transport" to "حمل‌ونقل",
                                    "other" to "سایر"
                                ).forEach { (code, label) ->
                                    FilterChip(
                                        selected = form.category == code,
                                        onClick = { viewModel.updateCategory(code) },
                                        label = { Text(label) }
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = form.notes,
                                onValueChange = viewModel::updateNotes,
                                label = { Text("توضیحات") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            if (form.error != null) {
                                Text(form.error!!, color = MaterialTheme.colorScheme.error)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = viewModel::save, enabled = !form.isSaving, modifier = Modifier.weight(1f)) {
                                    Text("ذخیره")
                                }
                                TextButton(onClick = viewModel::hideForm) { Text("انصراف") }
                            }
                        }
                    }
                }
            }

            if (expenses.isEmpty() && !form.show) {
                item {
                    EmptyState(message = "هنوز هزینه‌ای ثبت نشده است.")
                }
            }

            items(expenses, key = { it.id }) { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(expense.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            MoneyFormatter.format(expense.amount),
                            style = MaterialTheme.typography.bodyLarge,
                            color = DebtOrange
                        )
                        val cat = when (expense.category) {
                            "materials" -> "مصالح"
                            "transport" -> "حمل‌ونقل"
                            else -> "سایر"
                        }
                        Text(cat, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

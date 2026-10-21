package com.ostadkar.app.presentation.ui.screens.worker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.presentation.ui.components.AmountTextField
import com.ostadkar.app.presentation.ui.components.ConfirmDeleteDialog
import com.ostadkar.app.presentation.ui.components.EmptyState
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.viewmodel.WorkersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkersScreen(
    onBack: () -> Unit,
    onWorkerClick: (Long) -> Unit = {},
    viewModel: WorkersViewModel = hiltViewModel()
) {
    val workers by viewModel.workers.collectAsState()
    val form by viewModel.form.collectAsState()
    var pendingDelete by remember { mutableStateOf<Pair<Long, String>?>(null) }

    pendingDelete?.let { (id, name) ->
        ConfirmDeleteDialog(
            message = "«$name» حذف شود؟",
            onConfirm = {
                viewModel.deleteWorker(id)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }

    Scaffold(
        topBar = { OstadTopBar(title = "کارگران", onBack = onBack) },
        floatingActionButton = {
            if (!form.show) {
                FloatingActionButton(
                    onClick = viewModel::showForm,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "کارگر جدید")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (form.show) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                if (form.editingId != null) "ویرایش کارگر" else "کارگر جدید",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            OutlinedTextField(
                                value = form.name,
                                onValueChange = viewModel::updateName,
                                label = { Text("نام *") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = form.mobile,
                                onValueChange = viewModel::updateMobile,
                                label = { Text("موبایل") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = form.specialty,
                                onValueChange = viewModel::updateSpecialty,
                                label = { Text("تخصص") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = form.type == "worker",
                                    onClick = { viewModel.updateType("worker") },
                                    label = { Text("کارگر") }
                                )
                                FilterChip(
                                    selected = form.type == "master",
                                    onClick = { viewModel.updateType("master") },
                                    label = { Text("استادکار") }
                                )
                            }
                            AmountTextField(
                                value = form.defaultDailyWage,
                                onValueChange = viewModel::updateWage,
                                label = "دستمزد روزانه پیش‌فرض"
                            )
                            OutlinedTextField(
                                value = form.notes,
                                onValueChange = viewModel::updateNotes,
                                label = { Text("یادداشت") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            if (form.error != null) {
                                Text(form.error!!, color = MaterialTheme.colorScheme.error)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = viewModel::save,
                                    enabled = !form.isSaving,
                                    modifier = Modifier.weight(1f)
                                ) { Text(if (form.editingId != null) "ذخیره تغییرات" else "ثبت") }
                                TextButton(onClick = viewModel::hideForm) { Text("انصراف") }
                            }
                        }
                    }
                }
            }

            if (workers.isEmpty() && !form.show) {
                item { EmptyState(message = "هنوز کارگری ثبت نشده است.") }
            }

            items(workers, key = { it.id }) { worker ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(worker.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                            if (worker.specialty.isNotBlank()) {
                                Text(worker.specialty, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (worker.mobile.isNotBlank()) {
                                Text(worker.mobile, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (worker.defaultDailyWage > 0) {
                                Text(
                                    "روزانه: ${MoneyFormatter.format(worker.defaultDailyWage)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.editWorker(worker) }) {
                            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { pendingDelete = worker.id to worker.name }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

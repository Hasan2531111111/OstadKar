package com.ostadkar.app.presentation.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.model.Project
import com.ostadkar.app.domain.repository.ProjectRepository
import com.ostadkar.app.presentation.ui.components.EmptyState
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.theme.Primary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PaymentsHubViewModel @Inject constructor(
    projectRepository: ProjectRepository
) : ViewModel() {
    val projects: StateFlow<List<Project>> = projectRepository
        .observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsHubScreen(
    onBack: () -> Unit,
    onProjectPayments: (Long) -> Unit,
    viewModel: PaymentsHubViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState()

    Scaffold(
        topBar = { OstadTopBar(title = "پرداخت‌ها", onBack = onBack) }
    ) { padding ->
        if (projects.isEmpty()) {
            EmptyState(
                message = "هنوز پروژه‌ای نیست.\nابتدا یک پروژه بسازید، بعد پرداخت ثبت کنید.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "پروژه را انتخاب کنید تا پرداخت مشتری یا دستمزد ثبت شود",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(projects, key = { it.id }) { project ->
                    Card(
                        onClick = { onProjectPayments(project.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(5.dp)
                                    .height(88.dp)
                                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                                    .background(Primary)
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = project.title.ifBlank { "پروژه بدون عنوان" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "کارفرما: ${project.customerName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ثبت / مشاهده پرداخت‌ها ←",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

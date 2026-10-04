package com.ostadkar.app.presentation.ui.screens.project

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.presentation.ui.components.EmptyState
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.components.StatusBadge
import com.ostadkar.app.presentation.ui.components.WorkStatusLabels
import com.ostadkar.app.presentation.ui.theme.CreditBlue
import com.ostadkar.app.presentation.ui.theme.Primary
import com.ostadkar.app.presentation.ui.theme.ProfitGreen
import com.ostadkar.app.presentation.ui.theme.SoftBlue
import com.ostadkar.app.presentation.ui.theme.SoftGreen
import com.ostadkar.app.presentation.viewmodel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onBack: () -> Unit,
    onProjectClick: (Long) -> Unit,
    onNewProject: () -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState()

    Scaffold(
        topBar = { OstadTopBar(title = "پروژه‌ها", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewProject,
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "پروژه جدید")
            }
        }
    ) { padding ->
        if (projects.isEmpty()) {
            EmptyState(
                message = "هنوز پروژه‌ای ثبت نشده است.\nبا دکمه + یک کار جدید شروع کنید.",
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
                items(projects, key = { it.id }) { project ->
                    val (badgeColor, soft, barColor) = when (project.status) {
                        "active" -> Triple(ProfitGreen, SoftGreen, ProfitGreen)
                        "completed" -> Triple(Color(0xFF6B758A), Color(0xFFEEF0F3), Color(0xFF6B758A))
                        else -> Triple(CreditBlue, SoftBlue, CreditBlue)
                    }
                    Card(
                        onClick = { onProjectClick(project.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(5.dp)
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                                    .background(barColor)
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = project.title.ifBlank { "پروژه بدون عنوان" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusBadge(
                                        text = WorkStatusLabels.label(project.status),
                                        color = badgeColor,
                                        softColor = soft
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "کارفرما: ${project.customerName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (project.address.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = project.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
}

package com.ostadkar.app.presentation.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.presentation.ui.components.MenuCard
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.SummaryCard
import com.ostadkar.app.presentation.ui.theme.CreditBlue
import com.ostadkar.app.presentation.ui.theme.DebtOrange
import com.ostadkar.app.presentation.ui.theme.Primary
import com.ostadkar.app.presentation.ui.theme.PrimaryLight
import com.ostadkar.app.presentation.ui.theme.ProfitGreen
import com.ostadkar.app.presentation.ui.theme.PurpleAccent
import com.ostadkar.app.presentation.ui.theme.SoftBlue
import com.ostadkar.app.presentation.ui.theme.SoftGreen
import com.ostadkar.app.presentation.ui.theme.SoftOrange
import com.ostadkar.app.presentation.ui.theme.SoftPurple
import com.ostadkar.app.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProjects: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToWorkers: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNewProject: () -> Unit,
    onProjectClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val dashboard by viewModel.dashboard.collectAsState()
    val projects by viewModel.recentProjects.collectAsState()
    val activeCount = dashboard.activeProjectsCount

    Scaffold(
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Primary, PrimaryLight)
                            )
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 36.dp)
                ) {
                    Column {
                        Text(
                            text = "استاد کار",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "سلام، استادکار",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (activeCount > 0)
                                "امروز $activeCount پروژه فعال دارید"
                            else
                                "خلاصه وضعیت پروژه‌ها",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            item {
                // Overlap cards slightly
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            title = "پروژه‌های فعال",
                            value = "${dashboard.activeProjectsCount}",
                            valueColor = CreditBlue,
                            softColor = SoftBlue,
                            icon = Icons.Default.Home,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "طلب مشتریان",
                            value = MoneyFormatter.formatCompact(dashboard.receivableFromCustomers),
                            valueColor = DebtOrange,
                            softColor = SoftOrange,
                            icon = Icons.Default.AccountBalanceWallet,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            title = "درآمد کل",
                            value = MoneyFormatter.formatCompact(dashboard.totalIncome),
                            valueColor = ProfitGreen,
                            softColor = SoftGreen,
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "سود تقریبی",
                            value = MoneyFormatter.formatCompact(dashboard.approximateProfit),
                            valueColor = CreditBlue,
                            softColor = SoftBlue,
                            icon = Icons.Default.Assessment,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "دسترسی سریع",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 24.dp, top = 28.dp, bottom = 12.dp)
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MenuCard(
                            title = "پروژه‌ها",
                            icon = Icons.Default.Work,
                            softColor = SoftBlue,
                            iconTint = CreditBlue,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToProjects
                        )
                        MenuCard(
                            title = "مشتریان",
                            icon = Icons.Default.People,
                            softColor = SoftOrange,
                            iconTint = DebtOrange,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToCustomers
                        )
                        MenuCard(
                            title = "کارگران",
                            icon = Icons.Default.Groups,
                            softColor = SoftGreen,
                            iconTint = ProfitGreen,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToWorkers
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MenuCard(
                            title = "گزارش‌ها",
                            icon = Icons.Default.Assessment,
                            softColor = SoftPurple,
                            iconTint = PurpleAccent,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToReports
                        )
                        MenuCard(
                            title = "پرداخت‌ها",
                            icon = Icons.Default.Payments,
                            softColor = Color(0xFFFFEBEE),
                            iconTint = Color(0xFFE64747),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToPayments
                        )
                        MenuCard(
                            title = "تنظیمات",
                            icon = Icons.Default.Settings,
                            softColor = Color(0xFFEEF0F3),
                            iconTint = Color(0xFF6B758A),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToSettings
                        )
                    }
                }
            }

            if (projects.isNotEmpty()) {
                item {
                    Text(
                        text = "پروژه‌های اخیر",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 24.dp, top = 28.dp, bottom = 12.dp)
                    )
                }
                items(projects, key = { it.id }) { project ->
                    Card(
                        onClick = { onProjectClick(project.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(width = 5.dp, height = 72.dp)
                                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                                    .background(Primary)
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = project.title.ifBlank { project.customerName },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = project.customerName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

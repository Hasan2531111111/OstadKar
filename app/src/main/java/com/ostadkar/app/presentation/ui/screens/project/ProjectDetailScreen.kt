package com.ostadkar.app.presentation.ui.screens.project

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.presentation.ui.components.MenuCard
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.components.SummaryCard
import com.ostadkar.app.presentation.ui.theme.CreditBlue
import com.ostadkar.app.presentation.ui.theme.DebtOrange
import com.ostadkar.app.presentation.ui.theme.ProfitGreen
import com.ostadkar.app.presentation.viewmodel.ProjectDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    onBack: () -> Unit,
    onNavigateToWorkItems: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToWorkers: () -> Unit,
    onNavigateToStages: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val project by viewModel.project.collectAsState()
    val financial by viewModel.financial.collectAsState()

    Scaffold(
        topBar = {
            OstadTopBar(
                title = project?.title ?: "جزئیات پروژه",
                onBack = onBack
            )
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = project?.customerName ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!project?.address.isNullOrBlank()) {
                            Text(
                                text = project?.address ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "حساب مالی",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "مبلغ کل کار",
                        value = MoneyFormatter.formatCompact(financial.totalIncome),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "دریافت‌شده",
                        value = MoneyFormatter.formatCompact(financial.receivedFromCustomers),
                        valueColor = ProfitGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "طلب",
                        value = MoneyFormatter.formatCompact(financial.receivable),
                        valueColor = CreditBlue,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "بدهی به کارگران",
                        value = MoneyFormatter.formatCompact(financial.payableToWorkers),
                        valueColor = DebtOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                SummaryCard(
                    title = "سود تقریبی",
                    value = MoneyFormatter.format(financial.approximateProfit),
                    valueColor = if (financial.approximateProfit >= 0) ProfitGreen else DebtOrange
                )
            }

            item {
                Text(
                    text = "بخش‌ها",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuCard(
                        title = "آیتم‌های کار",
                        icon = Icons.Default.Construction,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToWorkItems
                    )
                    MenuCard(
                        title = "پرداخت مشتری",
                        icon = Icons.Default.Payments,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPayments
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuCard(
                        title = "کارگران",
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToWorkers
                    )
                    MenuCard(
                        title = "مراحل کار",
                        icon = Icons.Default.Timeline,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToStages
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuCard(
                        title = "هزینه‌ها",
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToExpenses
                    )
                    MenuCard(
                        title = "گزارش",
                        icon = Icons.Default.ListAlt,
                        modifier = Modifier.weight(1f),
                        onClick = { /* later */ }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

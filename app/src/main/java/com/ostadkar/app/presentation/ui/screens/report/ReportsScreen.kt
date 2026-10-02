package com.ostadkar.app.presentation.ui.screens.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.components.SummaryCard
import com.ostadkar.app.presentation.ui.theme.CreditBlue
import com.ostadkar.app.presentation.ui.theme.DebtOrange
import com.ostadkar.app.presentation.ui.theme.ProfitGreen
import com.ostadkar.app.presentation.viewmodel.ReportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val dashboard by viewModel.dashboard.collectAsState()

    Scaffold(
        topBar = { OstadTopBar(title = "گزارش‌ها", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "خلاصه کلی همه پروژه‌ها",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                SummaryCard(
                    title = "تعداد پروژه‌های فعال",
                    value = MoneyFormatter.toPersianDigits(dashboard.activeProjectsCount)
                )
            }
            item {
                SummaryCard(
                    title = "درآمد کل پروژه‌ها",
                    value = MoneyFormatter.format(dashboard.totalIncome),
                    valueColor = ProfitGreen
                )
            }
            item {
                SummaryCard(
                    title = "طلب از مشتریان",
                    value = MoneyFormatter.format(dashboard.receivableFromCustomers),
                    valueColor = CreditBlue
                )
            }
            item {
                SummaryCard(
                    title = "پرداخت به کارگران (تقریبی)",
                    value = MoneyFormatter.format(dashboard.totalExpenses),
                    valueColor = DebtOrange
                )
            }
            item {
                SummaryCard(
                    title = "سود تقریبی",
                    value = MoneyFormatter.format(dashboard.approximateProfit),
                    valueColor = if (dashboard.approximateProfit >= 0) ProfitGreen else DebtOrange
                )
            }
            item {
                Text(
                    text = "فیلتر بر اساس پروژه، مشتری، تاریخ و نوع کار در نسخه بعدی اضافه می‌شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

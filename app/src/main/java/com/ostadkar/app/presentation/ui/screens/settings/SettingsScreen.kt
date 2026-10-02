package com.ostadkar.app.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.fontWeight
import androidx.compose.ui.unit.dp
import com.ostadkar.app.presentation.ui.components.OstadTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { OstadTopBar(title = "تنظیمات", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionCard(title = "درباره برنامه") {
                SettingRow(
                    icon = Icons.Default.Info,
                    title = "نام برنامه",
                    subtitle = "استادکار — مدیریت پروژه ساختمانی"
                )
                HorizontalDivider()
                SettingRow(
                    icon = Icons.Default.PhoneAndroid,
                    title = "نسخه",
                    subtitle = "۱.۰.۱ (کد ۲)"
                )
                HorizontalDivider()
                SettingRow(
                    icon = Icons.Default.Storage,
                    title = "حالت کار",
                    subtitle = "کاملاً آفلاین — همه داده‌ها روی همین گوشی ذخیره می‌شوند"
                )
            }

            SectionCard(title = "نمایش و زبان") {
                SettingRow(
                    icon = Icons.Default.Language,
                    title = "زبان",
                    subtitle = "فارسی (راست‌چین)"
                )
                HorizontalDivider()
                SettingRow(
                    icon = Icons.Default.DarkMode,
                    title = "حالت تاریک",
                    subtitle = "از تنظیمات سیستم گوشی پیروی می‌کند"
                )
            }

            SectionCard(title = "تقویم و واحد پول") {
                SettingRow(
                    icon = Icons.Default.Info,
                    title = "تقویم",
                    subtitle = "شمسی (جلالی) همراه با روز هفته"
                )
                HorizontalDivider()
                SettingRow(
                    icon = Icons.Default.Info,
                    title = "واحد پول",
                    subtitle = "تومان — با جداکننده هزارگان فارسی"
                )
            }

            SectionCard(title = "پشتیبان‌گیری") {
                Text(
                    text = "داده‌ها داخل حافظه برنامه ذخیره می‌شوند. برای پشتیبان‌گیری، در نسخه‌های بعدی امکان خروجی Excel/PDF اضافه خواهد شد. فعلاً از پاک کردن حافظه برنامه در تنظیمات گوشی خودداری کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionCard(title = "راهنمای سریع") {
                Text(
                    text = "۱. پروژه جدید بسازید و مشتری را مشخص کنید.\n" +
                        "۲. اقلام کار و اندازه‌گیری را ثبت کنید.\n" +
                        "۳. کارگران را به پروژه اضافه و کارکرد روزانه ثبت کنید.\n" +
                        "۴. پرداخت مشتری و دستمزد را وارد کنید.\n" +
                        "۵. از بخش گزارش‌ها وضعیت مالی را ببینید.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "© ۱۴۰۵ استادکار — نسخه آزمایشی بازار",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.ostadkar.app.presentation.ui.screens.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit = {}
) {
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
            SectionCard(title = "حساب کاربری") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditProfile() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("پروفایل من", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "نام، تخصص، شماره و عکس پروفایل",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("ویرایش", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            }

            SectionCard(title = "درباره برنامه") {
                SettingRow(Icons.Default.Info, "نام برنامه", "استادکار — مدیریت پروژه ساختمانی")
                HorizontalDivider()
                SettingRow(Icons.Default.PhoneAndroid, "نسخه", "۱.۱.۲")
                HorizontalDivider()
                SettingRow(Icons.Default.Storage, "حالت کار", "کاملاً آفلاین — همه داده‌ها روی همین گوشی")
            }

            SectionCard(title = "نمایش و زبان") {
                SettingRow(Icons.Default.Language, "زبان", "فارسی (راست‌چین)")
                HorizontalDivider()
                SettingRow(Icons.Default.DarkMode, "حالت تاریک", "از تنظیمات سیستم گوشی پیروی می‌کند")
            }

            SectionCard(title = "تقویم و واحد پول") {
                SettingRow(Icons.Default.Info, "تقویم", "شمسی (جلالی) همراه با روز هفته")
                HorizontalDivider()
                SettingRow(Icons.Default.Info, "واحد پول", "تومان — با جداکننده هزارگان فارسی")
            }

            SectionCard(title = "پشتیبان‌گیری") {
                Text(
                    text = "داده‌ها داخل حافظه برنامه ذخیره می‌شوند. از پاک کردن حافظه برنامه در تنظیمات گوشی خودداری کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "© استادکار — نسخه آزمایشی",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Primary)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

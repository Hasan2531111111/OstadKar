package com.ostadkar.app.presentation.ui.screens.settings

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.ostadkar.app.data.backup.BackupSection
import com.ostadkar.app.presentation.ui.components.OstadTopBar
import com.ostadkar.app.presentation.ui.theme.Primary
import com.ostadkar.app.presentation.viewmodel.BackupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit = {},
    onRestorePurchase: () -> Unit = {},
    backupViewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val backupState by backupViewModel.state.collectAsState()

    LaunchedEffect(backupState.lastFile) {
        val file = backupState.lastFile ?: return@LaunchedEffect
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "پشتیبان استادکار — ${backupState.lastSectionTitle ?: ""}"
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    "فایل پشتیبان «${backupState.lastSectionTitle}» از برنامه استادکار"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(share, "ذخیره یا ارسال پشتیبان"))
            Toast.makeText(context, "فایل آماده ذخیره یا ارسال است", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "خطا در اشتراک‌گذاری", Toast.LENGTH_LONG).show()
        } finally {
            backupViewModel.clearShare()
        }
    }

    LaunchedEffect(backupState.error) {
        backupState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            backupViewModel.clearShare()
        }
    }

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
                        .clickable(onClick = onEditProfile)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("پروفایل من", fontWeight = FontWeight.SemiBold)
                        Text(
                            "نام، تخصص و عکس",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onRestorePurchase)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = Primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("بازیابی خرید", fontWeight = FontWeight.SemiBold)
                        Text(
                            "اگر قبلاً نسخه کامل را خریده‌اید",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            SectionCard(title = "برنامه") {
                SettingRow(Icons.Default.PhoneAndroid, "نسخه", "۱.۲.۴")
                HorizontalDivider()
                SettingRow(Icons.Default.Language, "زبان", "فارسی")
                HorizontalDivider()
                SettingRow(Icons.Default.DarkMode, "ظاهر", "سیستم / روشن / تاریک — به‌زودی")
            }

            SectionCard(title = "اطلاعات") {
                SettingRow(Icons.Default.Storage, "ذخیره‌سازی", "آفلاین روی همین دستگاه")
                HorizontalDivider()
                SettingRow(Icons.Default.Info, "تقویم", "شمسی همراه با روز هفته")
                HorizontalDivider()
                SettingRow(Icons.Default.Info, "واحد پول", "تومان — با جداکننده هزارگان فارسی")
            }

            SectionCard(title = "پشتیبان‌گیری") {
                Text(
                    text = "هر بخش را جداگانه به فایل متنی تبدیل کنید؛ سپس ذخیره یا ارسال (تلگرام، ایمیل، درایو و …).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (backupState.isWorking) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(12.dp))
                    }
                }

                BackupSection.entries.forEach { section ->
                    BackupActionRow(
                        title = section.titleFa,
                        enabled = !backupState.isWorking,
                        onClick = { backupViewModel.export(section) }
                    )
                    if (section != BackupSection.entries.last()) {
                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BackupActionRow(
    title: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.Backup, contentDescription = null, tint = Primary)
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Default.Share, contentDescription = "ذخیره یا ارسال", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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

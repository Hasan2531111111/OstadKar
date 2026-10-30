package com.ostadkar.app.presentation.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.ostadkar.app.presentation.viewmodel.BackupUiState
import com.ostadkar.app.presentation.viewmodel.BackupViewModel

/** وقتی فایل پشتیبان آماده شد، دیالوگ ذخیره/ارسال را باز می‌کند. */
@Composable
fun BackupShareEffect(
    state: BackupUiState,
    viewModel: BackupViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(state.lastFile) {
        val file = state.lastFile ?: return@LaunchedEffect
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
                    "پشتیبان استادکار — ${state.lastSectionTitle ?: ""}"
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    "فایل پشتیبان «${state.lastSectionTitle}» از برنامه استادکار"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(share, "ذخیره یا ارسال پشتیبان"))
            Toast.makeText(context, "فایل آماده ذخیره یا ارسال است", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "خطا در اشتراک‌گذاری", Toast.LENGTH_LONG).show()
        } finally {
            viewModel.clearShare()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearShare()
        }
    }
}

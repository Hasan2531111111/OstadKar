package com.ostadkar.app.presentation.viewmodel

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.billing.BazaarBillingManager
import com.ostadkar.app.billing.BillingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnlockViewModel @Inject constructor(
    private val billing: BazaarBillingManager
) : ViewModel() {

    val state: StateFlow<BillingUiState> = billing.state

    init {
        billing.refreshLocalLicense()
        billing.connect()
    }

    fun purchase(registry: ActivityResultRegistry) {
        viewModelScope.launch {
            billing.purchase(registry)
        }
    }

    fun restore() {
        billing.restorePurchases()
    }

    fun reconnect() {
        billing.connect()
    }

    override fun onCleared() {
        // اتصال سراسری را قطع نمی‌کنیم تا در تنظیمات هم بماند
        super.onCleared()
    }
}

package com.ostadkar.app.billing

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResultRegistry
import com.ostadkar.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.cafebazaar.poolakey.request.PurchaseRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class BillingConnectionState {
    Idle, Connecting, Connected, Failed
}

data class BillingUiState(
    val connection: BillingConnectionState = BillingConnectionState.Idle,
    val isUnlocked: Boolean = false,
    val isPurchasing: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

@Singleton
class BazaarBillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val licenseStore: LicenseStore
) {
    val productId: String = BuildConfig.BAZAAR_PRODUCT_ID.ifBlank { "ostadkar_full" }

    private val payment: Payment by lazy {
        val rsa = BuildConfig.BAZAAR_RSA_KEY.trim()
        val security = if (rsa.isNotEmpty()) {
            SecurityCheck.Enable(rsaPublicKey = rsa)
        } else {
            // تا وقتی کلید RSA را در BuildConfig نگذاشته‌اید، بررسی محلی خاموش است
            SecurityCheck.Disable
        }
        Payment(
            context = context,
            config = PaymentConfiguration(localSecurityCheck = security)
        )
    }

    private var connection: Connection? = null

    private val _state = MutableStateFlow(
        BillingUiState(isUnlocked = licenseStore.isUnlocked())
    )
    val state: StateFlow<BillingUiState> = _state.asStateFlow()

    fun refreshLocalLicense() {
        _state.value = _state.value.copy(isUnlocked = licenseStore.isUnlocked())
    }

    fun connect() {
        if (_state.value.connection == BillingConnectionState.Connected ||
            _state.value.connection == BillingConnectionState.Connecting
        ) return

        _state.value = _state.value.copy(
            connection = BillingConnectionState.Connecting,
            error = null,
            message = "در حال اتصال به بازار..."
        )

        connection = payment.connect {
            connectionSucceed {
                _state.value = _state.value.copy(
                    connection = BillingConnectionState.Connected,
                    message = "متصل به بازار"
                )
                queryPurchases()
            }
            connectionFailed { throwable ->
                _state.value = _state.value.copy(
                    connection = BillingConnectionState.Failed,
                    error = throwable.message ?: "اتصال به بازار برقرار نشد. برنامه بازار را نصب/به‌روز کنید.",
                    message = null
                )
            }
            disconnected {
                _state.value = _state.value.copy(
                    connection = BillingConnectionState.Idle,
                    message = "اتصال قطع شد"
                )
            }
        }
    }

    fun disconnect() {
        connection?.disconnect()
        connection = null
        _state.value = _state.value.copy(connection = BillingConnectionState.Idle)
    }

    fun queryPurchases() {
        payment.getPurchasedProducts {
            querySucceed { purchases ->
                val owned = purchases.any { it.productId == productId }
                if (owned) {
                    licenseStore.setUnlocked(true)
                    purchases.firstOrNull { it.productId == productId }?.purchaseToken?.let {
                        licenseStore.savePurchaseToken(it)
                    }
                }
                _state.value = _state.value.copy(
                    isUnlocked = licenseStore.isUnlocked() || owned,
                    message = if (owned || licenseStore.isUnlocked()) "نسخه کامل فعال است" else "نسخه کامل خریداری نشده"
                )
            }
            queryFailed { throwable ->
                // اگر آفلاین بودیم، وضعیت محلی را نگه می‌داریم
                _state.value = _state.value.copy(
                    isUnlocked = licenseStore.isUnlocked(),
                    error = if (!licenseStore.isUnlocked()) {
                        throwable.message ?: "بررسی خرید ناموفق بود"
                    } else null
                )
            }
        }
    }

    fun purchase(registry: ActivityResultRegistry) {
        if (_state.value.connection != BillingConnectionState.Connected) {
            connect()
            _state.value = _state.value.copy(
                error = "ابتدا به بازار وصل شوید؛ چند لحظه بعد دوباره امتحان کنید."
            )
            return
        }

        _state.value = _state.value.copy(isPurchasing = true, error = null, message = "در حال باز کردن صفحه پرداخت...")

        val request = PurchaseRequest(productId = productId)
        payment.purchaseProduct(
            registry = registry,
            request = request
        ) {
            purchaseFlowBegan {
                _state.value = _state.value.copy(message = "صفحه پرداخت بازار باز شد")
            }
            failedToBeginFlow { throwable ->
                _state.value = _state.value.copy(
                    isPurchasing = false,
                    error = throwable.message ?: "نتوانست صفحه پرداخت را باز کند"
                )
            }
            purchaseSucceed { purchaseEntity ->
                licenseStore.setUnlocked(true)
                licenseStore.savePurchaseToken(purchaseEntity.purchaseToken)
                _state.value = _state.value.copy(
                    isPurchasing = false,
                    isUnlocked = true,
                    message = "خرید موفق — نسخه کامل فعال شد",
                    error = null
                )
            }
            purchaseCanceled {
                _state.value = _state.value.copy(
                    isPurchasing = false,
                    message = "خرید لغو شد"
                )
            }
            purchaseFailed { throwable ->
                _state.value = _state.value.copy(
                    isPurchasing = false,
                    error = throwable.message ?: "خرید ناموفق بود"
                )
            }
        }
    }

    fun restorePurchases() {
        if (_state.value.connection != BillingConnectionState.Connected) {
            connect()
        }
        queryPurchases()
    }
}

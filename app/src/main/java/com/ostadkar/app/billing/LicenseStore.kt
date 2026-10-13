package com.ostadkar.app.billing

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isUnlocked(): Boolean = prefs.getBoolean(KEY_UNLOCKED, false)

    fun setUnlocked(unlocked: Boolean) {
        prefs.edit().putBoolean(KEY_UNLOCKED, unlocked).apply()
    }

    fun savePurchaseToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getPurchaseToken(): String = prefs.getString(KEY_TOKEN, "").orEmpty()

    companion object {
        private const val PREFS = "ostadkar_license"
        private const val KEY_UNLOCKED = "unlocked"
        private const val KEY_TOKEN = "purchase_token"
    }
}

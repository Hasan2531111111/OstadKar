package com.ostadkar.app.presentation.ui.components

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats monetary amounts in Iranian Toman with Persian digits and thousand separators.
 */
object MoneyFormatter {
    private val symbols = DecimalFormatSymbols(Locale("fa", "IR")).apply {
        decimalSeparator = '٫'
        groupingSeparator = '٬'
    }
    private val formatter = DecimalFormat("#,###", symbols)

    fun format(amount: Long): String {
        return formatter.format(amount) + " تومان"
    }

    fun formatCompact(amount: Long): String {
        return formatter.format(amount)
    }

    /** Convert Western digits to Persian digits */
    fun toPersianDigits(input: String): String {
        val persian = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        return buildString {
            input.forEach { c ->
                if (c in '0'..'9') append(persian[c - '0']) else append(c)
            }
        }
    }

    fun toPersianDigits(number: Long): String = toPersianDigits(number.toString())
    fun toPersianDigits(number: Int): String = toPersianDigits(number.toString())
    fun toPersianDigits(number: Double): String {
        val formatted = if (number % 1.0 == 0.0) number.toLong().toString()
        else String.format(Locale.US, "%.2f", number)
        return toPersianDigits(formatted)
    }
}

/**
 * Unit labels in Persian.
 */
object UnitLabels {
    fun label(unit: String, customName: String? = null): String = when (unit) {
        "square_meter" -> "متر مربع"
        "linear_meter" -> "متر طول"
        "piece" -> "عدد"
        "day" -> "روز"
        "hour" -> "ساعت"
        "project" -> "پروژه‌ای"
        "custom" -> customName ?: "سفارشی"
        else -> unit
    }
}

object WorkStatusLabels {
    fun label(status: String): String = when (status) {
        "active" -> "فعال"
        "completed" -> "تکمیل‌شده"
        "paused" -> "متوقف"
        "cancelled" -> "لغو‌شده"
        else -> status
    }
}

object StageStatusLabels {
    fun label(status: String): String = when (status) {
        "not_started" -> "انجام‌نشده"
        "in_progress" -> "در حال انجام"
        "completed" -> "تکمیل‌شده"
        else -> status
    }
}

object PaymentMethodLabels {
    fun label(method: String): String = when (method) {
        "cash" -> "نقدی"
        "card_reader" -> "کارت‌خوان"
        "card_to_card" -> "کارت به کارت"
        "bank_transfer" -> "انتقال بانکی"
        "other" -> "سایر"
        else -> method
    }
}

object WorkerTypeLabels {
    fun label(type: String): String = when (type) {
        "master" -> "استادکار"
        "worker" -> "کارگر"
        else -> type
    }
}

object DailyWorkTypeLabels {
    fun label(type: String): String = when (type) {
        "full_day" -> "روز کامل"
        "half_day" -> "نیم‌روز"
        "hourly" -> "ساعتی"
        "absent" -> "غیبت"
        "leave" -> "مرخصی"
        else -> type
    }
}

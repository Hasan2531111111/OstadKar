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

/**
 * Converts Gregorian epoch millis to Persian (Jalali) calendar display.
 * Output example: «شنبه ۱۵ فروردین ۱۴۰۴»
 */
object PersianDateFormatter {
    private val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )
    // Calendar.SUNDAY=1 ... SATURDAY=7 mapped to Persian week starting Saturday
    private val weekDays = listOf(
        "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"
    )

    fun formatFull(epochMillis: Long): String {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
        val gy = cal.get(java.util.Calendar.YEAR)
        val gm = cal.get(java.util.Calendar.MONTH) + 1
        val gd = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val dow = cal.get(java.util.Calendar.DAY_OF_WEEK) // 1=Sun .. 7=Sat
        val dayName = weekDays[dow - 1]
        val monthName = monthNames.getOrElse(jm - 1) { jm.toString() }
        return "$dayName ${MoneyFormatter.toPersianDigits(jd)} $monthName ${MoneyFormatter.toPersianDigits(jy)}"
    }

    fun formatShort(epochMillis: Long): String {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
        val gy = cal.get(java.util.Calendar.YEAR)
        val gm = cal.get(java.util.Calendar.MONTH) + 1
        val gd = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        return "${MoneyFormatter.toPersianDigits(jy)}/${MoneyFormatter.toPersianDigits(jm.toString().padStart(2, '0'))}/${MoneyFormatter.toPersianDigits(jd.toString().padStart(2, '0'))}"
    }

    /** Start of local calendar day for given millis (or now). */
    fun startOfDay(epochMillis: Long = System.currentTimeMillis()): Long {
        return java.util.Calendar.getInstance().apply {
            timeInMillis = epochMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun addDays(epochMillis: Long, days: Int): Long {
        return java.util.Calendar.getInstance().apply {
            timeInMillis = startOfDay(epochMillis)
            add(java.util.Calendar.DAY_OF_MONTH, days)
        }.timeInMillis
    }

    /**
     * Gregorian → Jalali conversion (civil calendar).
     * Based on the well-known algorithm used in Iranian software.
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var jy: Int
        var gy2 = gy - 1600
        var gm2 = gm - 1
        var gd2 = gd - 1
        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
        for (i in 0 until gm2) gDayNo += gDaysInMonth[i + 1]
        if (gm2 > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) gDayNo += 1
        gDayNo += gd2
        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053
        jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461
        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        var jm = 0
        while (jm < 11 && jDayNo >= jDaysInMonth[jm]) {
            jDayNo -= jDaysInMonth[jm]
            jm++
        }
        val jd = jDayNo + 1
        return Triple(jy, jm + 1, jd)
    }
}

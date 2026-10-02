package com.ostadkar.app.domain.usecase

/**
 * Pure calculation helpers – no Android dependencies.
 * All business math lives here so UI stays clean.
 */
object QuantityCalculator {
    /**
     * Calculate quantity based on unit and dimensions.
     * For square_meter: length × width (floor) or length × height (wall).
     * Falls back to manual quantity if dimensions are missing.
     */
    fun calculate(
        unit: String,
        length: Double?,
        width: Double?,
        height: Double?,
        manualQuantity: Double?
    ): Double {
        return when (unit) {
            "square_meter" -> {
                val l = length ?: 0.0
                val w = width ?: height ?: 0.0
                if (l > 0 && w > 0) l * w else (manualQuantity ?: 0.0)
            }
            "linear_meter" -> length ?: manualQuantity ?: 0.0
            "piece", "day", "hour", "project", "custom" -> manualQuantity ?: 0.0
            else -> manualQuantity ?: 0.0
        }
    }
}

object PriceCalculator {
    fun totalPrice(quantity: Double, unitPrice: Long): Long {
        return (quantity * unitPrice).toLong()
    }
}

object WageCalculator {
    fun baseWage(dailyWage: Long, workType: String, hours: Double = 0.0): Long {
        return when (workType) {
            "full_day" -> dailyWage
            "half_day" -> dailyWage / 2
            "hourly" -> ((dailyWage / 8.0) * hours).toLong() // assume 8h day
            "absent", "leave" -> 0L
            else -> 0L
        }
    }

    fun overtimeTotal(hours: Double, ratePerHour: Long): Long {
        return (hours * ratePerHour).toLong()
    }
}

object ProfitCalculator {
    fun approximateProfit(
        totalIncome: Long,
        workerWages: Long,
        materialsAndOther: Long
    ): Long {
        return totalIncome - workerWages - materialsAndOther
    }

    fun receivable(totalIncome: Long, received: Long): Long = totalIncome - received

    fun payable(totalWages: Long, paid: Long): Long = totalWages - paid
}

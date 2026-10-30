package com.ostadkar.app.data.backup

import android.content.Context
import com.ostadkar.app.data.local.OstadKarDatabase
import com.ostadkar.app.presentation.ui.components.MoneyFormatter
import com.ostadkar.app.presentation.ui.components.PersianDateFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class BackupSection(val filePrefix: String, val titleFa: String) {
    PROJECTS("projects", "پروژه‌ها و کارفرمایان"),
    WORKERS("workers", "کارگران"),
    WORK_ITEMS("work_items", "متراژ و آیتم‌های کار"),
    CUSTOMER_PAYMENTS("customer_payments", "دریافتی از مشتری"),
    WORKER_PAYMENTS("worker_payments", "حساب و پرداخت کارگران"),
    DAILY_WORK("daily_work", "کارکرد روزانه کارگران"),
    EXPENSES("expenses", "هزینه‌ها"),
    ALL("all_data", "همه داده‌ها")
}

enum class BackupItem(val filePrefix: String) {
    WORKER("worker"),
    CUSTOMER("customer"),
    PROJECT("project"),
    WORK_ITEM("work_item"),
    CUSTOMER_PAYMENT("customer_payment"),
    WORKER_PAYMENT("worker_payment"),
    DAILY_WORK("daily_work"),
    EXPENSE("expense")
}

@Singleton
class BackupExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: OstadKarDatabase
) {
    suspend fun export(section: BackupSection): File {
        val body = when (section) {
            BackupSection.PROJECTS -> buildProjects()
            BackupSection.WORKERS -> buildWorkers()
            BackupSection.WORK_ITEMS -> buildWorkItems()
            BackupSection.CUSTOMER_PAYMENTS -> buildCustomerPayments()
            BackupSection.WORKER_PAYMENTS -> buildWorkerPayments()
            BackupSection.DAILY_WORK -> buildDailyWork()
            BackupSection.EXPENSES -> buildExpenses()
            BackupSection.ALL -> buildAll()
        }
        val dir = File(context.cacheDir, "backups").apply { mkdirs() }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val file = File(dir, "ostadkar_${section.filePrefix}_$stamp.txt")
        file.writeText(body, Charsets.UTF_8)
        return file
    }

    suspend fun exportItem(item: BackupItem, id: Long): File {
        val (title, body) = when (item) {
            BackupItem.WORKER -> "کارگر" to buildOneWorker(id)
            BackupItem.CUSTOMER -> "کارفرما" to buildOneCustomer(id)
            BackupItem.PROJECT -> "پروژه" to buildOneProject(id)
            BackupItem.WORK_ITEM -> "متراژ کار" to buildOneWorkItem(id)
            BackupItem.CUSTOMER_PAYMENT -> "دریافتی" to buildOneCustomerPayment(id)
            BackupItem.WORKER_PAYMENT -> "پرداخت کارگر" to buildOneWorkerPayment(id)
            BackupItem.DAILY_WORK -> "کارکرد روزانه" to buildOneDailyWork(id)
            BackupItem.EXPENSE -> "هزینه" to buildOneExpense(id)
        }
        val dir = File(context.cacheDir, "backups").apply { mkdirs() }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val safe = title.replace(" ", "_")
        val file = File(dir, "ostadkar_${item.filePrefix}_${id}_$stamp.txt")
        file.writeText(body, Charsets.UTF_8)
        return file
    }

    private fun header(title: String): StringBuilder {
        val sb = StringBuilder()
        val now = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("fa", "IR")).format(Date())
        sb.appendLine("پشتیبان استادکار — $title")
        sb.appendLine("تاریخ تهیه: $now")
        sb.appendLine("=".repeat(40))
        sb.appendLine()
        return sb
    }

    private fun fmtDate(ms: Long?): String {
        if (ms == null || ms == 0L) return "—"
        return try {
            PersianDateFormatter.formatFull(ms)
        } catch (_: Exception) {
            SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR")).format(Date(ms))
        }
    }

    private fun money(v: Long) = MoneyFormatter.format(v)

    private suspend fun buildProjects(): String {
        val sb = header(BackupSection.PROJECTS.titleFa)
        val customers = db.customerDao().getAll().associateBy { it.id }
        val projects = db.projectDao().getAll()
        if (projects.isEmpty()) {
            sb.appendLine("موردی ثبت نشده است.")
            return sb.toString()
        }
        projects.forEachIndexed { i, p ->
            val c = customers[p.customerId]
            sb.appendLine("${i + 1}) ${p.title.ifBlank { "بدون عنوان" }}")
            sb.appendLine("   کارفرما: ${c?.name ?: "—"}")
            if (!c?.mobile.isNullOrBlank()) sb.appendLine("   موبایل کارفرما: ${c?.mobile}")
            if (p.address.isNotBlank()) sb.appendLine("   آدرس: ${p.address}")
            sb.appendLine("   وضعیت: ${p.status}")
            if (p.description.isNotBlank()) sb.appendLine("   توضیحات: ${p.description}")
            sb.appendLine("   شروع: ${fmtDate(p.startDate)}")
            sb.appendLine()
        }
        sb.appendLine("— کارفرمایان —")
        customers.values.forEachIndexed { i, c ->
            sb.appendLine("${i + 1}) ${c.name} | ${c.mobile} | ${c.address}")
        }
        return sb.toString()
    }

    private suspend fun buildWorkers(): String {
        val sb = header(BackupSection.WORKERS.titleFa)
        val list = db.workerDao().getAll()
        if (list.isEmpty()) {
            sb.appendLine("موردی ثبت نشده است.")
            return sb.toString()
        }
        list.forEachIndexed { i, w ->
            val type = if (w.type == "master") "استادکار" else "کارگر"
            sb.appendLine("${i + 1}) ${w.name} ($type)")
            if (w.mobile.isNotBlank()) sb.appendLine("   موبایل: ${w.mobile}")
            if (w.specialty.isNotBlank()) sb.appendLine("   تخصص: ${w.specialty}")
            if (w.defaultDailyWage > 0) sb.appendLine("   دستمزد روزانه: ${money(w.defaultDailyWage)}")
            if (w.notes.isNotBlank()) sb.appendLine("   یادداشت: ${w.notes}")
            sb.appendLine()
        }
        return sb.toString()
    }

    private suspend fun buildWorkItems(): String {
        val sb = header(BackupSection.WORK_ITEMS.titleFa)
        val projects = db.projectDao().getAll().associateBy { it.id }
        val items = db.workItemDao().getAll()
        if (items.isEmpty()) {
            sb.appendLine("موردی ثبت نشده است.")
            return sb.toString()
        }
        items.groupBy { it.projectId }.forEach { (pid, list) ->
            val title = projects[pid]?.title ?: "پروژه $pid"
            sb.appendLine("▸ پروژه: $title")
            list.forEachIndexed { i, item ->
                sb.appendLine("  ${i + 1}) ${item.workTypeName} — ${item.workLocationName}")
                sb.appendLine(
                    "     مقدار: ${MoneyFormatter.toPersianDigits(item.quantity.toString())} | فی: ${money(item.unitPrice)} | جمع: ${money(item.totalPrice)}"
                )
                val dims = listOfNotNull(
                    item.length?.let { "طول ${MoneyFormatter.toPersianDigits(it.toString())}" },
                    item.width?.let { "عرض ${MoneyFormatter.toPersianDigits(it.toString())}" },
                    item.height?.let { "ارتفاع ${MoneyFormatter.toPersianDigits(it.toString())}" }
                )
                if (dims.isNotEmpty()) sb.appendLine("     ابعاد: ${dims.joinToString(" ، ")}")
                if (item.notes.isNotBlank()) sb.appendLine("     یادداشت: ${item.notes}")
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    private suspend fun buildCustomerPayments(): String {
        val sb = header(BackupSection.CUSTOMER_PAYMENTS.titleFa)
        val projects = db.projectDao().getAll().associateBy { it.id }
        val list = db.customerPaymentDao().getAll()
        if (list.isEmpty()) {
            sb.appendLine("موردی ثبت نشده است.")
            return sb.toString()
        }
        list.forEachIndexed { i, p ->
            val title = projects[p.projectId]?.title ?: "پروژه ${p.projectId}"
            sb.appendLine("${i + 1}) $title")
            sb.appendLine("   مبلغ: ${money(p.amount)}")
            sb.appendLine("   تاریخ: ${fmtDate(p.paymentDate)}")
            sb.appendLine("   روش: ${p.method}")
            if (p.notes.isNotBlank()) sb.appendLine("   توضیحات: ${p.notes}")
            sb.appendLine()
        }
        return sb.toString()
    }

    private suspend fun buildWorkerPayments(): String {
        val sb = header(BackupSection.WORKER_PAYMENTS.titleFa)
        val projects = db.projectDao().getAll().associateBy { it.id }
        val workers = db.workerDao().getAll().associateBy { it.id }
        val list = db.workerPaymentDao().getAll()
        if (list.isEmpty()) {
            sb.appendLine("موردی ثبت نشده است.")
            return sb.toString()
        }
        list.forEachIndexed { i, p ->
            val title = projects[p.projectId]?.title ?: "پروژه ${p.projectId}"
            val name = workers[p.workerId]?.name ?: "کارگر ${p.workerId}"
            sb.appendLine("${i + 1}) $name — $title")
            sb.appendLine("   مبلغ: ${money(p.amount)}")
            sb.appendLine("   تاریخ: ${fmtDate(p.paymentDate)}")
            sb.appendLine("   نوع: ${p.paymentType} | روش: ${p.method}")
            if (p.notes.isNotBlank()) sb.appendLine("   توضیحات: ${p.notes}")
            sb.appendLine()
        }
        return sb.toString()
    }

    private suspend fun buildDailyWork(): String {
        val sb = header(BackupSection.DAILY_WORK.titleFa)
        val projects = db.projectDao().getAll().associateBy { it.id }
        val workers = db.workerDao().getAll().associateBy { it.id }
        val list = db.dailyWorkDao().getAll()
        if (list.isEmpty()) {
            sb.appendLine("موردی ثبت نشده است.")
            return sb.toString()
        }
        list.forEachIndexed { i, d ->
            val title = projects[d.projectId]?.title ?: "پروژه ${d.projectId}"
            val name = workers[d.workerId]?.name ?: "کارگر ${d.workerId}"
            sb.appendLine("${i + 1}) $name — $title")
            sb.appendLine("   تاریخ: ${fmtDate(d.workDate)}")
            sb.appendLine("   نوع کارکرد: ${d.workType}")
            if (d.hours > 0) sb.appendLine("   ساعت: ${MoneyFormatter.toPersianDigits(d.hours.toString())}")
            sb.appendLine("   دستمزد پایه: ${money(d.baseWage)}")
            if (d.notes.isNotBlank()) sb.appendLine("   یادداشت: ${d.notes}")
            sb.appendLine()
        }
        return sb.toString()
    }

    private suspend fun buildExpenses(): String {
        val sb = header(BackupSection.EXPENSES.titleFa)
        val projects = db.projectDao().getAll().associateBy { it.id }
        val list = db.projectExpenseDao().getAll()
        if (list.isEmpty()) {
            sb.appendLine("موردی ثبت نشده است.")
            return sb.toString()
        }
        list.forEachIndexed { i, e ->
            val title = projects[e.projectId]?.title ?: "پروژه ${e.projectId}"
            sb.appendLine("${i + 1}) ${e.title} — $title")
            sb.appendLine("   مبلغ: ${money(e.amount)}")
            sb.appendLine("   دسته: ${e.category}")
            sb.appendLine("   تاریخ: ${fmtDate(e.expenseDate)}")
            if (e.notes.isNotBlank()) sb.appendLine("   توضیحات: ${e.notes}")
            sb.appendLine()
        }
        return sb.toString()
    }

    private suspend fun buildAll(): String {
        val sb = StringBuilder()
        sb.appendLine(buildProjects())
        sb.appendLine()
        sb.appendLine(buildWorkers())
        sb.appendLine()
        sb.appendLine(buildWorkItems())
        sb.appendLine()
        sb.appendLine(buildCustomerPayments())
        sb.appendLine()
        sb.appendLine(buildWorkerPayments())
        sb.appendLine()
        sb.appendLine(buildDailyWork())
        sb.appendLine()
        sb.appendLine(buildExpenses())
        return sb.toString()
    }


    private suspend fun buildOneWorker(id: Long): String {
        val w = db.workerDao().getById(id) ?: return header("کارگر").appendLine("یافت نشد.").toString()
        val sb = header("کارگر: ${w.name}")
        val type = if (w.type == "master") "استادکار" else "کارگر"
        sb.appendLine("نام: ${w.name}")
        sb.appendLine("نوع: $type")
        if (w.mobile.isNotBlank()) sb.appendLine("موبایل: ${w.mobile}")
        if (w.specialty.isNotBlank()) sb.appendLine("تخصص: ${w.specialty}")
        if (w.defaultDailyWage > 0) sb.appendLine("دستمزد روزانه: ${money(w.defaultDailyWage)}")
        if (w.notes.isNotBlank()) sb.appendLine("یادداشت: ${w.notes}")
        return sb.toString()
    }

    private suspend fun buildOneCustomer(id: Long): String {
        val c = db.customerDao().getById(id) ?: return header("کارفرما").appendLine("یافت نشد.").toString()
        val sb = header("کارفرما: ${c.name}")
        sb.appendLine("نام: ${c.name}")
        if (c.mobile.isNotBlank()) sb.appendLine("موبایل: ${c.mobile}")
        if (c.address.isNotBlank()) sb.appendLine("آدرس: ${c.address}")
        if (c.notes.isNotBlank()) sb.appendLine("یادداشت: ${c.notes}")
        val projects = db.projectDao().getAll().filter { it.customerId == id }
        if (projects.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("— پروژه‌های این کارفرما —")
            projects.forEachIndexed { i, p ->
                sb.appendLine("${i + 1}) ${p.title.ifBlank { "بدون عنوان" }} | ${p.status}")
            }
        }
        return sb.toString()
    }

    private suspend fun buildOneProject(id: Long): String {
        val p = db.projectDao().getById(id) ?: return header("پروژه").appendLine("یافت نشد.").toString()
        val c = db.customerDao().getById(p.customerId)
        val sb = header("پروژه: ${p.title.ifBlank { "بدون عنوان" }}")
        sb.appendLine("عنوان: ${p.title.ifBlank { "بدون عنوان" }}")
        sb.appendLine("کارفرما: ${c?.name ?: "—"}")
        if (!c?.mobile.isNullOrBlank()) sb.appendLine("موبایل کارفرما: ${c?.mobile}")
        if (p.address.isNotBlank()) sb.appendLine("آدرس: ${p.address}")
        sb.appendLine("وضعیت: ${p.status}")
        if (p.description.isNotBlank()) sb.appendLine("توضیحات: ${p.description}")
        sb.appendLine("شروع: ${fmtDate(p.startDate)}")
        sb.appendLine()
        sb.appendLine("— متراژ و آیتم‌های کار —")
        val items = db.workItemDao().getByProject(id)
        if (items.isEmpty()) sb.appendLine("موردی نیست.")
        else items.forEachIndexed { i, it ->
            sb.appendLine("${i + 1}) ${it.workTypeName} — ${it.workLocationName}")
            sb.appendLine("   مقدار: ${MoneyFormatter.toPersianDigits(it.quantity.toString())} | مبلغ: ${money(it.totalPrice)}")
        }
        sb.appendLine()
        sb.appendLine("— دریافتی از مشتری —")
        val pays = db.customerPaymentDao().getAll().filter { it.projectId == id }
        if (pays.isEmpty()) sb.appendLine("موردی نیست.")
        else pays.forEachIndexed { i, pay ->
            sb.appendLine("${i + 1}) ${money(pay.amount)} | ${fmtDate(pay.paymentDate)} | ${pay.method}")
        }
        sb.appendLine()
        sb.appendLine("— پرداخت کارگران —")
        val wp = db.workerPaymentDao().getAll().filter { it.projectId == id }
        val workers = db.workerDao().getAll().associateBy { it.id }
        if (wp.isEmpty()) sb.appendLine("موردی نیست.")
        else wp.forEachIndexed { i, pay ->
            val name = workers[pay.workerId]?.name ?: "—"
            sb.appendLine("${i + 1}) $name | ${money(pay.amount)} | ${fmtDate(pay.paymentDate)}")
        }
        sb.appendLine()
        sb.appendLine("— هزینه‌ها —")
        val ex = db.projectExpenseDao().getAll().filter { it.projectId == id }
        if (ex.isEmpty()) sb.appendLine("موردی نیست.")
        else ex.forEachIndexed { i, e ->
            sb.appendLine("${i + 1}) ${e.title} | ${money(e.amount)} | ${fmtDate(e.expenseDate)}")
        }
        return sb.toString()
    }

    private suspend fun buildOneWorkItem(id: Long): String {
        val it = db.workItemDao().getById(id) ?: return header("متراژ کار").appendLine("یافت نشد.").toString()
        val p = db.projectDao().getById(it.projectId)
        val sb = header("متراژ کار")
        sb.appendLine("پروژه: ${p?.title ?: "—"}")
        sb.appendLine("نوع کار: ${it.workTypeName}")
        sb.appendLine("محل: ${it.workLocationName}")
        sb.appendLine("مقدار: ${MoneyFormatter.toPersianDigits(it.quantity.toString())}")
        sb.appendLine("قیمت واحد: ${money(it.unitPrice)}")
        sb.appendLine("جمع: ${money(it.totalPrice)}")
        if (it.notes.isNotBlank()) sb.appendLine("یادداشت: ${it.notes}")
        return sb.toString()
    }

    private suspend fun buildOneCustomerPayment(id: Long): String {
        val pay = db.customerPaymentDao().getById(id) ?: return header("دریافتی").appendLine("یافت نشد.").toString()
        val p = db.projectDao().getById(pay.projectId)
        val sb = header("دریافتی از مشتری")
        sb.appendLine("پروژه: ${p?.title ?: "—"}")
        sb.appendLine("مبلغ: ${money(pay.amount)}")
        sb.appendLine("تاریخ: ${fmtDate(pay.paymentDate)}")
        sb.appendLine("روش: ${pay.method}")
        if (pay.notes.isNotBlank()) sb.appendLine("توضیحات: ${pay.notes}")
        return sb.toString()
    }

    private suspend fun buildOneWorkerPayment(id: Long): String {
        val pay = db.workerPaymentDao().getById(id) ?: return header("پرداخت کارگر").appendLine("یافت نشد.").toString()
        val p = db.projectDao().getById(pay.projectId)
        val w = db.workerDao().getById(pay.workerId)
        val sb = header("پرداخت کارگر")
        sb.appendLine("کارگر: ${w?.name ?: "—"}")
        sb.appendLine("پروژه: ${p?.title ?: "—"}")
        sb.appendLine("مبلغ: ${money(pay.amount)}")
        sb.appendLine("تاریخ: ${fmtDate(pay.paymentDate)}")
        sb.appendLine("نوع: ${pay.paymentType} | روش: ${pay.method}")
        if (pay.notes.isNotBlank()) sb.appendLine("توضیحات: ${pay.notes}")
        return sb.toString()
    }

    private suspend fun buildOneDailyWork(id: Long): String {
        val d = db.dailyWorkDao().getById(id) ?: return header("کارکرد روزانه").appendLine("یافت نشد.").toString()
        val p = db.projectDao().getById(d.projectId)
        val w = db.workerDao().getById(d.workerId)
        val sb = header("کارکرد روزانه")
        sb.appendLine("کارگر: ${w?.name ?: "—"}")
        sb.appendLine("پروژه: ${p?.title ?: "—"}")
        sb.appendLine("تاریخ: ${fmtDate(d.workDate)}")
        sb.appendLine("نوع: ${d.workType}")
        if (d.hours > 0) sb.appendLine("ساعت: ${MoneyFormatter.toPersianDigits(d.hours.toString())}")
        sb.appendLine("دستمزد پایه: ${money(d.baseWage)}")
        if (d.notes.isNotBlank()) sb.appendLine("یادداشت: ${d.notes}")
        return sb.toString()
    }

    private suspend fun buildOneExpense(id: Long): String {
        val e = db.projectExpenseDao().getById(id) ?: return header("هزینه").appendLine("یافت نشد.").toString()
        val p = db.projectDao().getById(e.projectId)
        val sb = header("هزینه")
        sb.appendLine("پروژه: ${p?.title ?: "—"}")
        sb.appendLine("عنوان: ${e.title}")
        sb.appendLine("مبلغ: ${money(e.amount)}")
        sb.appendLine("دسته: ${e.category}")
        sb.appendLine("تاریخ: ${fmtDate(e.expenseDate)}")
        if (e.notes.isNotBlank()) sb.appendLine("توضیحات: ${e.notes}")
        return sb.toString()
    }

}

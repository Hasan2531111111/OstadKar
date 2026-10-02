package com.ostadkar.app.data.mapper

import com.ostadkar.app.data.local.entity.*
import com.ostadkar.app.domain.model.*

fun CustomerEntity.toDomain() = Customer(
    id = id, name = name, mobile = mobile, address = address,
    notes = notes, createdAt = createdAt, updatedAt = updatedAt
)

fun Customer.toEntity() = CustomerEntity(
    id = id, name = name, mobile = mobile, address = address,
    notes = notes, createdAt = if (createdAt == 0L) System.currentTimeMillis() else createdAt,
    updatedAt = System.currentTimeMillis()
)

fun ProjectEntity.toDomain(customerName: String = "") = Project(
    id = id, customerId = customerId, title = title, address = address,
    description = description, status = status, startDate = startDate,
    endDate = endDate, createdAt = createdAt, updatedAt = updatedAt,
    customerName = customerName
)

fun Project.toEntity() = ProjectEntity(
    id = id, customerId = customerId, title = title, address = address,
    description = description, status = status, startDate = startDate,
    endDate = endDate, createdAt = if (createdAt == 0L) System.currentTimeMillis() else createdAt,
    updatedAt = System.currentTimeMillis()
)

fun WorkTypeEntity.toDomain() = WorkType(
    id = id, code = code, nameFa = nameFa, isCustom = isCustom, sortOrder = sortOrder
)

fun WorkLocationEntity.toDomain() = WorkLocation(
    id = id, workTypeId = workTypeId, code = code, nameFa = nameFa,
    isCustom = isCustom, sortOrder = sortOrder
)

fun WorkItemEntity.toDomain() = WorkItem(
    id = id, projectId = projectId, workTypeId = workTypeId,
    workTypeName = workTypeName, workLocationId = workLocationId,
    workLocationName = workLocationName, unit = unit, unitCustomName = unitCustomName,
    length = length, width = width, height = height, quantity = quantity,
    unitPrice = unitPrice, totalPrice = totalPrice, notes = notes, sortOrder = sortOrder
)

fun WorkItem.toEntity() = WorkItemEntity(
    id = id, projectId = projectId, workTypeId = workTypeId,
    workTypeName = workTypeName, workLocationId = workLocationId,
    workLocationName = workLocationName, unit = unit, unitCustomName = unitCustomName,
    length = length, width = width, height = height, quantity = quantity,
    unitPrice = unitPrice, totalPrice = totalPrice, notes = notes, sortOrder = sortOrder,
    updatedAt = System.currentTimeMillis()
)

fun WorkerEntity.toDomain() = Worker(
    id = id, name = name, mobile = mobile, type = type, specialty = specialty,
    defaultDailyWage = defaultDailyWage, notes = notes, isActive = isActive
)

fun Worker.toEntity() = WorkerEntity(
    id = id, name = name, mobile = mobile, type = type, specialty = specialty,
    defaultDailyWage = defaultDailyWage, notes = notes, isActive = isActive,
    updatedAt = System.currentTimeMillis()
)

fun CustomerPaymentEntity.toDomain() = CustomerPayment(
    id = id, projectId = projectId, amount = amount, paymentDate = paymentDate,
    method = method, notes = notes
)

fun CustomerPayment.toEntity() = CustomerPaymentEntity(
    id = id, projectId = projectId, amount = amount, paymentDate = paymentDate,
    method = method, notes = notes
)

fun WorkerPaymentEntity.toDomain() = WorkerPayment(
    id = id, projectId = projectId, workerId = workerId, amount = amount,
    paymentDate = paymentDate, paymentType = paymentType, method = method, notes = notes
)

fun WorkerPayment.toEntity() = WorkerPaymentEntity(
    id = id, projectId = projectId, workerId = workerId, amount = amount,
    paymentDate = paymentDate, paymentType = paymentType, method = method, notes = notes
)

fun ProjectExpenseEntity.toDomain() = ProjectExpense(
    id = id, projectId = projectId, category = category, title = title,
    amount = amount, expenseDate = expenseDate, notes = notes
)

fun ProjectExpense.toEntity() = ProjectExpenseEntity(
    id = id, projectId = projectId, category = category, title = title,
    amount = amount, expenseDate = expenseDate, notes = notes
)

fun ProjectStageEntity.toDomain() = ProjectStage(
    id = id, projectId = projectId, name = name, sortOrder = sortOrder,
    status = status, completedAt = completedAt, notes = notes
)

fun ProjectStage.toEntity() = ProjectStageEntity(
    id = id, projectId = projectId, name = name, sortOrder = sortOrder,
    status = status, completedAt = completedAt, notes = notes
)

fun DailyWorkEntity.toDomain() = DailyWork(
    id = id, projectId = projectId, workerId = workerId, workDate = workDate,
    workType = workType, hours = hours, baseWage = baseWage, notes = notes
)

fun DailyWork.toEntity() = DailyWorkEntity(
    id = id, projectId = projectId, workerId = workerId, workDate = workDate,
    workType = workType, hours = hours, baseWage = baseWage, notes = notes
)

fun OvertimeEntity.toDomain() = Overtime(
    id = id, dailyWorkId = dailyWorkId, hours = hours, ratePerHour = ratePerHour,
    totalAmount = totalAmount, notes = notes
)

fun Overtime.toEntity() = OvertimeEntity(
    id = id, dailyWorkId = dailyWorkId, hours = hours, ratePerHour = ratePerHour,
    totalAmount = totalAmount, notes = notes
)

package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worker_payments",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkerEntity::class,
            parentColumns = ["id"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("workerId"), Index("paymentDate")]
)
data class WorkerPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val workerId: Long,
    val amount: Long,
    val paymentDate: Long,
    /**
     * full, advance, partial, settlement
     */
    val paymentType: String = "partial",
    /** cash, card_reader, card_to_card, bank_transfer, other */
    val method: String = "cash",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

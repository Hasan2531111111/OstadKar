package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "overtimes",
    foreignKeys = [
        ForeignKey(
            entity = DailyWorkEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyWorkId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dailyWorkId")]
)
data class OvertimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dailyWorkId: Long,
    val hours: Double = 0.0,
    val ratePerHour: Long = 0, // Tomans
    val totalAmount: Long = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

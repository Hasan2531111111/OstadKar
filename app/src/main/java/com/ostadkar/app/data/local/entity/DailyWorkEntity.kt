package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_works",
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
    indices = [Index("projectId"), Index("workerId"), Index("workDate")]
)
data class DailyWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val workerId: Long,
    /** Epoch millis at start of day (Persian date converted) */
    val workDate: Long,
    /**
     * full_day, half_day, hourly, absent, leave
     */
    val workType: String = "full_day",
    /** Used when workType == hourly */
    val hours: Double = 0.0,
    /** Calculated wage for this day (before overtime) */
    val baseWage: Long = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

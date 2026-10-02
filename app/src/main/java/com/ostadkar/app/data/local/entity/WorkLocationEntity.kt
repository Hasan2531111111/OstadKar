package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sub-locations for each work type (کف, دیوار, کف حمام, ...).
 * Custom locations are allowed and stored per work type or globally.
 */
@Entity(
    tableName = "work_locations",
    foreignKeys = [
        ForeignKey(
            entity = WorkTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["workTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workTypeId"), Index(value = ["workTypeId", "code"], unique = true)]
)
data class WorkLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workTypeId: Long,
    val code: String,
    val nameFa: String,
    val isCustom: Boolean = false,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

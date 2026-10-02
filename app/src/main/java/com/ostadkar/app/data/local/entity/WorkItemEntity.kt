package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single priced work line inside a project.
 * Supports calculated quantity (length × width / height) or manual quantity.
 */
@Entity(
    tableName = "work_items",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["workTypeId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = WorkLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["workLocationId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("projectId"), Index("workTypeId"), Index("workLocationId")]
)
data class WorkItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val workTypeId: Long?,
    /** Display name snapshot (important for custom types) */
    val workTypeName: String,
    val workLocationId: Long?,
    val workLocationName: String,
    /** square_meter, linear_meter, piece, day, hour, project, custom */
    val unit: String = "square_meter",
    val unitCustomName: String? = null,
    /** Dimensions for auto calculation */
    val length: Double? = null,
    val width: Double? = null,
    val height: Double? = null,
    /** Final quantity used for pricing */
    val quantity: Double = 0.0,
    val unitPrice: Long = 0, // in Tomans (smallest unit = 1 Toman)
    val totalPrice: Long = 0,
    val notes: String = "",
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Work types (کاشی‌کاری, سرامیک‌کاری, ...).
 * Predefined ones are seeded; custom ones can be added per project or globally.
 * Architecture allows adding new specialties (گچ‌کاری, نقاشی, ...) without code changes.
 */
@Entity(
    tableName = "work_types",
    indices = [Index(value = ["code"], unique = true)]
)
data class WorkTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** Unique code e.g. "tile", "ceramic", "custom_xxx" */
    val code: String,
    val nameFa: String,
    val isCustom: Boolean = false,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_expenses",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("expenseDate")]
)
data class ProjectExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    /** materials, transport, other, ... */
    val category: String = "other",
    val title: String,
    val amount: Long,
    val expenseDate: Long,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

package com.ostadkar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val displayName: String = "",
    val specialty: String = "",
    val phone: String = "",
    val city: String = "",
    val photoPath: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

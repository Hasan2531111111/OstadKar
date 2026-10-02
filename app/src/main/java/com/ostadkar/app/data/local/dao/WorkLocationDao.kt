package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.WorkLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkLocationDao {
    @Query("SELECT * FROM work_locations WHERE workTypeId = :workTypeId AND isActive = 1 ORDER BY sortOrder ASC, nameFa ASC")
    fun observeByWorkType(workTypeId: Long): Flow<List<WorkLocationEntity>>

    @Query("SELECT * FROM work_locations WHERE workTypeId = :workTypeId ORDER BY sortOrder ASC")
    suspend fun getByWorkType(workTypeId: Long): List<WorkLocationEntity>

    @Query("SELECT * FROM work_locations WHERE id = :id")
    suspend fun getById(id: Long): WorkLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: WorkLocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<WorkLocationEntity>)

    @Update
    suspend fun update(location: WorkLocationEntity)
}

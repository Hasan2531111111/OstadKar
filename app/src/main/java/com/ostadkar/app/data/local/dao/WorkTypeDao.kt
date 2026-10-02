package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.WorkTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkTypeDao {
    @Query("SELECT * FROM work_types WHERE isActive = 1 ORDER BY sortOrder ASC, nameFa ASC")
    fun observeAllActive(): Flow<List<WorkTypeEntity>>

    @Query("SELECT * FROM work_types ORDER BY sortOrder ASC, nameFa ASC")
    fun observeAll(): Flow<List<WorkTypeEntity>>

    @Query("SELECT * FROM work_types WHERE id = :id")
    suspend fun getById(id: Long): WorkTypeEntity?

    @Query("SELECT * FROM work_types WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): WorkTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workType: WorkTypeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workTypes: List<WorkTypeEntity>)

    @Update
    suspend fun update(workType: WorkTypeEntity)

    @Query("SELECT COUNT(*) FROM work_types")
    suspend fun count(): Int
}

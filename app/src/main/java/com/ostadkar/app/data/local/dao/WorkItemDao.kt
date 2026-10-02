package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.WorkItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkItemDao {
    @Query("SELECT * FROM work_items WHERE projectId = :projectId ORDER BY sortOrder ASC, id ASC")
    fun observeByProject(projectId: Long): Flow<List<WorkItemEntity>>

    @Query("SELECT * FROM work_items WHERE projectId = :projectId ORDER BY sortOrder ASC")
    suspend fun getByProject(projectId: Long): List<WorkItemEntity>

    @Query("SELECT * FROM work_items WHERE id = :id")
    suspend fun getById(id: Long): WorkItemEntity?

    @Query("SELECT COALESCE(SUM(totalPrice), 0) FROM work_items WHERE projectId = :projectId")
    fun observeProjectTotal(projectId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(totalPrice), 0) FROM work_items")
    fun observeAllProjectsTotal(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorkItemEntity): Long

    @Update
    suspend fun update(item: WorkItemEntity)

    @Delete
    suspend fun delete(item: WorkItemEntity)

    @Query("DELETE FROM work_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}

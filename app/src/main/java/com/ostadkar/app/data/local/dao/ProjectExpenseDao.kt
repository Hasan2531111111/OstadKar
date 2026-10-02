package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.ProjectExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectExpenseDao {
    @Query("SELECT * FROM project_expenses WHERE projectId = :projectId ORDER BY expenseDate DESC")
    fun observeByProject(projectId: Long): Flow<List<ProjectExpenseEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM project_expenses WHERE projectId = :projectId")
    fun observeProjectTotal(projectId: Long): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ProjectExpenseEntity): Long

    @Update
    suspend fun update(expense: ProjectExpenseEntity)

    @Delete
    suspend fun delete(expense: ProjectExpenseEntity)
}

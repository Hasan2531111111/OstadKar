package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.DailyWorkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWorkDao {
    @Query("SELECT * FROM daily_works WHERE projectId = :projectId ORDER BY workDate DESC")
    fun observeByProject(projectId: Long): Flow<List<DailyWorkEntity>>

    @Query("SELECT * FROM daily_works WHERE projectId = :projectId AND workerId = :workerId ORDER BY workDate DESC")
    fun observeByProjectAndWorker(projectId: Long, workerId: Long): Flow<List<DailyWorkEntity>>

    @Query("SELECT * FROM daily_works WHERE id = :id")
    suspend fun getById(id: Long): DailyWorkEntity?

    @Query("SELECT COALESCE(SUM(baseWage), 0) FROM daily_works WHERE projectId = :projectId")
    fun observeTotalBaseWage(projectId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(baseWage), 0) FROM daily_works WHERE projectId = :projectId AND workerId = :workerId")
    fun observeWorkerBaseWage(projectId: Long, workerId: Long): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyWork: DailyWorkEntity): Long

    @Update
    suspend fun update(dailyWork: DailyWorkEntity)

    @Delete
    suspend fun delete(dailyWork: DailyWorkEntity)
}

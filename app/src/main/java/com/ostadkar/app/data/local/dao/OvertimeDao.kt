package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.OvertimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OvertimeDao {
    @Query("SELECT * FROM overtimes WHERE dailyWorkId = :dailyWorkId")
    fun observeByDailyWork(dailyWorkId: Long): Flow<List<OvertimeEntity>>

    @Query("SELECT COALESCE(SUM(totalAmount), 0) FROM overtimes WHERE dailyWorkId IN (SELECT id FROM daily_works WHERE projectId = :projectId)")
    fun observeProjectOvertimeTotal(projectId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(totalAmount), 0) FROM overtimes WHERE dailyWorkId IN (SELECT id FROM daily_works WHERE projectId = :projectId AND workerId = :workerId)")
    fun observeWorkerOvertimeTotal(projectId: Long, workerId: Long): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(overtime: OvertimeEntity): Long

    @Update
    suspend fun update(overtime: OvertimeEntity)

    @Delete
    suspend fun delete(overtime: OvertimeEntity)
}

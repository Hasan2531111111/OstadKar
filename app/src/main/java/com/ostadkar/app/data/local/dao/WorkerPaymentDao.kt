package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.WorkerPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerPaymentDao {
    @Query("SELECT * FROM worker_payments WHERE projectId = :projectId ORDER BY paymentDate DESC")
    fun observeByProject(projectId: Long): Flow<List<WorkerPaymentEntity>>

    @Query("SELECT * FROM worker_payments WHERE projectId = :projectId AND workerId = :workerId ORDER BY paymentDate DESC")
    fun observeByProjectAndWorker(projectId: Long, workerId: Long): Flow<List<WorkerPaymentEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM worker_payments WHERE projectId = :projectId")
    fun observeProjectTotal(projectId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM worker_payments WHERE projectId = :projectId AND workerId = :workerId")
    fun observeWorkerTotal(projectId: Long, workerId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM worker_payments")
    fun observeAllTotal(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: WorkerPaymentEntity): Long

    @Update
    suspend fun update(payment: WorkerPaymentEntity)

    @Delete
    suspend fun delete(payment: WorkerPaymentEntity)
}

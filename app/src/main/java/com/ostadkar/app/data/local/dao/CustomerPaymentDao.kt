package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.CustomerPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerPaymentDao {
    @Query("SELECT * FROM customer_payments WHERE projectId = :projectId ORDER BY paymentDate DESC")
    fun observeByProject(projectId: Long): Flow<List<CustomerPaymentEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM customer_payments WHERE projectId = :projectId")
    fun observeProjectTotal(projectId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM customer_payments")
    fun observeAllTotal(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: CustomerPaymentEntity): Long

    @Update
    suspend fun update(payment: CustomerPaymentEntity)

    @Delete
    suspend fun delete(payment: CustomerPaymentEntity)
}

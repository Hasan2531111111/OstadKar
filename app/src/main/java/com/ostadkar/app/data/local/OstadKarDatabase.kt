package com.ostadkar.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ostadkar.app.data.local.converter.Converters
import com.ostadkar.app.data.local.dao.*
import com.ostadkar.app.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CustomerEntity::class,
        ProjectEntity::class,
        WorkTypeEntity::class,
        WorkLocationEntity::class,
        WorkItemEntity::class,
        WorkerEntity::class,
        ProjectWorkerEntity::class,
        DailyWorkEntity::class,
        OvertimeEntity::class,
        CustomerPaymentEntity::class,
        WorkerPaymentEntity::class,
        ProjectExpenseEntity::class,
        ProjectStageEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class OstadKarDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun projectDao(): ProjectDao
    abstract fun workTypeDao(): WorkTypeDao
    abstract fun workLocationDao(): WorkLocationDao
    abstract fun workItemDao(): WorkItemDao
    abstract fun workerDao(): WorkerDao
    abstract fun projectWorkerDao(): ProjectWorkerDao
    abstract fun dailyWorkDao(): DailyWorkDao
    abstract fun overtimeDao(): OvertimeDao
    abstract fun customerPaymentDao(): CustomerPaymentDao
    abstract fun workerPaymentDao(): WorkerPaymentDao
    abstract fun projectExpenseDao(): ProjectExpenseDao
    abstract fun projectStageDao(): ProjectStageDao

    companion object {
        const val DATABASE_NAME = "ostadkar.db"

        fun createCallback(scope: CoroutineScope): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seeding is done via DatabaseModule after Room is built
                }
            }
        }
    }
}

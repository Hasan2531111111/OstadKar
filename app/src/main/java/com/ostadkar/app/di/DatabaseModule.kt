package com.ostadkar.app.di

import android.content.Context
import androidx.room.Room
import com.ostadkar.app.data.local.DatabaseSeeder
import com.ostadkar.app.data.local.OstadKarDatabase
import com.ostadkar.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): OstadKarDatabase {
        val db = Room.databaseBuilder(
            context,
            OstadKarDatabase::class.java,
            OstadKarDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For v1; replace with proper migrations later
            .build()

        // Seed predefined work types after DB is ready
        scope.launch {
            val seeder = DatabaseSeeder(db.workTypeDao(), db.workLocationDao())
            seeder.seedIfNeeded()
        }
        return db
    }

    @Provides fun provideCustomerDao(db: OstadKarDatabase): CustomerDao = db.customerDao()
    @Provides fun provideProjectDao(db: OstadKarDatabase): ProjectDao = db.projectDao()
    @Provides fun provideWorkTypeDao(db: OstadKarDatabase): WorkTypeDao = db.workTypeDao()
    @Provides fun provideWorkLocationDao(db: OstadKarDatabase): WorkLocationDao = db.workLocationDao()
    @Provides fun provideWorkItemDao(db: OstadKarDatabase): WorkItemDao = db.workItemDao()
    @Provides fun provideWorkerDao(db: OstadKarDatabase): WorkerDao = db.workerDao()
    @Provides fun provideProjectWorkerDao(db: OstadKarDatabase): ProjectWorkerDao = db.projectWorkerDao()
    @Provides fun provideDailyWorkDao(db: OstadKarDatabase): DailyWorkDao = db.dailyWorkDao()
    @Provides fun provideOvertimeDao(db: OstadKarDatabase): OvertimeDao = db.overtimeDao()
    @Provides fun provideCustomerPaymentDao(db: OstadKarDatabase): CustomerPaymentDao = db.customerPaymentDao()
    @Provides fun provideWorkerPaymentDao(db: OstadKarDatabase): WorkerPaymentDao = db.workerPaymentDao()
    @Provides fun provideProjectExpenseDao(db: OstadKarDatabase): ProjectExpenseDao = db.projectExpenseDao()
    @Provides fun provideProjectStageDao(db: OstadKarDatabase): ProjectStageDao = db.projectStageDao()
}

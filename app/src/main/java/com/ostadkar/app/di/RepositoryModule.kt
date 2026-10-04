package com.ostadkar.app.di

import com.ostadkar.app.data.repository.*
import com.ostadkar.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @Binds @Singleton
    abstract fun bindWorkTypeRepository(impl: WorkTypeRepositoryImpl): WorkTypeRepository

    @Binds @Singleton
    abstract fun bindWorkLocationRepository(impl: WorkLocationRepositoryImpl): WorkLocationRepository

    @Binds @Singleton
    abstract fun bindWorkItemRepository(impl: WorkItemRepositoryImpl): WorkItemRepository

    @Binds @Singleton
    abstract fun bindWorkerRepository(impl: WorkerRepositoryImpl): WorkerRepository

    @Binds @Singleton
    abstract fun bindProjectWorkerRepository(impl: ProjectWorkerRepositoryImpl): ProjectWorkerRepository

    @Binds @Singleton
    abstract fun bindDailyWorkRepository(impl: DailyWorkRepositoryImpl): DailyWorkRepository

    @Binds @Singleton
    abstract fun bindOvertimeRepository(impl: OvertimeRepositoryImpl): OvertimeRepository

    @Binds @Singleton
    abstract fun bindCustomerPaymentRepository(impl: CustomerPaymentRepositoryImpl): CustomerPaymentRepository

    @Binds @Singleton
    abstract fun bindWorkerPaymentRepository(impl: WorkerPaymentRepositoryImpl): WorkerPaymentRepository

    @Binds @Singleton
    abstract fun bindProjectExpenseRepository(impl: ProjectExpenseRepositoryImpl): ProjectExpenseRepository

    @Binds @Singleton
    abstract fun bindProjectStageRepository(impl: ProjectStageRepositoryImpl): ProjectStageRepository

    @Binds @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository
}

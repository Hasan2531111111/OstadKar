package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.ProjectExpenseDao
import com.ostadkar.app.data.local.dao.ProjectStageDao
import com.ostadkar.app.data.local.entity.ProjectStageEntity
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.ProjectExpense
import com.ostadkar.app.domain.model.ProjectStage
import com.ostadkar.app.domain.repository.ProjectExpenseRepository
import com.ostadkar.app.domain.repository.ProjectStageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectExpenseRepositoryImpl @Inject constructor(
    private val dao: ProjectExpenseDao
) : ProjectExpenseRepository {
    override fun observeByProject(projectId: Long): Flow<List<ProjectExpense>> =
        dao.observeByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun observeProjectTotal(projectId: Long): Flow<Long> =
        dao.observeProjectTotal(projectId)

    override suspend fun save(expense: ProjectExpense): Long = dao.insert(expense.toEntity())

    override suspend fun delete(id: Long) {
        // via entity later
    }
}

@Singleton
class ProjectStageRepositoryImpl @Inject constructor(
    private val dao: ProjectStageDao
) : ProjectStageRepository {
    override fun observeByProject(projectId: Long): Flow<List<ProjectStage>> =
        dao.observeByProject(projectId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(stage: ProjectStage): Long = dao.insert(stage.toEntity())

    override suspend fun seedDefaultStages(projectId: Long) {
        val defaults = listOf(
            "تخریب",
            "آماده‌سازی",
            "زیرسازی",
            "عایق‌کاری",
            "اجرای کف",
            "اجرای دیوار",
            "بندکشی",
            "تمیزکاری",
            "تکمیل"
        )
        val entities = defaults.mapIndexed { index, name ->
            ProjectStageEntity(
                projectId = projectId,
                name = name,
                sortOrder = index + 1,
                status = "not_started"
            )
        }
        dao.insertAll(entities)
    }
}

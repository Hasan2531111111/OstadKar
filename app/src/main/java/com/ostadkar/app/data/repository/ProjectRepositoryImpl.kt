package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.CustomerDao
import com.ostadkar.app.data.local.dao.ProjectDao
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.Project
import com.ostadkar.app.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val customerDao: CustomerDao
) : ProjectRepository {
    override fun observeAll(): Flow<List<Project>> =
        combine(projectDao.observeAll(), customerDao.observeAll()) { projects, customers ->
            val map = customers.associateBy { it.id }
            projects.map { p -> p.toDomain(map[p.customerId]?.name ?: "") }
        }

    override fun observeActive(): Flow<List<Project>> =
        combine(projectDao.observeByStatus("active"), customerDao.observeAll()) { projects, customers ->
            val map = customers.associateBy { it.id }
            projects.map { p -> p.toDomain(map[p.customerId]?.name ?: "") }
        }

    override fun observeById(id: Long): Flow<Project?> =
        combine(projectDao.observeById(id), customerDao.observeAll()) { project, customers ->
            project?.let {
                val name = customers.find { c -> c.id == it.customerId }?.name ?: ""
                it.toDomain(name)
            }
        }

    override fun observeByCustomer(customerId: Long): Flow<List<Project>> =
        projectDao.observeByCustomer(customerId).map { list -> list.map { it.toDomain() } }

    override fun observeActiveCount(): Flow<Int> = projectDao.observeActiveCount()

    override suspend fun getById(id: Long): Project? {
        val p = projectDao.getById(id) ?: return null
        val name = customerDao.getById(p.customerId)?.name ?: ""
        return p.toDomain(name)
    }

    override suspend fun save(project: Project): Long = projectDao.insert(project.toEntity())

    override suspend fun delete(id: Long) = projectDao.deleteById(id)
}

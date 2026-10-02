package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.WorkItemDao
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.WorkItem
import com.ostadkar.app.domain.repository.WorkItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkItemRepositoryImpl @Inject constructor(
    private val dao: WorkItemDao
) : WorkItemRepository {
    override fun observeByProject(projectId: Long): Flow<List<WorkItem>> =
        dao.observeByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun observeProjectTotal(projectId: Long): Flow<Long> =
        dao.observeProjectTotal(projectId)

    override suspend fun save(item: WorkItem): Long = dao.insert(item.toEntity())

    override suspend fun delete(id: Long) = dao.deleteById(id)
}

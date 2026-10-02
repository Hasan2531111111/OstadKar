package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.ProjectWorkerDao
import com.ostadkar.app.data.local.dao.WorkerDao
import com.ostadkar.app.data.local.entity.ProjectWorkerEntity
import com.ostadkar.app.domain.model.ProjectWorker
import com.ostadkar.app.domain.repository.ProjectWorkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectWorkerRepositoryImpl @Inject constructor(
    private val projectWorkerDao: ProjectWorkerDao,
    private val workerDao: WorkerDao
) : ProjectWorkerRepository {
    override fun observeByProject(projectId: Long): Flow<List<ProjectWorker>> =
        combine(
            projectWorkerDao.observeByProject(projectId),
            workerDao.observeAll()
        ) { assignments, workers ->
            val workerMap = workers.associateBy { it.id }
            assignments.map { a ->
                val w = workerMap[a.workerId]
                ProjectWorker(
                    id = a.id,
                    projectId = a.projectId,
                    workerId = a.workerId,
                    dailyWage = a.dailyWage,
                    notes = a.notes,
                    workerName = w?.name ?: "",
                    workerType = w?.type ?: ""
                )
            }
        }

    override suspend fun assign(projectId: Long, workerId: Long, dailyWage: Long?): Long {
        val existing = projectWorkerDao.getAssignment(projectId, workerId)
        return if (existing != null) {
            projectWorkerDao.update(existing.copy(dailyWage = dailyWage))
            existing.id
        } else {
            projectWorkerDao.insert(
                ProjectWorkerEntity(
                    projectId = projectId,
                    workerId = workerId,
                    dailyWage = dailyWage
                )
            )
        }
    }

    override suspend fun remove(projectId: Long, workerId: Long) {
        projectWorkerDao.remove(projectId, workerId)
    }
}

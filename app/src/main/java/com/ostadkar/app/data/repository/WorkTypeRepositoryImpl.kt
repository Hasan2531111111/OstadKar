package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.WorkLocationDao
import com.ostadkar.app.data.local.dao.WorkTypeDao
import com.ostadkar.app.data.local.entity.WorkLocationEntity
import com.ostadkar.app.data.local.entity.WorkTypeEntity
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.domain.model.WorkLocation
import com.ostadkar.app.domain.model.WorkType
import com.ostadkar.app.domain.repository.WorkLocationRepository
import com.ostadkar.app.domain.repository.WorkTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkTypeRepositoryImpl @Inject constructor(
    private val dao: WorkTypeDao
) : WorkTypeRepository {
    override fun observeAllActive(): Flow<List<WorkType>> =
        dao.observeAllActive().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): WorkType? = dao.getById(id)?.toDomain()

    override suspend fun addCustom(nameFa: String): Long {
        val code = "custom_${System.currentTimeMillis()}"
        return dao.insert(
            WorkTypeEntity(
                code = code,
                nameFa = nameFa,
                isCustom = true,
                sortOrder = 999
            )
        )
    }
}

@Singleton
class WorkLocationRepositoryImpl @Inject constructor(
    private val dao: WorkLocationDao
) : WorkLocationRepository {
    override fun observeByWorkType(workTypeId: Long): Flow<List<WorkLocation>> =
        dao.observeByWorkType(workTypeId).map { list -> list.map { it.toDomain() } }

    override suspend fun addCustom(workTypeId: Long, nameFa: String): Long {
        val code = "custom_${System.currentTimeMillis()}"
        return dao.insert(
            WorkLocationEntity(
                workTypeId = workTypeId,
                code = code,
                nameFa = nameFa,
                isCustom = true,
                sortOrder = 999
            )
        )
    }
}

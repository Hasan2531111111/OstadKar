package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.OvertimeDao
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.Overtime
import com.ostadkar.app.domain.repository.OvertimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OvertimeRepositoryImpl @Inject constructor(
    private val dao: OvertimeDao
) : OvertimeRepository {
    override fun observeByDailyWork(dailyWorkId: Long): Flow<List<Overtime>> =
        dao.observeByDailyWork(dailyWorkId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(overtime: Overtime): Long = dao.insert(overtime.toEntity())

    override suspend fun delete(id: Long) {
        // OvertimeDao doesn't have getById; delete via entity if needed later
    }
}

package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.ProjectWorkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectWorkerDao {
    @Query("SELECT * FROM project_workers WHERE projectId = :projectId")
    fun observeByProject(projectId: Long): Flow<List<ProjectWorkerEntity>>

    @Query("SELECT * FROM project_workers WHERE projectId = :projectId")
    suspend fun getByProject(projectId: Long): List<ProjectWorkerEntity>

    @Query("SELECT * FROM project_workers WHERE projectId = :projectId AND workerId = :workerId LIMIT 1")
    suspend fun getAssignment(projectId: Long, workerId: Long): ProjectWorkerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assignment: ProjectWorkerEntity): Long

    @Update
    suspend fun update(assignment: ProjectWorkerEntity)

    @Query("DELETE FROM project_workers WHERE projectId = :projectId AND workerId = :workerId")
    suspend fun remove(projectId: Long, workerId: Long)
}

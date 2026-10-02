package com.ostadkar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ostadkar.app.data.local.entity.ProjectStageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectStageDao {
    @Query("SELECT * FROM project_stages WHERE projectId = :projectId ORDER BY sortOrder ASC")
    fun observeByProject(projectId: Long): Flow<List<ProjectStageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stage: ProjectStageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stages: List<ProjectStageEntity>)

    @Update
    suspend fun update(stage: ProjectStageEntity)

    @Delete
    suspend fun delete(stage: ProjectStageEntity)
}

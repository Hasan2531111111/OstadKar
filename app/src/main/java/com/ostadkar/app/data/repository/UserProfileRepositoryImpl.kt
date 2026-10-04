package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.UserProfileDao
import com.ostadkar.app.data.local.entity.UserProfileEntity
import com.ostadkar.app.domain.repository.UserProfile
import com.ostadkar.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override fun observe(): Flow<UserProfile> = dao.observe().map { it.toDomain() }

    override suspend fun get(): UserProfile = dao.get().toDomain()

    override suspend fun save(profile: UserProfile) {
        dao.upsert(
            UserProfileEntity(
                id = 1,
                displayName = profile.displayName.trim(),
                specialty = profile.specialty.trim(),
                phone = profile.phone.trim(),
                city = profile.city.trim(),
                photoPath = profile.photoPath,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun UserProfileEntity?.toDomain() = UserProfile(
        displayName = this?.displayName.orEmpty(),
        specialty = this?.specialty.orEmpty(),
        phone = this?.phone.orEmpty(),
        city = this?.city.orEmpty(),
        photoPath = this?.photoPath.orEmpty()
    )
}

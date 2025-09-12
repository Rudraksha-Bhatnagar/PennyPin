package com.example.pennypin.repository



import com.example.pennypin.data.UserProfile
import com.example.pennypin.data.UserProfileDao
import com.example.pennypin.data.remote.BackendClient
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val dao: UserProfileDao,
    private val backend: BackendClient
) {
    fun observeUser(uid: String): Flow<UserProfile?> = dao.getUserProfileFlow(uid)

    suspend fun getLocalUser(uid: String): UserProfile? = dao.getUserProfile(uid)

    suspend fun refreshUserFromBackend(idToken: String): UserProfile {
        val profile = backend.fetchUserProfile(idToken)
        dao.insert(profile) // save/replace local cache
        return profile
    }

    suspend fun saveLocalUser(profile: UserProfile) {
        dao.insert(profile)
    }
}

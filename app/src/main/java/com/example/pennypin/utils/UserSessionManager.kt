package com.example.pennypin.utils

import android.content.Context
import com.example.pennypin.repository.UserRepository
import com.example.pennypin.data.PennyPinDatabase
import com.example.pennypin.data.UserProfile
import com.example.pennypin.data.remote.BackendClient
import com.example.pennypin.data.session.SessionManager
import kotlinx.coroutines.flow.Flow

/**
 * Simple singleton to orchestrate local cache + backend refresh.
 * All network/db operations are suspend — call from a coroutine (ViewModel scope / activity scope).
 */
class UserSessionManager private constructor(
    private val repo: UserRepository,
    private val session: SessionManager
) {

    companion object {
        @Volatile
        private var INSTANCE: UserSessionManager? = null

        fun getInstance(context: Context, backendBaseUrl: String): UserSessionManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val db = PennyPinDatabase.getDatabase(context)
                    val backend = BackendClient(backendBaseUrl)
                    val repo = UserRepository(db.userProfileDao(), backend)
                    val session = SessionManager(context)
                    UserSessionManager(repo, session).also { INSTANCE = it }
                }
            }
    }

    // expose persisted uid/idToken flows if needed
    val storedUidFlow = session.uidFlow
    val storedIdTokenFlow = session.idTokenFlow

    // Observe user profile from local DB (reactive)
    fun observeUser(uid: String): Flow<UserProfile?> = repo.observeUser(uid)

    // On login: save idToken + uid, refresh from backend and cache locally
    suspend fun signInWithIdToken(idToken: String): UserProfile {
        return try {
            // backend verifies token and returns profile
            val profile = repo.refreshUserFromBackend(idToken)

            // persist token & uid locally for session
            session.saveIdToken(idToken)
            session.saveUid(profile.uid)

            profile
        } catch (e: Exception) {
            // rethrow or handle gracefully depending on UI
            throw e
        }
    }

    // At app start you can call this to load cached profile quickly
    suspend fun loadLocalProfile(uid: String): UserProfile? = repo.getLocalUser(uid)

    // Update profile locally (and usually you should update backend first)
    suspend fun updateLocalProfile(profile: UserProfile) = repo.saveLocalUser(profile)

    // Clear session
    suspend fun clearSession() {
        session.clear()
    }
}

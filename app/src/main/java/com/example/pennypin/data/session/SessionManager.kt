package com.example.pennypin.data.session



import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Handles local persistence of user session data (uid, idToken).
 * Backed by SharedPreferences + in-memory StateFlow for reactivity.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)

    // reactive flows
    private val _uidFlow = MutableStateFlow(prefs.getString(KEY_UID, null))
    val uidFlow: Flow<String?> = _uidFlow

    private val _idTokenFlow = MutableStateFlow(prefs.getString(KEY_ID_TOKEN, null))
    val idTokenFlow: Flow<String?> = _idTokenFlow

    fun saveUid(uid: String?) {
        prefs.edit().putString(KEY_UID, uid).apply()
        _uidFlow.value = uid
    }

    fun saveIdToken(token: String?) {
        prefs.edit().putString(KEY_ID_TOKEN, token).apply()
        _idTokenFlow.value = token
    }

    fun getUid(): String? = prefs.getString(KEY_UID, null)
    fun getIdToken(): String? = prefs.getString(KEY_ID_TOKEN, null)

    fun clear() {
        prefs.edit().clear().apply()
        _uidFlow.value = null
        _idTokenFlow.value = null
    }

    companion object {
        private const val KEY_UID = "uid"
        private const val KEY_ID_TOKEN = "id_token"
    }
}

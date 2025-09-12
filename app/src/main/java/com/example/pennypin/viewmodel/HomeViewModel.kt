package com.example.pennypin.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennypin.utils.UserSessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val uid: String, context: Context) : ViewModel() {
    private val session = UserSessionManager.getInstance(
        context,
        "https://your-backend-domain.com"
    )

    val userProfileFlow = session.observeUser(uid)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}

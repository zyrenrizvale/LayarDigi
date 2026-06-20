package com.layardigi.app.data.repository

import com.layardigi.app.LayarDigiApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Role {
    GUEST, USER, ADMIN
}

object AuthRepository {
    private val _currentUserRole = MutableStateFlow(Role.GUEST)
    val currentUserRole: StateFlow<Role> = _currentUserRole.asStateFlow()

    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername: StateFlow<String?> = _currentUsername.asStateFlow()

    fun login(username: String, role: Role) {
        _currentUsername.value = username
        _currentUserRole.value = role
        // Persist session
        try {
            LayarDigiApp.getPrefs().edit()
                .putString("username", username)
                .putString("role", role.name)
                .apply()
        } catch (_: Exception) {}
    }

    fun logout() {
        _currentUsername.value = null
        _currentUserRole.value = Role.GUEST
        // Clear session
        try {
            LayarDigiApp.getPrefs().edit().clear().apply()
        } catch (_: Exception) {}
    }
}

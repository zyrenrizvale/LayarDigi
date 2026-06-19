package com.layardigi.app.data.repository

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
    }

    fun logout() {
        _currentUsername.value = null
        _currentUserRole.value = Role.GUEST
    }
}

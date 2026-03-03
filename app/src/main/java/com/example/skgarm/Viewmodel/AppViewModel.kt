package com.example.skgarm.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skgarm.data.Local.Entity.Availability
import com.example.skgarm.data.Local.Entity.User

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

import com.example.skgarm.data.Repository.AppRepositoryImpl
import com.example.skgarm.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


sealed class AuthState {

    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()


}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: AppRepositoryImpl,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()


    init {
        // when ViewModel created → check if email is saved
        viewModelScope.launch {
            userPreferences.savedUser.first()?.let { savedUser ->
                _currentUser.value = savedUser
                _authState.value = AuthState.Success(savedUser)
            }
        }

    }


    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUser()
            _currentUser.value = null
            _authState.value = AuthState.Idle
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email.trim().lowercase(), password)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                    userPreferences.saveUser(user)

                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Sign in failed")

                }
            )

        }

    }


    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.registerUser(name.trim(), email.trim().lowercase(), password)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                    userPreferences.saveUser(user)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Registration  failed")
                }
            )
        }

    }

    fun toggleAvailability(date: String, timeSlot: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val isAvailable = repository
                .isUserAvailableFlow(date, timeSlot, user.email)
                .first()

            if (isAvailable > 0) {
                repository.markUnavailable(date, timeSlot, user.email)
            } else {
                repository.markAvailable(date, timeSlot, user)
            }

        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error)
            _authState.value = AuthState.Idle
    }

    fun attendeesFlow(date: String, timeSlot: String): Flow<List<Availability>> =
        repository.getAttendeesFlow(date, timeSlot)


    fun isAvailableFlow(date: String, timeSlot: String): Flow<Boolean> {
        val email = _currentUser.value?.email ?: return flowOf(false)
        return repository.isUserAvailableFlow(date, timeSlot, email)
            .map { it > 0 }
    }

}

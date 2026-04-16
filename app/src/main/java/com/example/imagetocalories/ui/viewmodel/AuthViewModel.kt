package com.example.imagetocalories.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetocalories.data.local.UserDao
import com.example.imagetocalories.data.local.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser = userDao.getCurrentUser()

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = userDao.login(email, password)
            if (user != null) {
                userDao.logoutAll() // Clear any existing sessions
                userDao.updateUser(user.copy(isLoggedIn = true))
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Invalid email or password")
            }
        }
    }

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        height: Float? = null,
        weight: Float? = null,
        targetCalories: Int? = null,
        birthDate: Long? = null,
        gender: Int? = null,
        activityLevel: Int? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                _authState.value = AuthState.Error("Email already exists")
                return@launch
            }

            userDao.logoutAll()
            val newUser = UserEntity(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                isLoggedIn = true,
                height = height,
                currentWeight = weight,
                targetCalories = targetCalories,
                birthDate = birthDate,
                gender = gender,
                activityLevel = activityLevel
            )
            userDao.insertUser(newUser)
            _authState.value = AuthState.Success
        }
    }

    fun logout() {
        viewModelScope.launch {
            userDao.logoutAll()
            _authState.value = AuthState.Idle
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            userDao.updateUser(user)
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            userDao.logoutAll()
            _authState.value = AuthState.Idle
        }
    }
}
package com.example.bytebattlesmobileapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.domain.model.User
import com.example.bytebattlesmobileapp.domain.usecase.LoginUseCase
import com.example.bytebattlesmobileapp.domain.usecase.RegisterUseCase
import com.example.bytebattlesmobileapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val user: User) : AuthState
    data class Error(val message: String) : AuthState
    object CheckingAuth : AuthState
}

sealed interface AuthEvent {
    data class Login(val email: String, val password: String) : AuthEvent
    data class Register(val username: String, val email: String, val password: String) : AuthEvent
    object Logout : AuthEvent
    object ClearError : AuthEvent
    object CheckAuthStatus : AuthEvent
}

data class AuthUIState(
    val authState: AuthState = AuthState.Idle,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val isCheckingAuth: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState: StateFlow<AuthUIState> = _uiState.asStateFlow()

    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    init {
        // При старте проверяем авторизацию
        checkAuthOnStart()
    }

    private fun checkAuthOnStart() {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Checking auth status on app start")
            checkAuthStatus()
        }
    }

    private suspend fun checkAuthStatus() {
        Log.d("AuthViewModel", "Checking authentication status")
        _uiState.update { it.copy(authState = AuthState.CheckingAuth, isCheckingAuth = true) }

        try {
            // Проверяем, есть ли сохраненный токен
            val hasToken = authRepository.isLoggedIn()

            if (hasToken) {
                Log.d("AuthViewModel", "Token found, navigating to main")
                _uiState.update {
                    it.copy(
                        authState = AuthState.Idle,
                        isLoggedIn = true,
                        isCheckingAuth = false
                    )
                }
                // Небольшая задержка для показа splash/start экрана
                delay(1000)
                _navigateToMain.value = true
            } else {
                Log.d("AuthViewModel", "No token found, staying on start screen")
                _uiState.update {
                    it.copy(
                        authState = AuthState.Idle,
                        isLoggedIn = false,
                        isCheckingAuth = false
                    )
                }
                // Остаемся на стартовом экране (StartScreen)
                // Пользователь сам выберет ВОЙТИ или СОЗДАТЬ АККАУНТ
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error checking auth status: ${e.message}")
            _uiState.update {
                it.copy(
                    authState = AuthState.Idle,
                    isLoggedIn = false,
                    isCheckingAuth = false
                )
            }
            // В случае ошибки остаемся на стартовом экране
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Attempting login for: $email")
            _uiState.update { it.copy(authState = AuthState.Loading, isLoading = true) }

            try {
                val user = loginUseCase(email, password)
                _uiState.update {
                    it.copy(
                        authState = AuthState.Success(user),
                        isLoggedIn = true,
                        currentUser = user,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                Log.d("AuthViewModel", "Login successful for user: ${user.username}")
                _navigateToMain.value = true
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed: ${e.message}")
                _uiState.update {
                    it.copy(
                        authState = AuthState.Error(e.message ?: "Login failed"),
                        isLoading = false,
                        errorMessage = e.message ?: "Login failed"
                    )
                }
            }
        }
    }

    fun register(firstName: String, lastName:String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading, isLoading = true) }

            try {
                val user = registerUseCase(firstName,lastName,email,password)
                _uiState.update {
                    it.copy(
                        authState = AuthState.Success(user),
                        isLoggedIn = true,
                        currentUser = user,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                _navigateToMain.value = true
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        authState = AuthState.Error(e.message ?: "Registration failed"),
                        isLoading = false,
                        errorMessage = e.message ?: "Registration failed"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Logging out")
            try {
                authRepository.logout()
                _uiState.update {
                    it.copy(
                        authState = AuthState.Idle,
                        isLoggedIn = false,
                        currentUser = null
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout error: ${e.message}")
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                authState = AuthState.Idle,
                errorMessage = null
            )
        }
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun navigationHandled() {
        _navigateToMain.value = false
    }

    fun loginNavigationHandled() {
        _navigateToLogin.value = false
    }
}
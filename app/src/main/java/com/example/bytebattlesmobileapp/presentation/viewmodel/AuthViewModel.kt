package com.example.bytebattlesmobileapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.domain.model.User
import com.example.bytebattlesmobileapp.domain.model.UserProfile
import com.example.bytebattlesmobileapp.domain.model.UserSettings
import com.example.bytebattlesmobileapp.domain.model.UserStats
import com.example.bytebattlesmobileapp.domain.usecase.LoginUseCase
import com.example.bytebattlesmobileapp.domain.usecase.RegisterUseCase
import com.example.bytebattlesmobileapp.domain.repository.AuthRepository
import com.example.bytebattlesmobileapp.domain.usecase.ChangePasswordUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetUserActivitiesUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetUserProfileUseCase
import com.example.bytebattlesmobileapp.domain.usecase.RefreshTokenUseCase
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
    object ChangingPassword : AuthState
    object PasswordChangedSuccess : AuthState
}

sealed interface AuthEvent {
    data class Login(val email: String, val password: String) : AuthEvent
    data class Register(val username: String, val email: String, val password: String) : AuthEvent
    data class ChangePassword(val oldPassword: String, val newPassword: String) : AuthEvent
    object Logout : AuthEvent
    object ClearError : AuthEvent
    object CheckAuthStatus : AuthEvent

}

// Обновляем UI состояние для смены пароля
data class AuthUIState(
    val authState: AuthState = AuthState.Idle,
    val isLoggedIn: Boolean = false,
    val currentUser: String? = null,
    val isLoading: Boolean = false,
    val isCheckingAuth: Boolean = true,
    val isChangingPassword: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
    private val getUserProfile: GetUserProfileUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState: StateFlow<AuthUIState> = _uiState.asStateFlow()

    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()
    private var shouldNavigateOnAuthCheck = true
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
    private fun handleSuccessfulAuth(user: User) {
        shouldNavigateOnAuthCheck = true
        _uiState.update {
            it.copy(
                authState = AuthState.Success(user),
                isLoggedIn = true,
                currentUser = user.username,
                isLoading = false,
                errorMessage = null
            )
        }
        _navigateToMain.value = true
    }
    private suspend fun checkAuthStatus() {
        Log.d("AuthViewModel", "Checking authentication status")
        _uiState.update { it.copy(authState = AuthState.CheckingAuth, isCheckingAuth = true) }

        try {
            // ПРОВЕРЯЕМ НЕ ТОЛЬКО НАЛИЧИЕ, НО И ВАЛИДНОСТЬ ТОКЕНА
            val hasToken = authRepository.isLoggedIn()

            if (hasToken && shouldNavigateOnAuthCheck) {
                // Попробуем получить профиль пользователя, чтобы проверить валидность токена
                try {
                    // Если токен валидный, этот запрос пройдет успешно
                    val userProfile = getUserProfile()

                    Log.d("AuthViewModel", "Token is valid, navigating to main. User: ${userProfile.userName}")
                    _uiState.update {
                        it.copy(
                            authState = AuthState.Idle,
                            isLoggedIn = true,
                            isCheckingAuth = false,
                            currentUser =  userProfile.userName
                        )
                    }
                    _navigateToMain.value = true
                } catch (e: Exception) {
                    // Если не удалось получить профиль, значит токен невалидный
                    Log.e("AuthViewModel", "Token is invalid: ${e.message}")
                    val refresh = refreshTokenUseCase()
                    try {
                        val userProfile = getUserProfile()

                        Log.d("AuthViewModel", "Token is valid, navigating to main. User: ${userProfile.userName}")
                        _uiState.update {
                            it.copy(
                                authState = AuthState.Idle,
                                isLoggedIn = true,
                                isCheckingAuth = false,
                                currentUser =  userProfile.userName
                            )
                        }
                        _navigateToMain.value = true
                    }
                    catch (e: Exception){
                        authRepository.logout()
                        _uiState.update {
                            it.copy(
                                authState = AuthState.Idle,
                                isLoggedIn = false,
                                isCheckingAuth = false,
                                currentUser = null
                            )
                        }
                    }
                }
            } else {
                Log.d("AuthViewModel", "No token found, staying on start screen")
                _uiState.update {
                    it.copy(
                        authState = AuthState.Idle,
                        isLoggedIn = false,
                        isCheckingAuth = false
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error checking auth status: ${e.message}")
            _uiState.update {
                it.copy(
                    authState = AuthState.Error("Authentication check failed"),
                    isLoggedIn = false,
                    isCheckingAuth = false,
                    errorMessage = "Authentication check failed: ${e.message}"
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
                handleSuccessfulAuth(user)
                Log.d("AuthViewModel", "Login successful for user: ${user.username}")
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


    fun register(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading, isLoading = true) }

            try {
                val user = registerUseCase(firstName, lastName, email, password)
                handleSuccessfulAuth(user)
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
                // Отключаем автоматическую навигацию
                shouldNavigateOnAuthCheck = false

                authRepository.logout()
                _uiState.update {
                    it.copy(
                        authState = AuthState.Idle,
                        isLoggedIn = false,
                        currentUser = null
                    )
                }

                // Явно указываем навигацию на логин
                _navigateToLogin.value = true

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout error: ${e.message}")
            }
        }
    }
    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Attempting to change password")

            // Валидация
            if (oldPassword.isBlank() || newPassword.isBlank()) {
                _uiState.update {
                    it.copy(
                        authState = AuthState.Error("Поля не могут быть пустыми"),
                        errorMessage = "Поля не могут быть пустыми"
                    )
                }
                return@launch
            }

            if (newPassword.length < 6) {
                _uiState.update {
                    it.copy(
                        authState = AuthState.Error("Новый пароль должен быть не менее 6 символов"),
                        errorMessage = "Новый пароль должен быть не менее 6 символов"
                    )
                }
                return@launch
            }

            if (oldPassword == newPassword) {
                _uiState.update {
                    it.copy(
                        authState = AuthState.Error("Новый пароль должен отличаться от старого"),
                        errorMessage = "Новый пароль должен отличаться от старого"
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    authState = AuthState.ChangingPassword,
                    isLoading = true,
                    isChangingPassword = true,
                    errorMessage = null
                )
            }
            Log.d("AuthViewModel", "=== START CHANGE PASSWORD ===")
            Log.d("AuthViewModel", "Old password length: ${oldPassword.length}")
            Log.d("AuthViewModel", "New password length: ${newPassword.length}")
            try {
                // Вызываем use case для смены пароля
                try {
                    changePasswordUseCase(oldPassword, newPassword)
                    Log.d("AuthViewModel", "ChangePasswordUseCase completed successfully")
                    // ... остальной код ...
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Change password exception: $e")
                    Log.e("AuthViewModel", "Exception message: ${e.message}")
                    Log.e("AuthViewModel", "Exception stack trace: ${e.stackTraceToString()}")
                    // ... остальной код ...
                }

                Log.d("AuthViewModel", "Password changed successfully")

                _uiState.update {
                    it.copy(
                        authState = AuthState.PasswordChangedSuccess,
                        isLoading = false,
                        isChangingPassword = false,
                        passwordChangeSuccess = true,
                        errorMessage = null
                    )
                }

                // Автоматически сбрасываем успех через 3 секунды
                launch {
                    kotlinx.coroutines.delay(3000)
                    _uiState.update {
                        it.copy(
                            authState = AuthState.Idle,
                            passwordChangeSuccess = false
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Password change failed: ${e.message}")
                _uiState.update {
                    it.copy(
                        authState = AuthState.Error(e.message ?: "Ошибка смены пароля"),
                        isLoading = false,
                        isChangingPassword = false,
                        errorMessage = e.message ?: "Ошибка смены пароля"
                    )
                }

                // Автоматически сбрасываем ошибку через 5 секунд
                launch {
                    kotlinx.coroutines.delay(5000)
                    if (_uiState.value.authState is AuthState.Error) {
                        _uiState.update {
                            it.copy(
                                authState = AuthState.Idle,
                                errorMessage = null
                            )
                        }
                    }
                }
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

    fun resetPasswordChangeSuccess() {
        _uiState.update {
            it.copy(
                passwordChangeSuccess = false,
                authState = AuthState.Idle
            )
        }
    }
    fun navigationHandled() {
        _navigateToMain.value = false
    }

    fun loginNavigationHandled() {
        _navigateToLogin.value = false
    }
}
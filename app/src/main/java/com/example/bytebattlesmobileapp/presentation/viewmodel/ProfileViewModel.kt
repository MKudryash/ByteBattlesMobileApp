package com.example.bytebattlesmobileapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.domain.model.Activities
import com.example.bytebattlesmobileapp.domain.model.UserLeader
import com.example.bytebattlesmobileapp.domain.usecase.GetLeaderBordUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetUserActivitiesUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetUserProfileUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetUserStatsUseCase
import com.example.bytebattlesmobileapp.domain.usecase.UpdateProfileUseCase
import com.example.bytebattlesmobileapp.presentation.viewmodel.LeaderboardViewModel.LeaderboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getUserActivitiesUseCase: GetUserActivitiesUseCase
) : ViewModel() {
    sealed class ProfileUiState {
        data object Loading : ProfileUiState()
        data object Empty : ProfileUiState()
        data class Success(val data: ProfileScreenData) : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
    }
    sealed class ActivitiesUIState {
        data object Loading : ActivitiesUIState()
        data object Empty : ActivitiesUIState()
        data class Success(val data: List<Activities>) : ActivitiesUIState()
        data class Error(val message: String) : ActivitiesUIState()
    }
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiStateActivities = MutableStateFlow<ActivitiesUIState>(ActivitiesUIState.Loading)
    val uiStateActivities: StateFlow<ActivitiesUIState> = _uiStateActivities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadProfileData()
        loadActivities()
    }
    fun loadActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            _uiStateActivities.value = ActivitiesUIState.Loading

            try {
                val activities = getUserActivitiesUseCase()

                if (activities.isEmpty()) {
                    _uiStateActivities.value = ActivitiesUIState.Empty
                } else {
                    _uiStateActivities.value = ActivitiesUIState.Success(activities)
                }
            } catch (e: Exception) {
                Log.e("Leader", e.message.toString())
                _uiStateActivities.value = ActivitiesUIState.Error("Ошибка загрузки активностей: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _uiState.value = ProfileUiState.Loading
            try {
                val profile = getUserProfileUseCase()
              //  val stats = getUserStatsUseCase(profile.id)

                // Для демонстрации - создаем тестовые данные
                val scoreHistory = listOf(
                    ScoreHistory(
                        date = "12.12.2023",
                        eventType = "Турнир",
                        eventName = "Новогодний баттл",
                        points = 150,
                        isPositive = true
                    ),
                    ScoreHistory(
                        date = "10.12.2023",
                        eventType = "Задача",
                        eventName = "Алгоритм поиска",
                        points = 50,
                        isPositive = true
                    ),
                    ScoreHistory(
                        date = "05.12.2023",
                        eventType = "Турнир",
                        eventName = "Осенний марафон",
                        points = -20,
                        isPositive = false
                    )
                )

                val achievements = listOf(
                    Achievement(
                        id = "1",
                        title = "Первая кровь",
                        description = "Победите в первом баттле",
                        iconRes = R.drawable.firstblood,
                        unlocked = true,
                        unlockDate = "01.12.2023"
                    ),
                    Achievement(
                        id = "2",
                        title = "Быстрый ученик",
                        description = "Решите 10 задач за неделю",
                        iconRes = R.drawable.firstblood,
                        unlocked = true,
                        unlockDate = "05.12.2023"
                    ),
                    Achievement(
                        id = "3",
                        title = "Решатель проблем",
                        description = "Решите 50 задач",
                        iconRes = R.drawable.firstblood,
                        unlocked = true,
                        unlockDate = "10.12.2023"
                    ),
                    Achievement(
                        id = "4",
                        title = "Мастер кода",
                        description = "Победите в 10 баттлах",
                        iconRes = R.drawable.firstblood,
                        unlocked = false,
                        unlockDate = null
                    ),
                    Achievement(
                        id = "5",
                        title = "Командный игрок",
                        description = "Примите участие в 5 командных баттлах",
                        iconRes = R.drawable.firstblood,
                        unlocked = false,
                        unlockDate = null
                    ),
                    Achievement(
                        id = "6",
                        title = "Перфекционист",
                        description = "Решите задачу с первой попытки",
                        iconRes = R.drawable.firstblood,
                        unlocked = false,
                        unlockDate = null
                    ),
                    Achievement(
                        id = "7",
                        title = "Инноватор",
                        description = "Создайте собственное решение",
                        iconRes = R.drawable.firstblood,
                        unlocked = false,
                        unlockDate = null
                    ),
                    Achievement(
                        id = "8",
                        title = "Легенда",
                        description = "Займите первое место в рейтинге",
                        iconRes = R.drawable.firstblood,
                        unlocked = false,
                        unlockDate = null
                    )
                )

                val profileData = ProfileScreenData(
                    profile = profile,
                    stats = profile.stats,
                    scoreHistory = scoreHistory,
                    achievements = achievements
                )
                _uiState.value = ProfileUiState.Success(profileData)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile: ${e.message}", e)
                _error.value = "Ошибка загрузки профиля: ${e.message}"
                _uiState.value = ProfileUiState.Error(e.message ?: "Неизвестная ошибка")
            } finally {
                _isLoading.value = false
                Log.d("ProfileViewModel", "Loading finished")
            }
        }
    }

    fun updateProfile(
        userName: String? = null,
        country: String? = null,
        bio: String? = null,
        link: String? = null,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {


                val updatedProfile = updateProfileUseCase(userName, country,link,bio)

                /*// Обновляем состояние с новыми данными
                _uiState.update { currentState ->
                    when (currentState) {
                        is ProfileUiState.Success -> {
                            val updatedData = currentState.data.copy(
                                profile = updatedProfile
                            )
                            ProfileUiState.Success(updatedData)
                        }
                        else -> currentState
                    }
                }*/
            } catch (e: Exception) {
                _error.value = "Ошибка обновления профиля: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }


}


// Вспомогательные классы для экрана статистики
data class ScoreHistory(
    val date: String,
    val eventType: String,
    val eventName: String,
    val points: Int,
    val isPositive: Boolean
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val unlocked: Boolean,
    val unlockDate: String?
)
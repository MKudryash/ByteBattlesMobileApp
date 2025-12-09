package com.example.bytebattlesmobileapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.domain.model.UserLeader
import com.example.bytebattlesmobileapp.domain.usecase.GetLeaderBordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getLeaderBordUseCase: GetLeaderBordUseCase
) : ViewModel() {

    sealed class LeaderboardUiState {
        data object Loading : LeaderboardUiState()
        data object Empty : LeaderboardUiState()
        data class Success(val leaders: List<UserLeader>) : LeaderboardUiState()
        data class Error(val message: String) : LeaderboardUiState()
    }

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 0
    private var hasMore = true

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = LeaderboardUiState.Loading

            try {
                val leaders = getLeaderBordUseCase()

                if (leaders.isEmpty()) {
                    _uiState.value = LeaderboardUiState.Empty
                } else {
                    _uiState.value = LeaderboardUiState.Success(leaders)
                    hasMore = leaders.size == 5 //
                }
            } catch (e: Exception) {
                Log.e("Leader", e.message.toString())
                _uiState.value = LeaderboardUiState.Error("Ошибка загрузки лидерборда: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun refresh() {
        currentPage = 0
        hasMore = true
    }
}
package com.example.bytebattlesmobileapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.domain.model.Language
import com.example.bytebattlesmobileapp.domain.model.Solution
import com.example.bytebattlesmobileapp.domain.model.Task
import com.example.bytebattlesmobileapp.domain.usecase.GetLanguageByIdUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetLanguagesUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetTaskByIdUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetTasksUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetTasksWithPaginationUseCase
import com.example.bytebattlesmobileapp.domain.usecase.SubmitSolutionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.forEachIndexed
import kotlin.collections.isNotEmpty
import kotlin.collections.plus
import kotlin.toString


@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getTasksWithPaginationUseCase: GetTasksWithPaginationUseCase,
    private val getLanguagesUseCase: GetLanguagesUseCase,
    private val getLanguageByIdUseCase: GetLanguageByIdUseCase,
    private val submitSolutionUseCase: SubmitSolutionUseCase,
) : ViewModel() {

    // Состояния загрузки задач
    private val _tasksState = MutableStateFlow<TaskState>(TaskState.Loading)
    val tasksState: StateFlow<TaskState> = _tasksState.asStateFlow()

    private val _selectedLanguageId = MutableStateFlow<String?>(null)
    val selectedLanguageId: StateFlow<String?> = _selectedLanguageId.asStateFlow()
    private val _taskState = MutableStateFlow<TaskDetailState>(TaskDetailState.Loading)
    val taskState: StateFlow<TaskDetailState> = _taskState.asStateFlow()

    private val _languageState = MutableStateFlow<LanguageState>(LanguageState.Loading)
    val languageState: StateFlow<LanguageState> = _languageState.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    private val _languages = MutableStateFlow<List<Language>>(emptyList())
    val languages: StateFlow<List<Language>> = _languages.asStateFlow()



    private val _submitState = MutableStateFlow<SubmitSolutionState>(SubmitSolutionState.Idle)
    val submitState: StateFlow<SubmitSolutionState> = _submitState.asStateFlow()


    // Пагинация
    private val _currentPage = MutableStateFlow(1)
    private val _hasNextPage = MutableStateFlow(true)

    init {
        loadInitialTasks()
        loadInitialLanguages()
    }
    fun selectLanguage(languageId: String?) {
        _selectedLanguageId.value = languageId
        print(languageId)
        print(languageId)
        Log.d("select", "come in")
        Log.d("select", languageId.toString())
        // Локальная фильтрация задач
        if (languageId == null) {
            // Показываем все задачи
            _tasks.value = _allTasks.value
            _tasksState.value = if (_allTasks.value.isEmpty()) {
                TaskState.Empty
            } else {

                TaskState.Success(_allTasks.value)
            }
        } else {
            // Фильтруем задачи по языку

            Log.d("select", languageId.toString())
            val filteredTasks = _allTasks.value.filter { task ->
                // Предполагаем, что у Task есть поле languageId или languages
                Log.d("select",  task.language?.id.toString())
                task.language?.id == languageId
            }

            _tasks.value = filteredTasks
            _tasksState.value = if (filteredTasks.isEmpty()) {
                TaskState.Empty
            } else {
                TaskState.Success(filteredTasks)
            }
        }
    }
    fun submitSolution(
        code: String,
        languageId: String,
        taskId: String
    ) = viewModelScope.launch {
        _submitState.value = SubmitSolutionState.Loading
        try {
            val solution = submitSolutionUseCase(code = code,languageId = languageId, taskId =taskId)
            _submitState.value = SubmitSolutionState.Success(solution)



        } catch (e: Exception) {
            _submitState.value = SubmitSolutionState.Error(
                error = e.message ?: "Неизвестная ошибка при отправке решения"
            )
            Log.e("TaskViewModel", "Error submitting solution: ${e.message}", e)
        }
    }

    fun loadInitialTasks(
        searchTerm: String? = null,
        difficulty: String? = null,
        languageId: String? = null
    ) = viewModelScope.launch {
        _tasksState.value = TaskState.Loading
        try {
            val tasks = getTasksUseCase(
                searchTerm = searchTerm,
                difficulty = difficulty,
                languageId = languageId
            )

            Log.d("TaskViewModel", "Loaded ${tasks.size} tasks from API")

            // Сохраняем все задачи
            _allTasks.value = tasks

            // Применяем текущий фильтр (если есть)
            val tasksToShow = if (_selectedLanguageId.value != null) {
                tasks.filter { task ->
                    task.language?.id == _selectedLanguageId.value ||
                            task.language?.id?.contains(_selectedLanguageId.value!!) == true ||
                            task.language?.id?.contains(_selectedLanguageId.value!!) == true
                }
            } else {
                tasks
            }

            if (tasksToShow.isEmpty()) {
                _tasksState.value = TaskState.Empty
                _tasks.value = emptyList()
                Log.d("TaskViewModel", "No tasks found")
            } else {
                _tasksState.value = TaskState.Success(tasksToShow)
                _tasks.value = tasksToShow
                Log.d("TaskViewModel", "Set ${tasksToShow.size} tasks to StateFlow")
                tasksToShow.forEachIndexed { index, task ->
                    Log.d("TaskViewModel", "Task $index: ${task.title}")
                }
            }
            _currentPage.value = 1
            _hasNextPage.value = tasks.isNotEmpty()
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown error"
            _tasksState.value = TaskState.Error(errorMessage)
            Log.e("TaskViewModel", "Error loading tasks: $errorMessage")
            _tasks.value = emptyList()
            _allTasks.value = emptyList()
        }
    }
    fun loadInitialLanguages(
        searchTerm: String? = null,
        difficulty: String? = null,
        languageId: String? = null
    ) = viewModelScope.launch {
       _languageState.value = LanguageState.Loading
        try {

            val languages = getLanguagesUseCase(
                searchTerm = searchTerm,
                difficulty = difficulty,
                languageId = languageId
            )
            if (languages.isEmpty()) {
                _languageState.value = LanguageState.Empty
                _languages.value = emptyList()
            } else {
                _languageState.value = LanguageState.Success(languages)

                _languages.value = languages
                Log.d("Languages", _languages.value.size.toString())
            }
            _currentPage.value = 1
            _hasNextPage.value = languages.isNotEmpty()
        } catch (e: Exception) {
           // _tasksState.value = TaskState.Error(e.message ?: "Unknown error")
            Log.d("Tasks", e.message.toString())
            _languages.value = emptyList()
            _languageState.value = LanguageState.Error(e.message ?: "Unknown error")
        }
    }

    fun loadMoreTasks(
        searchTerm: String? = null,
        difficulty: String? = null,
        languageId: String? = null
    ) = viewModelScope.launch {
        if (!_hasNextPage.value) return@launch

        val currentState = _tasksState.value
        if (currentState is TaskState.Success) {
            try {
                val nextPage = _currentPage.value + 1
                val newTasks = getTasksWithPaginationUseCase(
                    page = nextPage,
                    pageSize = 10,
                    searchTerm = searchTerm,
                    difficulty = difficulty,
                    languageId = languageId
                )

                if (newTasks.isNotEmpty()) {
                    val updatedTasks = currentState.tasks + newTasks
                    _tasksState.value = TaskState.Success(updatedTasks)
                    _currentPage.value = nextPage
                    _hasNextPage.value = true
                } else {
                    _hasNextPage.value = false
                }
            } catch (e: Exception) {
                // Можно показать сообщение об ошибке или проигнорировать для пагинации
                _hasNextPage.value = false
            }
        }
    }

    fun getTaskById(id: UUID) = viewModelScope.launch {
        _taskState.value = TaskDetailState.Loading
        try {
            val task = getTaskByIdUseCase(id)
            Log.d("TASK", task.toString())
            _taskState.value = TaskDetailState.Success(task)
        } catch (e: Exception) {
            _taskState.value = TaskDetailState.Error(e.message ?: "Failed to load task")
        }
    }

    fun clearTaskDetail() {
        _taskState.value = TaskDetailState.Loading
    }

    fun clearTasks() {
        _tasksState.value = TaskState.Loading
    }
    fun clearSubmitState() {
        _submitState.value = SubmitSolutionState.Idle
    }


    sealed class SubmitSolutionState {
        object Idle : SubmitSolutionState()
        object Loading : SubmitSolutionState()
        data class Success(val solution: Solution) : SubmitSolutionState()
        data class Error(val error: String) : SubmitSolutionState()
    }


    // Sealed классы для состояний
    sealed class TaskState {
        object Loading : TaskState()
        object Empty : TaskState()
        data class Success(val tasks: List<Task>) : TaskState()
        data class Error(val message: String) : TaskState()
    }
    sealed class LanguageState {
        object Loading : LanguageState()
        object Empty : LanguageState()
        data class Success(val tasks: List<Language>) : LanguageState()
        data class Error(val message: String) : LanguageState()
    }

    sealed class TaskDetailState {
        object Loading : TaskDetailState()
        data class Success(val task: Task) : TaskDetailState()
        data class Error(val message: String) : TaskDetailState()
    }
}
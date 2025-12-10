package com.example.bytebattlesmobileapp.domain.usecase


import com.example.bytebattlesmobileapp.data.network.dto.user.UpdateProfileRequest
import com.example.bytebattlesmobileapp.domain.model.*
import com.example.bytebattlesmobileapp.domain.repository.*
import java.util.*

// Auth Use Cases
class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): User {
        return repository.login(email, password)
    }
}

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(firstName: String, lastName:String, email: String, password: String): User {
        return repository.register(firstName,lastName,email,password)
    }
}

class RefreshTokenUseCase(private val repository: AuthRepository){
    suspend operator fun invoke(): String {
        return repository.refreshToken()
    }
}
// Task Use Cases
class GetTasksWithPaginationUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(page:Int=1,pageSize:Int=10,searchTerm:String?,
                                difficulty: String?,languageId: String? ): List<Task> {
        return repository.getTasksWithPagination(page = page, pageSize = pageSize,searchTerm = searchTerm, difficulty = difficulty,languageId = languageId)
    }
}
class GetTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(page:Int=1,pageSize:Int=10,searchTerm:String?,
                                difficulty: String?,languageId: String? ): List<Task> {
        return repository.getTasks(searchTerm = searchTerm, difficulty = difficulty,languageId = languageId)
    }
}

class GetLanguagesUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(page:Int=1,pageSize:Int=10,searchTerm:String?,
                                difficulty: String?,languageId: String? ): List<Language> {
        return repository.getLanguages(searchTerm = searchTerm, difficulty = difficulty,languageId = languageId)
    }
}

class GetTaskByIdUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id:UUID): Task {
        return repository.getTaskById(id)
    }
}
class GetLanguageByIdUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id:UUID): Language {
        return repository.getLanguageById(id)
    }
}



// User Use Cases
class GetUserProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): UserProfile {
        return repository.getProfile()
    }
}

class UpdateProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String? = null, country: String? = null, link: String?, bio: String?): UserProfile {
        return repository.updateProfile(UpdateProfileRequest(username,country,bio,link))
    }
}

class GetUserStatsUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(userId: String): UserStats {
        return repository.getUserStats(userId)
    }
}
class GetLeaderBordUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): List<UserLeader> {
        return repository.getLeaderBord()
    }
}

class GetUserActivitiesUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): List<Activities> {
        return repository.getRecentActivities()
    }
}

//Solution Use Case

class SubmitSolutionUseCase(private val repository: SolutionRepository){
    suspend operator fun invoke(code: String,languageId: String,taskId: String): Solution{
        return repository.submitSolution(code = code,languageId,taskId)
    }
}
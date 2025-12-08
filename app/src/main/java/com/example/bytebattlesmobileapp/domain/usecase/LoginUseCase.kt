package com.example.bytebattlesmobileapp.domain.usecase


import com.example.bytebattlesmobileapp.data.network.dto.auth.Role
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

// Battle Use Cases
class CreateBattleRoomUseCase(private val repository: BattleRepository) {
    suspend operator fun invoke(name: String, languageId: UUID, difficulty: BattleDifficulty): BattleRoom {
        return repository.createBattleRoom(name, languageId, difficulty)
    }
}

class JoinBattleRoomUseCase(private val repository: BattleRepository) {
    suspend operator fun invoke(roomId: UUID): BattleRoom {
        return repository.joinBattleRoom(roomId)
    }
}

class ToggleReadyStatusUseCase(private val repository: BattleRepository) {
    suspend operator fun invoke(roomId: UUID, isReady: Boolean) {
        repository.toggleReadyStatus(roomId, isReady)
    }
}

class SubmitBattleCodeUseCase(private val repository: BattleRepository) {
    suspend operator fun invoke(roomId: UUID, taskId: UUID, code: String, languageId: UUID): CodeSubmission {
        return repository.submitBattleCode(roomId, taskId, code, languageId)
    }
}

// Task Use Cases
class GetTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 10): List<Task> {
        return repository.getTasks(page, pageSize)
    }
}

/*class SubmitSolutionUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: UUID, code: String, languageId: UUID): CodeSubmission {
        return repository.submitSolution(taskId, code, languageId)
    }
}*/

// User Use Cases
class GetUserProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): User {
        return repository.getUserProfile()
    }
}

class UpdateProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String? = null, email: String? = null): User {
        return repository.updateProfile(username, email)
    }
}
package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.solution.SolutionDto
import com.example.bytebattlesmobileapp.data.network.dto.solution.SubmitSolutionDto

interface SolutionApiService {
    suspend fun submitSolution(request: SubmitSolutionDto): SolutionDto
}
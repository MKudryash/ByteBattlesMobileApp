package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.domain.model.Solution

interface SolutionRepository{
    suspend fun submitSolution(code: String,languageId: String,taskId: String): Solution
}
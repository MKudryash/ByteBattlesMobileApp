package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.solution.SolutionDto
import com.example.bytebattlesmobileapp.data.network.dto.solution.SubmitSolutionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SolutionApiServiceImpl(private val client: HttpClient) : SolutionApiService {
    override suspend fun submitSolution(request: SubmitSolutionDto): SolutionDto {
        return client.post("solution/test") {
            setBody(request
            )
        }.body()
    }

}
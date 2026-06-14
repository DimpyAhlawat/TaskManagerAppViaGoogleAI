package com.example.data.network

import com.example.data.network.dto.TodoResponse
import retrofit2.Response
import retrofit2.http.GET

interface TaskApi {
    @GET("todos")
    suspend fun getTodos(): Response<TodoResponse>
}

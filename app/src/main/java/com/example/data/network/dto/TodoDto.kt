package com.example.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TodoDto(
    @Json(name = "id") val id: Int,
    @Json(name = "todo") val todo: String,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "userId") val userId: Int
)

@JsonClass(generateAdapter = true)
data class TodoResponse(
    @Json(name = "todos") val todos: List<TodoDto>,
    @Json(name = "total") val total: Int,
)

package com.example.loginsignupapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class ChatRequest(val prompt: String)
data class ChatResponse(val reply: String)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("chat") // Replace with your AI assistant API endpoint
    fun sendMessage(@Body request: ChatRequest): Call<ChatResponse>
}

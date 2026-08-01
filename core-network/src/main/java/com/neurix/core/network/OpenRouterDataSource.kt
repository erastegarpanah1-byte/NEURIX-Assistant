package com.neurix.core.network

import com.neurix.core.common.Result
import com.neurix.core.network.model.Message
import com.neurix.core.network.model.OpenRouterRequest
import com.neurix.core.network.model.OpenRouterResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenRouterDataSource @Inject constructor(
    private val api: OpenRouterApi,
    private val authProvider: OpenRouterAuthProvider
) {
    suspend fun sendMessage(
        model: String,
        messages: List<Message>
    ): Result<OpenRouterResponse> {
        return try {
            val request = OpenRouterRequest(
                model = model,
                messages = messages,
                stream = false
            )
            val response: Response<OpenRouterResponse> = api.sendMessage(
                auth = authProvider.authHeader(),
                request = request
            )
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    message = "API error: ${response.code()} ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Result.Error(message = e.message ?: "Unknown error", exception = e)
        }
    }
}
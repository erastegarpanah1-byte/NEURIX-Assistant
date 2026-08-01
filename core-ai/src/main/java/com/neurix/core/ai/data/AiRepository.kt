package com.neurix.core.ai.data

import com.neurix.core.ai.domain.model.ChatMessage
import com.neurix.core.ai.domain.model.MessageRole
import com.neurix.core.common.Result
import com.neurix.core.network.OpenRouterDataSource
import com.neurix.core.network.model.Message
import com.neurix.core.network.model.OpenRouterResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val dataSource: OpenRouterDataSource
) {
    suspend fun sendMessage(
        model: String,
        messages: List<ChatMessage>,
        newMessage: String
    ): Result<String> {
        val apiMessages = messages.map { msg ->
            Message(role = msg.role.toApiString(), content = msg.content)
        } + Message(role = "user", content = newMessage)

        return when (val result = dataSource.sendMessage(model, apiMessages)) {
            is Result.Success -> {
                val response: OpenRouterResponse = result.data
                val content = response.choices
                    ?.firstOrNull()
                    ?.message
                    ?.content
                    ?: ""
                Result.Success(content)
            }
            is Result.Error -> {
                val msg = result.exception?.message ?: "Unknown error"
                Result.Error(msg, result.exception)
            }
        }
    }
}
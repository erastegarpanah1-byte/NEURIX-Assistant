package com.neurix.core.ai.domain.model

data class Conversation(
    val id: String,
    val title: String,
    val model: String,
    val createdAt: Long,
    val updatedAt: Long,
    val messages: List<ChatMessage> = emptyList()
)

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val model: String,
    val timestamp: Long
)

enum class MessageRole {
    USER, ASSISTANT;

    fun toApiString(): String = when (this) {
        USER -> "user"
        ASSISTANT -> "assistant"
    }

    companion object {
        fun fromApiString(value: String): MessageRole = when (value) {
            "user" -> USER
            "assistant" -> ASSISTANT
            else -> USER
        }
    }
}
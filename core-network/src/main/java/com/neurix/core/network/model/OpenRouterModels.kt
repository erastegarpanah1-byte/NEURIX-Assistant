package com.neurix.core.network.model

data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1024
)

data class Message(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val id: String?,
    val model: String?,
    val choices: List<Choice>?
)

data class Choice(
    val index: Int?,
    val message: Message?,
    val delta: Delta?,
    val finish_reason: String?
)

data class Delta(
    val role: String?,
    val content: String?
)

data class StreamDelta(
    val id: String?,
    val model: String?,
    val choices: List<Choice>?
)

data class OpenRouterModel(
    val id: String,
    val name: String
)

data class OpenRouterModelsResponse(
    val data: List<OpenRouterModel>
)
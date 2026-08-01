package com.neurix.feature.assistant.presentation

import com.neurix.core.ai.domain.model.ChatMessage
import com.neurix.core.ai.domain.model.Conversation
import com.neurix.core.common.MviState
import com.neurix.core.common.MviIntent
import com.neurix.core.common.MviEffect

data class AssistantState(
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val isThinking: Boolean = false,
    val recognizedText: String = "",
    val aiResponse: String = "",
    val conversations: List<Conversation> = emptyList(),
    val currentConversationId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val error: String? = null
) : MviState

sealed interface AssistantIntent : MviIntent {
    data object StartListening : AssistantIntent
    data object StopListening : AssistantIntent
    data class OnSpeechResult(val text: String) : AssistantIntent
    data class OnSpeechError(val error: String) : AssistantIntent
    data object DismissError : AssistantIntent
    data class DeleteConversation(val id: String) : AssistantIntent
    data class SelectConversation(val id: String) : AssistantIntent
}

sealed interface AssistantEffect : MviEffect {
    data class Speak(val text: String) : AssistantEffect
}
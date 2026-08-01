package com.neurix.feature.chat.presentation
import com.neurix.core.common.MviEffect
import com.neurix.core.common.MviIntent
import com.neurix.core.common.MviState
data class ChatState(val messages: List<ChatMessage> = fakeMessages,val isTyping: Boolean = false,val isLoading: Boolean = false,val isListening: Boolean = false,val inputText: String = "",val conversations: List<ConversationSummary> = emptyList(),val currentConversationId: String? = null,val error: String? = null,val isOnline: Boolean = true,val showNetworkError: Boolean = false) : MviState
data class ChatMessage(val id: String,val text: String,val isUser: Boolean,val timestamp: String = "")
data class ConversationSummary(val id: String,val title: String,val updatedAt: Long)
sealed interface ChatIntent : MviIntent {data class UpdateInput(val text: String) : ChatIntent;data object SendMessage : ChatIntent;data object TapMicrophone : ChatIntent;data class OnSpeechResult(val text: String) : ChatIntent;data object DismissError : ChatIntent;data class SelectConversation(val id: String) : ChatIntent;data class DeleteConversation(val id: String) : ChatIntent;data object NewConversation : ChatIntent;data object DismissNetworkError : ChatIntent}
sealed interface ChatEffect : MviEffect {data object None : ChatEffect}
internal val fakeMessages = listOf(ChatMessage("1","Hello! I'm Neurix. How can I help you today?",false,"10:30 AM"),ChatMessage("2","What can you do for me?",true,"10:31 AM"),ChatMessage("3","I can chat, answer questions, help with code, research, and much more!",false,"10:31 AM"),ChatMessage("4","That's amazing! Let's go.",true,"10:32 AM"),ChatMessage("5","I'm ready! Just ask me anything.",false,"10:32 AM"))

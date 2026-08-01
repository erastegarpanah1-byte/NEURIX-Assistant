package com.neurix.feature.chat.presentation

import androidx.lifecycle.viewModelScope
import com.neurix.core.actions.ActionEngine
import com.neurix.core.ai.domain.CreateConversationUseCase
import com.neurix.core.ai.domain.DeleteConversationUseCase
import com.neurix.core.ai.domain.GetConversationsUseCase
import com.neurix.core.ai.domain.GetMessagesUseCase
import com.neurix.core.ai.domain.SaveAssistantResponseUseCase
import com.neurix.core.ai.domain.SendMessageUseCase
import com.neurix.core.ai.domain.model.ChatMessage as DomainChatMessage
import com.neurix.core.ai.domain.model.MessageRole
import com.neurix.core.common.BaseViewModel
import com.neurix.core.common.Result
import com.neurix.core.speech.SpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveAssistantResponseUseCase: SaveAssistantResponseUseCase,
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase,
    private val actionEngine: ActionEngine,
    private val speechManager: SpeechManager
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(
    ChatState()
) {

    companion object {
        private const val DEFAULT_MODEL = "openai/gpt-4o-mini"
    }

    init {
        viewModelScope.launch {
            getConversationsUseCase().collect { conversations ->
                setState {
                    copy(conversations = conversations.map { conv ->
                        ConversationSummary(
                            id = conv.id,
                            title = conv.title,
                            updatedAt = conv.updatedAt
                        )
                    })
                }
            }
        }
    }

    override fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.UpdateInput -> setState { copy(inputText = intent.text) }
            ChatIntent.SendMessage -> sendMessage()
            ChatIntent.TapMicrophone -> startVoiceInput()
            is ChatIntent.OnSpeechResult -> onSpeechResult(intent.text)
            ChatIntent.DismissError -> setState { copy(error = null) }
            is ChatIntent.SelectConversation -> selectConversation(intent.id)
            is ChatIntent.DeleteConversation -> deleteConversation(intent.id)
            is ChatIntent.NewConversation -> setState {
                copy(currentConversationId = null, messages = fakeMessages, inputText = "")
            }
        }
    }

    private fun sendMessage() {
        val text = state.value.inputText.trim()
        if (text.isEmpty()) return

        setState { copy(inputText = "", isLoading = true, error = null) }

        viewModelScope.launch {
            val conversationId = state.value.currentConversationId
                ?: createNewConversation(text) ?: return@launch

            val result = actionEngine.processUserMessage(
                userMessage = text,
                model = DEFAULT_MODEL,
                history = state.value.messages.map { msg ->
                    DomainChatMessage(
                        id = msg.id,
                        role = if (msg.isUser) MessageRole.USER else MessageRole.ASSISTANT,
                        content = msg.text,
                        model = DEFAULT_MODEL,
                        timestamp = 0
                    )
                }
            )

            saveAssistantResponseUseCase(
                conversationId = conversationId,
                content = result.response,
                model = DEFAULT_MODEL
            )

            val currentMessages = state.value.messages.toMutableList()
            currentMessages.add(
                ChatMessage(
                    id = "user_${System.currentTimeMillis()}",
                    text = text,
                    isUser = true,
                    timestamp = formatTimestamp(System.currentTimeMillis())
                )
            )
            currentMessages.add(
                ChatMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    text = result.response,
                    isUser = false,
                    timestamp = formatTimestamp(System.currentTimeMillis())
                )
            )

            setState {
                copy(isLoading = false, messages = currentMessages)
            }
        }
    }

    private suspend fun createNewConversation(firstMessage: String): String? {
        val title = if (firstMessage.length > 30) firstMessage.take(30) + "..." else firstMessage
        return when (val result = createConversationUseCase(title, DEFAULT_MODEL)) {
            is Result.Success -> {
                val id = result.data.id
                setState { copy(currentConversationId = id) }
                listenToMessages(id)
                id
            }
            is Result.Error -> {
                setState { copy(error = result.exception?.message ?: "Unknown error", isLoading = false) }
                null
            }
        }
    }

    private fun listenToMessages(conversationId: String) {
        viewModelScope.launch {
            getMessagesUseCase(conversationId).collect { domainMessages ->
                setState {
                    copy(
                        messages = domainMessages.map { msg ->
                            ChatMessage(
                                id = msg.id,
                                text = msg.content,
                                isUser = msg.role == MessageRole.USER,
                                timestamp = formatTimestamp(msg.timestamp)
                            )
                        }
                    )
                }
            }
        }
    }

    private fun startVoiceInput() {
        setState { copy(isListening = true) }
        speechManager.startListening(
            onResult = { text -> handleIntent(ChatIntent.OnSpeechResult(text)) },
            onError = { error -> setState { copy(error = error, isListening = false) } }
        )
    }

    private fun onSpeechResult(text: String) {
        setState { copy(isListening = false, inputText = text) }
        handleIntent(ChatIntent.SendMessage)
    }

    private fun selectConversation(id: String) {
        setState { copy(currentConversationId = id) }
        listenToMessages(id)
    }

    private fun deleteConversation(id: String) {
        viewModelScope.launch {
            deleteConversationUseCase(id)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        if (timestamp == 0L) return ""
        val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}
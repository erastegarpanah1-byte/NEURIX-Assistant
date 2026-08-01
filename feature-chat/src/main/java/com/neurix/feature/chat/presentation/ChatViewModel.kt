package com.neurix.feature.chat.presentation

import androidx.lifecycle.viewModelScope
import com.neurix.core.actions.ActionEngine
import com.neurix.core.ai.domain.CreateConversationUseCase
import com.neurix.core.ai.domain.DeleteConversationUseCase
import com.neurix.core.ai.domain.GetConversationsUseCase
import com.neurix.core.ai.domain.GetMessagesUseCase
import com.neurix.core.ai.domain.SaveAssistantResponseUseCase
import com.neurix.core.ai.domain.SendMessageUseCase
import com.neurix.core.ai.domain.model.ChatMessage
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
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(ChatState()) {

    companion object { private const val DEFAULT_MODEL = "openai/gpt-4o-mini" }

    init {
        viewModelScope.launch {
            getConversationsUseCase().collect { conversations ->
                setState { copy(conversations = conversations.map { conv -> ConversationSummary(conv.id, conv.title, conv.updatedAt) }) }
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
            is ChatIntent.NewConversation -> setState { copy(currentConversationId = null, messages = ChatContract.fakeMessages, inputText = "") }
        }
    }

    private fun sendMessage() {
        val text = state.value.inputText.trim()
        if (text.isEmpty()) return
        setState { copy(inputText = "", isLoading = true, error = null) }
        viewModelScope.launch {
            val conversationId = state.value.currentConversationId ?: createNewConversation(text) ?: return@launch
            val result = actionEngine.processUserMessage(text, DEFAULT_MODEL, state.value.messages.map { msg ->
                ChatMessage(msg.id, if (msg.isUser) MessageRole.USER else MessageRole.ASSISTANT, msg.text, DEFAULT_MODEL, 0)
            })
            saveAssistantResponseUseCase(conversationId, result.response, DEFAULT_MODEL)
            val msgs = state.value.messages.toMutableList()
            msgs.add(ChatMessage("u_${System.currentTimeMillis()}", MessageRole.USER, text, DEFAULT_MODEL, System.currentTimeMillis()))
            msgs.add(ChatMessage("a_${System.currentTimeMillis()}", MessageRole.ASSISTANT, result.response, DEFAULT_MODEL, System.currentTimeMillis()))
            setState { copy(isLoading = false, messages = msgs.map { ChatMessage(it.id, it.content, it.role == MessageRole.USER, fmt(it.timestamp)) }) }
        }
    }

    private suspend fun createNewConversation(msg: String): String? {
        val t = if (msg.length > 30) msg.take(30) + "..." else msg
        return when (val r = createConversationUseCase(t, DEFAULT_MODEL)) {
            is Result.Success -> { setState { copy(currentConversationId = r.data.id) }; listen(r.data.id); r.data.id }
            is Result.Error -> { setState { copy(error = r.exception.message, isLoading = false) }; null }
        }
    }

    private fun listen(id: String) {
        viewModelScope.launch {
            getMessagesUseCase(id).collect { msgs ->
                setState { copy(messages = msgs.map { ChatMessage(it.id, it.content, it.role == MessageRole.USER, fmt(it.timestamp)) }) }
            }
        }
    }

    private fun startVoiceInput() {
        setState { copy(isListening = true) }
        speechManager.startListening({ text -> handleIntent(ChatIntent.OnSpeechResult(text)) }, { e -> setState { copy(error = e, isListening = false) } })
    }

    private fun onSpeechResult(text: String) { setState { copy(isListening = false, inputText = text) }; handleIntent(ChatIntent.SendMessage) }
    private fun selectConversation(id: String) { setState { copy(currentConversationId = id) }; listen(id) }
    private fun deleteConversation(id: String) { viewModelScope.launch { deleteConversationUseCase(id) } }
    private fun fmt(t: Long): String = if (t == 0L) "" else java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(t))
}
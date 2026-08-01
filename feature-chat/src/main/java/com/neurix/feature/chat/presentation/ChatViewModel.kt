package com.neurix.feature.chat.presentation

import androidx.lifecycle.viewModelScope
import com.neurix.core.actions.ActionEngine
import com.neurix.core.ai.domain.CreateConversationUseCase
import com.neurix.core.ai.domain.DeleteConversationUseCase
import com.neurix.core.ai.domain.GetConversationsUseCase
import com.neurix.core.ai.domain.GetMessagesUseCase
import com.neurix.core.ai.domain.SaveAssistantResponseUseCase
import com.neurix.core.ai.domain.SendMessageUseCase
import com.neurix.core.ai.domain.model.ChatMessage as Dm
import com.neurix.core.ai.domain.model.MessageRole
import com.neurix.core.common.BaseViewModel
import com.neurix.core.common.NetworkMonitor
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
    private val speechManager: SpeechManager,
    private val networkMonitor: NetworkMonitor
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(ChatState()) {

    companion object { private const val MODEL = "openai/gpt-4o-mini" }

    init {
        viewModelScope.launch {
            networkMonitor.observeNetwork().collect { setState { copy(isOnline = it) } }
        }
        viewModelScope.launch {
            getConversationsUseCase().collect { convs ->
                setState { copy(conversations = convs.map { ConversationSummary(it.id, it.title, it.updatedAt) }) }
            }
        }
    }

    override fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.UpdateInput -> setState { copy(inputText = intent.text) }
            ChatIntent.SendMessage -> sendMessage()
            ChatIntent.TapMicrophone -> {
                if (!state.value.isOnline) { setState { copy(showNetworkError = true) }; return }
                startVoiceInput()
            }
            is ChatIntent.OnSpeechResult -> onSpeechResult(intent.text)
            ChatIntent.DismissError -> setState { copy(error = null) }
            is ChatIntent.SelectConversation -> selectConversation(intent.id)
            is ChatIntent.DeleteConversation -> deleteConversation(intent.id)
            is ChatIntent.NewConversation -> setState { copy(currentConversationId = null, messages = fakeMessages, inputText = "") }
            ChatIntent.DismissNetworkError -> setState { copy(showNetworkError = false) }
        }
    }

    private fun sendMessage() {
        val text = state.value.inputText.trim()
        if (text.isEmpty()) return
        if (!state.value.isOnline) { setState { copy(showNetworkError = true) }; return }
        setState { copy(inputText = "", isLoading = true, error = null) }
        viewModelScope.launch {
            val cid = state.value.currentConversationId ?: createNewConversation(text) ?: return@launch
            val result = actionEngine.processUserMessage(text, MODEL, state.value.messages.map {
                Dm(it.id, if (it.isUser) MessageRole.USER else MessageRole.ASSISTANT, it.text, MODEL, 0)
            })
            saveAssistantResponseUseCase(cid, result.response, MODEL)
            val msgs = state.value.messages.toMutableList()
            msgs.add(ChatMessage("u_${System.currentTimeMillis()}", text, true, fmtTime(System.currentTimeMillis())))
            msgs.add(ChatMessage("a_${System.currentTimeMillis()}", result.response, false, fmtTime(System.currentTimeMillis())))
            setState { copy(isLoading = false, messages = msgs) }
        }
    }

    private suspend fun createNewConversation(msg: String): String? {
        val t = if (msg.length > 30) msg.take(30) + "..." else msg
        return when (val r = createConversationUseCase(t, MODEL)) {
            is Result.Success -> { val id = r.data.id; setState { copy(currentConversationId = id) }; listenToMessages(id); id }
            is Result.Error -> { setState { copy(error = r.exception?.message ?: "Error", isLoading = false) }; null }
        }
    }

    private fun listenToMessages(cid: String) {
        viewModelScope.launch {
            getMessagesUseCase(cid).collect { ms ->
                setState { copy(messages = ms.map { ChatMessage(it.id, it.content, it.role == MessageRole.USER, fmtTime(it.timestamp)) }) }
            }
        }
    }

    private fun startVoiceInput() {
        setState { copy(isListening = true) }
        speechManager.startListening(
            onResult = { handleIntent(ChatIntent.OnSpeechResult(it)) },
            onError = { setState { copy(error = it, isListening = false) } }
        )
    }

    private fun onSpeechResult(text: String) {
        setState { copy(isListening = false, inputText = text) }
        handleIntent(ChatIntent.SendMessage)
    }

    private fun selectConversation(id: String) { setState { copy(currentConversationId = id) }; listenToMessages(id) }
    private fun deleteConversation(id: String) { viewModelScope.launch { deleteConversationUseCase(id) } }

    private fun fmtTime(ts: Long): String {
        if (ts == 0L) return ""
        return java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(ts))
    }
}

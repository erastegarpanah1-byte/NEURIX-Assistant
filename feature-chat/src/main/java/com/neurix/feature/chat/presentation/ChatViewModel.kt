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
        viewModelScope.launch { networkMonitor.observeNetwork().collect { setState { copy(isOnline = it) } } }
        viewModelScope.launch { getConversationsUseCase().collect { setState { copy(conversations = it.map { ConversationSummary(it.id, it.title, it.updatedAt) }) } } }
    }
    override fun handleIntent(intent: ChatIntent) = when(intent) {
        is ChatIntent.UpdateInput -> setState { copy(inputText = intent.text) }
        ChatIntent.SendMessage -> send()
        ChatIntent.TapMicrophone -> { if(!state.value.isOnline) { setState { copy(showNetworkError = true) }; return }; voice() }
        is ChatIntent.OnSpeechResult -> speech(intent.text)
        ChatIntent.DismissError -> setState { copy(error = null) }
        is ChatIntent.SelectConversation -> sel(intent.id)
        is ChatIntent.DeleteConversation -> del(intent.id)
        ChatIntent.NewConversation -> setState { copy(currentConversationId = null, messages = fakeMessages, inputText = "") }
        ChatIntent.DismissNetworkError -> setState { copy(showNetworkError = false) }
    }
    private fun send() {
        val t = state.value.inputText.trim(); if(t.isEmpty()) return
        if(!state.value.isOnline) { setState { copy(showNetworkError = true) }; return }
        setState { copy(inputText = "", isLoading = true, error = null) }
        viewModelScope.launch {
            val c = state.value.currentConversationId ?: cr(t) ?: return@launch
            val r = actionEngine.processUserMessage(t, MODEL, state.value.messages.map { Dm(it.id, if(it.isUser) MessageRole.USER else MessageRole.ASSISTANT, it.text, MODEL, 0) })
            saveAssistantResponseUseCase(c, r.response, MODEL)
            val ms = state.value.messages.toMutableList(); ms.add(ChatMessage("u_${System.currentTimeMillis()}", t, true, fmt(System.currentTimeMillis()))); ms.add(ChatMessage("a_${System.currentTimeMillis()}", r.response, false, fmt(System.currentTimeMillis())))
            setState { copy(isLoading = false, messages = ms) }
        }
    }
    private suspend fun cr(msg: String): String? {
        val t = if(msg.length > 30) msg.take(30)+"..." else msg
        return when(val r = createConversationUseCase(t, MODEL)) { is Result.Success -> { val id = r.data.id; setState { copy(currentConversationId = id) }; ls(id); id }; is Result.Error -> { setState { copy(error = r.exception?.message ?: "Error", isLoading = false) }; null } }
    }
    private fun ls(cid: String) { viewModelScope.launch { getMessagesUseCase(cid).collect { setState { copy(messages = it.map { ChatMessage(it.id, it.content, it.role == MessageRole.USER, fmt(it.timestamp)) }) } } } }
    private fun voice() { setState { copy(isListening = true) }; speechManager.startListening({ handleIntent(ChatIntent.OnSpeechResult(it)) }, { setState { copy(error = it, isListening = false) } }) }
    private fun speech(t: String) { setState { copy(isListening = false, inputText = t) }; handleIntent(ChatIntent.SendMessage) }
    private fun sel(id: String) { setState { copy(currentConversationId = id) }; ls(id) }
    private fun del(id: String) { viewModelScope.launch { deleteConversationUseCase(id) } }
    private fun fmt(ts: Long) = if(ts == 0L) "" else java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(ts))
}

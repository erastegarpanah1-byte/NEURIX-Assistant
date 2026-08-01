package com.neurix.feature.assistant.presentation

import androidx.lifecycle.viewModelScope
import com.neurix.core.actions.ActionEngine
import com.neurix.core.ai.domain.CreateConversationUseCase
import com.neurix.core.ai.domain.DeleteConversationUseCase
import com.neurix.core.ai.domain.GetConversationsUseCase
import com.neurix.core.ai.domain.GetMessagesUseCase
import com.neurix.core.ai.domain.SaveAssistantResponseUseCase
import com.neurix.core.common.BaseViewModel
import com.neurix.core.common.Result
import com.neurix.core.speech.SpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val speechManager: SpeechManager,
    private val actionEngine: ActionEngine,
    private val saveAssistantResponseUseCase: SaveAssistantResponseUseCase,
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase
) : BaseViewModel<AssistantState, AssistantIntent, AssistantEffect>(AssistantState()) {

    companion object { private const val DEFAULT_MODEL = "openai/gpt-4o-mini" }

    init {
        viewModelScope.launch {
            getConversationsUseCase().collect { c -> setState { copy(conversations = c) } }
        }
    }

    override fun handleIntent(intent: AssistantIntent) {
        when (intent) {
            AssistantIntent.StartListening -> startListening()
            AssistantIntent.StopListening -> speechManager.stopListening().also { setState { copy(isListening = false) } }
            is AssistantIntent.OnSpeechResult -> onSpeechResult(intent.text)
            is AssistantIntent.OnSpeechError -> setState { copy(error = intent.error) }
            AssistantIntent.DismissError -> setState { copy(error = null) }
            is AssistantIntent.DeleteConversation -> viewModelScope.launch { deleteConversationUseCase(intent.id) }
            is AssistantIntent.SelectConversation -> { setState { copy(currentConversationId = intent.id) }; listen(intent.id) }
        }
    }

    private fun startListening() {
        setState { copy(isListening = true, error = null) }
        speechManager.startListening({ text -> handleIntent(AssistantIntent.OnSpeechResult(text)) }, { e -> handleIntent(AssistantIntent.OnSpeechError(e)) })
    }

    private fun onSpeechResult(text: String) {
        setState { copy(isListening = false, recognizedText = text, isThinking = true, error = null) }
        viewModelScope.launch {
            val cid = state.value.currentConversationId ?: createConv(text) ?: return@launch
            val result = actionEngine.processUserMessage(text, DEFAULT_MODEL, state.value.messages)
            saveAssistantResponseUseCase(cid, result.response, DEFAULT_MODEL)
            setState { copy(isThinking = false, aiResponse = result.response, recognizedText = "") }
            if (result.actionPerformed) sendEffect(AssistantEffect.Speak(result.response))
        }
    }

    private suspend fun createConv(msg: String): String? {
        val t = if (msg.length > 30) msg.take(30) + "..." else msg
        return when (val r = createConversationUseCase(t, DEFAULT_MODEL)) {
            is Result.Success -> { setState { copy(currentConversationId = r.data.id) }; listen(r.data.id); r.data.id }
            is Result.Error -> { setState { copy(error = r.exception.message, isThinking = false) }; null }
        }
    }

    private fun listen(id: String) {
        viewModelScope.launch { getMessagesUseCase(id).collect { m -> setState { copy(messages = m) } } }
    }
}
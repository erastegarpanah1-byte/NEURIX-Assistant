package com.neurix.feature.assistant.presentation

import androidx.lifecycle.viewModelScope
import com.neurix.core.actions.ActionEngine
import com.neurix.core.actions.ActionEngineResult
import com.neurix.core.ai.domain.CreateConversationUseCase
import com.neurix.core.ai.domain.DeleteConversationUseCase
import com.neurix.core.ai.domain.GetConversationsUseCase
import com.neurix.core.ai.domain.GetMessagesUseCase
import com.neurix.core.ai.domain.SaveAssistantResponseUseCase
import com.neurix.core.ai.domain.SendMessageUseCase
import com.neurix.core.ai.domain.model.MessageRole
import com.neurix.core.common.BaseViewModel
import com.neurix.core.common.Result
import com.neurix.core.speech.SpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveAssistantResponseUseCase: SaveAssistantResponseUseCase,
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase,
    private val actionEngine: ActionEngine,
    private val speechManager: SpeechManager
) : BaseViewModel<AssistantState, AssistantIntent, AssistantEffect>(
    AssistantState()
) {

    companion object {
        private const val DEFAULT_MODEL = "openai/gpt-4o-mini"
    }

    init {
        viewModelScope.launch {
            getConversationsUseCase().collect { conversations ->
                setState { copy(conversations = conversations) }
            }
        }
    }

    override fun handleIntent(intent: AssistantIntent) {
        when (intent) {
            AssistantIntent.DismissError -> setState { copy(error = null) }
            is AssistantIntent.OnSpeechError -> {
                setState { copy(isListening = false, error = intent.error) }
            }
            AssistantIntent.StartListening -> startListening()
            AssistantIntent.StopListening -> stopListening()
            is AssistantIntent.OnSpeechResult -> onSpeechResult(intent.text)
            is AssistantIntent.DeleteConversation -> deleteConversation(intent.id)
            is AssistantIntent.SelectConversation -> selectConversation(intent.id)
        }
    }

    private fun startListening() {
        setState { copy(isListening = true, error = null) }
        speechManager.startListening(
            onResult = { text ->
                handleIntent(AssistantIntent.OnSpeechResult(text))
            },
            onError = { error ->
                handleIntent(AssistantIntent.OnSpeechError(error))
            }
        )
    }

    private fun stopListening() {
        speechManager.stopListening()
        setState { copy(isListening = false) }
    }

    private fun onSpeechResult(text: String) {
        setState {
            copy(
                isListening = false,
                recognizedText = text,
                isThinking = true,
                error = null
            )
        }
        processUserMessage(text)
    }

    private fun processUserMessage(userMessage: String) {
        viewModelScope.launch {
            val conversationId = state.value.currentConversationId
                ?: createNewConversation(userMessage)
                    ?: return@launch

            val result: ActionEngineResult = actionEngine.processUserMessage(
                userMessage = userMessage,
                model = DEFAULT_MODEL,
                history = state.value.messages
            )

            saveAssistantResponseUseCase(
                conversationId = conversationId,
                content = result.response,
                model = DEFAULT_MODEL
            )

            setState {
                copy(
                    isThinking = false,
                    aiResponse = result.response,
                    recognizedText = ""
                )
            }

            if (result.actionPerformed) {
                sendEffect(AssistantEffect.Speak(result.response))
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
                setState { copy(error = result.exception?.message ?: "Unknown error", isThinking = false) }
                null
            }
        }
    }

    private fun listenToMessages(conversationId: String) {
        viewModelScope.launch {
            getMessagesUseCase(conversationId).collect { messages ->
                setState { copy(messages = messages) }
            }
        }
    }

    private fun deleteConversation(id: String) {
        viewModelScope.launch {
            deleteConversationUseCase(id)
        }
    }

    private fun selectConversation(id: String) {
        setState { copy(currentConversationId = id) }
        listenToMessages(id)
    }
}

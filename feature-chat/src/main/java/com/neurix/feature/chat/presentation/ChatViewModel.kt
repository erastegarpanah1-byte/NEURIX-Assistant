package com.neurix.feature.chat.presentation
import androidx.lifecycle.viewModelScope
import com.neurix.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : BaseViewModel<ChatState, ChatIntent, ChatEffect>(ChatState()) {
    override fun handleIntent(intent: ChatIntent) = when(intent) {
        is ChatIntent.UpdateInput -> setState { copy(inputText = intent.text) }
        ChatIntent.SendMessage -> send()
        ChatIntent.TapMicrophone -> {}
        is ChatIntent.OnSpeechResult -> {}
        ChatIntent.DismissError -> setState { copy(error = null) }
        is ChatIntent.SelectConversation -> {}
        is ChatIntent.DeleteConversation -> {}
        is ChatIntent.NewConversation -> setState { copy(currentConversationId = null, messages = fakeMessages, inputText = "") }
        ChatIntent.DismissNetworkError -> setState { copy(showNetworkError = false) }
    }
    private fun send() {
        val t = state.value.inputText.trim(); if(t.isEmpty()) return
        setState { copy(inputText = "", isLoading = true, error = null) }
        viewModelScope.launch {
            val ms = state.value.messages.toMutableList()
            ms.add(ChatMessage("u_${System.currentTimeMillis()}", t, true, fmt(System.currentTimeMillis())))
            ms.add(ChatMessage("a_${System.currentTimeMillis()}", mock(t), false, fmt(System.currentTimeMillis())))
            setState { copy(isLoading = false, messages = ms) }
        }
    }
    private fun mock(input: String) = when {
        input.contains("hello", true) || input.contains("hi", true) || input.contains("salam", true) -> "Hey! I'm Neurix. How can I help you today? \uD83D\uDE80"
        input.contains("how are you", true) || input.contains("chetori", true) -> "I'm great! Ready to help with anything."
        input.contains("what can you do", true) || input.contains("chi kar mikoni", true) -> "I can chat, answer questions, write code, translate, research, and more. Just ask! \uD83D\uDE0A"
        input.contains("bye", true) || input.contains("khodahafez", true) -> "Goodbye! Have a great day! \uD83D\uDC4B"
        else -> "That's interesting! I'm Neurix — your AI assistant. I can help with coding, answers, writing, and more. What else would you like to explore? \u2728"
    }
    private fun fmt(ts: Long) = if(ts == 0L) "" else java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(ts))
}

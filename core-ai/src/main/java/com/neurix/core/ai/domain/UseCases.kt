package com.neurix.core.ai.domain

import com.neurix.core.ai.data.AiRepository
import com.neurix.core.ai.data.ChatRepository
import com.neurix.core.ai.domain.model.ChatMessage
import com.neurix.core.ai.domain.model.Conversation
import com.neurix.core.ai.domain.model.MessageRole
import com.neurix.core.common.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        model: String,
        history: List<ChatMessage>,
        message: String
    ): Result<String> {
        chatRepository.saveMessage(
            conversationId = conversationId,
            role = MessageRole.USER,
            content = message,
            model = model
        )
        return aiRepository.sendMessage(model, history, message)
    }
}

class GetConversationsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<Conversation>> = chatRepository.getAllConversations()
}

class GetMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(conversationId: String): Flow<List<ChatMessage>> =
        chatRepository.getMessagesForConversation(conversationId)
}

class SaveAssistantResponseUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        model: String
    ): Result<ChatMessage> {
        return chatRepository.saveMessage(
            conversationId = conversationId,
            role = MessageRole.ASSISTANT,
            content = content,
            model = model
        )
    }
}

class CreateConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(title: String, model: String): Result<Conversation> {
        return chatRepository.createConversation(title, model)
    }
}

class DeleteConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(conversationId: String): Result<Unit> {
        return chatRepository.deleteConversation(conversationId)
    }
}
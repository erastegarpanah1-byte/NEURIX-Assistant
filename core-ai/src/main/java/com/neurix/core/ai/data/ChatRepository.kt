package com.neurix.core.ai.data

import com.neurix.core.ai.domain.model.ChatMessage
import com.neurix.core.ai.domain.model.Conversation
import com.neurix.core.ai.domain.model.MessageRole
import com.neurix.core.common.Result
import com.neurix.core.database.dao.ConversationDao
import com.neurix.core.database.dao.MessageDao
import com.neurix.core.database.entity.ConversationEntity
import com.neurix.core.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return messageDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun createConversation(title: String, model: String): Result<Conversation> {
        return try {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val entity = ConversationEntity(
                id = id,
                title = title,
                model = model,
                createdAt = now,
                updatedAt = now
            )
            conversationDao.insertConversation(entity)
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "", e)
        }
    }

    suspend fun saveMessage(
        conversationId: String,
        role: MessageRole,
        content: String,
        model: String
    ): Result<ChatMessage> {
        return try {
            val entity = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = role.toApiString(),
                content = content,
                model = model,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(entity)
            conversationDao.updateConversation(
                id = conversationId,
                title = generateTitle(content.take(50)),
                updatedAt = System.currentTimeMillis()
            )
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "", e)
        }
    }

    suspend fun deleteConversation(conversationId: String): Result<Unit> {
        return try {
            conversationDao.deleteConversation(conversationId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "", e)
        }
    }

    private fun generateTitle(preview: String): String {
        return if (preview.length > 30) preview.take(30) + "..." else preview
    }

    private fun ConversationEntity.toDomain() = Conversation(
        id = id,
        title = title,
        model = model,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun MessageEntity.toDomain() = ChatMessage(
        id = id,
        role = MessageRole.fromApiString(role),
        content = content,
        model = model,
        timestamp = timestamp
    )
}
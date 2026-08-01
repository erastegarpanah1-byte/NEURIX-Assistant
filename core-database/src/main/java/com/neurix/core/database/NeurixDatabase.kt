package com.neurix.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neurix.core.database.dao.ConversationDao
import com.neurix.core.database.dao.MessageDao
import com.neurix.core.database.entity.ConversationEntity
import com.neurix.core.database.entity.MessageEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NeurixDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
package com.neurix.core.database.di

import android.content.Context
import androidx.room.Room
import com.neurix.core.database.NeurixDatabase
import com.neurix.core.database.dao.ConversationDao
import com.neurix.core.database.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NeurixDatabase {
        return Room.databaseBuilder(
            context,
            NeurixDatabase::class.java,
            "neurix_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideConversationDao(db: NeurixDatabase): ConversationDao {
        return db.conversationDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(db: NeurixDatabase): MessageDao {
        return db.messageDao()
    }
}
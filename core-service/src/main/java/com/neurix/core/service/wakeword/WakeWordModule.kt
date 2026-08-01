package com.neurix.core.service.wakeword

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WakeWordModule {
    @Provides @Singleton
    fun provideWakeWordEngine(engine: PorcupineWakeWordEngine): WakeWordEngine = engine
}

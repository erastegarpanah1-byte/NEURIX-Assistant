package com.neurix.core.service

import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NeurixVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() { super.onReady() }
    override fun onStartSession(args: android.os.Bundle?): VoiceInteractionSession = NeurixVoiceInteractionSession(this)
}

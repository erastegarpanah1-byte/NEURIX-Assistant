package com.neurix.core.service

import android.os.Bundle
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NeurixVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() { super.onReady() }
    override fun onStartSession(args: Bundle?): VoiceInteractionSession = NeurixVoiceInteractionSession(this)
}

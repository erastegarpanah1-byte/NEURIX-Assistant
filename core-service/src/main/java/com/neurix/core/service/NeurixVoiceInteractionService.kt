package com.neurix.core.service

import android.service.voice.VoiceInteractionService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NeurixVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
    }
}
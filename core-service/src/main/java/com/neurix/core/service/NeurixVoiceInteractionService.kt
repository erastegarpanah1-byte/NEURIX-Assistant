package com.neurix.core.service

import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NeurixVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() { super.onReady() }
    override fun onHandleAssist(data: Bundle?, structure: AssistStructure?, content: AssistContent?) {
        startActivity(Intent(this, com.neurix.app.MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("from_assistant", true)
        })
    }
    override fun onHandleScreenshot(screenshot: AssistContent?) { onHandleAssist(null, null, screenshot) }
    override fun onStartSession(args: Bundle?): VoiceInteractionSession = NeurixVoiceInteractionSession(this)
}

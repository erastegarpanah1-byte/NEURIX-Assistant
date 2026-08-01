package com.neurix.core.service

import android.app.assist.AssistContent
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionSession

class NeurixVoiceInteractionSession(s: NeurixVoiceInteractionService) : VoiceInteractionSession(s) {
    override fun onCreate() {
        super.onCreate()
        context.startActivity(Intent(context, com.neurix.app.MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("from_assistant", true)
        })
    }
    override fun onHandleAssist(data: Bundle?, structure: AssistStructure?, content: AssistContent?) {
        context.startActivity(Intent(context, com.neurix.app.MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("from_assistant", true)
        })
    }
    override fun onHandleScreenshot(s: AssistContent?) { onHandleAssist(null, null, s) }
}

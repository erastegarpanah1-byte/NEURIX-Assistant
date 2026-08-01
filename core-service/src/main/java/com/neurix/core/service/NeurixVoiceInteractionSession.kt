package com.neurix.core.service

import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionSession

class NeurixVoiceInteractionSession(s: NeurixVoiceInteractionService) : VoiceInteractionSession(s) {
    override fun onCreate() { super.onCreate(); open() }
    override fun onHandleAssist(d: Bundle?, as1: android.app.assist.AssistStructure?, ac: android.app.assist.AssistContent?) { open() }
    private fun open() {
        context.startActivity(Intent().apply {
            setClassName(context, "com.neurix.app.MainActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("from_assistant", true)
        })
    }
}

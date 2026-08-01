package com.neurix.core.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionSession

class NeurixVoiceInteractionSession : VoiceInteractionSession {
    constructor(s: NeurixVoiceInteractionService) : super(s)
    constructor(c: Context) : super(c)

    override fun onCreate() { super.onCreate(); open() }
    override fun onHandleAssist(d: Bundle?, s: android.app.assist.AssistStructure?, c: android.app.assist.AssistContent?) { open() }
    private fun open() {
        context.startActivity(Intent().apply {
            setClassName(context, "com.neurix.app.MainActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("from_assistant", true)
        })
    }
}

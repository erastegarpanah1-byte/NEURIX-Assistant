package com.neurix.core.speech

interface TextToSpeechEngine {
    fun speak(text: String, onDone: (() -> Unit)? = null)
    fun stop()
    fun isSpeaking(): Boolean
    fun destroy()
}
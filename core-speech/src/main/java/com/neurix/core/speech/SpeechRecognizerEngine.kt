package com.neurix.core.speech

interface SpeechRecognizerEngine {
    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit)
    fun stopListening()
    fun isListening(): Boolean
    fun destroy()
}
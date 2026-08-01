package com.neurix.core.service.wakeword

interface WakeWordEngine {
    fun startListening(onWakeWord: () -> Unit, onError: (String) -> Unit)
    fun stopListening()
    fun isActive(): Boolean
    fun destroy()
}
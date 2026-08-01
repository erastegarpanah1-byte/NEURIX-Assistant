package com.neurix.core.service.wakeword

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceholderWakeWordEngine @Inject constructor() : WakeWordEngine {
    private var active = false
    private var listening = false
    private var onWakeCallback: (() -> Unit)? = null

    override fun startListening(onWakeWord: () -> Unit, onError: (String) -> Unit) {
        if (active) return
        active = true
        listening = true
        onWakeCallback = onWakeWord
        Log.d("NeurixWakeWord", "Wake word engine started (placeholder)")
    }

    override fun stopListening() {
        active = false
        listening = false
        Log.d("NeurixWakeWord", "Wake word engine stopped")
    }

    override fun isActive(): Boolean = active

    override fun destroy() {
        stopListening()
        onWakeCallback = null
    }

    fun simulateWakeWordDetection() {
        if (active && listening) onWakeCallback?.invoke()
    }
}
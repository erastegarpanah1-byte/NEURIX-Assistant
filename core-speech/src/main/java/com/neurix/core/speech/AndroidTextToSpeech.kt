package com.neurix.core.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidTextToSpeech @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeechEngine {

    private var tts: TextToSpeech? = null
    private var initialized = false

    private fun ensureInitialized(onDone: (() -> Unit)?) {
        if (initialized) return
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                initialized = true
                onDone?.invoke()
            }
        }
    }

    override fun speak(text: String, onDone: (() -> Unit)?) {
        ensureInitialized {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun stop() {
        tts?.stop()
    }

    override fun isSpeaking(): Boolean = tts?.isSpeaking ?: false

    override fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        initialized = false
    }
}
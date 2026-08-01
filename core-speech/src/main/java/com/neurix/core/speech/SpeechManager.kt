package com.neurix.core.speech

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechManager @Inject constructor(
    private val speechRecognizer: AndroidSpeechRecognizer,
    private val textToSpeech: AndroidTextToSpeech
) {
    private var onSpeechResult: ((String) -> Unit)? = null
    private var onSpeechError: ((String) -> Unit)? = null

    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        onSpeechResult = onResult
        onSpeechError = onError
        speechRecognizer.startListening(onResult, onError)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
    }

    fun isListening(): Boolean = speechRecognizer.isListening()

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        textToSpeech.speak(text, onDone)
    }

    fun stopSpeaking() {
        textToSpeech.stop()
    }

    fun isSpeaking(): Boolean = textToSpeech.isSpeaking()

    fun destroy() {
        speechRecognizer.destroy()
        textToSpeech.destroy()
    }
}
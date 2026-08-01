package com.neurix.core.speech

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer as AndroidSpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidSpeechRecognizer @Inject constructor(
    @ApplicationContext private val context: Context
) : SpeechRecognizerEngine {

    private var recognizer: AndroidSpeechRecognizer? = null
    private var isListening = false

    override fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        if (isListening) return
        isListening = true

        recognizer = AndroidSpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        recognizer?.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(AndroidSpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult(matches[0])
                }
                isListening = false
            }

            override fun onPartialResults(partialResults: android.os.Bundle?) {
                val matches = partialResults?.getStringArrayList(AndroidSpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult(matches[0])
                }
            }

            override fun onError(error: Int) {
                isListening = false
                onError("Speech recognition error: $error")
            }

            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })

        recognizer?.startListening(intent)
    }

    override fun stopListening() {
        recognizer?.stopListening()
        isListening = false
    }

    override fun isListening(): Boolean = isListening

    override fun destroy() {
        recognizer?.destroy()
        recognizer = null
        isListening = false
    }
}
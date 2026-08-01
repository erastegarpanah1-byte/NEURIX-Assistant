package com.neurix.core.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WakeWordService : Service() {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRestarting = false

    override fun onCreate() {
        super.onCreate()
        (getSystemService(NotificationManager::class.java)).createNotificationChannel(
            NotificationChannel("neurix_wake", "Neurix Wake", NotificationManager.IMPORTANCE_LOW))
    }

    override fun onBind(i: Intent?): IBinder? = null

    override fun onStartCommand(i: Intent?, f: Int, sid: Int): Int {
        startForeground(1001, notif())
        startListening()
        return START_STICKY
    }

    private fun startListening() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val ri = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        }
        speechRecognizer?.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onResults(r: Bundle?) {
                checkResults(r); restartAfter(100)
            }
            override fun onPartialResults(r: Bundle?) {
                if (checkResults(r)) { speechRecognizer?.stopListening() }
            }
            override fun onError(error: Int) { restartAfter(2000) }
            override fun onReadyForSpeech(p: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(dB: Float) {}
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() { restartAfter(100) }
            override fun onEvent(e: Int, p: Bundle?) {}
        })
        speechRecognizer?.startListening(ri)
    }

    private fun checkResults(r: Bundle?): Boolean {
        val matches = r?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return false
        for (text in matches) {
            val t = text.lowercase()
            if (t.contains("neurix") || t.contains("nerix") || t.contains("nurix")) {
                onWakeWordDetected(); return true
            }
        }
        return false
    }

    private fun restartAfter(delayMs: Long) {
        if (isRestarting) return
        isRestarting = true
        try { speechRecognizer?.destroy() } catch (_: Exception) {}
        android.os.Handler(mainLooper).postDelayed({
            isRestarting = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!isDestroyed && !isFinishing) startListening()
            } else {
                startListening()
            }
        }, delayMs)
    }

    private fun onWakeWordDetected() {
        val intent = Intent(this, OverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
    }

    override fun onDestroy() {
        try { speechRecognizer?.destroy() } catch (_: Exception) {}
        speechRecognizer = null
        super.onDestroy()
    }

    private fun notif(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent().apply { setClassName(packageName, "com.neurix.app.MainActivity"); flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return Notification.Builder(this, "neurix_wake").setContentTitle("Neurix").setContentText("Listening for \"Hey Neurix\"...").setSmallIcon(android.R.drawable.ic_btn_speak_now).setContentIntent(pi).setOngoing(true).build()
    }
}

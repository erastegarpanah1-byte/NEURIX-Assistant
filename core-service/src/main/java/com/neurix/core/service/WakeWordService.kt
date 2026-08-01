package com.neurix.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.neurix.core.service.wakeword.WakeWordEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WakeWordService : Service() {

    @Inject
    lateinit var wakeWordEngine: WakeWordEngine

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NEURIX_NOTIFICATION_ID, createNotification())
        wakeWordEngine.startListening(
            onWakeWord = { onWakeWordDetected() },
            onError = { }
        )
        return START_STICKY
    }

    private fun onWakeWordDetected() {
        val overlayIntent = Intent(this, OverlayService::class.java)
        startService(overlayIntent)
    }

    override fun onDestroy() {
        wakeWordEngine.stopListening()
        super.onDestroy()
    }

    private fun createNotification(): android.app.Notification {
        val channelId = "neurix_wake_word"
        val manager = getSystemService(android.app.NotificationManager::class.java)
        val channel = android.app.NotificationChannel(
            channelId, "Neurix Wake Word", android.app.NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)
        return android.app.Notification.Builder(this, channelId)
            .setContentTitle("Neurix")
            .setContentText("Listening for wake word...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    companion object {
        private const val NEURIX_NOTIFICATION_ID = 1001
    }
}
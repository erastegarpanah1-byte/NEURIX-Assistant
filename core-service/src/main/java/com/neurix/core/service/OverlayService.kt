package com.neurix.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var ov: FrameLayout? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        (getSystemService(NotificationManager::class.java)).createNotificationChannel(
            NotificationChannel("nx_ov", "Neurix Assistant", NotificationManager.IMPORTANCE_LOW))
    }

    override fun onBind(i: Intent?): IBinder? = null

    override fun onStartCommand(i: Intent?, f: Int, sid: Int): Int {
        startForeground(2001, notif()); show(); return START_STICKY
    }

    private fun show() {
        if (ov != null) return
        val ot = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val p = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, ot,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
            .apply { gravity = Gravity.CENTER }
        ov = FrameLayout(this)
        val c = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.argb(180, 0, 0, 0)); gravity = android.view.Gravity.CENTER
        }
        c.addView(TextView(this).apply { text = "Neurix"; textSize = 28f; setTextColor(Color.WHITE); gravity = android.view.Gravity.CENTER; setPadding(0, 0, 0, 40) })
        c.addView(TextView(this).apply { text = "Hey! I'm listening..."; textSize = 16f; setTextColor(Color.argb(180, 255, 255, 255)); gravity = android.view.Gravity.CENTER; setPadding(0, 0, 0, 32) })
        val ob = Button(this).apply { text = "Open Neurix"; textSize = 18f; setTextColor(Color.WHITE); setBackgroundColor(Color.parseColor("#7C3AED")); setPadding(48, 24, 48, 24) }
        ob.setOnClickListener { startActivity(Intent().apply { setClassName(packageName, "com.neurix.app.MainActivity"); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); putExtra("from_assistant", true) }) }
        c.addView(ob)
        val cb = Button(this).apply { text = "Close"; textSize = 14f; setTextColor(Color.WHITE); setBackgroundColor(Color.TRANSPARENT) }
        cb.setOnClickListener { close() }
        c.addView(cb)
        ov?.addView(c)
        wm.addView(ov, p)
    }

    private fun close() { ov?.let { wm.removeView(it) }; ov = null; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }

    private fun notif(): Notification {
        val i = Intent().apply { setClassName(packageName, "com.neurix.app.MainActivity"); flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return Notification.Builder(this, "nx_ov").setContentTitle("Neurix").setContentText("AI Assistant is active").setSmallIcon(android.R.drawable.ic_dialog_info).setContentIntent(pi).setOngoing(true).build()
    }

    override fun onDestroy() { close(); super.onDestroy() }
}

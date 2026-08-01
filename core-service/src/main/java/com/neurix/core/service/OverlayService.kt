package com.neurix.core.service
import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var ov: FrameLayout? = null
    private var pulseAnim: ValueAnimator? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        (getSystemService(NotificationManager::class.java)).createNotificationChannel(NotificationChannel("nx_ov", "Neurix Assistant", NotificationManager.IMPORTANCE_LOW))
    }
    override fun onBind(i: Intent?): IBinder? = null
    override fun onStartCommand(i: Intent?, f: Int, sid: Int): Int {
        startForeground(2001, notif()); show(); return START_STICKY
    }
    private fun show() {
        if (ov != null) return
        val ot = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val p = WindowManager.LayoutParams(MATCH_PARENT, MATCH_PARENT, ot, FLAG_NOT_FOCUSABLE or FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.CENTER }
        ov = FrameLayout(this)
        val c = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.argb(200, 8, 8, 20)); gravity = Gravity.CENTER; setPadding(48, 48, 48, 48) }

        c.addView(TextView(this).apply { text = "Neurix"; textSize = 32f; setTextColor(Color.WHITE); gravity = Gravity.CENTER; setPadding(0, 0, 0, 16); setTypeface(null, android.graphics.Typeface.BOLD) })

        val micBg = GradientDrawable().apply { shape = GradientDrawable.OVAL; setSize(160, 160); colors = intArrayOf(Color.parseColor("#7C3AED"), Color.parseColor("#A78BFA")); gradientType = GradientDrawable.LINEAR_GRADIENT }
        val micIcon = ImageView(this).apply {
            val bg = GradientDrawable().apply { shape = GradientDrawable.OVAL; setSize(100, 100); colors = intArrayOf(Color.parseColor("#6D28D9"), Color.parseColor("#7C3AED")); gradientType = GradientDrawable.LINEAR_GRADIENT }
            background = bg; setImageResource(android.R.drawable.ic_btn_speak_now); setColorFilter(Color.WHITE); setPadding(24, 24, 24, 24)
            setOnClickListener { startActivity(Intent().apply { setClassName(packageName, "com.neurix.app.MainActivity"); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); putExtra("from_assistant", true) }) }
        }
        val micWrap = LinearLayout(this).apply { gravity = Gravity.CENTER; addView(micIcon) }
        c.addView(micWrap)

        c.addView(TextView(this).apply { text = "Tap mic to talk"; textSize = 18f; setTextColor(Color.argb(180, 255, 255, 255)); gravity = Gravity.CENTER; setPadding(0, 20, 0, 32) })

        c.addView(Button(this).apply { text = "Open Neurix"; textSize = 18f; setTextColor(Color.WHITE); setBackgroundColor(Color.parseColor("#7C3AED")); setPadding(48, 24, 48, 24); minimumHeight = 0; minHeight = 0;
            setOnClickListener { startActivity(Intent().apply { setClassName(packageName, "com.neurix.app.MainActivity"); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); putExtra("from_assistant", true) }) }
        })
        val cb = Button(this).apply { text = "Close"; textSize = 16f; setTextColor(Color.argb(150, 255, 255, 255)); setBackgroundColor(Color.TRANSPARENT); setPadding(20, 16, 20, 16); minimumHeight = 0; minHeight = 0; setOnClickListener { close() } }
        c.addView(cb)

        startPulseAnimation(micIcon)
        ov?.addView(c); wm.addView(ov, p)
    }
    private fun startPulseAnimation(v: View) {
        pulseAnim = ValueAnimator.ofFloat(1f, 1.12f).apply { duration = 1000; repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.REVERSE; addUpdateListener { val s = it.animatedValue as Float; v.scaleX = s; v.scaleY = s }; start() }
    }
    private fun close() { pulseAnim?.cancel(); ov?.let { wm.removeView(it) }; ov = null; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
    private fun notif(): Notification {
        val i = Intent().apply { setClassName(packageName, "com.neurix.app.MainActivity"); flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return Notification.Builder(this, "nx_ov").setContentTitle("Neurix").setContentText("AI Assistant is active").setSmallIcon(android.R.drawable.ic_btn_speak_now).setContentIntent(pi).setOngoing(true).build()
    }
    override fun onDestroy() { close(); super.onDestroy() }
    companion object {
        private const val MATCH_PARENT = android.view.WindowManager.LayoutParams.MATCH_PARENT
        private const val FLAG_NOT_FOCUSABLE = android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        private const val FLAG_LAYOUT_IN_SCREEN = android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
    }
}

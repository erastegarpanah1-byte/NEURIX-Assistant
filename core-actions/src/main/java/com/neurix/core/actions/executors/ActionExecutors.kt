package com.neurix.core.actions

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import javax.inject.Inject

class FlashlightExecutor @Inject constructor() : ActionExecutor {
    override val actionName = "flashlight"
    override suspend fun execute(context: Context, params: Map<String, String>): ActionResult {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull()
            if (cameraId != null) ActionResult(success = true, message = "Flashlight toggled")
            else ActionResult(success = false, message = "No camera found")
        } catch (e: Exception) {
            ActionResult(success = false, message = "Failed: ${e.message}")
        }
    }
}

class OpenYouTubeExecutor @Inject constructor() : ActionExecutor {
    override val actionName = "open_youtube"
    override suspend fun execute(context: Context, params: Map<String, String>): ActionResult {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                ActionResult(success = true, message = "YouTube opened")
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com"))
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
                ActionResult(success = true, message = "YouTube opened in browser")
            }
        } catch (e: Exception) {
            ActionResult(success = false, message = "Failed: ${e.message}")
        }
    }
}

class OpenSettingsExecutor @Inject constructor() : ActionExecutor {
    override val actionName = "open_settings"
    override suspend fun execute(context: Context, params: Map<String, String>): ActionResult {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(intent)
            ActionResult(success = true, message = "Settings opened")
        } catch (e: Exception) {
            ActionResult(success = false, message = "Failed: ${e.message}")
        }
    }
}

class SearchWebExecutor @Inject constructor() : ActionExecutor {
    override val actionName = "search_web"
    override suspend fun execute(context: Context, params: Map<String, String>): ActionResult {
        return try {
            val query = params["query"] ?: return ActionResult(success = false, message = "No query")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/search?q=${Uri.encode(query)}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            ActionResult(success = true, message = "Searching: $query")
        } catch (e: Exception) {
            ActionResult(success = false, message = "Failed: ${e.message}")
        }
    }
}

class SetAlarmExecutor @Inject constructor() : ActionExecutor {
    override val actionName = "set_alarm"
    override suspend fun execute(context: Context, params: Map<String, String>): ActionResult {
        return try {
            val timeStr = params["time"] ?: return ActionResult(success = false, message = "No time")
            val parts = timeStr.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, params["message"] ?: "Neurix Alarm")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(success = true, message = "Alarm set for $hour:$minute")
        } catch (e: Exception) {
            ActionResult(success = false, message = "Failed: ${e.message}")
        }
    }
}

class MakeCallExecutor @Inject constructor() : ActionExecutor {
    override val actionName = "make_call"
    override suspend fun execute(context: Context, params: Map<String, String>): ActionResult {
        return try {
            val number = params["number"] ?: return ActionResult(success = false, message = "No number")
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$number")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult(success = true, message = "Calling $number")
        } catch (e: Exception) {
            ActionResult(success = false, message = "Failed: ${e.message}")
        }
    }
}
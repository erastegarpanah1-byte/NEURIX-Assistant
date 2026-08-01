package com.neurix.core.actions

import android.Manifest

data class ActionResult(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)

data class ActionDefinition(
    val name: String,
    val description: String,
    val keywords: List<String>,
    val requiredPermission: String? = null,
    val category: ActionCategory
)

enum class ActionCategory {
    DEVICE, MEDIA, COMMUNICATION, PRODUCTIVITY, SYSTEM, NAVIGATION
}

object KnownActions {
    val FLASHLIGHT = ActionDefinition(
        name = "flashlight",
        description = "Toggle the device flashlight on or off",
        keywords = listOf("flashlight", "torch", "light", "turn on light", "turn off light"),
        requiredPermission = Manifest.permission.CAMERA,
        category = ActionCategory.DEVICE
    )
    val OPEN_APP = ActionDefinition(
        name = "open_app",
        description = "Open an installed application",
        keywords = listOf("open", "launch", "start"),
        category = ActionCategory.NAVIGATION
    )
    val OPEN_YOUTUBE = ActionDefinition(
        name = "open_youtube",
        description = "Open the YouTube application",
        keywords = listOf("youtube", "open youtube", "launch youtube"),
        category = ActionCategory.MEDIA
    )
    val OPEN_SETTINGS = ActionDefinition(
        name = "open_settings",
        description = "Open device settings",
        keywords = listOf("settings", "open settings", "system settings"),
        category = ActionCategory.SYSTEM
    )
    val SEARCH_WEB = ActionDefinition(
        name = "search_web",
        description = "Search the web for a query",
        keywords = listOf("search", "google", "find", "look up", "search for"),
        category = ActionCategory.PRODUCTIVITY
    )
    val SET_ALARM = ActionDefinition(
        name = "set_alarm",
        description = "Set an alarm for a specific time",
        keywords = listOf("alarm", "set alarm", "wake me up", "reminder"),
        category = ActionCategory.PRODUCTIVITY
    )
    val MAKE_CALL = ActionDefinition(
        name = "make_call",
        description = "Make a phone call to a contact",
        keywords = listOf("call", "phone", "dial", "ring"),
        requiredPermission = Manifest.permission.CALL_PHONE,
        category = ActionCategory.COMMUNICATION
    )
}
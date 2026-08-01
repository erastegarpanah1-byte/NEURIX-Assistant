package com.neurix.core.actions

import android.content.Context

interface ActionExecutor {
    val actionName: String
    suspend fun execute(context: Context, params: Map<String, String>): ActionResult
}
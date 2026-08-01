package com.neurix.core.actions

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRegistry @Inject constructor() {
    private val executors: MutableMap<String, ActionExecutor> = mutableMapOf()

    fun register(executor: ActionExecutor) {
        executors[executor.actionName] = executor
    }

    fun getExecutor(actionName: String): ActionExecutor? = executors[actionName]

    fun getAllActionNames(): Set<String> = executors.keys

    fun getActionDescriptions(): String {
        return executors.values.joinToString("\n") { executor ->
            val action = allDefinitions().find { it.name == executor.actionName }
            "- ${executor.actionName}: ${action?.description ?: ""}"
        }
    }

    private fun allDefinitions(): List<ActionDefinition> = listOf(
        KnownActions.FLASHLIGHT,
        KnownActions.OPEN_APP,
        KnownActions.OPEN_YOUTUBE,
        KnownActions.OPEN_SETTINGS,
        KnownActions.SEARCH_WEB,
        KnownActions.SET_ALARM,
        KnownActions.MAKE_CALL
    )
}
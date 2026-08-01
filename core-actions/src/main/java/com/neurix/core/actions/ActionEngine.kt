package com.neurix.core.actions

import com.neurix.core.ai.data.AiRepository
import com.neurix.core.ai.domain.model.ChatMessage
import com.neurix.core.ai.domain.model.MessageRole
import com.neurix.core.common.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import com.neurix.core.actions.executors.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionEngine @Inject constructor(
    private val registry: ActionRegistry,
    private val aiRepository: AiRepository,
    @ApplicationContext private val context: Context
) {
    init {
        registry.register(FlashlightExecutor())
        registry.register(OpenYouTubeExecutor())
        registry.register(OpenSettingsExecutor())
        registry.register(SearchWebExecutor())
        registry.register(SetAlarmExecutor())
        registry.register(MakeCallExecutor())
    }

    suspend fun processUserMessage(
        userMessage: String,
        model: String,
        history: List<ChatMessage>
    ): ActionEngineResult {
        val systemPrompt = buildSystemPrompt()
        val systemMessage = ChatMessage(
            id = "system",
            role = MessageRole.USER,
            content = systemPrompt,
            model = model,
            timestamp = System.currentTimeMillis()
        )
        val fullHistory = listOf(systemMessage) + history

        return when (val result = aiRepository.sendMessage(model, fullHistory, userMessage)) {
            is Result.Success -> {
                val rawResponse = result.data
                val parsed = parseActionResponse(rawResponse)
                if (parsed != null) {
                    val executor = registry.getExecutor(parsed.action)
                    if (executor != null) {
                        val actionResult = executor.execute(context, parsed.params)
                        ActionEngineResult(
                            response = actionResult.message,
                            actionPerformed = true,
                            actionName = parsed.action,
                            actionResult = actionResult
                        )
                    } else {
                        ActionEngineResult(response = rawResponse, actionPerformed = false)
                    }
                } else {
                    ActionEngineResult(response = rawResponse, actionPerformed = false)
                }
            }
            is Result.Error -> ActionEngineResult(
                response = "Sorry, I couldn't process that request.",
                actionPerformed = false,
                error = result.exception
            )
        }
    }

    private fun buildSystemPrompt(): String {
        val actions = registry.getActionDescriptions()
        return "You are Neurix, a personal AI assistant for Android. Available actions:\n$actions\n\nWhen the user asks you to perform an action, respond with ACTION: <action_name>\nPARAMS: key1=value1, key2=value2\nIf just chatting, respond naturally. Be helpful, concise, and friendly."
    }

    private fun parseActionResponse(response: String): ParsedAction? {
        val lines = response.lines().map { it.trim() }
        val actionLine = lines.find { it.startsWith("ACTION:") } ?: return null
        val action = actionLine.removePrefix("ACTION:").trim()
        val paramsLine = lines.find { it.startsWith("PARAMS:") }
        val params = if (paramsLine != null) {
            paramsLine.removePrefix("PARAMS:").trim()
                .split(",").mapNotNull { part ->
                    val eq = part.indexOf("=")
                    if (eq > 0) part.substring(0, eq).trim() to part.substring(eq + 1).trim() else null
                }.toMap()
        } else emptyMap()
        return ParsedAction(action, params)
    }
}

data class ActionEngineResult(
    val response: String,
    val actionPerformed: Boolean,
    val actionName: String? = null,
    val actionResult: ActionResult? = null,
    val error: Exception? = null
)

private data class ParsedAction(val action: String, val params: Map<String, String>)
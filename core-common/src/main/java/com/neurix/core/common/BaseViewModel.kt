package com.neurix.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State : MviState, Intent : MviIntent, Effect : MviEffect>(
    initialState: State
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()
    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()
    val currentState: State get() = _state.value
    protected fun setState(reducer: State.() -> State) { _state.value = _state.value.reducer() }
    protected fun sendEffect(effect: Effect) { _effect.tryEmit(effect) }
    abstract fun handleIntent(intent: Intent)
}
package com.neurix.feature.home.presentation

import com.neurix.core.common.MviEffect
import com.neurix.core.common.MviIntent
import com.neurix.core.common.MviState

data class HomeState(val greeting: String = "Hello.") : MviState

sealed interface HomeIntent : MviIntent {
    data object TapMicrophone : HomeIntent
}

sealed interface HomeEffect : MviEffect {
    data object NavigateToChat : HomeEffect
}
package com.neurix.feature.home.presentation

import com.neurix.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {
    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.TapMicrophone -> sendEffect(HomeEffect.NavigateToChat)
        }
    }
}
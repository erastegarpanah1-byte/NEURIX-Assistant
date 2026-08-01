package com.neurix.core.network

interface OpenRouterAuthProvider {
    fun authHeader(): String
}
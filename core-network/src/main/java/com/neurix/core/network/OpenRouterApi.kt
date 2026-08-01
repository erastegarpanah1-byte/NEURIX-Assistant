package com.neurix.core.network

import com.neurix.core.network.model.OpenRouterModelsResponse
import com.neurix.core.network.model.OpenRouterRequest
import com.neurix.core.network.model.OpenRouterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface OpenRouterApi {

    @POST("api/v1/chat/completions")
    suspend fun sendMessage(
        @Header("Authorization") auth: String,
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>

    @GET("api/v1/models")
    suspend fun getModels(
        @Header("Authorization") auth: String
    ): Response<OpenRouterModelsResponse>
}
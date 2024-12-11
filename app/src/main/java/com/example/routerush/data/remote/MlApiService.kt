package com.example.routerush.data.remote

import com.example.routerush.data.response.OptimizeRouteResponse
import com.example.routerush.data.response.OptimizedResponse
import com.example.routerush.data.response.RouteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MlApiService {
    @POST("optimize-route") // Ganti dengan endpoint API Anda
    suspend fun optimizeRoute(@Body request: RouteRequest): Response<OptimizedResponse>
}
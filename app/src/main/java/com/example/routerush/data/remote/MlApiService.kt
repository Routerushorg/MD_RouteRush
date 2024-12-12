package com.example.routerush.data.remote

import com.example.routerush.data.response.AddressHistoryResponse
import com.example.routerush.data.response.RouteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MlApiService {
    @POST("optimize-route")
    suspend fun optimizeRoute(@Body request: RouteRequest): Response<List<String>>

    @GET("address-history")
    suspend fun getAddressHistory(@Query("email") email: String): Response<AddressHistoryResponse>
}
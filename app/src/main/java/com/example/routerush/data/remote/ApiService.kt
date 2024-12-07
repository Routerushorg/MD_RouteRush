package com.example.routerush.data.remote

import com.example.routerush.data.response.LoginAndRegisterResponse
import com.example.routerush.data.response.OptimizeRouteResponse
import com.example.routerush.data.response.RouteRequest
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginAndRegisterResponse

    @FormUrlEncoded
    @POST("signin")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginAndRegisterResponse

    @POST("signout")
    suspend fun signout(): LoginAndRegisterResponse


    @POST("optimize-route")
    suspend fun optimizeRoute(
        @Body requestBody: RouteRequest
    ): OptimizeRouteResponse
}
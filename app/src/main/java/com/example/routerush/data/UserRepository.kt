package com.example.routerush.data

import android.util.Log
import com.example.routerush.data.datastore.UserModel
import com.example.routerush.data.datastore.UserPreference
import com.example.routerush.data.remote.ApiService
import com.example.routerush.data.response.LoginAndRegisterResponse
import com.example.routerush.data.response.RouteRequest
import kotlinx.coroutines.flow.Flow

open class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference

) {


    suspend fun register(email: String, password: String): LoginAndRegisterResponse {
        return apiService.register(email, password)
    }
    suspend fun login(email: String, password: String) : LoginAndRegisterResponse{
        return apiService.login(email, password)
    }
    suspend fun saveAuth(user: UserModel) = userPreference.saveSession(user)


    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        apiService.signout()
        userPreference.logout()
    }

    suspend fun optimizeRoute(addresses: List<String>): Result<String> {
        return try {
            val requestBody = RouteRequest(addresses)
            Log.d("RequestBody", requestBody.toString())
            val response = apiService.optimizeRoute(requestBody)
            Result.success(response.optimizedRoute)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService,userPreference)
            }.also { instance = it }
    }

}
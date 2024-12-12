package com.example.routerush.data

import com.example.routerush.data.datastore.UserModel
import com.example.routerush.data.datastore.UserPreference
import com.example.routerush.data.remote.ApiService
import com.example.routerush.data.remote.MlApiService
import com.example.routerush.data.response.LoginAndRegisterResponse
import com.example.routerush.data.response.RouteRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

open class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val MlApiService: MlApiService

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

    suspend fun optimizeRoute(addresses: List<String>): Result<List<String>> {
        return try {
            val user = userPreference.getSession().firstOrNull()
            val email = user?.email

            if (email != null) {
                val request = RouteRequest(email, addresses)
                val response = MlApiService.optimizeRoute(request)

                if (response.isSuccessful) {
                    response.body()?.optimizedRoute?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Optimized route not found"))
                } else {
                    Result.failure(Exception("Failed to optimize route: ${response.message()}"))
                }
            } else {
                Result.failure(Exception("User session not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getAddressHistory(): Result<List<String>> {
        return try {
            val user = userPreference.getSession().firstOrNull()
            val email = user?.email

            if (email != null) {
                val response = MlApiService.getAddressHistory(email)
                if (response.isSuccessful) {
                    response.body()?.addresses?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("No addresses found in response"))
                } else {
                    Result.failure(Exception("Failed to fetch address history: ${response.message()}"))
                }
            } else {
                Result.failure(Exception("User session not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            mlApiService: MlApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService,userPreference,mlApiService)
            }.also { instance = it }
    }

}
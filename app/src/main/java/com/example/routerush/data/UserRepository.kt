package com.example.routerush.data

import androidx.lifecycle.LiveData
import com.example.routerush.data.datastore.UserModel
import com.example.routerush.data.datastore.UserPreference
import com.example.routerush.data.remote.ApiService
import com.example.routerush.data.response.LoginAndRegisterResponse
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
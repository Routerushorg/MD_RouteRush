package com.example.routerush.data

import android.content.Context
import com.example.routerush.data.datastore.UserPreference
import com.example.routerush.data.datastore.dataStore
import com.example.routerush.data.remote.ApiConfig
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService,pref)
    }
    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }
}
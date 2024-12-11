package com.example.routerush.data

import android.content.Context
import com.example.routerush.data.datastore.UserPreference
import com.example.routerush.data.datastore.dataStore
import com.example.routerush.data.remote.ApiConfig
import com.example.routerush.data.remote.MlApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val mlApiService = MlApiConfig.getApiService()
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService,pref, mlApiService )
    }
    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }
}
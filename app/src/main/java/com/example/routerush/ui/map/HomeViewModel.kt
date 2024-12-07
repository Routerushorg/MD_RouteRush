package com.example.routerush.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.routerush.data.UserRepository
import com.example.routerush.data.datastore.UserModel
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: UserRepository) : ViewModel() {



    private val _isLoading = MutableLiveData<Boolean>()


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    private val _optimizedRoute =  MutableLiveData<List<String>>()
    val optimizedRoute: LiveData<List<String>> get() = _optimizedRoute

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun optimizeRoute(addresses: List<String>) {
        viewModelScope.launch {
            try {
                val response = repository.optimizeRoute(addresses)
                val optimizedRoute = response.getOrNull()?.replace("Optimized route for addresses: ", "")?.split(", ") ?: emptyList()
                _optimizedRoute.postValue(optimizedRoute)
            } catch (e: Exception) {
                _error.postValue("Failed to optimize route: ${e.message}")
            }
        }
    }
}
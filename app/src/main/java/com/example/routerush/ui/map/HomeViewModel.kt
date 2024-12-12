package com.example.routerush.ui.map

import android.util.Log
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
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error



    private val _optimizedRoute = MutableLiveData<List<String>>()
    val optimizedRoute: LiveData<List<String>> get() = _optimizedRoute

    private val _addressHistory = MutableLiveData<List<String>>()
    val addressHistory: LiveData<List<String>> get() = _addressHistory

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }


    fun fetchAddressHistory() {
        viewModelScope.launch {
            val result = repository.getAddressHistory()
            result.onSuccess { addresses ->
                // Ambil 10 alamat terakhir
                _addressHistory.value =  addresses.take(10)
            }.onFailure { error ->
                // Tangani kesalahan jika diperlukan
                _addressHistory.value = emptyList()
            }
        }
    }

    fun optimizeRoute(addresses: List<String>) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.optimizeRoute(addresses)
                result.onSuccess { route ->
                    _optimizedRoute.value = route
                }.onFailure { error ->
                    _optimizedRoute.value = emptyList()
                    Log.e("OptimizedRoute", "Error optimizing route: ${error.message}")
                }
            } finally {
                _isLoading.value = false
            }
        }

    }

}
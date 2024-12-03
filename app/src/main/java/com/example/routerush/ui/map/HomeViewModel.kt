package com.example.routerush.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.routerush.data.UserRepository
import com.example.routerush.data.datastore.UserModel
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: UserRepository) : ViewModel() {



    private val _isLoading = MutableLiveData<Boolean>()

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}
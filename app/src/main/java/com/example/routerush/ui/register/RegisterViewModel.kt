package com.example.routerush.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routerush.data.UserRepository
import com.example.routerush.data.datastore.UserModel
import com.example.routerush.data.response.LoginAndRegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository
    ) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<LoginAndRegisterResponse>>()
    val registerResult: LiveData<Result<LoginAndRegisterResponse>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.register(email, password)
                _registerResult.value = Result.success(response)
                val loginResult = response.user
                val registerResult = response.user

                val userId = registerResult?.uid ?: ""
                val user = UserModel(
                    userId = userId,
                    name = name,
                    email = email,
                    isLogin = false
                )
                saveAuth(user)

            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
            finally{
                _isLoading.value = false
            }
        }
    }
    private fun saveAuth(userModel: UserModel) {
        viewModelScope.launch {
            repository.saveAuth(userModel)
        }
    }
}
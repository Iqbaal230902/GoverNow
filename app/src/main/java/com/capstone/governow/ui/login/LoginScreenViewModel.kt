package com.capstone.governow.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.governow.api.ApiConfig
import com.capstone.governow.data.repository.UserRepository
import com.capstone.governow.data.request.LoginRequest
import com.capstone.governow.data.respone.DataLoginResponse
import com.capstone.governow.data.respone.DataProfileResponse
import com.capstone.governow.data.respone.LoginResponse
import com.capstone.governow.data.respone.ProfileResponse
import com.capstone.governow.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginScreenViewModel(private val repository: UserRepository) : ViewModel() {
    fun loginUser(email: String, password: String): LoginResponse? {
        var result: LoginResponse? = null
        Log.d("email", email)
        Log.d("password", password)


        runBlocking(Dispatchers.IO) {
            val call = ApiConfig.apiInstance.loginUser(LoginRequest(email, password))
            val response = call.execute()
            Log.d("hohox", response.toString())
            if (response.isSuccessful) {
                result = LoginResponse(response.body()?.message, response.body()?.token)
            }else{
                Log.d("hoho", "denis")
            }
        }

        return result
    }

    fun getProfile(token: String): ProfileResponse? {
        var result: ProfileResponse? = null

        runBlocking(Dispatchers.IO) {
            val call = ApiConfig.apiInstance.getProfile("Bearer $token")
            val response = call.execute()
            if (response.isSuccessful) {
                result = ProfileResponse(response.body()?.message, DataProfileResponse(response.body()?.data?.fullName, response.body()?.data?.email, response.body()?.data?.username))
            }
        }

        return result
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}
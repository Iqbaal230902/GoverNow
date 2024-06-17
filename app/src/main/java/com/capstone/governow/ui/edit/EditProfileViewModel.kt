package com.capstone.governow.ui.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.governow.api.ApiConfig
import com.capstone.governow.data.repository.UserRepository
import com.capstone.governow.data.respone.ProfileData
import com.capstone.governow.data.respone.ProfileResponse
import com.capstone.governow.data.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class EditProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getProfile(token: String): ProfileResponse? {
        Log.d("hohoby", token)
        var result: ProfileResponse? = null

        runBlocking(Dispatchers.IO) {
            Log.d("hohoxyb", "Bearer $token")
            val call = ApiConfig.apiInstance.getProfile("Bearer $token")
            val response = call.execute()
            Log.d("hohoxyh", response.toString())
            if (response.isSuccessful) {
                result = ProfileResponse(response.body()?.message.toString(), ProfileData(response.body()?.data?.fullName.toString(), response.body()?.data?.email.toString(), response.body()?.data?.username.toString()))
            }
        }

        return result
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
}
package com.capstone.governow.ui.form.add

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.governow.api.ApiConfig
import com.capstone.governow.data.repository.UserRepository
import com.capstone.governow.data.respone.DefaultResponse
import com.capstone.governow.data.model.UserModel
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddFormViewModel (private val repository: UserRepository) : ViewModel() {
    fun addNewStory(token: String, photo: MultipartBody.Part, description: RequestBody, location: Location?): DefaultResponse? {
        var result: DefaultResponse? = null

        runBlocking(Dispatchers.IO) {
            val call = ApiConfig.apiInstance.addNewForm("Bearer $token", description, photo, location?.latitude, location?.longitude)
            val response = call.execute()
            if (response.isSuccessful) {
                result = response.body()
            }
        }

        return result
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}
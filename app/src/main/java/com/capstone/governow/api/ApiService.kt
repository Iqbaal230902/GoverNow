package com.capstone.governow.api

import com.capstone.governow.data.request.LoginRequest
import com.capstone.governow.data.request.RegisterRequest
import com.capstone.governow.data.respone.LoginResponse
import com.capstone.governow.data.respone.ProfileResponse
import com.capstone.governow.model.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("user/login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("user/profile")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<ProfileResponse>

    @POST("register")
    fun registerUser(
        @Body request: RegisterRequest
    ): Call<ProfileResponse>
}

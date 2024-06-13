package com.capstone.governow.data.respone

data class DataLoginResponse(
    val token:String?
)

data class LoginResponse (
    val message: String?,
    val token: String?,
)
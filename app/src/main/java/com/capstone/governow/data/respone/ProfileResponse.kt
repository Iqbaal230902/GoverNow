package com.capstone.governow.data.respone

data class DataProfileResponse(
    val fullName: String?,
    val email: String?,
    val username: String?
)

data class ProfileResponse(
    val message: String?,
    val data: DataProfileResponse?
)
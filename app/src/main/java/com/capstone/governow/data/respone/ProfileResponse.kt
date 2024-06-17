package com.capstone.governow.data.respone

data class ProfileResponse(
    val message: String,
    val data: ProfileData
)

data class ProfileData(
    val fullName: String,
    val email: String,
    val username: String
)

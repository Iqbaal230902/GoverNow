package com.capstone.governow.data.request

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val username: String,
    val password: String
)


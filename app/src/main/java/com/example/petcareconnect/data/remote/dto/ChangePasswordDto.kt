package com.example.petcareconnect.data.remote.dto

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)

data class ChangePasswordResponse(
    val success: Boolean,
    val message: String
)


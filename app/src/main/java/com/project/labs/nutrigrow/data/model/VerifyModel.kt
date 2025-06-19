package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class VerifyModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("password")
    val password: String,
    @field:SerializedName("role")
    val role: String,
    @field:SerializedName("is_active")
    val isActive: String,
    @field:SerializedName("token")
    val token: String,
)
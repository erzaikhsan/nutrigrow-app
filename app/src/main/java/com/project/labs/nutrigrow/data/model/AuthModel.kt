package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("role")
    val role: String,
    @field:SerializedName("region")
    val region: String,
    @field:SerializedName("token")
    val token: String,
)
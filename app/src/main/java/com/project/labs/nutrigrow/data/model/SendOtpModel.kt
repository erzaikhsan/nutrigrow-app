package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class SendOtpModel(
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("password")
    val password: String,
)
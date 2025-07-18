package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @field:SerializedName("id")
    val user_id: String,
    @field:SerializedName("full_name")
    val full_name: String,
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("date_of_birth")
    val date_of_birth: String,
    @field:SerializedName("phone_number")
    val phone_number: String,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("region")
    val region: String,
    @field:SerializedName("is_active")
    val is_active: Boolean,
    @field:SerializedName("active_period")
    val active_period: String,
)
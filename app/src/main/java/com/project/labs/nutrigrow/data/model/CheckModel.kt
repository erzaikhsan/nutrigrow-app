package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class CheckModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("parents_id")
    val parents_id: String,
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("age")
    val age: Int,
    @field:SerializedName("height")
    val height: Double,
    @field:SerializedName("hfa_status")
    val hfa_status: String,
)
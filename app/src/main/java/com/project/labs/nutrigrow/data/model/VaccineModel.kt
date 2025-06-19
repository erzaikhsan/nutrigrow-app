package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class VaccineModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("children_id")
    val children_id: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("vaccine_name")
    val vaccine_name: String,
)
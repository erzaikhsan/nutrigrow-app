package com.project.labs.nutrigrow.data.remote.response

import com.google.gson.annotations.SerializedName

data class TemplateResponse <T: Any> (
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: T,
)
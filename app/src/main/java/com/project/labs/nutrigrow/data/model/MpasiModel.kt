package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class MpasiModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("writer")
    val writer: String,
    @field:SerializedName("image")
    val image: String,
    @field:SerializedName("description")
    val description: String,
)
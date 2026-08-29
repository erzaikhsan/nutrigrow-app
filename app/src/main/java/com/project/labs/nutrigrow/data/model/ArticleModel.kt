package com.project.labs.nutrigrow.data.model

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName

data class ArticleModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("writer")
    val writer: String,
    @field:SerializedName("image")
    @DrawableRes
    val image: Int,
    @field:SerializedName("description")
    val description: String,
)
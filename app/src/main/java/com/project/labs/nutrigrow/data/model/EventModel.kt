package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class EventModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("start_time")
    val start_time: String,
    @field:SerializedName("end_time")
    val end_time: String,
    @field:SerializedName("place")
    val place: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("region")
    val region: String,
)
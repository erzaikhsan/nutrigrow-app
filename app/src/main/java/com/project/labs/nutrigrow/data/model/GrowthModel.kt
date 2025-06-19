package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class GrowthModel(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("children_id")
    val children_id: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("age")
    val age: Int,
    @field:SerializedName("weight")
    val weight: Number,
    @field:SerializedName("wfa_status")
    val wfa_status: String,
    @field:SerializedName("height")
    val height: Number,
    @field:SerializedName("hfa_status")
    val hfa_status: String,
    @field:SerializedName("wfh_status")
    val wfh_status: String,
    @field:SerializedName("head_circum")
    val head_circum: Number,
    @field:SerializedName("arm_circum")
    val arm_circum: Number,
    @field:SerializedName("note")
    val note: String,
)
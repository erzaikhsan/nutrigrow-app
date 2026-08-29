package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class ChildrenModel(
    @field:SerializedName("children_id")
    val children_id: String,
    @field:SerializedName("parents_id")
    val parents_id: String,
    @field:SerializedName("full_name")
    val full_name: String,
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("place_of_birth")
    val place_of_birth: String,
    @field:SerializedName("date_of_birth")
    val date_of_birth: String,
    @field:SerializedName("father")
    val father: String,
    @field:SerializedName("mother")
    val mother: String,
    @field:SerializedName("order_of_child")
    val order_of_child: Number,
    @field:SerializedName("region")
    val region: String,
    @field:SerializedName("birth_weight")
    val birth_weight: Number,
    @field:SerializedName("wfa_status")
    val wfa_status: String,
    @field:SerializedName("birth_height")
    val birth_height: Number,
    @field:SerializedName("hfa_status")
    val hfa_status: String,
    @field:SerializedName("wfh_status")
    val wfh_status: String,
    @field:SerializedName("birth_head_circum")
    val birth_head_circum: Number,
    @field:SerializedName("nik")
    val nik: String = "",
    @field:SerializedName("status")
    val status: String = "",
)
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
    @field:SerializedName("wfa_zscore")
    val wfa_zscore: Double? = null,
    @field:SerializedName("hfa_zscore")
    val hfa_zscore: Double? = null,
    @field:SerializedName("wfh_zscore")
    val wfh_zscore: Double? = null,
    @field:SerializedName("head_circum_zscore")
    val head_circum_zscore: Double? = null,
    @field:SerializedName("muac_status")
    val muac_status: String = "",
    @field:SerializedName("head_circum_status")
    val head_circum_status: String = "",
    @field:SerializedName("weight_gain")
    val weight_gain: Double? = null,
    @field:SerializedName("gain_status")
    val gain_status: String = "",
    @field:SerializedName("consecutive_no_gain")
    val consecutive_no_gain: Int = 0,
    @field:SerializedName("needs_referral")
    val needs_referral: Boolean = false,
    @field:SerializedName("is_flagged")
    val is_flagged: Boolean = false,
    @field:SerializedName("flag_reason")
    val flag_reason: String = "",
)
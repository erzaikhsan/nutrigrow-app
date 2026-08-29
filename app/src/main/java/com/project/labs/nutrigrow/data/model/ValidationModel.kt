package com.project.labs.nutrigrow.data.model

import com.google.gson.annotations.SerializedName

data class ZScoreSample(
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("age")
    val age: Int,
    @field:SerializedName("weight")
    val weight: Double,
    @field:SerializedName("height")
    val height: Double,
    @field:SerializedName("label")
    val label: String = "",
)

data class ZScoreBatchRequest(
    @field:SerializedName("samples")
    val samples: List<ZScoreSample>,
)

data class ReferenceBand(
    @field:SerializedName("basis")
    val basis: String,
    @field:SerializedName("sd_neg3")
    val sd_neg3: Double,
    @field:SerializedName("sd_neg2")
    val sd_neg2: Double,
    @field:SerializedName("sd_neg1")
    val sd_neg1: Double,
    @field:SerializedName("median")
    val median: Double,
    @field:SerializedName("sd_1")
    val sd_1: Double,
    @field:SerializedName("sd_2")
    val sd_2: Double,
    @field:SerializedName("sd_3")
    val sd_3: Double,
) {
    fun points(): List<Pair<String, Double>> = listOf(
        "−3" to sd_neg3,
        "−2" to sd_neg2,
        "−1" to sd_neg1,
        "M" to median,
        "+1" to sd_1,
        "+2" to sd_2,
        "+3" to sd_3,
    )
}

data class IndicatorResult(
    @field:SerializedName("z_score")
    val z_score: Double?,
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("measured")
    val measured: Double,
    @field:SerializedName("unit")
    val unit: String,
    @field:SerializedName("reference")
    val reference: ReferenceBand?,
)

data class ZScoreCheckModel(
    @field:SerializedName("label")
    val label: String,
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("age")
    val age: Int,
    @field:SerializedName("weight")
    val weight: Double,
    @field:SerializedName("height")
    val height: Double,
    @field:SerializedName("wfa")
    val wfa: IndicatorResult,
    @field:SerializedName("hfa")
    val hfa: IndicatorResult,
    @field:SerializedName("wfh")
    val wfh: IndicatorResult,
) {
    fun indicatorOf(index: String): IndicatorResult = when (index) {
        "BB/U" -> wfa
        "TB/U" -> hfa
        else -> wfh
    }
}

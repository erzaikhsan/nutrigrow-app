package com.project.labs.nutrigrow.ui.screen.validation

import com.google.gson.annotations.SerializedName
import com.project.labs.nutrigrow.data.model.ZScoreCheckModel
import com.project.labs.nutrigrow.data.model.ZScoreSample

data class ValidationSample(
    @field:SerializedName("no")
    val no: Int,
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("age")
    val age: Int,
    @field:SerializedName("weight")
    val weight: Double,
    @field:SerializedName("height")
    val height: Double,
    @field:SerializedName("index")
    val index: String,
    @field:SerializedName("ref_z")
    val ref_z: Double,
    @field:SerializedName("ref_label")
    val ref_label: String,
) {
    fun toRequest(): ZScoreSample = ZScoreSample(
        gender = gender,
        age = age,
        weight = weight,
        height = height,
        label = no.toString(),
    )
}

data class ValidationSampleSet(
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("source")
    val source: String,
    @field:SerializedName("samples")
    val samples: List<ValidationSample>,
)

fun nutritionBand(index: String, z: Double?): Int = when {
    z == null -> -1
    z < -3.0 -> 0
    z < -2.0 -> 1
    index == "TB/U" -> if (z <= 3.0) 2 else 3
    index == "BB/TB" -> when {
        z <= 1.0 -> 2
        z <= 2.0 -> 3
        z <= 3.0 -> 4
        else -> 5
    }
    z <= 1.0 -> 2
    else -> 3
}

data class ValidationRow(
    val sample: ValidationSample,
    val result: ZScoreCheckModel?,
) {
    val appZScore: Double? get() = result?.indicatorOf(sample.index)?.z_score
    val appLabel: String get() = result?.indicatorOf(sample.index)?.status ?: "-"
    val delta: Double? get() = appZScore?.let { it - sample.ref_z }
    val matched: Boolean
        get() = appZScore != null &&
            nutritionBand(sample.index, appZScore) == nutritionBand(sample.index, sample.ref_z)
}

data class ValidationSummary(
    val total: Int,
    val matched: Int,
) {
    val percent: Double get() = if (total == 0) 0.0 else matched * 100.0 / total
}

data class CalculatorForm(
    val gender: String = "M",
    val age: String = "",
    val weight: String = "",
    val height: String = "",
)

package com.project.labs.nutrigrow.utils

const val FIELD_DATE = "date"
const val FIELD_WEIGHT = "weight"
const val FIELD_HEIGHT = "height"
const val FIELD_HEAD = "head_circum"
const val FIELD_ARM = "arm_circum"

const val FIELD_NAME = "full_name"
const val FIELD_GENDER = "gender"
const val FIELD_PLACE = "place_of_birth"
const val FIELD_DOB = "date_of_birth"
const val FIELD_FATHER = "father"
const val FIELD_MOTHER = "mother"
const val FIELD_ORDER = "order_of_child"
const val FIELD_REGION = "region"
const val FIELD_BIRTH_WEIGHT = "birth_weight"
const val FIELD_BIRTH_HEIGHT = "birth_height"
const val FIELD_BIRTH_HEAD = "birth_head_circum"

data class MeasurementRange(
    val label: String,
    val min: Double,
    val max: Double,
    val unit: String,
) {
    fun outOfRangeMessage(): String =
        "$label wajarnya antara ${format(min)} sampai ${format(max)} $unit. Periksa kembali angkanya."

    private fun format(value: Double): String =
        (if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()).replace('.', ',')
}

val WEIGHT_RANGE = MeasurementRange("Berat badan", 1.0, 40.0, "kg")
val HEIGHT_RANGE = MeasurementRange("Tinggi badan", 40.0, 140.0, "cm")
val HEAD_RANGE = MeasurementRange("Lingkar kepala", 25.0, 60.0, "cm")
val ARM_RANGE = MeasurementRange("Lingkar lengan", 7.0, 30.0, "cm")

val BIRTH_WEIGHT_RANGE = MeasurementRange("Berat badan lahir", 0.5, 7.0, "kg")
val BIRTH_HEIGHT_RANGE = MeasurementRange("Tinggi badan lahir", 25.0, 65.0, "cm")
val BIRTH_HEAD_RANGE = MeasurementRange("Lingkar kepala lahir", 20.0, 45.0, "cm")

fun parseMeasurement(input: String): Double? =
    input.trim().replace(',', '.').toDoubleOrNull()

fun validateMeasurement(input: String, range: MeasurementRange): String? {
    if (input.isBlank()) return "${range.label} belum diisi."
    val value = parseMeasurement(input) ?: return "${range.label} harus berupa angka, misalnya 11,2."
    if (value <= 0) return "${range.label} harus lebih besar dari nol."
    if (value < range.min || value > range.max) return range.outOfRangeMessage()
    return null
}

fun requireText(input: String, label: String): String? =
    if (input.isBlank()) "$label belum diisi." else null

fun validateOrderOfChild(input: String): String? {
    if (input.isBlank()) return "Anak ke- belum diisi."
    val value = input.trim().toIntOrNull() ?: return "Anak ke- harus berupa angka bulat."
    if (value < 1 || value > 20) return "Anak ke- wajarnya antara 1 sampai 20."
    return null
}

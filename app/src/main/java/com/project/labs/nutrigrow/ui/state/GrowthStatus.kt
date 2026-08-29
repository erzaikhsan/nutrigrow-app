package com.project.labs.nutrigrow.ui.state

import androidx.compose.ui.graphics.Color

val StatusNormal = Color(0xFF00BF63)
val StatusRingan = Color(0xFFFFEB3B)
val StatusSedang = Color(0xFFFF9800)
val StatusBerat = Color(0xFFF44336)
val StatusTidakDiketahui = Color(0xFF929492)

private val WFA_COLOR = mapOf(
    "Normal" to StatusNormal,
    "Underweight" to StatusSedang,
    "Severely Underweight" to StatusBerat,
    "Risk of Overweight" to StatusRingan,
    "Unknown" to StatusTidakDiketahui,
)

private val HFA_COLOR = mapOf(
    "Normal" to StatusNormal,
    "Stunted" to StatusSedang,
    "Severely Stunted" to StatusBerat,
    "Tall" to StatusRingan,
    "Unknown" to StatusTidakDiketahui,
)

private val WFH_COLOR = mapOf(
    "Normal" to StatusNormal,
    "Wasting" to StatusSedang,
    "Severely Wasting" to StatusBerat,
    "Possible Risk of Overweight" to StatusRingan,
    "Overweight" to StatusSedang,
    "Obese" to StatusBerat,
    "Unknown" to StatusTidakDiketahui,
)

private val MUAC_COLOR = mapOf(
    "Normal" to StatusNormal,
    "Gizi Kurang Akut" to StatusSedang,
    "Gizi Buruk Akut" to StatusBerat,
    "Tidak Berlaku" to StatusTidakDiketahui,
)

private val HCA_COLOR = mapOf(
    "Normal" to StatusNormal,
    "Mikrosefali" to StatusSedang,
    "Makrosefali" to StatusSedang,
    "Unknown" to StatusTidakDiketahui,
)

private val GAIN_COLOR = mapOf(
    "N" to StatusNormal,
    "T" to StatusBerat,
    "O" to StatusTidakDiketahui,
    "B" to StatusRingan,
)

fun wfaColor(status: String): Color = WFA_COLOR[status] ?: StatusTidakDiketahui
fun hfaColor(status: String): Color = HFA_COLOR[status] ?: StatusTidakDiketahui
fun wfhColor(status: String): Color = WFH_COLOR[status] ?: StatusTidakDiketahui
fun muacColor(status: String): Color = MUAC_COLOR[status] ?: StatusTidakDiketahui
fun hcaColor(status: String): Color = HCA_COLOR[status] ?: StatusTidakDiketahui
fun gainColor(status: String): Color = GAIN_COLOR[status] ?: StatusTidakDiketahui

private val WFA_LABEL = mapOf(
    "Severely Underweight" to "Berat Badan Sangat Kurang",
    "Underweight" to "Berat Badan Kurang",
    "Normal" to "Berat Badan Normal",
    "Risk of Overweight" to "Risiko Berat Badan Lebih",
    "Unknown" to "Tidak Diketahui",
)

private val HFA_LABEL = mapOf(
    "Severely Stunted" to "Sangat Pendek",
    "Stunted" to "Pendek",
    "Normal" to "Normal",
    "Tall" to "Tinggi",
    "Unknown" to "Tidak Diketahui",
)

private val WFH_LABEL = mapOf(
    "Severely Wasting" to "Gizi Buruk",
    "Wasting" to "Gizi Kurang",
    "Normal" to "Gizi Baik",
    "Possible Risk of Overweight" to "Berisiko Gizi Lebih",
    "Overweight" to "Gizi Lebih",
    "Obese" to "Obesitas",
    "Unknown" to "Tidak Diketahui",
)

private val GAIN_LABEL = mapOf(
    "N" to "Naik sesuai KBM",
    "T" to "Tidak naik sesuai KBM",
    "O" to "Tidak ditimbang bulan lalu",
    "B" to "Penimbangan pertama",
)

fun wfaLabel(status: String): String = WFA_LABEL[status] ?: status
fun hfaLabel(status: String): String = HFA_LABEL[status] ?: status
fun wfhLabel(status: String): String = WFH_LABEL[status] ?: status
fun gainLabel(status: String): String = GAIN_LABEL[status] ?: status

fun zScoreColor(z: Double?): Color = when {
    z == null -> StatusTidakDiketahui
    z < -3.0 || z > 3.0 -> StatusBerat
    z < -2.0 || z > 2.0 -> StatusSedang
    z > 1.0 -> StatusRingan
    else -> StatusNormal
}

fun formatZScore(z: Double?): String =
    if (z == null) "-" else String.format("%+.2f SD", z)

private val HCA_LABEL = mapOf(
    "Normal" to "Lingkar Kepala Normal",
    "Mikrosefali" to "Mikrosefali",
    "Makrosefali" to "Makrosefali",
    "Unknown" to "Tidak Diketahui",
)

private val MUAC_LABEL = mapOf(
    "Normal" to "Lingkar Lengan Normal",
    "Gizi Kurang Akut" to "Gizi Kurang Akut",
    "Gizi Buruk Akut" to "Gizi Buruk Akut",
    "Tidak Berlaku" to "Tidak Berlaku",
    "Unknown" to "Tidak Diketahui",
)

fun hcaLabel(status: String): String = HCA_LABEL[status] ?: "Tidak Diketahui"
fun muacLabel(status: String): String = MUAC_LABEL[status] ?: "Tidak Diketahui"

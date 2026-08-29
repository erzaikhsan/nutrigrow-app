package com.project.labs.nutrigrow.ui.screen.child

import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun calculateAge(dateOfBirth: String): String {
    val dob = LocalDate.parse(dateOfBirth.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val currentDate = LocalDate.now(ZoneId.systemDefault())
    val period = Period.between(dob, currentDate)
    return "${period.years} tahun ${period.months} bulan"
}

fun formatDate(inputDate: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)
    val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
    val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
    return zonedDateTime.format(outputFormatter)
}

package com.project.labs.nutrigrow.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_italic, style = FontStyle.Italic),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_mediumitalic, FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_semibolditalic, FontWeight.SemiBold, style = FontStyle.Italic),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_bolditalic, FontWeight.Bold, style = FontStyle.Italic),
)

private val default = Typography()

val NutriTypography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = Poppins),
    displayMedium = default.displayMedium.copy(fontFamily = Poppins),
    displaySmall = default.displaySmall.copy(fontFamily = Poppins),

    headlineLarge = default.headlineLarge.copy(fontFamily = Poppins),
    headlineMedium = default.headlineMedium.copy(fontFamily = Poppins),
    headlineSmall = default.headlineSmall.copy(fontFamily = Poppins),

    titleLarge = default.titleLarge.copy(
        fontFamily = Poppins,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleMedium = default.titleMedium.copy(fontFamily = Poppins),
    titleSmall = default.titleSmall.copy(
        fontFamily = Poppins,
        fontSize = 15.sp,
        lineHeight = 21.sp,
    ),

    bodyLarge = default.bodyLarge.copy(fontFamily = Poppins),
    bodyMedium = default.bodyMedium.copy(
        fontFamily = Poppins,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodySmall = default.bodySmall.copy(
        fontFamily = Poppins,
        fontSize = 13.sp,
        lineHeight = 19.sp,
    ),

    labelLarge = default.labelLarge.copy(fontFamily = Poppins),
    labelMedium = default.labelMedium.copy(fontFamily = Poppins),
    labelSmall = default.labelSmall.copy(fontFamily = Poppins),
)

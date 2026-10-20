package com.example.myapplication.ui.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

/** Bundled OFL fonts: no network request and full Cyrillic/Latin coverage. */
@OptIn(ExperimentalTextApi::class)
private fun spaceFontFamily(resource: Int): FontFamily {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return FontFamily.SansSerif
    return FontFamily(listOf(400, 500, 600, 700, 800, 900).map { requestedWeight ->
        val axisWeight = if (resource == R.font.manrope_variable) requestedWeight.coerceAtMost(800) else requestedWeight
        Font(resource, weight = FontWeight(requestedWeight),
            variationSettings = FontVariation.Settings(FontVariation.weight(axisWeight)))
    })
}

val SpaceDisplayFont = spaceFontFamily(R.font.exo2_variable)
val SpaceBodyFont = spaceFontFamily(R.font.manrope_variable)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = SpaceDisplayFont, fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp, lineHeight = 46.sp, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontFamily = SpaceDisplayFont, fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.4).sp),
    displaySmall = TextStyle(
        fontFamily = SpaceDisplayFont,
        fontWeight = FontWeight.Black,
        fontSize = 30.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineLarge = TextStyle(fontFamily = SpaceDisplayFont, fontWeight = FontWeight.Bold,
        fontSize = 26.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(
        fontFamily = SpaceDisplayFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 27.sp
    ),
    headlineSmall = TextStyle(fontFamily = SpaceDisplayFont, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(
        fontFamily = SpaceDisplayFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 23.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceDisplayFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(fontFamily = SpaceDisplayFont, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(
        fontFamily = SpaceBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontFeatureSettings = "tnum"
    ),
    bodyMedium = TextStyle(
        fontFamily = SpaceBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontFeatureSettings = "tnum"
    ),
    bodySmall = TextStyle(fontFamily = SpaceBodyFont, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 18.sp, fontFeatureSettings = "tnum"),
    labelLarge = TextStyle(
        fontFamily = SpaceDisplayFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(fontFamily = SpaceDisplayFont, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.15.sp),
    labelSmall = TextStyle(
        fontFamily = SpaceDisplayFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    )
)

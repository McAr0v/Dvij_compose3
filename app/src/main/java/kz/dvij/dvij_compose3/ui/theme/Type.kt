package kz.dvij.dvij_compose3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kz.dvij.dvij_compose3.R

// Создаем переменную нашего шрифта (копия шрифтов на всякий случай лежит на облаке)

val sfProFont = FontFamily(

    Font(R.font.sfprodisplay_black, weight = FontWeight.Black),
    Font(R.font.sfprodisplay_bold, weight = FontWeight.Bold),
    Font(R.font.sfprodisplay_light, weight = FontWeight.Light),
    Font(R.font.sfprodisplay_medium, weight = FontWeight.Medium),
    Font(R.font.sfprodisplay_regular, weight = FontWeight.Normal),
    Font(R.font.sfprodisplay_semibold, weight = FontWeight.SemiBold),
    Font(R.font.sfprodisplay_thin, weight = FontWeight.Thin)

)

// Создаем шаблоны текста

val Typography = Typography(

    // ОБЫЧНЫЙ ТЕКСТ

    // Обычный текст БОЛЬШОЙ

    bodyLarge = TextStyle(
        fontFamily = sfProFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // Обычный текст СРЕДНИЙ

    bodyMedium = TextStyle(
        fontFamily = sfProFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // Обычный текст МАЛЕНЬКИЙ

    bodySmall = TextStyle(fontFamily = sfProFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp),

displaySmall = TextStyle(fontFamily = sfProFont,
    fontWeight = FontWeight.Normal,
    fontSize = 9.sp,
    lineHeight = 15.sp,
    letterSpacing = 0.5.sp),

    // ЗАГОЛОВКИ

    // Заголовок БОЛЬШОЙ

    titleLarge = TextStyle(
        fontFamily = sfProFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // Заголовок СРЕДНИЙ

    titleMedium = TextStyle(
        fontFamily = sfProFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // Заголовок МАЛЕНЬКИЙ

    titleSmall = TextStyle(
        fontFamily = sfProFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // ПОДПИСИ

    // Подпись БОЛЬШАЯ

    labelLarge = TextStyle(fontFamily = sfProFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp),

    // Подпись СРЕДНЯЯ

    labelMedium = TextStyle(fontFamily = sfProFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp),

    // Подпись МАЛЕНЬКАЯ

    labelSmall = TextStyle(
        fontFamily = sfProFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

)

/* изначальные настройки типографии
val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

)*/
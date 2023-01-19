package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Grey100_95
import kz.dvij.dvij_compose3.ui.theme.PrimaryColor
import kz.dvij.dvij_compose3.ui.theme.Typography

// --------- ЭКРАН "ИДЕТ ЗАГРУЗКА" ----------

@Composable
fun LoadingScreen (messageText: String) {

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey100_95), // полупрозрачный фон
        verticalAlignment = Alignment.CenterVertically, // выравнивание по вертикали
        horizontalArrangement = Arrangement.Center // выравнивание по горизонтали
    ) {

        // ---- ЦИРКУЛЛИРУЮЩИЙ ИНДИКАТОР ------

        CircularProgressIndicator(
            color = PrimaryColor, // цвет крутилки
            strokeWidth = 3.dp, // толщина крутилки
            modifier = Modifier.size(40.dp) // размер крутилки
        )

        Spacer(modifier = Modifier.width(20.dp)) // разделитель между крутилкой и текстом

        Text(
            text = messageText, // текст рядом с крутилкой
            style = Typography.bodyMedium, // стиль текста
            color = Grey10 // цвет
        )
    }
}
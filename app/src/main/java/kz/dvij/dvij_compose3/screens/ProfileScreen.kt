package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.navigation.TopBar
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Typography

// функция превью экрана

@Preview
@Composable
fun ProfileScreen () {

    Column() {
        // создаем колонку, чтобы элементы отображались друг за другом
        TopBar(stringResource(id = R.string.profile)) // вызываем верхнее меню, передаем название страницы
        // ОТ НАЗВАНИЯ СТРАНИЦЫ ЗАВИСИТ ЗАГОЛОВОК МЕНЮ, ВЫЗОВ НУЖНОГО СОДЕРЖАНИЯ СТРАНИЦЫ И ПОКАЗ ЗНАЧКА ФИЛЬТРА!

    }

}


// экран профиля

@Composable
fun ProfileScreenContent (){

    Column (
        modifier = Modifier
            .background(Grey10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "ProfileScreen", style = Typography.titleLarge)
        Text(text = "ProfileScreen", style = Typography.titleMedium)
        Text(text = "ProfileScreen", style = Typography.titleSmall)
        Text(text = "ProfileScreen", style = Typography.bodyLarge)
        Text(text = "ProfileScreen", style = Typography.bodyMedium)
        Text(text = "ProfileScreen", style = Typography.bodySmall)
        Text(text = "ProfileScreen", style = Typography.labelLarge)
        Text(text = "ProfileScreen", style = Typography.labelMedium)
        Text(text = "ProfileScreen", style = Typography.labelSmall)

    }
}





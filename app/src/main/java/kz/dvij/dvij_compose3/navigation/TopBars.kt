package kz.dvij.dvij_compose3.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.Typography

// ДИЗАЙН ВЕРХНЕЙ ПАНЕЛИ
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TopBar(
    topBarName: String, // На вход должны получить заголовок панели
    onNavigationIconClick: () -> Unit // Так же должны получить функцию, которая запускается при нажатии на кнопку меню
){
    // Сама функция верхней панели
    TopAppBar(

        // работаем с заголовком
        title = {
            Text(
                text = topBarName, // Передаем полученный заголовок панели
                color = Grey10, // цвет заголовка
                style = Typography.titleMedium, // Стиль текста заголовка
                maxLines = 1, // максимальное количество строк
                overflow = TextOverflow.Ellipsis // если не влазит текст в контейнер, обрезается точками

            )
        },
        backgroundColor = Grey100, // цвет панели
        contentColor = Grey10, // цвет контента

        // сама навигационная иконка
        navigationIcon = {
            IconButton(
                onClick = onNavigationIconClick // Передаем полученное действие при нажатии на кнопку
            ) {
                // Работаем над самой иконкой:
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu), // передаем значек иконки
                    contentDescription = stringResource(id = R.string.cd_side_menu)) // Описание для слабослышащих
            }
        }
    )
}
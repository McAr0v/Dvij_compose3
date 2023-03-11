package kz.dvij.dvij_compose3.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

// ДИЗАЙН ВЕРХНЕЙ ПАНЕЛИ
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TopBarInApp(
    topBarName: String, // На вход должны получить заголовок панели
    onNavigationIconClick: () -> Unit // Так же должны получить функцию, которая запускается при нажатии на кнопку меню
){
    // Сама функция верхней панели
    TopAppBar(

        // работаем с заголовком
        title = {
            Text(
                text = topBarName, // Передаем полученный заголовок панели
                color = WhiteDvij, // цвет заголовка
                style = Typography.titleMedium, // Стиль текста заголовка
                maxLines = 1, // максимальное количество строк
                overflow = TextOverflow.Ellipsis // если не влазит текст в контейнер, обрезается точками

            )
        },
        backgroundColor = Grey_OnBackground, // цвет панели
        contentColor = WhiteDvij, // цвет контента

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

@Composable
fun TopBarWithBackButton(navController: NavController, text: Int){

    // ---- ПАНЕЛЬ С КНОПКОЙ НАЗАД -------

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(), // на всю ширину
        backgroundColor = Grey_OnBackground, // цвет фона
        contentColor = WhiteDvij // цвет контента
    ) {

        // ----- КНОПКА НАЗАД -------

        IconButton(
            onClick = { navController.popBackStack() } // действие на нажатие
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back), // иконка
                contentDescription = stringResource(id = R.string.cd_go_back), // описание для слабовидящих
                tint = WhiteDvij, // цвет
                modifier = Modifier.size(20.dp) // размер иконки
            )
        }

        Divider(modifier = Modifier.width(20.dp)) // разделитель

        // Заголовок панели

        androidx.compose.material.Text(
            text = stringResource(id = text), // сам текст
            style = Typography.titleMedium, // стиль текста
            color = WhiteDvij // цвет
        )
    }
}
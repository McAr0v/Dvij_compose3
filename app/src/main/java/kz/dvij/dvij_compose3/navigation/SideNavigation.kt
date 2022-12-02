package kz.dvij.dvij_compose3.navigation

import android.media.Image
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.ui.theme.*


// https://semicolonspace.com/jetpack-compose-navigation-drawer/
// https://www.youtube.com/watch?v=JLICaBEiJS0

@Composable
fun HeaderSideNavigation(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Grey100)
            .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 20.dp),
        contentAlignment = Alignment.CenterStart
    ){
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.dvij_logo),
                contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.cd_logo),
                tint = PrimaryColor
            )
        }


    }
}

@Composable
fun AvatarBoxSideNavigation(
    auth: Boolean
){
    if (auth) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Grey100)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.zhanna_avatar),
                contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.cd_avatar),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Макарова Жанна",
                    color = Grey40,
                    style = Typography.titleMedium
                )
                Text(
                    text = "makarovazhanna@mail.ru",
                    color = Grey40,
                    style = Typography.labelMedium
                )

            }

            IconButton(onClick = {}) {
                Icon(
                    tint = Grey40, // цвет иконки
                    painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.ic_edit), // задаем иконку
                    contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.to_change_location) // описание для слабовидящих
                )
            }



        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Grey100)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /*Icon(
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.ic_person),
                contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.cd_avatar),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Grey95),
                tint = Grey40,

            )*/


            Column(
                modifier = Modifier
                    //.padding(start = 20.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(id = kz.dvij.dvij_compose3.R.string.guest),
                    color = Grey40,
                    style = Typography.titleMedium
                )
                Text(
                    text = stringResource(id = kz.dvij.dvij_compose3.R.string.login_or_register),
                    color = Grey40,
                    style = Typography.labelSmall
                )

            }

            IconButton(onClick = {}) {
                Icon(
                    tint = Grey40, // цвет иконки
                    painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.ic_login), // задаем иконку
                    contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.login_or_register) // описание для слабовидящих
                )
            }


        }

    }
    

}

@Composable
fun CityHeaderSideNavigation (city: String) {
    Column(
        modifier = Modifier
            .background(Grey100)
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = kz.dvij.dvij_compose3.R.string.city),
            color = Grey10,
            style = Typography.bodyMedium
        )

        Row (
            modifier = Modifier
                .fillMaxWidth(), // строка должна занимать всю ширину
                //.padding(vertical = 10.dp), // паддинги элементов
            verticalAlignment = Alignment.CenterVertically

        ) {

            // Иконка возле текста
            Icon(
                tint = Grey40, // цвет иконки
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.ic_baseline_places), // задаем иконку
                contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.cd_location) // описание для слабовидящих
            )

            // разделитель между текстом и иконкой
            Spacer(modifier = Modifier.width(15.dp))

            // Сам текст
            Text(
                text = city, // берем заголовок
                style = Typography.labelLarge, // Стиль текста
                modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                color = Grey40 // цвет текста
            )

            // разделитель между текстом и иконкой
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {}) {
                Icon(
                    tint = Grey40, // цвет иконки
                    painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.ic_edit), // задаем иконку
                    contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.to_change_location) // описание для слабовидящих
                )
            }
        }
    }
}

// Функция с элементами бокового меню
@Composable
fun BodySideNavigation(
    navController: NavController, // принимаем НавКонтроллер
    scaffoldState: ScaffoldState // Принимаем состояние скаффолда для реализации закрытия бокового меню после нажатия на элемент
) {
    // Инициализируем список элементов бокового меню
    val sideNavigationItemsList = listOf<SideNavigationItems>(
        SideNavigationItems.About,
        SideNavigationItems.PrivatePolicy,
        SideNavigationItems.Ads,
        SideNavigationItems.Bugs
    )

    val coroutineScope = rememberCoroutineScope() // инициализируем корутину
    val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
    val currentRoute =
        navBackStackEntry?.destination?.route // получаем доступ к корню открытой страницы

    LazyColumn(
        Modifier
            .background(color = Grey100) // окрашиваем в черный
            //.fillMaxHeight() // занимаем весь размер
            .padding(vertical = 20.dp)
    ) {
        // Помещаем все в "ленивую" колонку

        // Начинаем создавать элемент меню

        items(sideNavigationItemsList) { item -> // для каждого итема в списке sideNavigationItemsList

            // Создаем строку (иконка и текст должны идти друг за другом по горизонтали)

            Row(
                modifier = Modifier
                    .fillMaxWidth() // строка должна занимать всю ширину
                    .clickable {
                        // действие на клик
                        navController.navigate(item.navRoute) // открываем нужную страницу

                        // запускаем в корутине действие, чтобы после нажатия на элемент, боковое меню закрывалось
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                    }
                    .padding(vertical = 10.dp, horizontal = 20.dp) // паддинги элементов
            ) {

                // Иконка возле текста
                Icon(
                    tint = if (item.navRoute == currentRoute) PrimaryColor else Grey40, // цвет иконки
                    painter = painterResource(id = item.icon), // задаем иконку, прописанную в sealed class
                    contentDescription = stringResource(id = item.contentDescription) // описание для слабовидящих - вшито тоже в sealed class
                )

                // разделитель между текстом и иконкой
                Spacer(modifier = Modifier.width(15.dp))

                // Сам текст "Кнопки"
                Text(
                    text = stringResource(id = item.title), // берем заголовок
                    style = Typography.labelLarge, // Стиль текста
                    modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                    color = if (item.navRoute == currentRoute) PrimaryColor else Grey40 // цвет текста
                )
            }
        }
    }
}


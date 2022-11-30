package kz.dvij.dvij_compose3.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Header", fontSize = 60.sp)
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
    val currentRoute = navBackStackEntry?.destination?.route // получаем доступ к корню открытой страницы

    LazyColumn (
        Modifier
            .background(color = Grey100) // окрашиваем в черный
            //.fillMaxHeight() // занимаем весь размер
            .padding(vertical = 20.dp)
            ) {
        // Помещаем все в "ленивую" колонку

        // Начинаем создавать элемент меню

        items(sideNavigationItemsList) { item -> // для каждого итема в списке sideNavigationItemsList

            // Создаем строку (иконка и текст должны идти друг за другом по горизонтали)

            Row (
                modifier = Modifier
                    .fillMaxWidth() // строка должна занимать всю ширину
                    .clickable {
                        // действие на клик
                        navController.navigate(item.navRoute) // открываем нужную страницу

                        // запускаем в корутине действие, чтобы после нажатия на элемент, боковое меню закрывалось
                        coroutineScope.launch{
                            scaffoldState.drawerState.close()
                        }
                    }
                    .padding(vertical = 15.dp, horizontal = 20.dp) // паддинги элементов
                    ){

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


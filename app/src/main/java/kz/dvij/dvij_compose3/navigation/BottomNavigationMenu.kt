package kz.dvij.dvij_compose3.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

// Рисуем через обычную @Composable функцию нижнее меню BottomNavigationMenu

@Composable
fun BottomNavigationMenu (navController: NavController) {
    // для работы нужно извне передать нам NavController. NavController инициализируется на MainActivity
    // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.

    // Передаем список созданных кнопок для навигации
    val navItems = listOf(
        BottomNavigationItem.Profile,
        BottomNavigationItem.Meetings,
        BottomNavigationItem.Places,
        BottomNavigationItem.Stock
    )

    // графически создаем непосредственно саму навигацию. Уже есть готовая составная функция BottomNavigation

    BottomNavigation (
        // Здесь обычная настройка, как у текста, Column и тд. Я передал только цвет фона
        backgroundColor = Grey100
            ) {

        // определяем текущий маршрут как состояние, он не может быть статичным.
        // Т.е ведь если мы перейдем на другой экран, текущий маршрут изменится

        val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
        val currentRoute = navBackStackEntry?.destination?.route // теперь мы можем получить текущий путь

        // проходимся по каждому элементу из списка navItems и для каждого создаем внешний вид
        // forEach - это как раз функция, чтобы прошелся по каждому элементу

        navItems.forEach { item ->

            // вызываем составную функцию BottomNavigationItem - нижний элемент навигации.
            // Здесь уже конкретно влияем на внешний вид этих самых элементов нижнего меню.

            BottomNavigationItem(

                selected = currentRoute == item.navRoute, // передаем сюда текущий путь экрана, который выбран

                onClick = {
                    navController.navigate(item.navRoute) // при нажатии на элемент срабатывает, куда нужно перейти
                          },

                label = {
                    // подпись под иконкой
                    Text(
                        text = stringResource(id = item.title),
                        style = Typography.displaySmall
                    )
                        },

                icon = {
                    // сама иконка
                    Icon(painter = painterResource(id = item.icon),
                        tint = if (item.navRoute == currentRoute){PrimaryColor} else {Grey10}, // условие цвета, без него будет просто статично окрашена
                        contentDescription = stringResource(id = item.title), // описание иконки для слабовидящих. Сделаем здесь текст заголовка итема
                        modifier = Modifier
                            .size(24.dp) // размер иконки
                            .padding(bottom = 2.dp)) // отступ от текста под иконкой
                       },
                selectedContentColor = PrimaryColor, // цвет выбранного элемента
                unselectedContentColor = Grey10 // цвет невыбранного элемента

            )
        }
    }
}



// ----- ЛЕТАЮЩАЯ КНОПКА ДОБАВИТЬ -----------

@Composable
fun FloatingButton(onClick: () -> Unit ){

    // помещаем кнопку в BOX
    Box(
        modifier = Modifier
            .fillMaxSize() // занять полный размер
            .fillMaxWidth() // занять всю ширину
            .fillMaxHeight() // занять всю высоту
    ) {

        // --- САМА КНОПКА -------

        FloatingActionButton(

            onClick = { // действие на нажатие
                onClick()
            },
            shape = CircleShape, // форма кнопки
            contentColor = Grey00, // цвет содержимого кнопки
            containerColor = SuccessColor, // цвет фона кнопки
            modifier = Modifier
                .padding(16.dp) // отступы
                .align(alignment = Alignment.BottomEnd) // выравнивание кнопки в боке
        ) {
            // ------- СОДЕРЖИМОЕ КНОПКИ --------
            Icon(
                painter = painterResource(id = R.drawable.ic_add), // сама иконка
                contentDescription = stringResource(id = R.string.cd_create) // описание для слабовидящих
            )
        }
    }
}
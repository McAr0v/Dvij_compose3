package kz.dvij.dvij_compose3.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.PrimaryColor
import kz.dvij.dvij_compose3.ui.theme.Typography

// Рисуем через обычную @Composable функцию нижнее меню BottomNavigationMenu
@Composable

fun BottomNavigationMenu (navController: NavController) {
    // для работы нужно извне передать нам NavController. NavController инициализируется на MainActivity
    // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.

    // Передаем список созданных кнопок для навигации
    val navItems = listOf(BottomNavigationItem.Profile, BottomNavigationItem.Meetings, BottomNavigationItem.Places, BottomNavigationItem.Stock )

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
        // forEach - это как раз функция, чтобы прошелска по каждому элементу

        navItems.forEach { item ->

            // вызываем составную функцию BottomNavigationItem - нижний элемент навигации.
            // Здесь уже конкретно влияем на внешний вид этих самых элементов нижнего меню.

            BottomNavigationItem(

                selected = currentRoute == item.navRoute, // передаем сюда текущий путь экрана, который выбран
                onClick = { navController.navigate(item.navRoute) }, // при нажатии на элемент срабатывает, куда нужно перейти
                label = { Text(text = stringResource(id = item.title), style = Typography.labelSmall)}, // подпись под иконкой
                icon = { // сама иконка
                    Icon(painter = painterResource(id = item.icon),
                        tint = if (item.navRoute == currentRoute){PrimaryColor} else {Grey10}, // условие цвета, без него будет просто статично окрашена
                        contentDescription = "${item.title}", // если не ошибаюсь, описание иконки для слабовидящих
                        modifier = Modifier.size(24.dp). // размер иконки
                        padding(bottom = 2.dp)) // отступ от текста под иконкой
                       },
                selectedContentColor = PrimaryColor, // цвет выбранного элемента
                unselectedContentColor = Grey10 // цвет невыбранного элемента

            )
        }
    }
}
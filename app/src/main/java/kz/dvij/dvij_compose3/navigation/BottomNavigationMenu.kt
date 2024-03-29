package kz.dvij.dvij_compose3.navigation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
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
        backgroundColor = Grey_OnBackground
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
                        tint = if (item.navRoute == currentRoute){YellowDvij} else {WhiteDvij}, // условие цвета, без него будет просто статично окрашена
                        contentDescription = stringResource(id = item.title), // описание иконки для слабовидящих. Сделаем здесь текст заголовка итема
                        modifier = Modifier
                            .size(24.dp) // размер иконки
                            .padding(bottom = 2.dp)) // отступ от текста под иконкой
                       },
                selectedContentColor = YellowDvij, // цвет выбранного элемента
                unselectedContentColor = WhiteDvij // цвет невыбранного элемента

            )
        }
    }
}

// ----- ЛЕТАЮЩАЯ КНОПКА ДОБАВИТЬ -----------

@Composable
fun FloatingButton(
    onClick: () -> Unit
){

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
            contentColor = Grey_OnBackground, // цвет содержимого кнопки
            containerColor = YellowDvij, // цвет фона кнопки
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

// ----- ЛЕТАЮЩАЯ КНОПКА ФИЛЬТРА МЕРОПРИЯТИЙ -----------

@Composable
fun FloatingMeetingFilterButton(
    city: String = "Выбери город",
    category: String = "Выбери категорию",
    date: String = "Выбери дату",
    typeOfFilter: String,
    onClick: () -> Unit
){

    val counter = when (typeOfFilter){

        "cityCategoryDate" -> {
            " (3)"
        }
        "cityCategory" -> {
            " (2)"
        }
        "cityDate" -> {
            " (2)"
        }
        "city" -> {
            " (1)"
        }
        "categoryDate" -> {
            " (2)"
        }
        "category" -> {
            " (1)"
        }
        "date" -> {
            " (1)"
        }
        "noFilter" -> {
            ""
        }

        else -> {""}
    }

    val containerColor = remember {
        mutableStateOf(YellowDvij)
    }

    val contentColor = remember {
        mutableStateOf(Grey_OnBackground)
    }

    if (city != "Выбери город" || category != "Выбери категорию" || date != "Выбери дату") {
        containerColor.value = YellowDvij
        contentColor.value = Grey_OnBackground
    } else {
        containerColor.value = Grey_ForCards
        contentColor.value = WhiteDvij
    }

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
            contentColor = contentColor.value, // цвет содержимого кнопки
            containerColor = containerColor.value, // цвет фона кнопки
            modifier = Modifier
                .padding(16.dp) // отступы
                .align(alignment = Alignment.BottomEnd) // выравнивание кнопки в боке
        ) {

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(15.dp)
            ){

                Text(text = "Фильтр$counter", color = contentColor.value)

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_filter), // сама иконка
                    contentDescription = "Кнопка фильтра" // описание для слабовидящих
                )

            }
        }
    }
}

// ----- ЛЕТАЮЩАЯ КНОПКА ФИЛЬТРА ЗАВЕДЕНИЙ -----------

@Composable
fun FloatingPlaceFilterButton(
    city: String = "Выбери город",
    category: String = "Выбери категорию",
    typeOfFilter: String,
    isOpen: Boolean,
    onClick: () -> Unit
){

    val counter = when (typeOfFilter){

        "cityCategory" -> {
            if (isOpen){
                " (3)"
            } else {" (2)" }

        }

        "city" -> {
            if (isOpen){
                " (2)"
            } else {" (1)"}

        }

        "category" -> {
            if (isOpen){
                " (2)"
            } else {" (1)"}

        }

        "noFilter" -> {
            if (isOpen){
                " (1)"
            } else {""}

        }

        else -> {""}
    }

    val containerColor = remember {
        mutableStateOf(YellowDvij)
    }

    val contentColor = remember {
        mutableStateOf(Grey_OnBackground)
    }

    if (city != "Выбери город" || category != "Выбери категорию" || isOpen) {
        containerColor.value = YellowDvij
        contentColor.value = Grey_OnBackground
    } else {
        containerColor.value = Grey_ForCards
        contentColor.value = WhiteDvij
    }

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
            contentColor = contentColor.value, // цвет содержимого кнопки
            containerColor = containerColor.value, // цвет фона кнопки
            modifier = Modifier
                .padding(16.dp) // отступы
                .align(alignment = Alignment.BottomEnd) // выравнивание кнопки в боке
        ) {

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(15.dp)
            ){

                Text(text = "Фильтр$counter", color = contentColor.value)

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_filter), // сама иконка
                    contentDescription = "Кнопка фильтра" // описание для слабовидящих
                )

            }
        }
    }
}

// ----- ЛЕТАЮЩАЯ КНОПКА ФИЛЬТРА МЕРОПРИЯТИЙ -----------

@Composable
fun FloatingStockFilterButton(
    city: String = "Выбери город",
    category: String = "Выбери категорию",
    startDate: String = "Выбери дату",
    finishDate: String = "Выбери дату",
    typeOfFilter: String,
    onClick: () -> Unit
){

    val counter = when (typeOfFilter){

        "cityCategoryStartFinish" -> " (4)"
        "cityCategoryStart" -> " (3)"
        "cityCategoryFinish" -> " (3)"
        "cityStartFinish" -> " (3)"
        "cityCategory" -> " (2)"
        "cityFinish" -> " (2)"
        "cityStart" -> " (2)"
        "city" -> " (1)"
        "categoryStartFinish" -> " (3)"
        "categoryStart" -> " (2)"
        "categoryFinish" -> " (2)"
        "startFinish" -> " (2)"
        "category" -> " (1)"
        "finish" -> " (1)"
        "start" -> " (1)"
        "noFilter" -> ""

        else -> ""
    }

    val containerColor = remember {
        mutableStateOf(YellowDvij)
    }

    val contentColor = remember {
        mutableStateOf(Grey_OnBackground)
    }

    if (city != "Выбери город" || category != "Выбери категорию" || startDate != "Выбери дату" || finishDate != "Выбери дату") {
        containerColor.value = YellowDvij
        contentColor.value = Grey_OnBackground
    } else {
        containerColor.value = Grey_ForCards
        contentColor.value = WhiteDvij
    }

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
            contentColor = contentColor.value, // цвет содержимого кнопки
            containerColor = containerColor.value, // цвет фона кнопки
            modifier = Modifier
                .padding(16.dp) // отступы
                .align(alignment = Alignment.BottomEnd) // выравнивание кнопки в боке
        ) {

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(15.dp)
            ){

                Text(text = "Фильтр$counter", color = contentColor.value)

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_filter), // сама иконка
                    contentDescription = "Кнопка фильтра" // описание для слабовидящих
                )

            }
        }
    }
}
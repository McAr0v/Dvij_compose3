package kz.dvij.dvij_compose3.navigation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.screens.AboutScreen
import kz.dvij.dvij_compose3.screens.ProfileScreenContent
import kz.dvij.dvij_compose3.ui.theme.Grey00
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.Typography

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TopBar(
    topBarName: String
){

    // Создаем верхнее меню для главных страниц

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val sideNavigationItemsList = listOf<SideNavigationItems>(SideNavigationItems.About, SideNavigationItems.PrivatePolicy, SideNavigationItems.Ads, SideNavigationItems.Bugs)

    val navControllerSide = rememberNavController()

    // помещаем верхнее меню в Scaffold

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { // Код самого верхнего меню
            TopAppBar(
                title = { Text(text = topBarName, color = Grey10, style = Typography.titleSmall) }, // заголовок. Текст заголовка - topBarName.
                // ОТ ТЕКСТА ЗАГОЛОВКА ЕСТЬ ЗАВИСИМОСТЬ КАКУЮ ВЫДАВАТЬ СТРАНИЦУ! (СМ НИЖЕ)
                backgroundColor = Grey100, // цвет фона
                contentColor = Grey10, // цвет контента
                navigationIcon = { // Иконка навигации
                    IconButton(
                        onClick = {coroutineScope.launch { scaffoldState.drawerState.open() }} // действие на нажатие. СЮДА ВСТАВИТЬ КОД ВЫЗОВА ВЫДВИГАЮЩЕГОСЯ МЕНЮ
                    ) {
                            Icon(painter = painterResource(id = R.drawable.ic_menu), contentDescription = "") // Сама иконка меню
                    }
                },

                actions = {
                    // дополнительные иконки справа. Туда вставить фильтр И ВОЗМОЖНО КНОПКУ ИЗБРАННЫЕ

                    // условие показа дополнительных иконок - если не равно Профиль, то показывать

                            if (topBarName != stringResource(id = R.string.profile)){
                                Row() { // помещаем в строку, чтобы элементы были друг за другом по горизонтали
                                    Icon( // добавляем иконку фильтра
                                        painter = painterResource(id = R.drawable.ic_filter), // сам ресурс иконки
                                        contentDescription = "", // описание для слабовидящих
                                        modifier = Modifier.padding(end = 12.dp), // паддинг справа
                                        tint = Grey00 // цвет иконки
                                    )
                                }
                            }
                        }
                    )
                 },
        drawerContent = {
            HeaderSideNavigation()
            BodySideNavigation(items = sideNavigationItemsList, onItemClick = {

            })
        },
        content = { // наполнение под топ меню
            // создаем колонку для паддингов (как всегда, без них не работает)
            Column(
            modifier = Modifier
                .padding(PaddingValues()) // указываем как раз паддинги
                .fillMaxWidth(), // занять максимальную ширину
            verticalArrangement = Arrangement.Center, // вертикальное выравнивание по центру
            horizontalAlignment = Alignment.CenterHorizontally // Горизонтальное выравнивание по центру
        ) {
                // само наполнение. Условие отображения нужных страниц
               when (topBarName) { // если название X, то показываем соответствующую страницу

                    stringResource(id = R.string.meetings) -> TabMenu(bottomPage = MEETINGS_ROOT)
                    stringResource(id = R.string.stock) -> TabMenu(bottomPage = STOCK_ROOT)
                    stringResource(id = R.string.places) -> TabMenu(bottomPage = PLACES_ROOT)
                    stringResource(id = R.string.profile) -> ProfileScreenContent()
                }
            }
       }
    )
}


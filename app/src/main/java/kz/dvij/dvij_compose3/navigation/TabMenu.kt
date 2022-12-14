package kz.dvij.dvij_compose3.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.screens.*
import kz.dvij.dvij_compose3.ui.theme.*


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable

// ДИЗАЙН И ФУНКЦИОНАЛ ТАБОВ (МОИ, ИЗБРАННЫЕ, ЛЕНТА) В РАЗДЕЛАХ МЕРОПРИЯТИЯ, ЗАВЕДЕНИЯ, АКЦИИ

fun TabMenu (bottomPage: String, navController: NavController, activity: MainActivity){

    // bottomPage принимаем для того, чтобы использовать одно меню для отображения на разных страницах
    // (Смотри Horizontal Pager)


    val tabItem = listOf(TabName.TapeTab, TabName.FavTab, TabName.MyTab ) // делаем список из заготовленных табов в TabItem
    val pagerState = rememberPagerState(pageCount = tabItem.size) // Инициализируем pagerState. pageCount - количество страниц в меню табов. Прописываем не цифрой, а количеством итомов табов в списке tabItem
    val tabIndex = pagerState.currentPage // создаем переменную tabIndex. Это будет текущая страница
    val coroutineScope = rememberCoroutineScope() // инициализируем корутину

    // Начинаем делать дизайн меню табов
    // помещаем в контейнер Column

    Column() {
        TabRow( // выбираем TabRow - чтобы табы были по горизонтали
            selectedTabIndex = tabIndex, // указываем, что выбранный индекс это индекс текущей открытой страницы

            // настраиваем индикатор под названиями табов
            indicator = { pos ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, pos),
                            color = PrimaryColor
                        )
            },
            contentColor = Grey10, // цвет контента
            backgroundColor = Grey100 // цвет фона табов
        ) {
            // начинаем отрабатывать для каждого таба дизайн
            tabItem.forEachIndexed{index, tabItems ->
                Tab(selected = pagerState.currentPage == index, // указываем индекс выбранного таба
                    onClick = { //действие на клик по табу
                        coroutineScope.launch { // запускаем во второстепенном потоке переход на выбранный таб
                        pagerState.animateScrollToPage(index) // сам переход на страницу index, которую указываем ниже в HorizontalPager
                    }
                              },
                    selectedContentColor = PrimaryColor, // цвет выбранного таба
                    unselectedContentColor = Grey30, // цвет не выбранного тада
                    text = {
                        Text(text = stringResource(id = tabItems.title), // Заголовок берем из табов
                            style = Typography.labelMedium) // указываем стиль текста
                    }

                )
            }
        }

        HorizontalPager(
            state = pagerState, // за состояние отвечает pagerState
        ) { page -> // настраиваем страницы


            // делаем условие - в зависимости от того, какая выбрана страница, табы будут открывать тоже разные страницы.
            // Это сделано для того, чтобы таб меню было как шаблон, и не пришлось отрисовывать его для каждой страницы

            when (bottomPage) {

                // если мероприятия

                MEETINGS_ROOT -> {
                    // в завимисости от того, какой индекс страницы
                    when (page) {
                        0 -> activity.meetingsScreens.MeetingsTapeScreen(navController = navController) // мероприятия Лента
                        1 -> activity.meetingsScreens.MeetingsFavScreen(navController) // мероприятия Избранные
                        else -> activity.meetingsScreens.MeetingsMyScreen(navController) // мероприятия Мои
                }
                }

                PLACES_ROOT -> {
                    // в завимисости от того, какой индекс страницы
                    when (page) {
                        0 -> activity.placesScreens.PlacesTapeScreen() // заведения Лента
                        1 -> activity.placesScreens.PlacesFavScreen() // заведения Избранные
                        else -> activity.placesScreens.PlacesMyScreen() // заведения Мои
                    }
                }
                else -> {
                    // в завимисости от того, какой индекс страницы
                    when (page) {
                        0 -> activity.stockScreen.StockTapeScreen() // Акции Лента
                        1 -> activity.stockScreen.StockFavScreen() // Акции Избранные
                        else -> activity.stockScreen.StockMyScreen() // Акции МОИ
                    }
                }
            }
        }
    }
}
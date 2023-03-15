package kz.dvij.dvij_compose3.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.tapesscreens.*
import kz.dvij.dvij_compose3.ui.theme.*


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable

// ДИЗАЙН И ФУНКЦИОНАЛ ТАБОВ (МОИ, ИЗБРАННЫЕ, ЛЕНТА) В РАЗДЕЛАХ МЕРОПРИЯТИЯ, ЗАВЕДЕНИЯ, АКЦИИ

fun TabMenu (
    bottomPage: String,
    navController: NavController,
    activity: MainActivity,
    meetingKey: MutableState<String>? = null,
    placesKey: MutableState<String>? = null,
    stockKey: MutableState<String>? = null,
    cityForFilter: MutableState<String>? = null,
    meetingCategoryForFilter: MutableState<String>? = null,
    meetingStartDateForFilter: MutableState<String>? = null,
    meetingFinishDateForFilter: MutableState<String>? = null,
    meetingSortingForFilter: MutableState<String>? = null,
    stockCategoryForFilter: MutableState<String>? = null,
    stockStartDateForFilter: MutableState<String>? = null,
    stockFinishDateForFilter: MutableState<String>? = null,
    stockSortingForFilter: MutableState<String>? = null,
    placeCategoryForFilter: MutableState<String>? = null,
    placeIsOpenForFilter: MutableState<Boolean>? = null,
    placeSortingForFilter: MutableState<String>? = null,
    filledMeeting: MutableState<MeetingsAdsClass>? = null,
    filledPlace: MutableState<PlacesAdsClass>? = null,
    filledStock: MutableState<StockAdsClass>? = null
){

    // bottomPage принимаем для того, чтобы использовать одно меню для отображения на разных страницах
    // (Смотри Horizontal Pager)


    val tabItem = listOf(TabName.TapeTab, TabName.FavTab, TabName.MyTab ) // делаем список из заготовленных табов в TabItem
    val pagerState = rememberPagerState(pageCount = tabItem.size) // Инициализируем pagerState. pageCount - количество страниц в меню табов. Прописываем не цифрой, а количеством итомов табов в списке tabItem
    val tabIndex = pagerState.currentPage // создаем переменную tabIndex. Это будет текущая страница
    val coroutineScope = rememberCoroutineScope() // инициализируем корутину

    // Начинаем делать дизайн меню табов
    // помещаем в контейнер Column

    Column {
        TabRow( // выбираем TabRow - чтобы табы были по горизонтали
            selectedTabIndex = tabIndex, // указываем, что выбранный индекс это индекс текущей открытой страницы

            // настраиваем индикатор под названиями табов
            indicator = { pos ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, pos),
                            color = YellowDvij
                        )
            },
            contentColor = WhiteDvij, // цвет контента
            backgroundColor = Grey_OnBackground // цвет фона табов
        ) {
            // начинаем отрабатывать для каждого таба дизайн
            tabItem.forEachIndexed{index, tabItems ->
                Tab(selected = pagerState.currentPage == index, // указываем индекс выбранного таба
                    onClick = { //действие на клик по табу
                        coroutineScope.launch { // запускаем во второстепенном потоке переход на выбранный таб
                        pagerState.animateScrollToPage(index) // сам переход на страницу index, которую указываем ниже в HorizontalPager
                    }
                              },
                    selectedContentColor = YellowDvij, // цвет выбранного таба
                    unselectedContentColor = WhiteDvij, // цвет не выбранного тада
                    text = {
                        Text(text = stringResource(id = tabItems.title), // Заголовок берем из табов
                            style = Typography.bodySmall) // указываем стиль текста
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
                        0 -> meetingKey?.let {
                            activity.meetingsScreens.MeetingsTapeScreen(
                                navController = navController,
                                meetingKey = it,
                                cityForFilter = cityForFilter!!,
                                meetingCategoryForFilter = meetingCategoryForFilter!!,
                                meetingStartDateForFilter = meetingStartDateForFilter!!,
                                meetingFinishDateForFilter = meetingFinishDateForFilter!!,
                                meetingSortingForFilter = meetingSortingForFilter!!,
                                filledMeeting = filledMeeting!!,
                                filledPlace = filledPlace!!
                            )
                        } // мероприятия Лента
                        1 -> meetingKey?.let {
                            activity.meetingsScreens.MeetingsFavScreen(
                                navController,
                                it,
                                filledMeeting = filledMeeting!!,
                                filledPlace = filledPlace!!
                            )
                        } // мероприятия Избранные
                        else -> meetingKey?.let {
                            activity.meetingsScreens.MeetingsMyScreen(
                                navController,
                                it,
                                filledMeeting = filledMeeting!!,
                                filledPlace = filledPlace!!
                            )
                        } // мероприятия Мои
                }
                }

                PLACES_ROOT -> {
                    // в завимисости от того, какой индекс страницы
                    when (page) {
                        0 -> placesKey?.let { activity.placesScreens.PlacesTapeScreen(
                            navController = navController,
                            placeKey = it,
                            cityForFilter = cityForFilter!!,
                            placeSortingForFilter = placeSortingForFilter!!,
                            placeCategoryForFilter = placeCategoryForFilter!!,
                            placeIsOpenForFilter = placeIsOpenForFilter!!
                        ) } // заведения Лента
                        1 -> placesKey?.let { activity.placesScreens.PlacesFavScreen(navController = navController, placeKey = it) } // заведения Избранные
                        else -> placesKey?.let { activity.placesScreens.PlacesMyScreen(navController = navController, placeKey = it) } // заведения Мои
                    }
                }
                else -> {
                    // в завимисости от того, какой индекс страницы
                    when (page) {
                        0 -> stockKey?.let { activity.stockScreen.StockTapeScreen(
                            navController = navController,
                            stockKey = it,
                            cityForFilter = cityForFilter!!,
                            stockCategoryForFilter = stockCategoryForFilter!!,
                            stockStartDateForFilter = stockStartDateForFilter!!,
                            stockFinishDateForFilter = stockFinishDateForFilter!!,
                            stockSortingForFilter = stockSortingForFilter!!,
                            filledStockInfoFromAct = filledStock!!,
                            filledPlace = filledPlace!!

                        ) } // Акции Лента
                        1 -> stockKey?.let { activity.stockScreen.StockFavScreen(
                            navController = navController,
                            stockKey = it,
                            filledStockInfoFromAct = filledStock!!,
                            filledPlace = filledPlace!!
                        ) } // Акции Избранные
                        else -> stockKey?.let { activity.stockScreen.StockMyScreen(
                            navController = navController,
                            stockKey = it,
                            filledStockInfoFromAct = filledStock!!,
                            filledPlace = filledPlace!!
                        ) } // Акции МОИ
                    }
                }
            }
        }
    }
}
package kz.dvij.dvij_compose3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.screens.*
import kz.dvij.dvij_compose3.ui.theme.Grey100

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController() // обязательная строчка для того, чтобы нижнее меню работало. Инициализируем navController
            // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.

            // Непосредственно само нижнее меню:
            // Нижнее меню нужно поместить в Scaffold (это типа "пространство". Как Column, Row, только Scaffold)
            val coroutineScope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()
            val sideNavigationItemsList = listOf<SideNavigationItems>(SideNavigationItems.About, SideNavigationItems.PrivatePolicy, SideNavigationItems.Ads, SideNavigationItems.Bugs)
            val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
            val currentRoute = navBackStackEntry?.destination?.route


            androidx.compose.material.Scaffold(

                scaffoldState = scaffoldState,
                bottomBar = { BottomNavigationMenu(navController = navController) },
                topBar = {
                         TopBar(
                             topBarName = stringResource(id = when (currentRoute) {
                                 null -> R.string.meetings
                                 MEETINGS_ROOT -> R.string.meetings
                                 PLACES_ROOT -> R.string.places
                                 STOCK_ROOT -> R.string.stock
                                 PROFILE_ROOT -> R.string.profile
                                 ABOUT_ROOT -> R.string.side_about
                                 POLICY_ROOT -> R.string.side_private_policy
                                 ADS_ROOT -> R.string.side_ad
                                 BUGS_ROOT -> R.string.side_report_bug
                                 else -> R.string.app_name
                             }),
                             onNavigationIconClick = {
                                 coroutineScope.launch { scaffoldState.drawerState.open() }
                             }
                         )
                         },

                drawerContent = {
                    HeaderSideNavigation()
                    BodySideNavigation(items = sideNavigationItemsList, navController = navController, scaffoldState)
                }// вызываем функцию, где рисуется наше нижнее меню и передаем туда navController
                // параметр paddingValues обычно находится тоже в круглых скобках,
                // но можно вынести его отдельно, как лямбда (см.ниже)
                )
            // начиная с какой то версии materials требуется указывать паддинги.
            // Это реализуется путем передачи через лямду paddingValues (т.е вынести в фигурные скобки как будет ниже), а затем
            // в paddingValues мы передаем Column.
            // Т.е мы помещаем элементы нижнего меню в Column

            { paddingValues ->
                // как я и писал - помещаем все в колонку
                Column(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxWidth())
                {

                    // Навигационный хост. Здесь мы казываем,
                    // какой элемент нажат (пример MEETINGS_ROOT), и куда нужно перейти {например MeetingScreen()},

                    NavHost(
                        navController = navController, // указываем navController
                        startDestination = MEETINGS_ROOT // При первом открытии приложения какой элемент будет выбран по умолчанию сразу
                    ) {

                        // прописываем путь элемента, нажав на который куда нужно перейти

                        composable(MEETINGS_ROOT) { MeetingsScreen()}
                        composable(PLACES_ROOT) { PlacesScreen()}
                        composable(STOCK_ROOT) { StockScreen()}
                        composable(PROFILE_ROOT) { ProfileScreen()}
                        composable(ABOUT_ROOT) { AboutScreen()}
                        composable(POLICY_ROOT) { PrivatePolicyScreen()}
                        composable(ADS_ROOT) { AdsScreen() }
                        composable(BUGS_ROOT) { BugsScreen() }

                    }
                }
            }
        }
    }
}
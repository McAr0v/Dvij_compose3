package kz.dvij.dvij_compose3

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.screens.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController() // обязательная строчка для того, чтобы нижнее меню работало. Инициализируем navController
            // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.

            // Непосредственно само нижнее меню:
            // Нижнее меню нужно поместить в Scaffold (это типа "пространство". Как Column, Row, только Scaffold)


            androidx.compose.material.Scaffold(
                bottomBar = { BottomNavigationMenu(navController = navController) } // вызываем функцию, где рисуется наше нижнее меню и передаем туда navController
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
                        startDestination = MEETINGS_TAPE_ROOT // При первом открытии приложения какой элемент будет выбран по умолчанию сразу
                    ) {

                        // прописываем путь элемента, нажав на который куда нужно перейти

                        composable(MEETINGS_TAPE_ROOT) { MeetingsTapeScreen()}
                        composable(PLACES_TAPE_ROOT) { PlacesTapeScreen() }
                        composable(STOCK_TAPE_ROOT) { StockTapeScreen() }
                        composable(PROFILE_ROOT) { ProfileScreen() }
                    }
                }
            }
        }
    }
}
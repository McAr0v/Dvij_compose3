package kz.dvij.dvij_compose3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.screens.MeetingsScreen
import kz.dvij.dvij_compose3.screens.PlacesScreen
import kz.dvij.dvij_compose3.screens.ProfileScreen
import kz.dvij.dvij_compose3.screens.StockScreen


// MainActivity - то активити, которое открывается первым при запуске приложения. Наследуется от ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            androidx.compose.material.Scaffold(bottomBar = { BottomNavigationMenu(navController = navController) })
            { paddingValues ->
                Column(Modifier.padding(paddingValues).fillMaxWidth())
                {
                    NavHost(
                        navController = navController,
                        startDestination = MEETINGS_ROOT
                    ) {
                        composable(MEETINGS_ROOT) { MeetingsScreen() }
                        composable(PLACES_ROOT) { PlacesScreen() }
                        composable(STOCK_ROOT) { StockScreen() }
                        composable(PROFILE_ROOT) { ProfileScreen() }
                    }
                }
            }

        }
    }
}
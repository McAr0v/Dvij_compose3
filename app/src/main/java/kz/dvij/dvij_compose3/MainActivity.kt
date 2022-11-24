package kz.dvij.dvij_compose3


import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kz.dvij.dvij_compose3.ui.theme.*


// MainActivity - то активити, которое открывается первым при запуске приложения. Наследуется от ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // разметка создается в функции setContent
        setContent {
            val navController = rememberNavController()

            MeetingsScreen()

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



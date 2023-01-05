package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.navigation.PLACES_ROOT
import kz.dvij.dvij_compose3.navigation.TabMenu
import kz.dvij.dvij_compose3.ui.theme.Primary10

// функция превью экрана
class PlacesScreens (val act: MainActivity) {

    private val user = act.mAuth.currentUser

    @Composable
    fun PlacesScreen(navController: NavController) {

        Column() {

            TabMenu(bottomPage = PLACES_ROOT, navController, activity = act, null)

        }
    }


// экран заведений

    @Composable
    fun PlacesTapeScreen() {
        Column(
            modifier = Modifier
                .background(Primary10)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "PlacesScreen TAPE")

        }
    }


    @Composable
    fun PlacesFavScreen() {
        Column(
            modifier = Modifier
                .background(Primary10)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "PlacesScreen FAV")

        }
    }

    @Composable
    fun PlacesMyScreen() {
        Column(
            modifier = Modifier
                .background(Primary10)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "PlacesScreen MY")

        }
    }
}
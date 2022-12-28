package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.Primary10

// функция превью экрана


@Composable
fun PlacesScreen (navController: NavController, user: FirebaseUser?) {

    Column() {

        TabMenu(bottomPage = PLACES_ROOT, navController, user)

    }
}


// экран заведений

@Composable
fun PlacesTapeScreen (){
    Column (
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
fun PlacesFavScreen (){
    Column (
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
fun PlacesMyScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "PlacesScreen MY")

    }
}
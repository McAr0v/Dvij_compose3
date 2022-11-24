package kz.dvij.dvij_compose3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Primary10

// функция превью экрана
@Preview
@Composable
fun LookChangesPlaces () {
    PlacesScreen()
}


// экран профиля
@Composable
fun PlacesScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "PlacesScreen")

    }
}
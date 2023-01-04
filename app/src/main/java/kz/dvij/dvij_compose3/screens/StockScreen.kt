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
import com.google.firebase.auth.FirebaseUser
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.Primary70

// функция превью экрана

@Composable
fun StockScreen (navController: NavController, user: FirebaseUser?) {

    Column {

        TabMenu(bottomPage = STOCK_ROOT, navController, user)
    }

}


// экран акций

@Composable
fun StockTapeScreen (){
    Column (
        modifier = Modifier
            .background(Primary70)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "StockScreen TAPE")

    }
}

@Composable
fun StockMyScreen (){
    Column (
        modifier = Modifier
            .background(Primary70)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "StockScreen MY")

    }
}

@Composable
fun StockFavScreen (){
    Column (
        modifier = Modifier
            .background(Primary70)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "StockScreen FAV")

    }
}
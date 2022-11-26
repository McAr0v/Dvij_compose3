package kz.dvij.dvij_compose3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kz.dvij.dvij_compose3.screens.PlacesTapeScreen

class ProbaClass : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlacesTapeScreen()
        }
    }
}
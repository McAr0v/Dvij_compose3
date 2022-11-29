package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kz.dvij.dvij_compose3.ui.theme.Primary10
import kz.dvij.dvij_compose3.ui.theme.Primary30

@Composable
fun AdsScreen(){
    Column (
        modifier = Modifier
            .background(Primary30)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "AdsScreen")

    }
}
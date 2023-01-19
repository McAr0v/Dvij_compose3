package kz.dvij.dvij_compose3.tapesscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kz.dvij.dvij_compose3.ui.theme.Primary40

@Composable
fun PrivatePolicyScreen(){
    Column (
        modifier = Modifier
            .background(Primary40)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "PrivatePolicy Screen")

    }
}
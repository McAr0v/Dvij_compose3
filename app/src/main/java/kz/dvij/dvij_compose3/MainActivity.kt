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
import kz.dvij.dvij_compose3.ui.theme.*


// MainActivity - то активити, которое открывается первым при запуске приложения. Наследуется от ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var count = 0

        // разметка создается в функции setContent
        setContent {
            CustomDvijTheme {
                Column (
                    modifier = Modifier
                        .background(Grey100)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MeetingCard()
                }
            }
        }
    }
}



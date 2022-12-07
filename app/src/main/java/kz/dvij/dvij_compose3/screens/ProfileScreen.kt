package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Typography

// функция превью экрана


@Composable
fun ProfileScreen () {
    ProfileScreenContent()
}


// экран профиля

@Composable
fun ProfileScreenContent (){

    Column (
        modifier = Modifier
            .background(Grey10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "ProfileScreen", style = Typography.titleLarge)
        Text(text = "ProfileScreen", style = Typography.titleMedium)
        Text(text = "ProfileScreen", style = Typography.titleSmall)
        Text(text = "ProfileScreen", style = Typography.bodyLarge)
        Text(text = "ProfileScreen", style = Typography.bodyMedium)
        Text(text = "ProfileScreen", style = Typography.bodySmall)
        Text(text = "ProfileScreen", style = Typography.labelLarge)
        Text(text = "ProfileScreen", style = Typography.labelMedium)
        Text(text = "ProfileScreen", style = Typography.labelSmall)

    }
}





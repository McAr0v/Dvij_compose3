package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.Grey95
import kz.dvij.dvij_compose3.ui.theme.MeetingCard
import kz.dvij.dvij_compose3.ui.theme.Primary10

// функция превью экрана

@Preview
@Composable
fun MeetingsScreen () {
    Column() {

        TabMenu(bottomPage = MEETINGS_ROOT)

    }


}

// экран мероприятий

@Composable
fun MeetingsTapeScreen (){
    Column (
        modifier = Modifier
            .background(Grey95)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        MeetingCard("Развлечение", "Куда-то мы пойдем сегодня", "21:00", "4 октября")
        MeetingCard("Хобби", "Выступление великолепной группы Korn", "11:00", "5 ноября")
        MeetingCard("Хобби", "Выступление великолепной группы Korn", "11:00", "5 ноября")
    }
}

@Composable
fun MeetingsMyScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "MeetingsMyScreen")

    }
}

@Composable
fun MeetingsFavScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "MeetingsFavScreen")

    }
}
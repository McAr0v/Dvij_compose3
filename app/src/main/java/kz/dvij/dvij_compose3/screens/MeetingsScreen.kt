package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.Grey95
import kz.dvij.dvij_compose3.ui.theme.MeetingCard
import kz.dvij.dvij_compose3.ui.theme.Primary10

// функция превью экрана


@Composable
fun MeetingsScreen (navController: NavController, user: FirebaseUser?) {
    Column {

        TabMenu(bottomPage = MEETINGS_ROOT, navController, user)

    }


}

// экран мероприятий

@Composable
fun MeetingsTapeScreen (navController: NavController){
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
fun MeetingsMyScreen (navController: NavController, user: FirebaseUser?){

    Surface(modifier = Modifier.fillMaxSize()) {

        Column (
            modifier = Modifier
                .background(Primary10)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            MeetingCard("Развлечение", "Куда-то мы пойдем сегодня", "21:00", "4 октября")
            MeetingCard("Хобби", "Выступление великолепной группы Korn", "11:00", "5 ноября")
            MeetingCard("Хобби", "Выступление великолепной группы Korn", "11:00", "5 ноября")
            Text(text = "MeetingsMyScreen")

        }

        if (user != null && user.isEmailVerified) {
            FloatingButton { navController.navigate(CREATE_MEETINGS_SCREEN) }
        }
    }
}

@Composable
fun MeetingsFavScreen (navController: NavController){
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
package kz.dvij.dvij_compose3.screens

import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.Grey95
import kz.dvij.dvij_compose3.ui.theme.MeetingCard
import kz.dvij.dvij_compose3.ui.theme.Primary10

class MeetingsScreens (private val act: MainActivity) {

    private val user = act.mAuth.currentUser
    private val databaseManager = act.databaseManager

    @Composable
    fun MeetingsScreen (navController: NavController, meetingsList: MutableState<List<MeetingsAdsClass>>) {
        Column {

            TabMenu(bottomPage = MEETINGS_ROOT, navController = navController, act, meetingsList)

        }
    }

// экран мероприятий

    @Composable
    fun MeetingsTapeScreen (navController: NavController, meetingsList: MutableState<List<MeetingsAdsClass>>){

        if (meetingsList.value.isNotEmpty()){
            LazyColumn{
                items(meetingsList.value){ item ->
                    MeetingCard(meetingItem = item)
                }
            }
        } else {

            Column (
                modifier = Modifier
                    .background(Grey95)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                    Button(onClick = { navController.navigate(MEETINGS_ROOT)}) {
                        Text(text = "Идет загрузка. Обновить?")
                    }
            }
        }
    }

    @Composable
    fun MeetingsMyScreen (navController: NavController){

        val user = act.mAuth.currentUser

        Surface(modifier = Modifier.fillMaxSize()) {

            Column (
                modifier = Modifier
                    .background(Primary10)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

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

}
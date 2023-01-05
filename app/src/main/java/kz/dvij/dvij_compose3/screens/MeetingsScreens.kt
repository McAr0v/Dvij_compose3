package kz.dvij.dvij_compose3.screens

import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.Grey95
import kz.dvij.dvij_compose3.ui.theme.MeetingCard
import kz.dvij.dvij_compose3.ui.theme.Primary10

class MeetingsScreens (private val act: MainActivity) {

    private val user = act.mAuth.currentUser

    var list = listOf<MeetingsAdsClass>()

    @Composable
    fun MeetingsScreen (navController: NavController) {
        Column {

            TabMenu(bottomPage = MEETINGS_ROOT, navController = navController, act)

        }


    }

// экран мероприятий

    @Composable
    fun MeetingsTapeScreen (navController: NavController){

        if (list.isNotEmpty()){
            LazyColumn{
                items(list){ item ->
                    MeetingCard(meetingItem = item)
                }
            }
        } else {

        }





        Column (
            modifier = Modifier
                .background(Grey95)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

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
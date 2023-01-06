package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.Image
import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingsScreens (val act: MainActivity) {

    private val databaseManager = act.databaseManager

    private val default = MeetingsAdsClass (
        description = "def"
    )

    @Composable
    fun MeetingsScreen (navController: NavController) {
        Column {

            TabMenu(bottomPage = MEETINGS_ROOT, navController = navController, act)

        }
    }

// экран мероприятий

    @Composable
    fun MeetingsTapeScreen (navController: NavController){

        val meetingsList = remember {
            mutableStateOf(listOf<MeetingsAdsClass>())
        }

        databaseManager.readMeetingDataFromDb(meetingsList)

        Column (
            modifier = Modifier
                .background(Grey95)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (meetingsList.value.isNotEmpty() && meetingsList.value != listOf(default)){

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){
                    items(meetingsList.value){ item ->
                        MeetingCard(meetingItem = item)
                    }
                }
            } else if (meetingsList.value == listOf(default)){
                Text(
                    text = "Пусто",
                    style = Typography.bodyMedium,
                    color = Grey10
                ) } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    CircularProgressIndicator(
                        color = PrimaryColor,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = stringResource(id = R.string.ss_loading),
                        style = Typography.bodyMedium,
                        color = Grey10
                    )

                }
            }
        }
    }

    @Composable
    fun MeetingsMyScreen (navController: NavController){

        val myMeetingsList = remember {
            mutableStateOf(listOf<MeetingsAdsClass>())
        }

        databaseManager.readMeetingMyDataFromDb(myMeetingsList)

        Surface(modifier = Modifier.fillMaxSize()) {

            Column (
                modifier = Modifier
                    .background(Grey95)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (myMeetingsList.value.isNotEmpty() && myMeetingsList.value != listOf(default)){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey95),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){
                        items(myMeetingsList.value){ item ->
                            MeetingCard(meetingItem = item)
                        }
                    }
                } else if (myMeetingsList.value == listOf(default) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){
                    Text(
                        text = "Пусто",
                        style = Typography.bodyMedium,
                        color = Grey10
                    )
                } else if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

                    Text(
                        text = "Сначала зарегайся",
                        style = Typography.bodyMedium,
                        color = Grey10
                    )

                } else {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodyMedium,
                            color = Grey10
                        )

                    }


                }
            }

            if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
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
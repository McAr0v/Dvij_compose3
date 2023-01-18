package kz.dvij.dvij_compose3.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.elements.SpacerTextWithLine
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingsScreens (val act: MainActivity) {

    private val databaseManager = act.databaseManager

    private val default = MeetingsAdsClass (
        description = "def"
    )

    @Composable
    fun MeetingsScreen (navController: NavController, meetingKey: MutableState<String>) {
        Column {

            TabMenu(bottomPage = MEETINGS_ROOT, navController = navController, act, meetingKey)

        }
    }

// экран мероприятий

    @Composable
    fun MeetingsTapeScreen (navController: NavController, meetingKey: MutableState<String>){

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

            Button(onClick = {navController.navigate("picasso")}) {

            }

            if (meetingsList.value.isNotEmpty() && meetingsList.value != listOf(default)){

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){
                    items(meetingsList.value){ item ->
                        MeetingCard(meetingItem = item, navController = navController, meetingKey = meetingKey)
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
    fun MeetingsMyScreen (navController: NavController, meetingKey: MutableState<String>){

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
                            MeetingCard(meetingItem = item, navController = navController, meetingKey = meetingKey)
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

    @Composable
    fun MeetingViewScreen (key: String, navController: NavController){

        val meetingDatabase = databaseManager.meetingDatabase

        var meetingInfo = remember {
            mutableStateOf<MeetingsAdsClass>(MeetingsAdsClass())
        }

        databaseManager.readOneMeetingFromDataBase(meetingInfo, key)

        Column(
            modifier = Modifier
                .background(Grey95)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top

        ) {

            AsyncImage(
                model = meetingInfo.value.image1,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top

            ) {

                // -------- ЗАГОЛОВОК МЕРОПРИЯТИЯ ----------

                if (meetingInfo.value.headline != null) {

                    Text(
                        text = meetingInfo.value.headline!!,
                        style = Typography.titleLarge,
                        color = Grey10

                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // -------- КАТЕГОРИЯ МЕРОПРИЯТИЯ ----------

                if (meetingInfo.value.category != null) {

                    Button(
                        onClick = {
                            Toast
                                .makeText(act, "Сделать функцию", Toast.LENGTH_SHORT)
                                .show()
                        },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor,
                        contentColor = Grey95
                    ),
                    shape = RoundedCornerShape(30.dp)) {

                        Text(
                            text = meetingInfo.value.category!!,
                            style = Typography.bodyMedium
                        )

                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ------- BOX С ДАННЫМИ МЕРОПРИЯТИЯ -----------


                Column(

                    modifier = Modifier
                        .background(Grey100, shape = RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start

                ) {

                    // ------ ДАТА --------

                    if (meetingInfo.value.data != null) {
                        IconText(icon = R.drawable.ic_calendar, inputText = meetingInfo.value.data!!)
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), color = Grey60, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // ----- ВРЕМЯ -----------

                    if (meetingInfo.value.startTime != null && meetingInfo.value.finishTime != null) {

                        IconText(
                            icon = R.drawable.ic_time,
                            inputText = if (meetingInfo.value.finishTime == ""){
                                "Начало в ${meetingInfo.value.startTime!!}"
                            } else {
                                "${meetingInfo.value.startTime} - ${meetingInfo.value.finishTime}"
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), color = Grey60, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // --------- СТОИМОСТЬ БИЛЕТА ----------

                    if (meetingInfo.value.price != null) {

                        IconText(

                            icon = R.drawable.ic_tenge,
                            inputText = if (meetingInfo.value.price == ""){
                                stringResource(id = R.string.free_price)
                            } else {
                                "${meetingInfo.value.price} тенге"
                            },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }


                }

                Spacer(modifier = Modifier.height(10.dp))

                SpacerTextWithLine(headline = "Связаться с организатором")

                Spacer(modifier = Modifier.height(10.dp))

                Row (modifier = Modifier.fillMaxSize()){



                    // ------ КНОПКА ПОЗВОНИТЬ ---------



                    if (meetingInfo.value.phone != null) {

                        Button(
                            onClick = {
                                act.makeACall(act, meetingInfo.value.phone!!)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = SuccessColor,
                                contentColor = Grey95
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {

                            Icon(painter = painterResource(id = R.drawable.ic_phone), contentDescription = "", tint = Grey95)

                            Spacer(modifier = Modifier
                                .width(15.dp)
                                .height(30.dp))

                            Text("Позвонить", style = Typography.bodyMedium)

                        }

                    }

                    if (meetingInfo.value.whatsapp != null && meetingInfo.value.whatsapp != "+77") {

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                            onClick = {
                                act.writeInWhatsapp(act, meetingInfo.value.whatsapp!!)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = SuccessColor,
                                contentColor = Grey95
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {

                            Icon(painter = painterResource(id = R.drawable.whatsapp), contentDescription = "", tint = Grey95)

                            Spacer(modifier = Modifier
                                .width(15.dp)
                                .height(30.dp))

                            Text("Написать", style = Typography.bodyMedium)

                        }

                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                // ---------- ОПИСАНИЕ -------------

                Text(
                    text = stringResource(id = R.string.about_meeting),
                    style = Typography.titleMedium,
                    color = Grey10
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (meetingInfo.value.description !=null){

                    Text(
                        text = meetingInfo.value.description!!,
                        style = Typography.bodyMedium,
                        color = Grey10
                    )

                }



            }
        }
    }



}
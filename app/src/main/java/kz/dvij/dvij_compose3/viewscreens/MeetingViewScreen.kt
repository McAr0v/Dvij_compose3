package kz.dvij.dvij_compose3.viewscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.elements.IconText
import kz.dvij.dvij_compose3.elements.SpacerTextWithLine
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingViewScreen(val act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun MeetingViewScreen (key: String, navController: NavController){

        val meetingInfo = remember {
            mutableStateOf(MeetingsAdsClass())
        }

        act.databaseManager.readOneMeetingFromDataBase(meetingInfo, key)

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


                }

                if (meetingInfo.value.city != null) {

                    Text(
                        text = meetingInfo.value.city!!,
                        style = Typography.bodyMedium,
                        color = Grey40

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
                        shape = RoundedCornerShape(30.dp)
                    ) {

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
                                act.callAndWhatsapp.makeACall(act, meetingInfo.value.phone!!)
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
                                act.callAndWhatsapp.writeInWhatsapp(act, meetingInfo.value.whatsapp!!)
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
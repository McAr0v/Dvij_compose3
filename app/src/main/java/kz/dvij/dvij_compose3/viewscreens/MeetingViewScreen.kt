package kz.dvij.dvij_compose3.viewscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
        
        val iconFavColor = remember {
            mutableStateOf(Grey90)
        }

        // Переменная текста рядом с иконкой Избранные

        val favText = remember {
            mutableStateOf("")
        }

        // Переменная счетчика людей, добавивших в избранное мероприятие
        val favCounter = remember {
            mutableStateOf(0)
        }

        // Переменная счетчика просмотра мероприятия
        val viewCounter = remember {
            mutableStateOf(0)
        }

        // Считываем данные про мероприятие и счетчики добавивших в избранное и количество просмотров мероприятия

        act.databaseManager.readOneMeetingFromDataBase(meetingInfo, key){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров мероприятия

        }

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.databaseManager.favIconMeeting(key) {
                if (it) {
                    iconFavColor.value = PrimaryColor
                    favText.value = "В избранном"
                } else {
                    iconFavColor.value = Grey90
                    favText.value = "Добавить в избранное"
                }
            }
        }

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

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                        shape = RoundedCornerShape(50)
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = "Иконка количества просмотров",
                            modifier = Modifier.size(20.dp),
                            tint = Grey40
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = viewCounter.value.toString(),
                            style = Typography.labelMedium,
                            color = Grey40
                        )

                    }



                    Spacer(modifier = Modifier.width(20.dp))

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = iconFavColor.value),
                        shape = RoundedCornerShape(50)
                    ) {

                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Favorite, // сам векторный файл иконки
                            contentDescription = "Иконка добавить в избранные", // описание для слабовидящих
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {

                                    if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                                        act.databaseManager.favIconMeeting(key) {
                                            if (it) {
                                                iconFavColor.value = Grey90
                                                act.databaseManager.removeFavouriteMeeting(key) {
                                                    if (it) {
                                                        Toast
                                                            .makeText(
                                                                act,
                                                                "Удалено из избранных",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()

                                                    }
                                                }
                                            } else {

                                                act.databaseManager.addFavouriteMeeting(key) {

                                                    if (it) {
                                                        iconFavColor.value = PrimaryColor
                                                        Toast
                                                            .makeText(
                                                                act,
                                                                "Добавлено в избранные",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()

                                                    }

                                                }
                                            }
                                        }
                                    } else {
                                        Toast
                                            .makeText(act, "Сначала зарегайся", Toast.LENGTH_SHORT)
                                            .show()
                                    }


                                }, // размер иконки
                            tint = Grey10//favIconColor.value // Цвет иконки
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = favCounter.value.toString(),
                            style = Typography.labelMedium,
                            color = Grey40
                        )

                    }

                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                    ) {

                        if (meetingInfo.value.data != null){
                            headlineAndDesc(headline = meetingInfo.value.data!!, desc = "Дата")
                        }

                    }

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)) {

                        if (meetingInfo.value.startTime != null && meetingInfo.value.finishTime != null){
                            headlineAndDesc(headline = "${meetingInfo.value.startTime!!} - ${meetingInfo.value.finishTime!!}", desc = "Время проведения")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (meetingInfo.value.price != null){
                    headlineAndDesc(headline = meetingInfo.value.price!!, desc = "Цена билета")
                }

                Spacer(modifier = Modifier.height(20.dp))
                


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

                            Text("Позвонить", style = Typography.labelMedium)

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

    @Composable
    fun headlineAndDesc (headline: String, desc: String){

        Column(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = headline,
                color = Grey10,
                style = Typography.titleSmall
            )

            Text(
                text = desc,
                color = Grey10,
                style = Typography.labelSmall
            )

        }

    }
}
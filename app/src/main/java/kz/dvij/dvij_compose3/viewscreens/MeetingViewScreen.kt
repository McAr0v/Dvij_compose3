package kz.dvij.dvij_compose3.viewscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import kz.dvij.dvij_compose3.constants.INSTAGRAM_URL
import kz.dvij.dvij_compose3.constants.TELEGRAM_URL
import kz.dvij.dvij_compose3.elements.HeadlineAndDesc
import kz.dvij.dvij_compose3.elements.PlacesCard
import kz.dvij.dvij_compose3.elements.SpacerTextWithLine
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingViewScreen(val act: MainActivity) {

    val placesDatabaseManager = PlacesDatabaseManager(act)
    val placeCard = PlacesCard(act)

    @SuppressLint("NotConstructor")
    @Composable
    fun MeetingViewScreen (key: String, navController: NavController, placeKey: MutableState<String>){


        // Переменная, которая содержит в себе информацию о мероприятии
        val meetingInfo = remember {
            mutableStateOf(MeetingsAdsClass())
        }

        // Переменная, отвечающая за цвет кнопки избранных
        val buttonFavColor = remember {
            mutableStateOf(Grey90)
        }

        // Переменная, отвечающая за цвет иконки избранных
        val iconTextFavColor = remember {
            mutableStateOf(Grey10)
        }

        // Переменная счетчика людей, добавивших в избранное мероприятие
        val favCounter = remember {
            mutableStateOf(0)
        }

        // Переменная счетчика просмотра мероприятия
        val viewCounter = remember {
            mutableStateOf(0)
        }

        val placeInfo = remember {
            mutableStateOf(PlacesAdsClass())
        }


        // Считываем данные про мероприятие и счетчики добавивших в избранное и количество просмотров мероприятия

        act.meetingDatabaseManager.readOneMeetingFromDataBase(meetingInfo, key){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров мероприятия

            meetingInfo.value.placeKey?.let { it1 ->
                act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo = placeInfo, key = it1) {

                }
            }

        }



        // Если пользователь авторизован, проверяем, добавлено ли уже мероприятие в избранное, или нет

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.meetingDatabaseManager.favIconMeeting(key) {
                if (it) {
                    buttonFavColor.value = Grey90_2
                    iconTextFavColor.value = PrimaryColor
                } else {
                    buttonFavColor.value = Grey90
                    iconTextFavColor.value = Grey40
                }
            }
        }


        // ---------- КОНТЕНТ СТРАНИЦЫ --------------

        Column(
            modifier = Modifier
                .background(Grey95)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top

        ) {

            // ------- КАРТИНКА МЕРОПРИЯТИЯ ----------

            AsyncImage(
                model = meetingInfo.value.image1,
                contentDescription = stringResource(id = R.string.cm_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // --------- КОНТЕНТ ПОД КАРТИНКОЙ ----------

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

                // ------- ГОРОД ------------

                if (meetingInfo.value.city != null) {

                    Text(
                        text = meetingInfo.value.city!!,
                        style = Typography.bodyMedium,
                        color = Grey40
                    )
                }

                // --------- КАТЕГОРИЯ, СЧЕТЧИКИ, ИЗБРАННОЕ -------------

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {


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
                                style = Typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))


                    // --------- СЧЕТЧИК КОЛИЧЕСТВА ПРОСМОТРОВ ------------


                    Button(
                        onClick = {Toast.makeText(act,act.getString(R.string.meeting_view_counter),Toast.LENGTH_SHORT).show()},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                        shape = RoundedCornerShape(50)
                    ) {

                        // ----- Иконка просмотра ------

                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = stringResource(id = R.string.cd_counter_view_meeting),
                            modifier = Modifier.size(20.dp),
                            tint = Grey40
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        // ----------- Счетчик просмотров мероприятия ----------

                        Text(
                            text = viewCounter.value.toString(),
                            style = Typography.labelMedium,
                            color = Grey40
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))


                    // -------- ИЗБРАННЫЕ ---------


                    Button(
                        onClick = {

                            // --- Если клиент авторизован, проверяем, добавлено ли уже в избранное это мероприятие -----
                            // Если не авторизован, условие else

                            if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                                act.meetingDatabaseManager.favIconMeeting(key) {

                                    // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                    if (it) {

                                        // Убираем из избранных
                                        act.meetingDatabaseManager.removeFavouriteMeeting(key) {

                                            // Если пришел колбак, что успешно

                                            if (it) {

                                                iconTextFavColor.value = Grey40 // При нажатии окрашиваем текст и иконку в белый
                                                buttonFavColor.value = Grey80 // При нажатии окрашиваем кнопку в темно-серый

                                                // Выводим ТОСТ
                                                Toast.makeText(act,act.getString(R.string.delete_from_fav),Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    } else {

                                        // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                        act.meetingDatabaseManager.addFavouriteMeeting(key) {

                                            // Если пришел колбак, что успешно

                                            if (it) {

                                                iconTextFavColor.value = PrimaryColor // При нажатии окрашиваем текст и иконку в черный
                                                buttonFavColor.value = Grey90_2 // Окрашиваем кнопку в главный цвет

                                                // Выводим ТОСТ
                                                Toast.makeText(act,act.getString(R.string.add_to_fav),Toast.LENGTH_SHORT).show()

                                            }
                                        }
                                    }
                                }

                            } else {

                                // Если пользователь не авторизован, то ему выводим ТОСТ

                                Toast
                                    .makeText(act, act.getString(R.string.need_reg_meeting_to_fav), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonFavColor.value),
                        shape = RoundedCornerShape(50)
                    ) {

                        // --- Иконка СЕРДЕЧКО -----

                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Favorite, // сам векторный файл иконки
                            contentDescription = stringResource(id = R.string.cd_add_to_fav), // описание для слабовидящих
                            modifier = Modifier
                                .size(20.dp), // размер иконки
                            tint = iconTextFavColor.value // Цвет иконки
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        // ------ Счетчик добавлено в избранное

                        Text(
                            text = favCounter.value.toString(),
                            style = Typography.labelMedium,
                            color = Grey40
                        )
                    }
                }


                // --------- ДАТА И ВРЕМЯ -----------


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                    // --- ДАТА ----

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                    ) {
                        if (meetingInfo.value.data != null){
                            HeadlineAndDesc(headline = meetingInfo.value.data!!, desc = act.getString(R.string.cm_date2))
                        }
                    }

                    // ---- ВРЕМЯ -----

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)) {

                        if (meetingInfo.value.startTime != null && meetingInfo.value.finishTime != null){
                            HeadlineAndDesc(
                                headline = if (meetingInfo.value.finishTime == ""){
                                    meetingInfo.value.startTime!!
                                } else {
                                    "${meetingInfo.value.startTime} - ${meetingInfo.value.finishTime}"
                                },
                                desc = if (meetingInfo.value.finishTime == ""){
                                    act.getString(R.string.cm_start_in)
                                } else {
                                    act.getString(R.string.cm_all_time)
                                } //
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ----- ЦЕНА ---------

                val tenge = act.getString(R.string.ss_tenge)

                if (meetingInfo.value.price != null){
                    HeadlineAndDesc(
                        headline = if (meetingInfo.value.price == ""){
                            stringResource(id = R.string.free_price)
                        } else {
                            "${meetingInfo.value.price} $tenge"
                        },
                        desc = act.getString(R.string.cm_price)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                SpacerTextWithLine(headline = stringResource(id = R.string.meeting_call_org))

                Spacer(modifier = Modifier.height(10.dp))

                Row (modifier = Modifier.fillMaxSize()){

                    // ----- КНОПКА ПОЗВОНИТЬ --------

                    if (meetingInfo.value.phone != null) {

                        IconButton(
                            onClick = { act.callAndWhatsapp.makeACall(meetingInfo.value.phone!!) },
                            modifier = Modifier.background(Grey90, shape = RoundedCornerShape(50))
                        ) {

                            Icon(painter = painterResource(id = R.drawable.ic_phone), contentDescription = "", tint = Grey10)

                        }

                        Spacer(modifier = Modifier
                            .width(10.dp)
                        )

                    }

                    // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                    if (meetingInfo.value.whatsapp != null && meetingInfo.value.whatsapp != "+77") {

                        IconButton(
                            onClick = { act.callAndWhatsapp.writeInWhatsapp(meetingInfo.value.whatsapp!!) },
                            modifier = Modifier.background(Grey90, shape = RoundedCornerShape(50))
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = stringResource(id = R.string.social_whatsapp),
                                tint = Grey10
                            )

                        }

                        Spacer(modifier = Modifier
                            .width(10.dp)
                        )

                    }

                    // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                    if (meetingInfo.value.instagram != null && meetingInfo.value.instagram != INSTAGRAM_URL) {

                        IconButton(
                            onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(meetingInfo.value.instagram!!) },
                            modifier = Modifier.background(Grey90, shape = RoundedCornerShape(50))
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.instagram),
                                contentDescription = stringResource(id = R.string.social_instagram),
                                tint = Grey10
                            )

                        }

                        Spacer(modifier = Modifier
                            .width(10.dp)
                        )

                    }

                    // ---- КНОПКА НАПИСАТЬ В ТЕЛЕГРАМ -----------

                    if (meetingInfo.value.telegram != null && meetingInfo.value.telegram != TELEGRAM_URL) {

                        IconButton(
                            onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(meetingInfo.value.telegram!!) },
                            modifier = Modifier.background(Grey90, shape = RoundedCornerShape(50))
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.telegram),
                                contentDescription = stringResource(id = R.string.social_telegram),
                                tint = Grey10
                            )

                        }

                        Spacer(modifier = Modifier
                            .width(10.dp)
                        )

                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Местро проведения",
                    style = Typography.titleMedium,
                    color = Grey10
                )

                Spacer(modifier = Modifier.height(20.dp))

                placeInfo.value.placeKey?.let {

                    placeCard.PlaceCard(navController = navController, placeItem = placeInfo.value, placeKey = placeKey)

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
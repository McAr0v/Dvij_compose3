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
import kz.dvij.dvij_compose3.elements.SpacerTextWithLine
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingViewScreen(val act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun MeetingViewScreen (key: String, navController: NavController){

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

        // Считываем данные про мероприятие и счетчики добавивших в избранное и количество просмотров мероприятия

        act.databaseManager.readOneMeetingFromDataBase(meetingInfo, key){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров мероприятия

        }

        // Если пользователь авторизован, проверяем, добавлено ли уже мероприятие в избранное, или нет

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.databaseManager.favIconMeeting(key) {
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
                contentDescription = "",
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
                        onClick = {Toast.makeText(act,"Количество просмотров мероприятия",Toast.LENGTH_SHORT).show()},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                        shape = RoundedCornerShape(50)
                    ) {

                        // ----- Иконка просмотра ------

                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = "Иконка количества просмотров",
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
                                act.databaseManager.favIconMeeting(key) {

                                    // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                    if (it) {

                                        // Убираем из избранных
                                        act.databaseManager.removeFavouriteMeeting(key) {

                                            // Если пришел колбак, что успешно

                                            if (it) {

                                                iconTextFavColor.value = Grey40 // При нажатии окрашиваем текст и иконку в белый
                                                buttonFavColor.value = Grey80 // При нажатии окрашиваем кнопку в темно-серый

                                                // Выводим ТОСТ
                                                Toast.makeText(act,"Удалено из избранных",Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    } else {

                                        // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                        act.databaseManager.addFavouriteMeeting(key) {

                                            // Если пришел колбак, что успешно

                                            if (it) {

                                                iconTextFavColor.value = PrimaryColor // При нажатии окрашиваем текст и иконку в черный
                                                buttonFavColor.value = Grey90_2 // Окрашиваем кнопку в главный цвет

                                                // Выводим ТОСТ
                                                Toast.makeText(act,"Добавлено в избранные",Toast.LENGTH_SHORT).show()

                                            }
                                        }
                                    }
                                }

                            } else {

                                // Если пользователь не авторизован, то ему выводим ТОСТ

                                Toast
                                    .makeText(act, "Сначала зарегайся", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonFavColor.value),
                        shape = RoundedCornerShape(50)
                    ) {

                        // --- Иконка СЕРДЕЧКО -----

                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Favorite, // сам векторный файл иконки
                            contentDescription = "Иконка добавить в избранные", // описание для слабовидящих
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
                            headlineAndDesc(headline = meetingInfo.value.data!!, desc = "Дата")
                        }
                    }

                    // ---- ВРЕМЯ -----

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)) {

                        if (meetingInfo.value.startTime != null && meetingInfo.value.finishTime != null){
                            headlineAndDesc(
                                headline = if (meetingInfo.value.finishTime == ""){
                                    meetingInfo.value.startTime!!
                                } else {
                                    "${meetingInfo.value.startTime} - ${meetingInfo.value.finishTime}"
                                },//"${meetingInfo.value.startTime!!} - ${meetingInfo.value.finishTime!!}",
                                desc = if (meetingInfo.value.finishTime == ""){
                                    "Начинаем в"
                                } else {
                                    "Время проведения"
                                } //
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ----- ЦЕНА ---------

                if (meetingInfo.value.price != null){
                    headlineAndDesc(
                        headline = if (meetingInfo.value.price == ""){
                            stringResource(id = R.string.free_price)
                        } else {
                            "${meetingInfo.value.price} тенге"
                        },
                        desc = "Цена билета"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                SpacerTextWithLine(headline = "Связаться с организатором")

                Spacer(modifier = Modifier.height(10.dp))

                Row (modifier = Modifier.fillMaxSize()){

                    // ----- КНОПКА ПОЗВОНИТЬ --------

                    if (meetingInfo.value.phone != null) {

                        IconButton(
                            onClick = { act.callAndWhatsapp.makeACall(act, meetingInfo.value.phone!!) },
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
                            onClick = { act.callAndWhatsapp.writeInWhatsapp(act, meetingInfo.value.whatsapp!!) },
                            modifier = Modifier.background(Grey90, shape = RoundedCornerShape(50))
                        ) {

                            Icon(painter = painterResource(id = R.drawable.whatsapp), contentDescription = "", tint = Grey10)

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
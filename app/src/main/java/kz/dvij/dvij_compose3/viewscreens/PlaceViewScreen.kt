package kz.dvij.dvij_compose3.viewscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
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
import kz.dvij.dvij_compose3.elements.SpacerTextWithLine
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.ui.theme.*

class PlaceViewScreen (val act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun PlaceViewScreen (key: String, navController: NavController, meetingKey: MutableState<String>, stockKey: MutableState<String>){

        // Переменная, которая содержит в себе информацию о заведении
        val placeInfo = remember {
            mutableStateOf(PlacesAdsClass())
        }

        // Переменная, отвечающая за цвет кнопки избранных
        val buttonFavColor = remember {
            mutableStateOf(Grey90)
        }

        // Переменная, отвечающая за цвет иконки избранных
        val iconTextFavColor = remember {
            mutableStateOf(Grey10)
        }

        // Переменная счетчика людей, добавивших в избранное заведение
        val favCounter = remember {
            mutableStateOf(0)
        }

        // Переменная счетчика просмотра заведения
        val viewCounter = remember {
            mutableStateOf(0)
        }

        // инициализируем список мероприятий этого заведения
        val meetingsList = remember {
            mutableStateOf(listOf<MeetingsAdsClass>())
        }

        // инициализируем список акций
        val stockList = remember {
            mutableStateOf(listOf<StockAdsClass>())
        }

        val defaultMeeting = MeetingsAdsClass (
            description = "Default"
        )

        // создаем акцию по умолчанию
        val defaultStock = StockAdsClass (
            description = "Default"
        )

        // Считываем данные про заведение и счетчики добавивших в избранное и количество просмотров заведения

        act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo, key){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров заведения

        }

        // Если пользователь авторизован, проверяем, добавлено ли уже заведение в избранное, или нет

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.placesDatabaseManager.favIconPlace(key) {
                if (it) {
                    buttonFavColor.value = Grey90_2
                    iconTextFavColor.value = PrimaryColor
                } else {
                    buttonFavColor.value = Grey90
                    iconTextFavColor.value = Grey40
                }
            }
        }

        // Считываем мероприятия заведения

        placeInfo.value.placeKey?.let { nonNullPlaceKey ->
            act.meetingDatabaseManager.readMeetingInPlaceDataFromDb(meetingsList = meetingsList,
                nonNullPlaceKey
            )
        }

        // Считываем акции заведения

        placeInfo.value.placeKey?.let { act.stockDatabaseManager.readStockInPlaceFromDb(stockList = stockList, placeKey = it) }


        // ---------- КОНТЕНТ СТРАНИЦЫ --------------

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey95),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {

            item {

                // ------- КАРТИНКА Заведения ----------

                AsyncImage(
                    model = placeInfo.value.logo,
                    contentDescription = "Логотип заведения",
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

                    // -------- НАЗВАНИЕ ЗАВЕДЕНИЯ ----------

                    if (placeInfo.value.placeName != null) {

                        Text(
                            text = placeInfo.value.placeName!!,
                            style = Typography.titleLarge,
                            color = Grey10
                        )
                    }

                    // ------- ГОРОД ------------

                    if (placeInfo.value.city != null) {

                        Text(
                            text = placeInfo.value.city!!,
                            style = Typography.bodyMedium,
                            color = Grey40
                        )
                    }

                    // ------- АДРЕС ------------

                    if (placeInfo.value.address != null) {

                        Text(
                            text = placeInfo.value.address!!,
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


                        // -------- КАТЕГОРИЯ заведения ----------


                        if (placeInfo.value.category != null) {

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
                                    text = placeInfo.value.category!!,
                                    style = Typography.labelMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))


                        // --------- СЧЕТЧИК КОЛИЧЕСТВА ПРОСМОТРОВ ------------


                        Button(
                            onClick = {
                                Toast.makeText(
                                    act, "Количество просмотров завдения",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                            shape = RoundedCornerShape(50)
                        ) {

                            // ----- Иконка просмотра ------

                            androidx.compose.material.Icon(
                                painter = painterResource(id = R.drawable.ic_visibility),
                                contentDescription = stringResource(id = R.string.cd_counter_view_meeting),
                                modifier = Modifier.size(20.dp),
                                tint = Grey40
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            // ----------- Счетчик просмотров ----------

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

                                // --- Если клиент авторизован, проверяем, добавлено ли уже в избранное это заведение -----
                                // Если не авторизован, условие else

                                if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                                    act.placesDatabaseManager.favIconPlace(key) {

                                        // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                        if (it) {

                                            // Убираем из избранных
                                            act.placesDatabaseManager.removeFavouritePlace(key) {

                                                // Если пришел колбак, что успешно

                                                if (it) {

                                                    iconTextFavColor.value =
                                                        Grey40 // При нажатии окрашиваем текст и иконку в белый
                                                    buttonFavColor.value =
                                                        Grey80 // При нажатии окрашиваем кнопку в темно-серый

                                                    // Выводим ТОСТ
                                                    Toast.makeText(
                                                        act,
                                                        act.getString(R.string.delete_from_fav),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                        } else {

                                            // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                            act.placesDatabaseManager.addFavouritePlace(key) {

                                                // Если пришел колбак, что успешно

                                                if (it) {

                                                    iconTextFavColor.value =
                                                        PrimaryColor // При нажатии окрашиваем текст и иконку в черный
                                                    buttonFavColor.value =
                                                        Grey90_2 // Окрашиваем кнопку в главный цвет

                                                    // Выводим ТОСТ
                                                    Toast.makeText(
                                                        act, act.getString(R.string.add_to_fav),
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }
                                            }
                                        }
                                    }

                                } else {

                                    // Если пользователь не авторизован, то ему выводим ТОСТ

                                    Toast
                                        .makeText(
                                            act,
                                            "Чтобы добавить заведение в избранные, тебе нужно авторизоваться",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = buttonFavColor.value),
                            shape = RoundedCornerShape(50)
                        ) {

                            // --- Иконка СЕРДЕЧКО -----

                            Icon(
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


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // --- ДАТА ----

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                        ) {
                            if (placeInfo.value.openTime != null) {
                                HeadlineAndDesc(
                                    headline = placeInfo.value.openTime!!, desc = act.getString(
                                        R.string.cm_date2
                                    )
                                )
                            }
                        }

                        // ---- ВРЕМЯ -----

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                        ) {

                            if (placeInfo.value.openTime != null && placeInfo.value.closeTime != null) {
                                HeadlineAndDesc(
                                    headline = if (placeInfo.value.closeTime == "") {
                                        placeInfo.value.openTime!!
                                    } else {
                                        "${placeInfo.value.openTime} - ${placeInfo.value.closeTime}"
                                    },
                                    desc = if (placeInfo.value.closeTime == "") {
                                        act.getString(R.string.cm_start_in)
                                    } else {
                                        act.getString(R.string.cm_all_time)
                                    } //
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))


                    SpacerTextWithLine(headline = stringResource(id = R.string.meeting_call_org))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxSize()) {

                        // ----- КНОПКА ПОЗВОНИТЬ --------

                        if (placeInfo.value.phone != null) {

                            IconButton(
                                onClick = { act.callAndWhatsapp.makeACall(placeInfo.value.phone!!) },
                                modifier = Modifier.background(
                                    Grey90,
                                    shape = RoundedCornerShape(50)
                                )
                            ) {

                                androidx.compose.material.Icon(
                                    painter = painterResource(id = R.drawable.ic_phone),
                                    contentDescription = "",
                                    tint = Grey10
                                )

                            }

                            Spacer(
                                modifier = Modifier
                                    .width(10.dp)
                            )

                        }

                        // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                        if (placeInfo.value.whatsapp != null && placeInfo.value.whatsapp != "+77") {

                            IconButton(
                                onClick = { act.callAndWhatsapp.writeInWhatsapp(placeInfo.value.whatsapp!!) },
                                modifier = Modifier.background(
                                    Grey90,
                                    shape = RoundedCornerShape(50)
                                )
                            ) {

                                androidx.compose.material.Icon(
                                    painter = painterResource(id = R.drawable.whatsapp),
                                    contentDescription = stringResource(id = R.string.social_whatsapp),
                                    tint = Grey10
                                )

                            }

                            Spacer(
                                modifier = Modifier
                                    .width(10.dp)
                            )

                        }

                        // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                        if (placeInfo.value.instagram != null && placeInfo.value.instagram != INSTAGRAM_URL) {

                            IconButton(
                                onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(placeInfo.value.instagram!!) },
                                modifier = Modifier.background(
                                    Grey90,
                                    shape = RoundedCornerShape(50)
                                )
                            ) {

                                androidx.compose.material.Icon(
                                    painter = painterResource(id = R.drawable.instagram),
                                    contentDescription = stringResource(id = R.string.social_instagram),
                                    tint = Grey10
                                )

                            }

                            Spacer(
                                modifier = Modifier
                                    .width(10.dp)
                            )

                        }

                        // ---- КНОПКА НАПИСАТЬ В ТЕЛЕГРАМ -----------

                        if (placeInfo.value.telegram != null && placeInfo.value.telegram != TELEGRAM_URL) {

                            IconButton(
                                onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(placeInfo.value.telegram!!) },
                                modifier = Modifier.background(
                                    Grey90,
                                    shape = RoundedCornerShape(50)
                                )
                            ) {

                                androidx.compose.material.Icon(
                                    painter = painterResource(id = R.drawable.telegram),
                                    contentDescription = stringResource(id = R.string.social_telegram),
                                    tint = Grey10
                                )

                            }

                            Spacer(
                                modifier = Modifier
                                    .width(10.dp)
                            )

                        }

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- ОПИСАНИЕ -------------

                    Text(
                        text = stringResource(id = R.string.about_meeting),
                        style = Typography.titleMedium,
                        color = Grey10
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (placeInfo.value.placeDescription != null) {

                        Text(
                            text = placeInfo.value.placeDescription!!,
                            style = Typography.bodyMedium,
                            color = Grey10
                        )
                    }
                }


            }

            item {

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Мероприятия этого заведения",
                    style = Typography.titleMedium,
                    color = Grey10
                )
            }

            if (meetingsList.value.isNotEmpty() && meetingsList.value != listOf(defaultMeeting)){

                // для каждого элемента из списка указываем шаблон для отображения

                items(meetingsList.value) { item ->

                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        // сам шаблон карточки мероприятия
                        act.meetingsCard.MeetingCard(
                            navController = navController,
                            meetingItem = item,
                            meetingKey = meetingKey
                        )
                    }
                }

            } else if (meetingsList.value == listOf(defaultMeeting)) {

                // ----- ЕСЛИ НЕТ МЕРОПРИЯТИЙ -------

                item {

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)) {

                        Text(
                            text = "У этого места пока нет мероприятий",
                            style = Typography.bodyMedium,
                            color = Grey10
                        )

                    }

                }

            } else {

                // -------- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // крутилка индикатор

                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        // текст рядом с крутилкой

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodyMedium,
                            color = Grey10
                        )

                    }

                }

            }

            item {

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Акции этого заведения",
                    style = Typography.titleMedium,
                    color = Grey10
                )
            }

            if (stockList.value.isNotEmpty() && stockList.value != listOf(defaultStock)){

                // для каждого элемента из списка указываем шаблон для отображения

                items(stockList.value) { item ->

                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        // сам шаблон карточки акции
                        act.stockCard.StockCard(navController = navController, stockItem = item, stockKey = stockKey)
                    }
                }

            } else if (stockList.value == listOf(defaultStock)) {

                // ----- ЕСЛИ НЕТ АКЦИЙ -------

                item {

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)) {

                        Text(
                            text = "У этого места пока нет акций",
                            style = Typography.bodyMedium,
                            color = Grey10
                        )

                    }

                }

            } else {

                // -------- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // крутилка индикатор

                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        // текст рядом с крутилкой

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodyMedium,
                            color = Grey10
                        )

                    }

                }

            }

        }

    }

}
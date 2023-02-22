package kz.dvij.dvij_compose3.elements

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class PlacesCard (val act: MainActivity) {

    @Composable
    fun PlaceCardSmall (navController: NavController, placeItem: PlacesAdsClass, placeKey: MutableState<String>) {

        val iconFavColor = remember{ mutableStateOf(Grey10) } // Переменная цвета иконки ИЗБРАННОЕ
        val meetingCounter = remember{ mutableStateOf("") } // Счетчик количества мероприятий
        val stockCounter = remember{ mutableStateOf("") } // Счетчик количества акций

        // Считываем с базы данных - добавлено ли это заведение в избранное?

        act.placesDatabaseManager.favIconPlace(placeItem.placeKey!!){
            // Если колбак тру, то окрашиваем иконку в нужный цвет
            if (it){
                iconFavColor.value = PrimaryColor
            } else {
                // Если колбак фалс, то в обычный цвет
                iconFavColor.value = Grey10
            }
        }

        // Считываем количество мероприятий у этого заведения

        act.meetingDatabaseManager.readMeetingCounterInPlaceDataFromDb(placeItem.placeKey){ meetingsCounter ->
            meetingCounter.value = meetingsCounter.toString()
        }

        // Считываем количество акций у этого заведения

        act.stockDatabaseManager.readStockCounterInPlaceFromDb(placeKey = placeItem.placeKey) { stockCounters ->
            stockCounter.value = stockCounters.toString()
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(10.dp)
                .clickable {

                    // При клике на карточку - передаем на Main Activity placeKey. Ключ берем из дата класса заведения

                    placeKey.value = placeItem.placeKey.toString()

                    // так же при нажатии регистрируем счетчик просмотров - добавляем 1 просмотр

                    placeItem.placeKey.let {
                        act.placesDatabaseManager.viewCounterPlace(it) { result ->

                            // если колбак тру, то счетчик успешно сработал, значит переходим на страницу заведения
                            if (result) {

                                navController.navigate(PLACE_VIEW)
                            }
                        }
                    }

                },
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(Grey100)
        ) {

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)){

                // ----- ЛОГОТИП ЗАВЕДЕНИЯ ---------

                if (placeItem.logo != null && placeItem.logo != ""){

                    AsyncImage(
                        model = placeItem.logo,
                        contentDescription = "",
                        modifier = Modifier
                            .width(170.dp)
                            .height(170.dp),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )

                }

                // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 0.dp, end = 0.dp, start = 110.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {

                    // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp, bottomStart = 15.dp),
                        elevation = CardDefaults.cardElevation(5.dp),
                        colors = CardDefaults.cardColors(Grey100)
                    ) {

                        Column(modifier = Modifier.padding(20.dp)) {

                            // ----- НАЗВАНИЕ ЗАВЕДЕНИЯ --------

                            if (placeItem.placeName != null && placeItem.placeName != ""){

                                Text(
                                    text = placeItem.placeName,
                                    style = Typography.titleSmall,
                                    color = Grey10
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                            }

                            // ------- ГОРОД ---------

                            if (placeItem.city != null && placeItem.city != ""){

                                Text(
                                    text = placeItem.city,
                                    style = Typography.bodyMedium,
                                    color = Grey40
                                )

                            }

                            // --------- АДРЕС -------------

                            if (placeItem.address != null && placeItem.address != ""){

                                Text(
                                    text = placeItem.address,
                                    style = Typography.bodyMedium,
                                    color = Grey40
                                )

                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                    shape = RoundedCornerShape(50)
                                ) {

                                    // ----- Иконка мероприятий ------

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_celebration),
                                        contentDescription = "",
                                        modifier = Modifier.size(15.dp),
                                        tint = Grey40
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // ----------- Счетчик мероприятий ----------

                                    androidx.compose.material.Text(
                                        text = meetingCounter.value,
                                        style = Typography.labelSmall,
                                        color = Grey40
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                    shape = RoundedCornerShape(50)
                                ) {

                                    // ----- Иконка акций ------

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_fire),
                                        contentDescription = "",
                                        modifier = Modifier.size(15.dp),
                                        tint = Grey40
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    // ----------- Счетчик акций ----------

                                    androidx.compose.material.Text(
                                        text = stockCounter.value,
                                        style = Typography.labelSmall,
                                        color = Grey40
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun PlaceCard (navController: NavController, placeItem: PlacesAdsClass, placeKey: MutableState<String>) {

        val iconFavColor = remember{ mutableStateOf(Grey10) } // Переменная цвета иконки ИЗБРАННОЕ
        val meetingCounter = remember{ mutableStateOf("") } // Счетчик количества мероприятий
        val stockCounter = remember{ mutableStateOf("") } // Счетчик количества акций

        // Считываем с базы данных - добавлено ли это заведение в избранное?

        act.placesDatabaseManager.favIconPlace(placeItem.placeKey!!){
            // Если колбак тру, то окрашиваем иконку в нужный цвет
            if (it){
                iconFavColor.value = PrimaryColor
            } else {
                // Если колбак фалс, то в обычный цвет
                iconFavColor.value = Grey10
            }
        }

        // Считываем количество мероприятий у этого заведения

        act.meetingDatabaseManager.readMeetingCounterInPlaceDataFromDb(placeItem.placeKey){ meetingsCounter ->
            meetingCounter.value = meetingsCounter.toString()
        }

        // Считываем количество акций у этого заведения

        act.stockDatabaseManager.readStockCounterInPlaceFromDb(placeKey = placeItem.placeKey) { stockCounters ->
            stockCounter.value = stockCounters.toString()
        }

        // Переменная, которая содержит в себе информацию о заведении
        val placeInfo = remember {
            mutableStateOf(PlacesAdsClass())
        }

        // Переменная счетчика людей, добавивших в избранное заведение
        val favCounter = remember {
            mutableStateOf(0)
        }

        // Переменная счетчика просмотра заведения
        val viewCounter = remember {
            mutableStateOf(0)
        }

        // Считываем данные про заведение и счетчики добавивших в избранное и количество просмотров заведения

        act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo, placeItem.placeKey){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров заведения

        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable {

                    // При клике на карточку - передаем на Main Activity placeKey. Ключ берем из дата класса заведения

                    placeKey.value = placeItem.placeKey.toString()

                    // так же при нажатии регистрируем счетчик просмотров - добавляем 1 просмотр

                    placeItem.placeKey.let {
                        act.placesDatabaseManager.viewCounterPlace(it) { result ->

                            // если колбак тру, то счетчик успешно сработал, значит переходим на страницу заведения
                            if (result) {

                                navController.navigate(PLACE_VIEW)
                            }
                        }
                    }

                }
            ,
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(Grey100)
        ) {

            Box(modifier = Modifier.fillMaxWidth()){

                if (placeItem.logo != null){

                    AsyncImage(
                        model = placeItem.logo, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТНОГО ЗАВЕДЕНИЯ ИЗ БД
                        contentDescription = "Логотип заведения", // описание изображения для слабовидящих
                        modifier = Modifier
                            .height(260.dp), // заполнить картинкой весь контейнер
                        contentScale = ContentScale.FillWidth, // обрезать картинку, что не вмещается
                        //alignment = Alignment.Center
                    )

                } else {

                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        painter = painterResource(id = R.drawable.rest_logo2),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // ------- КНОПКА КАТЕГОРИИ -----------

                    if (placeItem.category != null) {

                        Button(
                            onClick = { Toast.makeText(act, "Сделать фунцию", Toast.LENGTH_SHORT).show()},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                            shape = RoundedCornerShape(50)
                        ) {

                            androidx.compose.material.Text(
                                text = placeItem.category,
                                style = Typography.labelSmall,
                                color = Grey40
                            )

                        }

                    }

                    // ----------- Счетчик избранных ----------

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                        shape = RoundedCornerShape(50)
                    ) {

                        androidx.compose.material.Text(
                            text = favCounter.value.toString(),
                            style = Typography.labelSmall,
                            color = Grey40
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        // ----- Иконка избранное ------

                        Icon(
                            painter = painterResource(id = R.drawable.ic_bookmark),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = iconFavColor.value
                        )

                    }

                }

                // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 245.dp, end = 0.dp, start = 0.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {

                    // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp, bottomStart = 15.dp),
                        elevation = CardDefaults.cardElevation(5.dp),
                        colors = CardDefaults.cardColors(Grey100)
                    ) {

                        Column(modifier = Modifier.padding(20.dp)) {

                            // ----- НАЗВАНИЕ ЗАВЕДЕНИЯ --------

                            if (placeItem.placeName != null) {

                                Text(
                                    text = placeItem.placeName,
                                    style = Typography.titleLarge,
                                    color = Grey10
                                )

                            }


                            // ----- ГОРОД -----

                            Spacer(modifier = Modifier.height(10.dp))

                            if (placeItem.city != null){

                                Text(
                                    text = placeItem.city,
                                    style = Typography.bodyMedium,
                                    color = Grey40
                                )

                            }


                            // ---- АДРЕС -----

                            if (placeItem.address != null){

                                Text(
                                    text = placeItem.address,
                                    style = Typography.bodyMedium,
                                    color = Grey40
                                )

                            }



                            Spacer(modifier = Modifier.height(10.dp))

                            // ----- ВРЕМЯ РАБОТЫ ------

                            if (placeItem.openTime != null && placeItem.closeTime != null) {

                                IconText(icon = R.drawable.ic_time, inputText = "${placeItem.openTime} - ${placeItem.closeTime}")

                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                    shape = RoundedCornerShape(50)
                                ) {

                                    // ----- Иконка мероприятий ------

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_celebration),
                                        contentDescription = "",
                                        modifier = Modifier.size(20.dp),
                                        tint = Grey40
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // ----------- Счетчик мероприятий ----------

                                    androidx.compose.material.Text(
                                        text = when (meetingCounter.value) {
                                            "1" -> "${meetingCounter.value} мероприятие"
                                            "2","3","4" -> "${meetingCounter.value} мероприятия"
                                            else -> "${meetingCounter.value} мероприятий"
                                        },
                                        style = Typography.labelSmall,
                                        color = Grey40
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                    shape = RoundedCornerShape(50)
                                ) {

                                    // ----- Иконка акций ------

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_fire),
                                        contentDescription = "",
                                        modifier = Modifier.size(20.dp),
                                        tint = Grey40
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    // ----------- Счетчик акций ----------

                                    androidx.compose.material.Text(
                                        text = when (stockCounter.value) {
                                            "1" -> "${stockCounter.value} акция"
                                            "2","3","4" -> "${stockCounter.value} акции"
                                            else -> "${stockCounter.value} акций"
                                        },
                                        style = Typography.labelSmall,
                                        color = Grey40
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package kz.dvij.dvij_compose3.elements

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.FOR_CARDS
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesCardClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PlacesCard (val act: MainActivity) {

    @Composable
    fun PlaceCardSmall (
        navController: NavController,
        placeItem: PlacesCardClass,
        placeKey: MutableState<String>,
        openLoading: MutableState<Boolean>,
    ) {

        val iconFavColor = remember{ mutableStateOf(Grey10) } // Переменная цвета иконки ИЗБРАННОЕ
        val meetingCounter = remember{ mutableStateOf("") } // Счетчик количества мероприятий
        val stockCounter = remember{ mutableStateOf("") } // Счетчик количества акций

        // Считываем с базы данных - добавлено ли это заведение в избранное?

        act.placesDatabaseManager.favIconPlace(placeItem.placeKey!!){
            // Если колбак тру, то окрашиваем иконку в нужный цвет
            if (it){
                iconFavColor.value = YellowDvij
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                    openLoading.value = true

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            // ----- ЛОГОТИП ЗАВЕДЕНИЯ ---------

            if (placeItem.logo != null && placeItem.logo != ""){

                AsyncImage(
                    model = placeItem.logo,
                    contentDescription = "",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {

                    // ----- НАЗВАНИЕ ЗАВЕДЕНИЯ --------

                    if (placeItem.placeName != null && placeItem.placeName != ""){

                        Text(
                            text = placeItem.placeName,
                            style = Typography.bodyMedium,
                            color = WhiteDvij
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                    }

                    // --------- АДРЕС -------------

                    if (placeItem.address != null && placeItem.address != ""){

                        Text(
                            text = placeItem.address,
                            style = Typography.labelMedium,
                            color = Grey_Text
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                    }

                    // ------ МЕРОПРИЯТИЯ --------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {

                        Row (verticalAlignment = Alignment.CenterVertically) {

                            // ----- Иконка мероприятий ------

                            Icon(
                                painter = painterResource(id = R.drawable.ic_celebration),
                                contentDescription = "",
                                //modifier = Modifier.size(15.dp),
                                tint = YellowDvij
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // ----------- Счетчик мероприятий ----------

                            androidx.compose.material.Text(
                                text = meetingCounter.value,
                                style = Typography.labelMedium,
                                color = WhiteDvij
                            )

                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Row (verticalAlignment = Alignment.CenterVertically) {

                            // ----- Иконка акций ------

                            Icon(
                                painter = painterResource(id = R.drawable.ic_fire),
                                contentDescription = "",
                                //modifier = Modifier.size(15.dp),
                                tint = YellowDvij
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            // ----------- Счетчик акций ----------

                            androidx.compose.material.Text(
                                text = stockCounter.value,
                                style = Typography.labelMedium,
                                color = WhiteDvij
                            )

                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // ИКОНКА РЕДАКТИРОВАТЬ

                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.ic_right), // сама иконка
                    contentDescription = stringResource(id = R.string.cd_move_to_profile), // описание для слабовидящих
                    tint = WhiteDvij // цвет иконки
                )
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PlaceCardForNewClass (
        navController: NavController,
        placeItem: PlacesCardClass,
        placeKeyFromAct: MutableState<String>,
        filledPlaceInfoFromAct: MutableState<PlacesCardClass>,
        isAd: Boolean = false,
        openLoadingState: MutableState<Boolean>,
    ) {

        val iconFavColor = remember{ mutableStateOf(WhiteDvij) } // Переменная цвета иконки ИЗБРАННОЕ

        val getNowTime = ZonedDateTime.now(ZoneId.of("Asia/Almaty"))
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE, HH:mm"))

        val splitDate = getNowTime.split(", ")

        val nowDay = splitDate[1]
        val nowTime = splitDate[2]

        val placeTimeOnToday = act.placesDatabaseManager.returnWrightTimeOnCurrentDay(nowDay, placeItem)

        val nowIsOpen = act.placesDatabaseManager.nowIsOpenPlace(nowTime, placeTimeOnToday[0], placeTimeOnToday[1])

        val openConfirmChoose = remember {mutableStateOf(false)} // диалог действительно хотите удалить?




        // Считываем с базы данных - добавлено ли это заведение в избранное?

        placeItem.placeKey?.let {key ->
            act.placesDatabaseManager.favIconPlace(key){ result ->
                // Если колбак тру, то окрашиваем иконку в нужный цвет
                if (result){
                    iconFavColor.value = YellowDvij
                } else {
                    // Если колбак фалс, то в обычный цвет
                    iconFavColor.value = WhiteDvij
                }
            }
        }

        // Переменная, которая содержит в себе информацию о заведении
        val placeInfo = remember {
            mutableStateOf(PlacesCardClass())
        }

        // Переменная счетчика людей, добавивших в избранное заведение
        val favCounter = remember {
            mutableStateOf(placeItem.favCounter)
        }

        // Переменная счетчика просмотра заведения
        val viewCounter = remember {
            mutableStateOf(placeItem.viewCounter)
        }

        // Считываем данные про заведение и счетчики добавивших в избранное и количество просмотров заведения

        placeItem.placeKey?.let { placeKey->
            act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo, placeKey){

                favCounter.value = it[0].toString() // данные из списка - количество добавивших в избранное
                viewCounter.value = it[1].toString() // данные из списка - количество просмотров заведения

            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable {

                    openLoadingState.value = true

                    // При клике на карточку - передаем на Main Activity placeKey. Ключ берем из дата класса заведения

                    placeKeyFromAct.value = placeInfo.value.placeKey!!

                    // так же при нажатии регистрируем счетчик просмотров - добавляем 1 просмотр

                    act.placesDatabaseManager.viewCounterPlace(placeItem.placeKey!!) { result ->

                        // если колбак тру, то счетчик успешно сработал, значит переходим на страницу заведения
                        if (result) {

                            navController.navigate(PLACE_VIEW)
                        }
                    }
                }
            ,
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(Grey_Background)
        ) {

            Box(modifier = Modifier.fillMaxWidth()){

                if (placeItem.logo != null){

                    AsyncImage(
                        model = placeItem.logo, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТНОГО ЗАВЕДЕНИЯ ИЗ БД
                        contentDescription = "Логотип заведения", // описание изображения для слабовидящих
                        modifier = Modifier
                            .height(260.dp), // заполнить картинкой весь контейнер
                        contentScale = ContentScale.Crop, // обрезать картинку, что не вмещается
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Bubble(
                        buttonText = if (viewCounter.value != null && viewCounter.value != "null" ) {
                            viewCounter.value.toString()
                        } else {"0"},
                        leftIcon = R.drawable.ic_visibility,
                        typeButton = FOR_CARDS
                    ) {
                        Toast.makeText(act,"Количество просмотров акции",Toast.LENGTH_SHORT).show()
                    }

                    Bubble(
                        buttonText = if (favCounter.value != null && favCounter.value != "null" ) {
                            favCounter.value.toString()
                        } else {"0"},
                        rightIcon = R.drawable.ic_fav,
                        typeButton = FOR_CARDS,
                        rightIconColor = iconFavColor.value
                    ) {
                        // --- Если клиент авторизован, проверяем, добавлено ли уже в избранное это заведение -----
                        // Если не авторизован, условие else

                        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                            act.placesDatabaseManager.favIconPlace(placeItem.placeKey!!) { inFav ->

                                // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                if (inFav) {

                                    // Убираем из избранных
                                    act.placesDatabaseManager.removeFavouritePlace(placeItem.placeKey) { yes ->

                                        // Если пришел колбак, что успешно

                                        if (yes) {
                                            act.placesDatabaseManager.readFavCounter(placeItem.placeKey){

                                                favCounter.value = it

                                            }

                                            iconFavColor.value =
                                                WhiteDvij // При нажатии окрашиваем кнопку в темно-серый

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

                                    act.placesDatabaseManager.addFavouritePlace(placeItem.placeKey) { notInFav ->

                                        // Если пришел колбак, что успешно

                                        if (notInFav) {

                                            act.placesDatabaseManager.readFavCounter(placeItem.placeKey){

                                                favCounter.value = it

                                            }

                                            iconFavColor.value =
                                                YellowDvij // При нажатии окрашиваем текст и иконку в черный

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
                    }


                }

                // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 235.dp, end = 0.dp, start = 0.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {

                    // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
                        elevation = CardDefaults.cardElevation(5.dp),
                        colors = CardDefaults.cardColors(Grey_ForCards)
                    ) {

                        Column(modifier = Modifier.padding(20.dp)) {


                            Row {
                                androidx.compose.material3.Text(
                                    text = "#Место",
                                    color = Grey_Text,
                                    style = Typography.labelMedium
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                androidx.compose.material3.Text(
                                    text = "#${placeItem.category}",
                                    color = Grey_Text,
                                    style = Typography.labelMedium
                                )
                            }

                            if (isAd){

                                Spacer(modifier = Modifier.height(5.dp))

                                androidx.compose.material3.Text(
                                    text = "#Рекламный пост",
                                    color = Grey_Text,
                                    style = Typography.labelMedium
                                )

                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            // ----- НАЗВАНИЕ ЗАВЕДЕНИЯ --------

                            if (placeItem.placeName != null) {

                                Text(
                                    text = placeItem.placeName,
                                    style = Typography.titleMedium,
                                    color = WhiteDvij
                                )

                            }


                            // ----- ГОРОД -----

                            if (placeItem.city != null && placeItem.address != null){

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "${placeItem.city}, ${placeItem.address}",
                                    style = Typography.labelMedium,
                                    color = Grey_Text
                                )

                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (nowIsOpen){
                                Bubble(
                                    buttonText = "Открыто до ${placeTimeOnToday[1]}"
                                ) {}
                            } else {

                                Bubble(buttonText = "Сейчас закрыто", typeButton = ATTENTION) {}

                            }

                            Spacer(modifier = Modifier.height(20.dp))


                            if (placeItem.placeDescription != null){

                                androidx.compose.material3.Text(
                                    text = placeItem.placeDescription,
                                    style = Typography.bodySmall,
                                    color = WhiteDvij
                                )

                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {

                                Row (verticalAlignment = Alignment.CenterVertically) {

                                    // ----- Иконка мероприятий ------

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_celebration),
                                        contentDescription = "",
                                        //modifier = Modifier.size(15.dp),
                                        tint = YellowDvij
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // ----------- Счетчик мероприятий ----------

                                    androidx.compose.material.Text(
                                        text = if (placeItem.meetingCounter != null && placeItem.meetingCounter != "null"){
                                            placeItem.meetingCounter
                                        } else {
                                            "0"
                                        },
                                        style = Typography.labelMedium,
                                        color = WhiteDvij
                                    )

                                }

                                Spacer(modifier = Modifier.width(20.dp))

                                Row (verticalAlignment = Alignment.CenterVertically) {

                                    // ----- Иконка акций ------

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_fire),
                                        contentDescription = "",
                                        //modifier = Modifier.size(15.dp),
                                        tint = YellowDvij
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    // ----------- Счетчик акций ----------

                                    androidx.compose.material.Text(
                                        text = if (placeItem.stockCounter != null && placeItem.stockCounter != "null"){
                                            placeItem.stockCounter
                                        } else {
                                            "0"
                                        },

                                        style = Typography.labelMedium,
                                        color = WhiteDvij
                                    )

                                }
                            }



                        }

                        if (placeItem.owner == act.mAuth.uid){

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Grey_OnBackground).padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                // ----- КНОПКА РЕДАКТИРОВАТЬ ------

                                androidx.compose.material.Text(
                                    text = stringResource(id = R.string.edit),
                                    style = Typography.bodySmall,
                                    color = YellowDvij,
                                    modifier = Modifier.clickable {

                                        GlobalScope.launch(Dispatchers.Main) {

                                            openLoadingState.value = true

                                            GlobalScope.launch(Dispatchers.IO){

                                                // Считываем данные о заведении
                                                placeInfo.value.placeKey?.let {
                                                    act.placesDatabaseManager.readOnePlaceFromDataBaseReturnDataClass(it){ place ->

                                                        // если пришел дата класс заведения, присваеваем его в переменную на МАИН АКТИВИТИ
                                                        filledPlaceInfoFromAct.value = place

                                                        // Переходим на страницу редактирования
                                                        navController.navigate(EDIT_PLACES_SCREEN)

                                                    }
                                                }
                                            }
                                        }
                                    }
                                )

                                // ------ КНОПКА УДАЛИТЬ -------

                                androidx.compose.material.Text(
                                    text = stringResource(id = R.string.delete),
                                    style = Typography.bodySmall,
                                    color = AttentionRed,
                                    modifier = Modifier.clickable { openConfirmChoose.value = true }
                                )

                            }

                        }

                        if (openConfirmChoose.value) {

                            ConfirmDialog(onDismiss = { openConfirmChoose.value = false }) {

                                GlobalScope.launch(Dispatchers.Main) {

                                    openLoadingState.value = true

                                    GlobalScope.launch(Dispatchers.Main){

                                        placeInfo.value.placeKey?.let {
                                            placeInfo.value.logo?.let { it1 ->
                                                act.placesDatabaseManager.deletePlace(it, it1){

                                                    navController.navigate(PLACES_ROOT) {popUpTo(0)}

                                                }
                                            }
                                        }

                                    }

                                }


                            }
                        }
                    }
                }
            }
        }
    }
}
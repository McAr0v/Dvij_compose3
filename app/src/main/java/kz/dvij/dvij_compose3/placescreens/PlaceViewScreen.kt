package kz.dvij.dvij_compose3.placescreens

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
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
import kz.dvij.dvij_compose3.constants.*
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.navigation.EDIT_PLACES_SCREEN
import kz.dvij.dvij_compose3.navigation.PLACES_ROOT
import kz.dvij.dvij_compose3.ui.theme.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PlaceViewScreen (val act: MainActivity) {

    private val ownerCard = OwnerCard(act)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotConstructor")
    @Composable
    fun PlaceViewScreen (
        key: String, // Ключ заведения, для считываения данных о заведении
        navController: NavController,
        meetingKey: MutableState<String>, // КЛЮЧ МЕРОПРИЯТИЯ ДЛЯ ПЕРЕХОДА НА КЛИК ПО КАРТОЧКЕ МЕРОПРИЯТИЯ
        stockKey: MutableState<String>, // КЛЮЧ АКЦИЙ для перехода на клик по карточке Акции
        filledPlaceInfoFromAct: MutableState<PlacesCardClass>, // ЗАПОЛНЕННЫЕ ДАННЫЕ О ЗАВЕДЕНИИ ДЛЯ ПЕРЕХОДА НА РЕДАКТИРОВАНИЕ
        filledMeetingInfoFromAct: MutableState<MeetingsAdsClass>, // ЗАПОЛНЕННЫЕ ДАННЫЕ О ЗАВЕДЕНИИ ДЛЯ ПЕРЕХОДА НА РЕДАКТИРОВАНИЕ
        filledStockInfoFromAct: MutableState<StockAdsClass>
    ){

        val getNowTime = ZonedDateTime.now(ZoneId.of("Asia/Almaty"))
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE, HH:mm"))

        val splitDate = getNowTime.split(", ")

        val nowDay = splitDate[1]
        val nowTime = splitDate[2]





        //val nowIsOpen = act.placesDatabaseManager.nowIsOpenPlace(nowTime, placeTimeOnToday[0], placeTimeOnToday[1])

        // Переменная, которая содержит в себе информацию о заведении
        val placeInfo = remember {
            mutableStateOf(PlacesCardClass())
        }

        // Переменная, отвечающая за цвет кнопки избранных
        val buttonFavColor = remember {
            mutableStateOf(Grey90)
        }

        // Переменная, отвечающая за цвет иконки избранных
        val iconTextFavColor = remember {
            mutableStateOf(WhiteDvij)
        }

        // Переменная счетчика людей, добавивших в избранное заведение
        val favCounter = remember {
            mutableStateOf(placeInfo.value.favCounter)
        }

        // Переменная счетчика просмотра заведения
        val viewCounter = remember {
            mutableStateOf(placeInfo.value.viewCounter)
        }

        // инициализируем список мероприятий этого заведения
        val meetingsList = remember {
            mutableStateOf(listOf<MeetingsCardClass>())
        }

        // инициализируем список акций
        val stockList = remember {
            mutableStateOf(listOf<StockCardClass>())
        }

        val defaultMeeting = MeetingsCardClass (
            description = "Default"
        )

        // создаем акцию по умолчанию
        val defaultStock = StockCardClass (
            description = "Default"
        )

        val openConfirmChoose = remember {mutableStateOf(false)} // диалог действительно хотите удалить?

        val placeTimeOnToday = remember {
            mutableStateOf(listOf<String>())
        }

        // Считываем данные про заведение и счетчики добавивших в избранное и количество просмотров заведения

        act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo, key){

            placeTimeOnToday.value = act.placesDatabaseManager.returnWrightTimeOnCurrentDay(nowDay,placeInfo.value)

            favCounter.value = it[0].toString() // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1].toString() // данные из списка - количество просмотров заведения

        }

        // Если пользователь авторизован, проверяем, добавлено ли уже заведение в избранное, или нет

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.placesDatabaseManager.favIconPlace(key) {
                if (it) {
                    iconTextFavColor.value = YellowDvij
                } else {
                    iconTextFavColor.value = WhiteDvij
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

        if (openConfirmChoose.value) {

            ConfirmDialog(onDismiss = { openConfirmChoose.value = false }) {

                placeInfo.value.placeKey?.let {
                    placeInfo.value.logo?.let { it1 ->
                        act.placesDatabaseManager.deletePlace(it, it1){

                            navController.navigate(PLACES_ROOT) {popUpTo(0)}

                        }
                    }
                }

            }
        }

        // ---------- КОНТЕНТ СТРАНИЦЫ --------------

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey_Background),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {

            item {

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(0.dp)) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        // ------- КАРТИНКА МЕРОПРИЯТИЯ ----------

                        if (placeInfo.value.logo != null) {

                            // ------- КАРТИНКА Заведения ----------

                            AsyncImage(
                                model = placeInfo.value.logo,
                                contentDescription = "Логотип заведения",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            // ЗДЕСЬ ЛАЙКИ И ПРОСМОТРЫ

                            Bubble(
                                buttonText = viewCounter.value.toString(),
                                leftIcon = R.drawable.ic_visibility,
                                typeButton = DARK
                            ) {
                                Toast.makeText(act, "Количество просмотров акции", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            Bubble(
                                buttonText = favCounter.value.toString(),
                                rightIcon = R.drawable.ic_fav,
                                typeButton = DARK,
                                rightIconColor = iconTextFavColor.value
                            ) {

                                // --- Если клиент авторизован, проверяем, добавлено ли уже в избранное это заведение -----
                                // Если не авторизован, условие else

                                if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                                    act.placesDatabaseManager.favIconPlace(key) { inFav ->

                                        // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                        if (inFav) {

                                            // Убираем из избранных
                                            act.placesDatabaseManager.removeFavouritePlace(key) { yes ->

                                                // Если пришел колбак, что успешно

                                                if (yes) {

                                                    iconTextFavColor.value = WhiteDvij // При нажатии окрашиваем текст и иконку в белый

                                                    act.placesDatabaseManager.readFavCounter(
                                                        placeInfo.value.placeKey!!
                                                    ) { fav ->

                                                        favCounter.value = fav

                                                    }

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

                                            act.placesDatabaseManager.addFavouritePlace(key) { notInFav ->

                                                // Если пришел колбак, что успешно

                                                if (notInFav) {

                                                    iconTextFavColor.value = YellowDvij // При нажатии окрашиваем текст и иконку в черный

                                                    act.placesDatabaseManager.readFavCounter(
                                                        placeInfo.value.placeKey!!
                                                    ) { fav ->

                                                        favCounter.value = fav

                                                    }

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

                            androidx.compose.material3.Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(
                                    topStart = 30.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp,
                                    bottomStart = 0.dp
                                ),
                                elevation = CardDefaults.cardElevation(5.dp),
                                colors = CardDefaults.cardColors(Grey_Background)
                            ) {

                                Column(
                                    modifier = Modifier.padding(
                                        vertical = 30.dp,
                                        horizontal = 20.dp
                                    )
                                ) {


                                    Row {
                                        Text(
                                            text = "#Место",
                                            color = Grey_Text,
                                            style = Typography.labelMedium
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = "#${placeInfo.value.category}",
                                            color = Grey_Text,
                                            style = Typography.labelMedium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // -------- НАЗВАНИЕ АКЦИИ ----------

                                    if (placeInfo.value.placeName != null && placeInfo.value.placeName != "null" && placeInfo.value.placeName != "") {

                                        Text(
                                            text = placeInfo.value.placeName!!,
                                            style = Typography.titleMedium,
                                            color = WhiteDvij
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))



                                    if (
                                        placeInfo.value.city != null
                                        && placeInfo.value.city != "null"
                                        && placeInfo.value.city != ""
                                        && placeInfo.value.address != null
                                        && placeInfo.value.address != "null"
                                        && placeInfo.value.address != ""
                                    ) {

                                        Text(
                                            text = "${placeInfo.value.city}, ${placeInfo.value.address}",
                                            style = Typography.bodySmall,
                                            color = WhiteDvij
                                        )
                                    }

                                    if (placeTimeOnToday.value != listOf<String>()){

                                        val nowIsOpen = act.placesDatabaseManager.nowIsOpenPlace(nowTime, placeTimeOnToday.value[0], placeTimeOnToday.value[1])

                                        Spacer(modifier = Modifier.height(20.dp))

                                        if (nowIsOpen){
                                        Bubble(
                                            buttonText = "Открыто до ${placeTimeOnToday.value[1]}"
                                        ) {}
                                    } else {

                                        Bubble(buttonText = "Сейчас закрыто", typeButton = ATTENTION) {}

                                        }
                                    }

                                    if (placeInfo.value.placeDescription != null && placeInfo.value.placeDescription != "" && placeInfo.value.placeDescription != "null") {

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Text(
                                            text = placeInfo.value.placeDescription!!,
                                            style = Typography.bodySmall,
                                            color = WhiteDvij
                                        )

                                    }

                                    Spacer(modifier = Modifier.height(30.dp))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(
                                                width = 2.dp,
                                                color = YellowDvij,
                                                shape = RoundedCornerShape(
                                                    topStart = 15.dp,
                                                    topEnd = 15.dp,
                                                    bottomStart = 15.dp,
                                                    bottomEnd = 0.dp
                                                )
                                            )
                                            .background(
                                                color = Grey_OnBackground,
                                                shape = RoundedCornerShape(
                                                    topStart = 15.dp,
                                                    topEnd = 15.dp,
                                                    bottomStart = 15.dp,
                                                    bottomEnd = 0.dp
                                                )
                                            )
                                            .padding(20.dp)
                                        ,
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Контакты заведения",
                                            style = Typography.bodyLarge,
                                            color = WhiteDvij
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Row (modifier = Modifier.fillMaxSize()){

                                            // ----- КНОПКА ПОЗВОНИТЬ --------

                                            if (placeInfo.value.phone != null && placeInfo.value.phone != "7") {

                                                SocialButtonCustom(icon = R.drawable.ic_phone) {
                                                    act.callAndWhatsapp.makeACall(placeInfo.value.phone!!)
                                                }

                                                Spacer(modifier = Modifier
                                                    .width(10.dp)
                                                )

                                            }

                                            // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                                            if (
                                                placeInfo.value.whatsapp != null
                                                && placeInfo.value.whatsapp != "7"
                                                && placeInfo.value.whatsapp != ""
                                                && placeInfo.value.whatsapp != "+7"
                                                && placeInfo.value.whatsapp != "+77"
                                            ) {

                                                SocialButtonCustom(icon = R.drawable.whatsapp) {
                                                    act.callAndWhatsapp.writeInWhatsapp(placeInfo.value.whatsapp!!)
                                                }

                                                Spacer(modifier = Modifier
                                                    .width(10.dp)
                                                )

                                            }

                                            // ---- КНОПКА ПЕРЕХОДА В ИНСТАГРАМ -----------

                                            if (
                                                placeInfo.value.instagram != null
                                                && placeInfo.value.instagram != "null"
                                                && placeInfo.value.instagram != ""
                                            ) {

                                                SocialButtonCustom(icon = R.drawable.instagram) {
                                                    act.callAndWhatsapp.goToInstagramOrTelegram(placeInfo.value.instagram!!, INSTAGRAM_URL)
                                                }

                                                Spacer(modifier = Modifier
                                                    .width(10.dp)
                                                )

                                            }

                                            // ---- КНОПКА НАПИСАТЬ В ТЕЛЕГРАМ -----------

                                            if (
                                                placeInfo.value.telegram != null
                                                && placeInfo.value.telegram != "null"
                                                && placeInfo.value.telegram != ""
                                            ) {

                                                SocialButtonCustom(icon = R.drawable.telegram) {
                                                    act.callAndWhatsapp.goToInstagramOrTelegram(placeInfo.value.telegram!!, TELEGRAM_URL)
                                                }

                                                Spacer(modifier = Modifier
                                                    .width(10.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(30.dp))

                                    Text(
                                        text = "Режим работы",
                                        style = Typography.bodyLarge,
                                        color = WhiteDvij
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    if (placeInfo.value != PlacesCardClass()){

                                        WorkTimePlace(placeInfo = placeInfo.value, today = nowDay)

                                    }

                                }
                            }
                        }
                    }
                }

            }

            item {

                // ---- МЕРОПРИЯТИЯ ЭТОГО ЗАВЕДЕНИЯ -----

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Мероприятия этого заведения",
                    style = Typography.titleMedium,
                    color = WhiteDvij
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (meetingsList.value.isNotEmpty() && meetingsList.value != listOf(defaultMeeting)){

                // для каждого элемента из списка указываем шаблон для отображения

                items(meetingsList.value) { item ->

                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        // сам шаблон карточки мероприятия
                        act.meetingsCard.MeetingCard(
                            navController = navController,
                            meetingItem = item,
                            meetingKey = meetingKey,
                            filledMeeting = filledMeetingInfoFromAct,
                            filledPlace = filledPlaceInfoFromAct
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
                            style = Typography.bodySmall,
                            color = WhiteDvij
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
                            color = YellowDvij,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        // текст рядом с крутилкой

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodySmall,
                            color = WhiteDvij
                        )

                    }

                }

            }

            item {

                // ------- РЕКЛАМНЫЙ БАННЕР -------

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(200.dp),
                    painter = painterResource(id = R.drawable.korn_concert),
                    contentDescription = "",
                    contentScale = ContentScale.Crop

                    )

            }

            item {

                // ---- АКЦИИ ЭТОГО ЗАВЕДЕНИЯ -----

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Акции этого заведения",
                    style = Typography.titleMedium,
                    color = WhiteDvij
                )

                Spacer(modifier = Modifier.height(20.dp))

            }

            if (stockList.value.isNotEmpty() && stockList.value != listOf(defaultStock)){

                // для каждого элемента из списка указываем шаблон для отображения

                items(stockList.value) { item ->

                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        // сам шаблон карточки акции
                        act.stockCard.StockCard(navController = navController, stockItem = item, stockKey = stockKey, filledStock = filledStockInfoFromAct, filledPlace = filledPlaceInfoFromAct)
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
                            color = YellowDvij,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        // текст рядом с крутилкой

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodySmall,
                            color = WhiteDvij
                        )

                    }
                }
            }

            item {

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)) {

                    if (placeInfo.value.owner != null && placeInfo.value.owner != "null" && placeInfo.value.owner != ""){

                        Spacer(modifier = Modifier.height(20.dp))

                        ownerCard.OwnerCardView(userKey = placeInfo.value.owner!!, "Создатель места")

                    }

                }

            }

            item {
                if (placeInfo.value.owner == act.mAuth.uid){

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(modifier = Modifier.padding(20.dp)) {

                        ButtonCustom(buttonText = "Редактировать") {

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

                        Spacer(modifier = Modifier.height(30.dp))

                        ButtonCustom(
                            buttonText = "Удалить",
                            typeButton = ATTENTION,
                            leftIcon = R.drawable.ic_close
                        ) {
                            openConfirmChoose.value = true
                        }

                    }




                }
            }
        }
    }
}
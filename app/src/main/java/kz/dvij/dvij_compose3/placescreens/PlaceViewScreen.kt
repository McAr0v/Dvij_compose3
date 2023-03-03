package kz.dvij.dvij_compose3.placescreens

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
        filledPlaceInfoFromAct: MutableState<PlacesAdsClass> // ЗАПОЛНЕННЫЕ ДАННЫЕ О ЗАВЕДЕНИИ ДЛЯ ПЕРЕХОДА НА РЕДАКТИРОВАНИЕ
    ){

        val getNowTime = ZonedDateTime.now(ZoneId.of("Asia/Almaty"))
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE, HH:mm"))

        val splitDate = getNowTime.split(", ")

        val nowDay = splitDate[1]
        val nowTime = splitDate[2]



        //val nowIsOpen = act.placesDatabaseManager.nowIsOpenPlace(nowTime, placeTimeOnToday[0], placeTimeOnToday[1])

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

            placeTimeOnToday.value = act.placesDatabaseManager.returnWrightTimeOnCurrentDayInStandartClass(nowDay,placeInfo.value)

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

                    // ------ КНОПКА РЕДАКТИРОВАТЬ ------

                    if (placeInfo.value.owner == act.mAuth.uid){

                        // ----- ЕСЛИ КЛЮЧ АВТОРА СОВПОДАЕТ С МОИ КЛЮЧОМ, ТО ДОСТУПНА КНОПКА РЕДАКТИРОВАТЬ ------

                        Button(

                            onClick = {

                                // Считываем данные о заведении
                                placeInfo.value.placeKey?.let {
                                    act.placesDatabaseManager.readOnePlaceFromDataBaseReturnDataClass(it){ place ->

                                        // если пришел дата класс заведения, присваеваем его в переменную на МАИН АКТИВИТИ
                                        filledPlaceInfoFromAct.value = place

                                        // Переходим на страницу редактирования
                                        navController.navigate(EDIT_PLACES_SCREEN)

                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth() // кнопка на всю ширину
                                .height(50.dp)// высота - 50
                                .padding(horizontal = 30.dp), // отступы от краев
                            shape = RoundedCornerShape(50), // скругление углов
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = SuccessColor, // цвет кнопки
                                contentColor = Grey100 // цвет контента на кнопке
                            )
                        ) {
                            Text(
                                text = "Редактировать",
                                style = Typography.labelMedium
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            androidx.compose.material.Icon(
                                painter = painterResource(id = R.drawable.ic_publish),
                                contentDescription = stringResource(id = R.string.cd_publish_button),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ------ КНОПКА УДАЛЕНИЯ ------

                        Button(

                            onClick = {

                                openConfirmChoose.value = true

                            },
                            modifier = Modifier
                                .fillMaxWidth() // кнопка на всю ширину
                                .height(50.dp)// высота - 50
                                .padding(horizontal = 30.dp), // отступы от краев
                            shape = RoundedCornerShape(50), // скругление углов
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = SuccessColor, // цвет кнопки
                                contentColor = Grey100 // цвет контента на кнопке
                            )
                        ) {
                            Text(
                                text = "Удалить",
                                style = Typography.labelMedium
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            androidx.compose.material.Icon(
                                painter = painterResource(id = R.drawable.ic_publish),
                                contentDescription = "Кнопка удалить",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }



                    // -------- НАЗВАНИЕ ЗАВЕДЕНИЯ ----------

                    if (placeInfo.value.placeName != null && placeInfo.value.placeName != "" && placeInfo.value.placeName != "null") {

                        Text(
                            text = placeInfo.value.placeName!!,
                            style = Typography.titleLarge,
                            color = Grey10
                        )
                    }

                    // ------- ГОРОД ------------

                    if (placeInfo.value.city != null && placeInfo.value.city != "" && placeInfo.value.city != "null" && placeInfo.value.city != "Выбери город") {

                        Text(
                            text = placeInfo.value.city!!,
                            style = Typography.bodyMedium,
                            color = Grey40
                        )
                    }

                    // ------- АДРЕС ------------

                    if (placeInfo.value.address != null && placeInfo.value.address != "" && placeInfo.value.address != "null") {

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


                        if (placeInfo.value.category != null && placeInfo.value.category != "" && placeInfo.value.category != "null" && placeInfo.value.category != "Выбери категорию") {

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
                                    act.placesDatabaseManager.favIconPlace(key) { inFav ->

                                        // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                        if (inFav) {

                                            // Убираем из избранных
                                            act.placesDatabaseManager.removeFavouritePlace(key) { yes ->

                                                // Если пришел колбак, что успешно

                                                if (yes) {

                                                    iconTextFavColor.value =
                                                        Grey40 // При нажатии окрашиваем текст и иконку в белый
                                                    buttonFavColor.value =
                                                        Grey80 // При нажатии окрашиваем кнопку в темно-серый
                                                    favCounter.value = favCounter.value - 1

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

                                                    iconTextFavColor.value =
                                                        PrimaryColor // При нажатии окрашиваем текст и иконку в черный
                                                    buttonFavColor.value =
                                                        Grey90_2 // Окрашиваем кнопку в главный цвет

                                                    favCounter.value++

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

                        // ---- ВРЕМЯ -----

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                        ) {

                            // сделать РЕЖИМ РАБОТЫ

                            if (placeTimeOnToday.value != listOf<String>()){

                                HeadlineAndDesc(
                                    headline = "${placeTimeOnToday.value[0]} - ${placeTimeOnToday.value[1]}",
                                    desc = nowDay //
                                )

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))



                    // ----- РАЗДЕЛ СВЯЗАТЬСЯ С ОРГАНИЗАТОРОМ -----

                    SpacerTextWithLine(headline = "Контакты заведения")

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxSize()) {

                        // ----- КНОПКА ПОЗВОНИТЬ --------

                        if (placeInfo.value.phone != null && placeInfo.value.phone != "7" && placeInfo.value.phone != "+77" && placeInfo.value.phone != "" && placeInfo.value.phone != "null") {

                            IconButton(
                                onClick = { act.callAndWhatsapp.makeACall(placeInfo.value.phone!!) }, // функция набора номера
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

                        if (placeInfo.value.whatsapp != null && placeInfo.value.whatsapp != "7" && placeInfo.value.whatsapp != "+77" && placeInfo.value.whatsapp != "" && placeInfo.value.whatsapp != "null") {

                            IconButton(
                                onClick = { act.callAndWhatsapp.writeInWhatsapp(placeInfo.value.whatsapp!!) }, // Функция перехода в ватсапп
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

                        // ---- КНОПКА НАПИСАТЬ В ИНСТАГРАМ -----------

                        if (placeInfo.value.instagram != null && placeInfo.value.instagram != "" && placeInfo.value.instagram != "null") {

                            IconButton(
                                onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(placeInfo.value.instagram!!, INSTAGRAM_URL) }, // Функция перейти на инстаграм
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

                        if (placeInfo.value.telegram != null && placeInfo.value.telegram != "" && placeInfo.value.telegram != "null") {

                            IconButton(
                                onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(placeInfo.value.telegram!!, TELEGRAM_URL) }, // Функция написать в телеграм
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

                    SpacerTextWithLine(headline = "Создатель")

                    Spacer(modifier = Modifier.height(10.dp))

                    // КАРТОЧКА СОЗДАТЕЛЯ

                    if (placeInfo.value.owner != null && placeInfo.value.owner != "null" && placeInfo.value.owner != ""){

                        ownerCard.OwnerCardView(userKey = placeInfo.value.owner!!)

                        Spacer(modifier = Modifier.height(20.dp))

                    }



                    // ---------- ОПИСАНИЕ -------------



                    if (placeInfo.value.placeDescription != null && placeInfo.value.placeDescription != "" && placeInfo.value.placeDescription != "null") {

                        Text(
                            text = "О заведении",
                            style = Typography.titleMedium,
                            color = Grey10
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = placeInfo.value.placeDescription!!,
                            style = Typography.bodyMedium,
                            color = Grey10
                        )
                    }
                }


            }

            item {

                // ---- МЕРОПРИЯТИЯ ЭТОГО ЗАВЕДЕНИЯ -----

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
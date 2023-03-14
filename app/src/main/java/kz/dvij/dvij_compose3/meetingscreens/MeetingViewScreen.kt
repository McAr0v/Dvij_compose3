package kz.dvij.dvij_compose3.meetingscreens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.navigation.EDIT_MEETINGS_SCREEN
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.MEETING_VIEW
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingViewScreen(val act: MainActivity) {

    val placesDatabaseManager = PlacesDatabaseManager(act)
    private val placeCard = PlacesCard(act)
    private val ownerCard = OwnerCard(act)

    @SuppressLint("NotConstructor")
    @Composable
    fun MeetingViewScreen (
        meetingKey: MutableState<String>,
        navController: NavController,
        placeKey: MutableState<String>,
        filledMeetingInfoFromAct: MutableState<MeetingsAdsClass>,
        filledPlaceInfoFromAct: MutableState<PlacesAdsClass>
    ){


        // Переменная, которая содержит в себе информацию о мероприятии
        val meetingInfo = remember {
            mutableStateOf(MeetingsAdsClass())
        }

        // Переменная, отвечающая за цвет кнопки избранных
        val buttonFavColor = remember {
            mutableStateOf(Grey_ForCards)
        }

        // Переменная, отвечающая за цвет иконки избранных
        val iconTextFavColor = remember {
            mutableStateOf(WhiteDvij)
        }

        // Переменная счетчика людей, добавивших в избранное мероприятие
        val favCounter = remember {
            mutableStateOf(0)
        }

        // Переменная счетчика просмотра мероприятия
        val viewCounter = remember {
            mutableStateOf(0)
        }

        // Переменная, которая содержит информацию о заведении-организаторе
        val placeInfo = remember {
            mutableStateOf(PlacesAdsClass())
        }

        val openConfirmChoose = remember {mutableStateOf(false)} // диалог действительно хотите удалить?


        // Считываем данные про мероприятие и счетчики добавивших в избранное и количество просмотров мероприятия

        act.meetingDatabaseManager.readOneMeetingFromDataBase(meetingInfo, meetingKey.value){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров мероприятия

            // если считалось мероприятие, то берем из него ключ заведения и считываем данные о заведении

            meetingInfo.value.placeKey?.let { it1 ->
                act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo = placeInfo, key = it1) {

                }
            }
        }



        // Если пользователь авторизован, проверяем, добавлено ли уже мероприятие в избранное, или нет

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.meetingDatabaseManager.favIconMeeting(meetingKey.value) {
                if (it) {
                    buttonFavColor.value = Grey_ForCards
                    iconTextFavColor.value = YellowDvij
                } else {
                    buttonFavColor.value = Grey_ForCardsFav
                    iconTextFavColor.value = WhiteDvij
                }
            }
        }

        if (openConfirmChoose.value) {

            ConfirmDialog(onDismiss = { openConfirmChoose.value = false }) {

                if (meetingInfo.value.key != null && meetingInfo.value.placeKey != null && meetingInfo.value.image1 != null){

                    act.meetingDatabaseManager.deleteMeetingWithPlaceNote(
                        meetingKey = meetingInfo.value.key!!,
                        placeKey = meetingInfo.value.placeKey!!,
                        imageUrl = meetingInfo.value.image1!!
                    ) {

                        if (it) {

                            Log.d ("MyLog", "Удалилась и картинка и само мероприятие и запись у заведения")
                            navController.navigate(MEETINGS_ROOT) {popUpTo(0)}

                        } else {

                            Log.d ("MyLog", "Почемуто не удалилось")

                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Card( modifier = Modifier.fillMaxWidth() ) {

                Box(modifier = Modifier
                    .fillMaxWidth()
                ){

                    // ------- КАРТИНКА МЕРОПРИЯТИЯ ----------

                    if (meetingInfo.value.image1 !=null){
                        AsyncImage(
                            model = meetingInfo.value.image1, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТНОГО МЕРОПРИЯТИЯ ИЗ БД
                            contentDescription = stringResource(id = R.string.cd_meeting_image), // описание изображения для слабовидящих
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp), // заполнить картинкой весь контейнер
                            contentScale = ContentScale.Crop // обрезать картинку, что не вмещается
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.korn_concert), // картинка карточки. Потом сюда надо подставлять картинку с базы данных
                            modifier = Modifier.height(260.dp), // заполнить картинкой весь контейнер
                            contentScale = ContentScale.Crop, // обрезать картинку, что не вмещается
                            contentDescription = stringResource(id = R.string.cd_meeting_image) // описание изображения для слабовидящих
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
                            Toast.makeText(act,act.getString(R.string.meeting_view_counter),Toast.LENGTH_SHORT).show()
                        }

                        Bubble(
                            buttonText = favCounter.value.toString(),
                            rightIcon = R.drawable.ic_fav,
                            typeButton = DARK,
                            rightIconColor = iconTextFavColor.value
                        ) {

                            // --- Если клиент авторизован, проверяем, добавлено ли уже в избранное это мероприятие -----
                            // Если не авторизован, условие else

                            if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                                act.meetingDatabaseManager.favIconMeeting(meetingKey.value) {

                                    // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                    if (it) {

                                        // Убираем из избранных
                                        act.meetingDatabaseManager.removeFavouriteMeeting(meetingKey.value) { result ->

                                            // Если пришел колбак, что успешно

                                            if (result) {

                                                //favCounterBackground.value = Grey_ForCardsFav

                                                act.meetingDatabaseManager.readFavCounter(meetingKey.value){ counter ->
                                                    favCounter.value = counter
                                                }

                                                iconTextFavColor.value = WhiteDvij

                                                // Выводим ТОСТ
                                                Toast.makeText(act,act.getString(R.string.delete_from_fav),Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    } else {

                                        // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                        act.meetingDatabaseManager.addFavouriteMeeting(meetingKey.value) { inFav ->

                                            // Если пришел колбак, что успешно

                                            if (inFav) {

                                                act.meetingDatabaseManager.readFavCounter(meetingKey.value){ counter ->
                                                    favCounter.value = counter
                                                }

                                                iconTextFavColor.value = YellowDvij

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
                        }
                    }

                    // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 235.dp, end = 0.dp, start = 0.dp, bottom = 0.dp)
                            ,
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

                            Column(modifier = Modifier.padding(vertical = 30.dp, horizontal = 20.dp)) {

                                Row {
                                    Text(
                                        text = "#Мероприятие",
                                        color = Grey_Text,
                                        style = Typography.labelMedium
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = "#${meetingInfo.value.category}",
                                        color = Grey_Text,
                                        style = Typography.labelMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // -------- ЗАГОЛОВОК МЕРОПРИЯТИЯ ----------

                                if (meetingInfo.value.headline != null && meetingInfo.value.headline != "null" && meetingInfo.value.headline != "") {

                                    Text(
                                        text = meetingInfo.value.headline!!,
                                        style = Typography.titleMedium,
                                        color = WhiteDvij
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row {

                                    if (
                                        meetingInfo.value.data != null
                                        && meetingInfo.value.data != "null"
                                        && meetingInfo.value.data != ""
                                        && meetingInfo.value.data != "Выбери дату"
                                    ){

                                        Bubble(buttonText = meetingInfo.value.data!!) {}
                                        Spacer(modifier = Modifier.width(10.dp))

                                    }

                                    if (
                                        meetingInfo.value.startTime != null
                                        && meetingInfo.value.finishTime != null
                                        && meetingInfo.value.startTime != "null"
                                        && meetingInfo.value.startTime != ""
                                    ){

                                        Bubble(
                                            buttonText = if (
                                                meetingInfo.value.finishTime == ""
                                                || meetingInfo.value.finishTime == "Выбери время"
                                            ){
                                                meetingInfo.value.startTime!!
                                            } else {
                                                "${meetingInfo.value.startTime} - ${meetingInfo.value.finishTime}"
                                            }
                                        ) {}

                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // ------- ГОРОД ------------

                                if (meetingInfo.value.city != null && meetingInfo.value.city != "null" && meetingInfo.value.city != "") {

                                    Text(
                                        text = meetingInfo.value.city!!,
                                        style = Typography.labelMedium,
                                        color = WhiteDvij
                                    )
                                }

                                // ----- КАРТОЧКА ЗАВЕДЕНИЯ ----------

                                if (
                                    meetingInfo.value.placeKey != "Empty"
                                    && meetingInfo.value.placeKey != ""
                                    && meetingInfo.value.placeKey != "null"
                                    && meetingInfo.value.placeKey != null
                                    && placeInfo.value.placeKey != "Empty"
                                    && placeInfo.value.placeKey != ""
                                    && placeInfo.value.placeKey != "null"
                                    && placeInfo.value.placeKey != null
                                ) {

                                    Text(
                                        text = "${placeInfo.value.placeName}, ${placeInfo.value.address}",
                                        style = Typography.bodySmall,
                                        color = WhiteDvij
                                    )

                                } else {

                                    Text(
                                        text = "${meetingInfo.value.headlinePlaceInput}, ${meetingInfo.value.addressPlaceInput}",
                                        style = Typography.bodySmall,
                                        color = WhiteDvij
                                    )

                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                if (
                                    meetingInfo.value.price != null
                                    && meetingInfo.value.price != "null"
                                ){

                                    Bubble(
                                        buttonText = if (meetingInfo.value.price == ""){
                                            stringResource(id = R.string.free_price)
                                        } else {
                                            "${meetingInfo.value.price} тенге"
                                        }
                                    ) {}

                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                if (meetingInfo.value.description !=null && meetingInfo.value.description != "null" && meetingInfo.value.description != "" ){

                                    Text(
                                        text = meetingInfo.value.description!!,
                                        style = Typography.bodySmall,
                                        color = WhiteDvij
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))


                                // ------ ЗАБРОНИРОВАТЬ БИЛЕТЫ -----

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
                                        text = "Купить билеты",
                                        style = Typography.bodyLarge,
                                        color = WhiteDvij
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row (modifier = Modifier.fillMaxSize()){

                                        // ----- КНОПКА ПОЗВОНИТЬ --------

                                        if (meetingInfo.value.phone != null && meetingInfo.value.phone != "7") {

                                            SocialButtonCustom(icon = R.drawable.ic_phone) {
                                                act.callAndWhatsapp.makeACall(meetingInfo.value.phone!!)
                                            }

                                            Spacer(modifier = Modifier
                                                .width(10.dp)
                                            )

                                        }

                                        // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                                        if (meetingInfo.value.whatsapp != null && meetingInfo.value.whatsapp != "7" && meetingInfo.value.whatsapp != "" && meetingInfo.value.whatsapp != "+7" && meetingInfo.value.whatsapp != "+77") {

                                            SocialButtonCustom(icon = R.drawable.whatsapp) {
                                                act.callAndWhatsapp.writeInWhatsapp(meetingInfo.value.whatsapp!!)
                                            }

                                            Spacer(modifier = Modifier
                                                .width(10.dp)
                                            )

                                        }

                                        // ---- КНОПКА ПЕРЕХОДА В ИНСТАГРАМ -----------

                                        if (meetingInfo.value.instagram != null && meetingInfo.value.instagram != "null" && meetingInfo.value.instagram != "") {

                                            SocialButtonCustom(icon = R.drawable.instagram) {
                                                act.callAndWhatsapp.goToInstagramOrTelegram(meetingInfo.value.instagram!!, INSTAGRAM_URL)
                                            }

                                            Spacer(modifier = Modifier
                                                .width(10.dp)
                                            )

                                        }

                                        // ---- КНОПКА НАПИСАТЬ В ТЕЛЕГРАМ -----------

                                        if (meetingInfo.value.telegram != null && meetingInfo.value.telegram != "null" && meetingInfo.value.telegram != "") {

                                            SocialButtonCustom(icon = R.drawable.telegram) {
                                                act.callAndWhatsapp.goToInstagramOrTelegram(meetingInfo.value.telegram!!, TELEGRAM_URL)
                                            }

                                            Spacer(modifier = Modifier
                                                .width(10.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(40.dp))



                                Spacer(modifier = Modifier.height(20.dp))

                                SpacerTextWithLine(headline = "Организитор")

                                Spacer(modifier = Modifier.height(10.dp))

                                // КАРТОЧКА СОЗДАТЕЛЯ

                                if (meetingInfo.value.ownerKey != null && meetingInfo.value.ownerKey != "null" && meetingInfo.value.ownerKey != ""){

                                    ownerCard.OwnerCardView(userKey = meetingInfo.value.ownerKey!!)

                                    Spacer(modifier = Modifier.height(20.dp))

                                }


                                // ----- КАРТОЧКА ЗАВЕДЕНИЯ ----------

                                if (meetingInfo.value.placeKey != "Empty" && meetingInfo.value.placeKey != "" && meetingInfo.value.placeKey != "null" && meetingInfo.value.placeKey != null
                                    && placeInfo.value.placeKey != "Empty" && placeInfo.value.placeKey != "" && placeInfo.value.placeKey != "null" && placeInfo.value.placeKey != null) {

                                    Text(
                                        text = "Место проведения",
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    placeCard.PlaceCardSmall(navController = navController, placeItem = placeInfo.value, placeKey = placeKey)

                                    Spacer(modifier = Modifier.height(20.dp))

                                } else {

                                    Text(
                                        text = "Место проведения",
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    meetingInfo.value.headlinePlaceInput?.let {
                                        Text(
                                            text = it,
                                            style = Typography.titleSmall,
                                            color = Grey10
                                        )
                                    }

                                    meetingInfo.value.addressPlaceInput?.let {
                                        Text(
                                            text = it,
                                            style = Typography.bodyMedium,
                                            color = Grey10
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                }


                                // КНОПКА РЕДАКТИРОВАТЬ

                                if (act.mAuth.uid != null && meetingInfo.value.ownerKey == act.mAuth.uid){

                                    Button(

                                        onClick = {

                                            meetingInfo.value.key?.let {
                                                act.meetingDatabaseManager.readOneMeetingFromDBReturnClass(it){meeting ->

                                                    if (meeting.placeKey != null && meeting.placeKey != "null" && meeting.placeKey != "") {

                                                        filledPlaceInfoFromAct.value = placeInfo.value

                                                    } else {

                                                        filledPlaceInfoFromAct.value = PlacesAdsClass(
                                                            placeName = meeting.headlinePlaceInput,
                                                            address = meeting.addressPlaceInput
                                                        )
                                                    }

                                                    filledMeetingInfoFromAct.value = meeting
                                                    navController.navigate(EDIT_MEETINGS_SCREEN)

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

                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_publish),
                                            contentDescription = "Кнопка редактировать",
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

                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_publish),
                                            contentDescription = "Кнопка удалить",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                }

                            }
                        }
                    }
                }

                }

            }



//----------------------------------------
        // ---------- КОНТЕНТ СТРАНИЦЫ --------------

        /*Column(
            modifier = Modifier
                .background(Grey_Background)
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

                // КНОПКА РЕДАКТИРОВАТЬ

                if (act.mAuth.uid != null && meetingInfo.value.ownerKey == act.mAuth.uid){

                    Button(

                        onClick = {

                            meetingInfo.value.key?.let {
                                act.meetingDatabaseManager.readOneMeetingFromDBReturnClass(it){meeting ->

                                    if (meeting.placeKey != null && meeting.placeKey != "null" && meeting.placeKey != "") {

                                        filledPlaceInfoFromAct.value = placeInfo.value

                                    } else {

                                        filledPlaceInfoFromAct.value = PlacesAdsClass(
                                            placeName = meeting.headlinePlaceInput,
                                            address = meeting.addressPlaceInput
                                        )
                                    }

                                    filledMeetingInfoFromAct.value = meeting
                                    navController.navigate(EDIT_MEETINGS_SCREEN)

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

                        Icon(
                            painter = painterResource(id = R.drawable.ic_publish),
                            contentDescription = "Кнопка редактировать",
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

                        Icon(
                            painter = painterResource(id = R.drawable.ic_publish),
                            contentDescription = "Кнопка удалить",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }





                // -------- ЗАГОЛОВОК МЕРОПРИЯТИЯ ----------

                if (meetingInfo.value.headline != null && meetingInfo.value.headline != "null" && meetingInfo.value.headline != "") {

                    Text(
                        text = meetingInfo.value.headline!!,
                        style = Typography.titleLarge,
                        color = Grey10
                    )
                }

                // ------- ГОРОД ------------

                if (meetingInfo.value.city != null && meetingInfo.value.city != "null" && meetingInfo.value.city != "") {

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


                    if (meetingInfo.value.category != null && meetingInfo.value.category != "null" && meetingInfo.value.category != "" && meetingInfo.value.category != "Выбери категорию") {

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
                                act.meetingDatabaseManager.favIconMeeting(meetingKey.value) {

                                    // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                    if (it) {

                                        // Убираем из избранных
                                        act.meetingDatabaseManager.removeFavouriteMeeting(meetingKey.value) { result ->

                                            // Если пришел колбак, что успешно

                                            if (result) {

                                                iconTextFavColor.value = Grey40 // При нажатии окрашиваем текст и иконку в белый
                                                buttonFavColor.value = Grey80 // При нажатии окрашиваем кнопку в темно-серый

                                                // Выводим ТОСТ
                                                Toast.makeText(act,act.getString(R.string.delete_from_fav),Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    } else {

                                        // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                        act.meetingDatabaseManager.addFavouriteMeeting(meetingKey.value) { inFav ->

                                            // Если пришел колбак, что успешно

                                            if (inFav) {

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

                if (meetingInfo.value.price != null && meetingInfo.value.price != "null"){
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

                SpacerTextWithLine(headline = "Забронировать посещение")

                Spacer(modifier = Modifier.height(10.dp))

                Row (modifier = Modifier.fillMaxSize()){

                    // ----- КНОПКА ПОЗВОНИТЬ --------

                    if (meetingInfo.value.phone != null && meetingInfo.value.phone != "7") {

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

                    if (meetingInfo.value.whatsapp != null && meetingInfo.value.whatsapp != "7" && meetingInfo.value.whatsapp != "" && meetingInfo.value.whatsapp != "+7" && meetingInfo.value.whatsapp != "+77") {

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

                    // ---- КНОПКА ПЕРЕХОДА В ИНСТАГРАМ -----------

                    if (meetingInfo.value.instagram != null && meetingInfo.value.instagram != "null" && meetingInfo.value.instagram != "") {

                        IconButton(
                            onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(meetingInfo.value.instagram!!, INSTAGRAM_URL) },
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

                    if (meetingInfo.value.telegram != null && meetingInfo.value.telegram != "null" && meetingInfo.value.telegram != "") {

                        IconButton(
                            onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(meetingInfo.value.telegram!!, TELEGRAM_URL) },
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

                SpacerTextWithLine(headline = "Организитор")

                Spacer(modifier = Modifier.height(10.dp))

                // КАРТОЧКА СОЗДАТЕЛЯ

                if (meetingInfo.value.ownerKey != null && meetingInfo.value.ownerKey != "null" && meetingInfo.value.ownerKey != ""){

                    ownerCard.OwnerCardView(userKey = meetingInfo.value.ownerKey!!)

                    Spacer(modifier = Modifier.height(20.dp))

                }


                // ----- КАРТОЧКА ЗАВЕДЕНИЯ ----------

                if (meetingInfo.value.placeKey != "Empty" && meetingInfo.value.placeKey != "" && meetingInfo.value.placeKey != "null" && meetingInfo.value.placeKey != null
                    && placeInfo.value.placeKey != "Empty" && placeInfo.value.placeKey != "" && placeInfo.value.placeKey != "null" && placeInfo.value.placeKey != null) {

                    Text(
                        text = "Место проведения",
                        style = Typography.titleMedium,
                        color = Grey10
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    placeCard.PlaceCardSmall(navController = navController, placeItem = placeInfo.value, placeKey = placeKey)

                    Spacer(modifier = Modifier.height(20.dp))

                } else {

                    Text(
                        text = "Место проведения",
                        style = Typography.titleMedium,
                        color = Grey10
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    meetingInfo.value.headlinePlaceInput?.let {
                        Text(
                            text = it,
                            style = Typography.titleSmall,
                            color = Grey10
                        )
                    }

                    meetingInfo.value.addressPlaceInput?.let {
                        Text(
                            text = it,
                            style = Typography.bodyMedium,
                            color = Grey10
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }



                // ---------- ОПИСАНИЕ -------------

                Text(
                    text = stringResource(id = R.string.about_meeting),
                    style = Typography.titleMedium,
                    color = Grey10
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (meetingInfo.value.description !=null && meetingInfo.value.description != "null" && meetingInfo.value.description != "" ){

                    Text(
                        text = meetingInfo.value.description!!,
                        style = Typography.bodyMedium,
                        color = Grey10
                    )
                }
            }
        }*/
    }
}
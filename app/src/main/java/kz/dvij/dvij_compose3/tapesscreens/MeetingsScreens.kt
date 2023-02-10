package kz.dvij.dvij_compose3.tapesscreens

import androidx.compose.foundation.Image
import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingsScreens (val act: MainActivity) {

    private val databaseManager = act.meetingDatabaseManager // инициализируем датабаз менеджер

    // создаем мероприятие по умолчанию
    private val default = MeetingsAdsClass (
        description = "Default"
    )

    // ------ ЭКРАН, ВНУТРИ КОТОРОГО ТАБЫ С ИЗБРАННЫМ, ЛЕНТОЙ И МОИМИ ---------

    @Composable
    fun MeetingsScreen (navController: NavController, meetingKey: MutableState<String>) {
        Column {

            // Отображаем меню табов, передаем все что нужно

            TabMenu(bottomPage = MEETINGS_ROOT, navController = navController, act, meetingKey)

        }
    }


    // ---- ЛЕНТА МЕРОПРИЯТИЙ ----------

    @Composable
    fun MeetingsTapeScreen (navController: NavController, meetingKey: MutableState<String>){

        // инициализируем список мероприятий
        val meetingsList = remember {
            mutableStateOf(listOf<MeetingsAdsClass>())
        }

        // обращаемся к базе данных и записываем в список мероприятий мероприятия
        databaseManager.readMeetingDataFromDb(meetingsList)

        // -------- САМ КОНТЕНТ СТРАНИЦЫ ----------

        Column (
            modifier = Modifier
                .background(Grey95)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ---- ЕСЛИ ЗАГРУЗИЛИСЬ МЕРОПРИЯТИЯ С БД --------

            if (meetingsList.value.isNotEmpty() && meetingsList.value != listOf(default)){

                // ---- ЛЕНИВАЯ КОЛОНКА --------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // для каждого элемента из списка указываем шаблон для отображения

                    items(meetingsList.value){ item ->

                        // сам шаблон карточки мероприятия
                        act.meetingsCard.MeetingCard(
                            navController = navController,
                            meetingItem = item,
                            meetingKey = meetingKey
                        )
                    }
                }
            } else if (meetingsList.value == listOf(default)){

                // ----- ЕСЛИ НЕТ МЕРОПРИЯТИЙ -------

                Text(
                    text = stringResource(id = R.string.empty_meeting),
                    style = Typography.bodyMedium,
                    color = Grey10
                )

            } else {

                // -------- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                Row(
                    modifier = Modifier.fillMaxWidth(),
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

    // --- СТРАНИЦА МОИХ МЕРОПРИЯТИЙ -----

    @Composable
    fun MeetingsMyScreen (navController: NavController, meetingKey: MutableState<String>){

        // инициализируем пустой список мероприятий

        val myMeetingsList = remember {
            mutableStateOf(listOf<MeetingsAdsClass>())
        }

        // считываем с БД мои мероприятия

        databaseManager.readMeetingMyDataFromDb(myMeetingsList)

        // Surface для того, чтобы внизу отображать кнопочку "ДОБАВИТЬ МЕРОПРИЯТИЕ"

        Surface(modifier = Modifier.fillMaxSize()) {

            // ------- САМ КОНТЕНТ СТРАНИЦЫ ----------

            Column (
                modifier = Modifier
                    .background(Grey95)
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ----- ЕСЛИ ЗАГРУЗИЛИСЬ МОИ МЕРОПРИЯТИЯ ---------

                if (myMeetingsList.value.isNotEmpty() && myMeetingsList.value != listOf(default)){

                    // ЗАПУСКАЕМ ЛЕНИВУЮ КОЛОНКУ

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey95),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){

                        // ШАБЛОН ДЛЯ КАЖДОГО ЭЛЕМЕНТА СПИСКА

                        items(myMeetingsList.value){ item ->
                            act.meetingsCard.MeetingCard(
                                navController = navController,
                                meetingItem = item,
                                meetingKey = meetingKey
                            )
                        }
                    }
                } else if (myMeetingsList.value == listOf(default) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                    // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                    Text(
                        text = stringResource(id = R.string.empty_meeting),
                        style = Typography.bodyMedium,
                        color = Grey10
                    )

                } else if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

                    // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН ИЛИ НЕ ПОДТВЕРДИЛ ИМЕЙЛ

                    Image(
                        painter = painterResource(
                            id = R.drawable.sign_in_illustration
                        ),
                        contentDescription = stringResource(id = R.string.cd_illustration), // описание для слабовидящих
                        modifier = Modifier.size(200.dp)
                    )


                    Spacer(modifier = Modifier.height(20.dp)) // разделитель

                    Text(
                        modifier = Modifier.padding(20.dp),
                        text = stringResource(id = R.string.need_reg_meeting),
                        style = Typography.bodyMedium,
                        color = Grey10,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ------------------- КНОПКА ВОЙТИ ---------------------------------

                    Button(

                        onClick = { navController.navigate(LOG_IN_ROOT) },

                        modifier = Modifier
                            .fillMaxWidth() // кнопка на всю ширину
                            .height(50.dp) // высота - 50
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(50), // скругление углов
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = PrimaryColor, // цвет кнопки
                            contentColor = Grey100 // цвет контента на кнопке
                        )
                    )
                    {

                        // СОДЕРЖИМОЕ КНОПКИ

                        Icon(
                            painter = painterResource(id = R.drawable.ic_login), // иконка
                            contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                            tint = Grey100 // цвет иконки
                        )

                        Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                        Text(
                            text = stringResource(id = R.string.to_login), // если свитч другой, то текст "Войти",
                            style = Typography.labelMedium // стиль текста
                        )
                    }

                } else {

                    // ------- ЕСЛИ ИДЕТ ЗАГРУЗКА ---------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodyMedium,
                            color = Grey10
                        )

                    }
                }
            }

            // -------- ПЛАВАЮЩАЯ КНОПКА СОЗДАНИЯ МЕРОПРИЯТИЯ --------------

            if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                FloatingButton { navController.navigate(CREATE_MEETINGS_SCREEN) }
            }
        }
    }

    // --------- ЭКРАН ИЗБРАННЫХ МЕРОПРИЯТИЙ -------------

    @Composable
    fun MeetingsFavScreen (navController: NavController, meetingKey: MutableState<String>){

        // Инициализируем список мероприятий

        val favMeetingsList = remember {
            mutableStateOf(listOf<MeetingsAdsClass>())
        }

        // Считываем с базы данных избранные мероприятия

        databaseManager.readMeetingFavDataFromDb(favMeetingsList)


        // --------- САМ КОНТЕНТ СТРАНИЦЫ ----------

        Column (
            modifier = Modifier
                .background(Grey95)
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --------- ЕСЛИ СПИСОК НЕ ПУСТОЙ ----------

            if (favMeetingsList.value.isNotEmpty() && favMeetingsList.value != listOf(default)){

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // Шаблон для каждого мероприятия

                    items(favMeetingsList.value){ item ->
                        act.meetingsCard.MeetingCard(
                            navController = navController,
                            meetingItem = item,
                            meetingKey = meetingKey
                        )
                    }
                }
            } else if (favMeetingsList.value == listOf(default) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                Text(
                    text = stringResource(id = R.string.empty_meeting),
                    style = Typography.bodyMedium,
                    color = Grey10
                )

            } else if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

                // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН ИЛИ НЕ ПОДТВЕРДИЛ ИМЕЙЛ

                Image(
                    painter = painterResource(
                        id = R.drawable.sign_in_illustration
                    ),
                    contentDescription = stringResource(id = R.string.cd_illustration), // описание для слабовидящих
                    modifier = Modifier.size(200.dp)
                )


                Spacer(modifier = Modifier.height(20.dp)) // разделитель

                Text(
                    modifier = Modifier.padding(20.dp),
                    text = stringResource(id = R.string.need_reg_meeting),
                    style = Typography.bodyMedium,
                    color = Grey10,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ------------------- КНОПКА ВОЙТИ ---------------------------------

                Button(

                    onClick = { navController.navigate(LOG_IN_ROOT) },

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp) // высота - 50
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor, // цвет кнопки
                        contentColor = Grey100 // цвет контента на кнопке
                    )
                )
                {

                    // СОДЕРЖИМОЕ КНОПКИ

                    Icon(
                        painter = painterResource(id = R.drawable.ic_login), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                    Text(
                        text = stringResource(id = R.string.to_login), // если свитч другой, то текст "Войти",
                        style = Typography.labelMedium // стиль текста
                    )
                }

            } else {

                // ---- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    CircularProgressIndicator(
                        color = PrimaryColor,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

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
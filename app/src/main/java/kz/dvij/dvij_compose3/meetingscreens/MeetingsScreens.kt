package kz.dvij.dvij_compose3.meetingscreens

import android.os.Build
import androidx.annotation.RequiresApi
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
import kz.dvij.dvij_compose3.elements.FilterDialog
import kz.dvij.dvij_compose3.filters.FilterFunctions
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.MeetingsCardClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*
import java.util.*

class MeetingsScreens (val act: MainActivity) {

    private val databaseManager = act.meetingDatabaseManager // инициализируем датабаз менеджер
    private val filterDialog = FilterDialog(act)
    private val filterFunctions = FilterFunctions(act)

    // создаем мероприятие по умолчанию
    private val default = MeetingsAdsClass (
        description = "Default"
    )

    private val defaultCardMeeting = MeetingsCardClass (
        description = "Default"
    )

    // ------ ЭКРАН, ВНУТРИ КОТОРОГО ТАБЫ С ИЗБРАННЫМ, ЛЕНТОЙ И МОИМИ ---------

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MeetingsScreen (
        navController: NavController,
        meetingKey: MutableState<String>,
        cityForFilter: MutableState<String>,
        meetingCategoryForFilter: MutableState<String>,
        meetingStartDateForFilter: MutableState<String>,
        meetingFinishDateForFilter: MutableState<String>,
        meetingSortingForFilter: MutableState<String>,
        filledMeeting: MutableState<MeetingsAdsClass>,
        filledPlace: MutableState<PlacesAdsClass>
    ) {
        Column {

            // Отображаем меню табов, передаем все что нужно

            TabMenu(
                bottomPage = MEETINGS_ROOT,
                navController = navController,
                act,
                meetingKey,
                cityForFilter = cityForFilter,
                meetingCategoryForFilter = meetingCategoryForFilter,
                meetingStartDateForFilter = meetingStartDateForFilter,
                meetingFinishDateForFilter = meetingFinishDateForFilter,
                meetingSortingForFilter = meetingSortingForFilter,
                filledMeeting = filledMeeting,
                filledPlace = filledPlace
            )

        }
    }


    // ---- ЛЕНТА МЕРОПРИЯТИЙ ----------

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MeetingsTapeScreen (
        navController: NavController,
        meetingKey: MutableState<String>,
        cityForFilter: MutableState<String>,
        meetingCategoryForFilter: MutableState<String>,
        meetingStartDateForFilter: MutableState<String>,
        meetingFinishDateForFilter: MutableState<String>,
        meetingSortingForFilter: MutableState<String>,
        filledMeeting: MutableState<MeetingsAdsClass>,
        filledPlace: MutableState<PlacesAdsClass>
    ){

        // ----- СПИСКИ -----

        // инициализируем список мероприятий
        val meetingsList = remember {
            mutableStateOf(listOf<MeetingsCardClass>())
        }


        val openFilterDialog = remember { mutableStateOf(false) } // диалог ЗАВЕДЕНИЙ

        val filter = filterFunctions.createMeetingFilter(cityForFilter.value, meetingCategoryForFilter.value, meetingStartDateForFilter.value)

        val removeQuery = filterFunctions.splitFilter(filter)

        val typeFilter = filterFunctions.getTypeOfMeetingFilter(removeQuery)


        databaseManager.readFilteredMeetingDataFromDb(
            meetingsList = meetingsList,
            cityForFilter = cityForFilter,
            meetingCategoryForFilter = meetingCategoryForFilter,
            meetingStartDateForFilter = meetingStartDateForFilter,
            meetingFinishDateForFilter = meetingFinishDateForFilter,
            meetingSortingForFilter = meetingSortingForFilter
        )

        if (openFilterDialog.value){

            filterDialog.FilterMeetingChooseDialog(
                cityForFilter = cityForFilter,
                meetingCategoryForFilter = meetingCategoryForFilter,
                meetingStartDateForFilter = meetingStartDateForFilter,
                meetingFinishDateForFilter = meetingFinishDateForFilter,
                meetingSortingForFilter = meetingSortingForFilter
            ) {
               openFilterDialog.value = false
            }

        }



        Surface(modifier = Modifier.fillMaxSize()) {

            // -------- САМ КОНТЕНТ СТРАНИЦЫ ----------

            Column (
                modifier = Modifier
                    .background(Grey_Background)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ---- ЕСЛИ ЗАГРУЗИЛИСЬ МЕРОПРИЯТИЯ С БД --------

                if (meetingsList.value.isNotEmpty() && meetingsList.value != listOf(defaultCardMeeting)){

                    // ---- ЛЕНИВАЯ КОЛОНКА --------

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey_Background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){

                        // для каждого элемента из списка указываем шаблон для отображения

                        items(meetingsList.value){ item ->

                            if (meetingsList.value.isNotEmpty() && meetingsList.value != listOf(defaultCardMeeting)){
                                // сам шаблон карточки мероприятия
                                act.meetingsCard.MeetingCard(
                                    navController = navController,
                                    meetingItem = item,
                                    meetingKey = meetingKey,
                                    filledMeeting = filledMeeting,
                                    filledPlace = filledPlace
                                )

                            }

                        }
                    }
                } else if (meetingsList.value == listOf(defaultCardMeeting)){

                    // ----- ЕСЛИ НЕТ МЕРОПРИЯТИЙ -------

                    Text(
                        text = stringResource(id = R.string.empty_meeting),
                        style = Typography.bodySmall,
                        color = WhiteDvij
                    )

                } else {

                    // -------- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        CircularProgressIndicator(
                            color = YellowDvij,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodySmall,
                            color = WhiteDvij
                        )

                    }
                }
            }

            // -------- ПЛАВАЮЩАЯ КНОПКА ФИЛЬТРА --------------

            FloatingMeetingFilterButton(
                city = cityForFilter.value,
                category = meetingCategoryForFilter.value,
                date = meetingStartDateForFilter.value,
                typeOfFilter = typeFilter
            ) {
                openFilterDialog.value = true
            }


        }
    }

    // --- СТРАНИЦА МОИХ МЕРОПРИЯТИЙ -----

    @Composable
    fun MeetingsMyScreen (
        navController: NavController,
        meetingKey: MutableState<String>,
        filledMeeting: MutableState<MeetingsAdsClass>,
        filledPlace: MutableState<PlacesAdsClass>
    ){

        // инициализируем пустой список мероприятий

        val myMeetingsList = remember {
            mutableStateOf(listOf<MeetingsCardClass>())
        }

        // считываем с БД мои мероприятия

        databaseManager.readMeetingMyDataFromDb(myMeetingsList)

        // Surface для того, чтобы внизу отображать кнопочку "ДОБАВИТЬ МЕРОПРИЯТИЕ"

        Surface(modifier = Modifier.fillMaxSize()) {

            // ------- САМ КОНТЕНТ СТРАНИЦЫ ----------

            Column (
                modifier = Modifier
                    .background(Grey_Background)
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ----- ЕСЛИ ЗАГРУЗИЛИСЬ МОИ МЕРОПРИЯТИЯ ---------

                if (myMeetingsList.value.isNotEmpty() && myMeetingsList.value != listOf(defaultCardMeeting)){

                    // ЗАПУСКАЕМ ЛЕНИВУЮ КОЛОНКУ

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey_Background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){

                        // ШАБЛОН ДЛЯ КАЖДОГО ЭЛЕМЕНТА СПИСКА

                        items(myMeetingsList.value ){ item ->

                            if (myMeetingsList.value.isNotEmpty() && myMeetingsList.value != listOf(defaultCardMeeting)){

                                act.meetingsCard.MeetingCard(
                                    navController = navController,
                                    meetingItem = item,
                                    meetingKey = meetingKey,
                                    filledMeeting = filledMeeting,
                                    filledPlace = filledPlace
                                )

                            }

                        }
                    }
                } else if (myMeetingsList.value == listOf(defaultCardMeeting) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                    // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                    Text(
                        text = stringResource(id = R.string.empty_meeting),
                        style = Typography.bodySmall,
                        color = WhiteDvij
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
                            color = YellowDvij,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodySmall,
                            color = WhiteDvij
                        )

                    }
                }
            }

            // -------- ПЛАВАЮЩАЯ КНОПКА СОЗДАНИЯ МЕРОПРИЯТИЯ --------------

            if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                FloatingButton {
                    meetingKey.value = "0"

                    navController.navigate(CREATE_MEETINGS_SCREEN)
                }
            }
        }
    }

    // --------- ЭКРАН ИЗБРАННЫХ МЕРОПРИЯТИЙ -------------

    @Composable
    fun MeetingsFavScreen (
        navController: NavController,
        meetingKey: MutableState<String>,
        filledMeeting: MutableState<MeetingsAdsClass>,
        filledPlace: MutableState<PlacesAdsClass>
    ){

        // Инициализируем список мероприятий

        val favMeetingsList = remember {
            mutableStateOf(listOf<MeetingsCardClass>())
        }

        // Считываем с базы данных избранные мероприятия

        databaseManager.readMeetingFavDataFromDb(favMeetingsList)


        // --------- САМ КОНТЕНТ СТРАНИЦЫ ----------

        Column (
            modifier = Modifier
                .background(Grey_Background)
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --------- ЕСЛИ СПИСОК НЕ ПУСТОЙ ----------

            if (favMeetingsList.value.isNotEmpty() && favMeetingsList.value != listOf(defaultCardMeeting) && act.mAuth.currentUser != null){

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey_Background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    if (favMeetingsList.value.isNotEmpty() && favMeetingsList.value != listOf(defaultCardMeeting)){

                        // Шаблон для каждого мероприятия

                        items(favMeetingsList.value){ item ->
                            act.meetingsCard.MeetingCard(
                                navController = navController,
                                meetingItem = item,
                                meetingKey = meetingKey,
                                filledMeeting = filledMeeting,
                                filledPlace = filledPlace
                            )
                        }

                    }
                }
            } else if (favMeetingsList.value == listOf(defaultCardMeeting) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                Text(
                    text = stringResource(id = R.string.empty_meeting),
                    style = Typography.bodySmall,
                    color = WhiteDvij
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
                        color = YellowDvij,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = stringResource(id = R.string.ss_loading),
                        style = Typography.bodySmall,
                        color = WhiteDvij
                    )
                }
            }
        }
    }
}
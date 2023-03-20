package kz.dvij.dvij_compose3.placescreens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.elements.FilterDialog
import kz.dvij.dvij_compose3.elements.LoadingScreen
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesCardClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// функция превью экрана
class PlacesScreens (val act: MainActivity) {

    private val databaseManager = PlacesDatabaseManager(act) // инициализируем датабаз менеджер

    // создаем заведение по умолчанию
    private val default = PlacesAdsClass (
        placeDescription = "Default"
    )

    val defaultForCard = PlacesCardClass (
        placeDescription = "Default"
    )

    private val filterDialog = FilterDialog(act)

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PlacesScreen(
        navController: NavController,
        placeKey: MutableState<String>,
        cityForFilter: MutableState<String>,
        placeCategoryForFilter: MutableState<String>,
        placeIsOpenForFilter: MutableState<Boolean>,
        placeSortingForFilter: MutableState<String>,
        filledPlaceInfoFromAct: MutableState<PlacesCardClass>
    ) {

        Column {

            TabMenu(
                bottomPage = PLACES_ROOT,
                navController,
                activity = act,
                placesKey = placeKey,
                cityForFilter = cityForFilter,
                placeCategoryForFilter = placeCategoryForFilter,
                placeIsOpenForFilter = placeIsOpenForFilter,
                placeSortingForFilter = placeSortingForFilter,
                filledPlace = filledPlaceInfoFromAct
            )

        }
    }


    // ----- ЛЕНТА ЗАВЕДЕНИЙ -------

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PlacesTapeScreen(
        navController: NavController,
        placeKey: MutableState<String>,
        cityForFilter: MutableState<String>,
        placeCategoryForFilter: MutableState<String>,
        placeIsOpenForFilter: MutableState<Boolean>,
        placeSortingForFilter: MutableState<String>,
        filledPlaceInfoFromAct: MutableState<PlacesCardClass>
    ) {

        // инициализируем список заведений
        val placeList = remember {
            mutableStateOf(listOf<PlacesCardClass>())
        }

        val openFilterDialog = remember { mutableStateOf(false) } // диалог ЗАВЕДЕНИЙ

        if (openFilterDialog.value){

            filterDialog.FilterPlaceChooseDialog(
                cityForFilter = cityForFilter,
                placeCategoryForFilter = placeCategoryForFilter,
                placeSortingForFilter = placeSortingForFilter,
                placeIsOpenForFilter = placeIsOpenForFilter
            ) {
                openFilterDialog.value = false
            }

        }

        val openLoading = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА
        val closeFilter = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА




        val filter = act.filterFunctions.createPlaceFilter(cityForFilter.value, placeCategoryForFilter.value)

        val removeQuery = act.filterFunctions.splitFilter(filter)

        val typeFilter = act.filterFunctions.getTypeOfPlaceFilter(removeQuery)


        // обращаемся к базе данных и записываем в список заведений заведения
        //databaseManager.readPlaceDataFromDb(placeList)
        databaseManager.readPlaceSortedDataFromDb(
            placeList,
            cityForFilter = cityForFilter,
            placeCategoryForFilter = placeCategoryForFilter,
            placeIsOpenForFilter = placeIsOpenForFilter,
            placeSortingForFilter = placeSortingForFilter
        )

        // -------- САМ КОНТЕНТ СТРАНИЦЫ ----------

        Surface(modifier = Modifier.fillMaxSize()) {


            Column (
                modifier = Modifier
                    .background(Grey_Background)
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {



                // ---- ЕСЛИ ЗАГРУЗИЛИСЬ Заведения С БД --------

                if (placeList.value.isNotEmpty() && placeList.value != listOf(defaultForCard)){

                    closeFilter.value = false

                    // ---- ЛЕНИВАЯ КОЛОНКА --------

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey_Background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){

                        // для каждого элемента из списка указываем шаблон для отображения

                        items(placeList.value){ item ->

                            if (placeList.value.isNotEmpty() && placeList.value != listOf(defaultForCard)){
                                // сам шаблон карточки
                                act.placesCard.PlaceCardForNewClass(
                                    navController = navController,
                                    placeKeyFromAct = placeKey,
                                    placeItem = item,
                                    filledPlaceInfoFromAct = filledPlaceInfoFromAct,
                                    openLoadingState = openLoading
                                )

                            }

                        }
                    }
                } else if (placeList.value == listOf(defaultForCard)){

                    closeFilter.value = false

                    // ----- ЕСЛИ НЕТ ЗАВЕДЕНИЙ -------

                    Text(
                        text = stringResource(id = R.string.empty_meeting),
                        style = Typography.bodySmall,
                        color = WhiteDvij
                    )

                } else {

                    closeFilter.value = true


                    // -------- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                    LoadingScreen(messageText = stringResource(id = R.string.ss_loading))

                    /*Row(
                        modifier = Modifier.fillMaxWidth(),
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

                    }*/
                }
            }


            // -------- ПЛАВАЮЩАЯ КНОПКА ФИЛЬТРА --------------

            if (!openLoading.value && closeFilter.value == false){

                FloatingPlaceFilterButton(
                    city = cityForFilter.value,
                    category = placeCategoryForFilter.value,
                    typeOfFilter = typeFilter,
                    isOpen = placeIsOpenForFilter.value
                ) {
                    openFilterDialog.value = true
                }

            }



        }

        if (openLoading.value){

            closeFilter.value = true
            LoadingScreen(act.resources.getString(R.string.ss_loading))

        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PlacesFavScreen(
        navController: NavController,
        placeKey: MutableState<String>,
        filledPlaceInfoFromAct: MutableState<PlacesCardClass>,
        //placeIsOpenForFilter: MutableState<Boolean>,
    ) {

        val openLoading = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА



        // Инициализируем список заведений

        val favPlacesList = remember {
            mutableStateOf(listOf<PlacesCardClass>())
        }

        // Считываем с базы данных избранные заведения

        databaseManager.readPlacesFavDataFromDb(favPlacesList)


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

            if (openLoading.value){

                LoadingScreen(act.resources.getString(R.string.ss_loading))

            }

            if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

            // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН ИЛИ НЕ ПОДТВЕРДИЛ ИМЕЙЛ

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)) {

                Text(
                    text = "Чтобы добавить заведение в этот раздел, тебе нужно авторизоваться",
                    style = Typography.bodySmall,
                    color = WhiteDvij,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ------------------- КНОПКА ВОЙТИ ---------------------------------

                ButtonCustom(
                    buttonText = stringResource(id = R.string.to_login)
                ) {
                    navController.navigate(LOG_IN_ROOT)
                }

            }

        } else if (favPlacesList.value.isNotEmpty() && favPlacesList.value != listOf(defaultForCard)){

                // --------- ЕСЛИ СПИСОК НЕ ПУСТОЙ --------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey_Background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // Шаблон для каждого заведения

                    if (favPlacesList.value.isNotEmpty() && favPlacesList.value != listOf(defaultForCard)){

                        items(favPlacesList.value){ item ->
                            act.placesCard.PlaceCardForNewClass(
                                navController = navController,
                                placeItem = item,
                                placeKeyFromAct = placeKey,
                                filledPlaceInfoFromAct = filledPlaceInfoFromAct,
                                openLoadingState = openLoading
                            )
                        }
                    }
                }
            }  else if (favPlacesList.value == listOf(defaultForCard) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                Text(
                    text = stringResource(id = R.string.empty_meeting),
                    style = Typography.bodySmall,
                    color = WhiteDvij
                )

            }  else {

                // ---- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------


                LoadingScreen(messageText = stringResource(id = R.string.ss_loading))

                /*Row(
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
                }*/
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PlacesMyScreen(
        navController: NavController,
        placeKey: MutableState<String>,
        filledPlaceInfoFromAct: MutableState<PlacesCardClass>
    ) {

        val openLoading = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА
        val closeButtonCreate = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА




        // инициализируем пустой список заведений

        val myPlacesList = remember {
            mutableStateOf(listOf<PlacesCardClass>())
        }

        // считываем с БД мои заведения

        databaseManager.readPlaceMyDataFromDb(myPlacesList)

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



                // ----- ЕСЛИ ЗАГРУЗИЛИСЬ МОИ Заведения ---------

                if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

                    closeButtonCreate.value = true

                // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН ИЛИ НЕ ПОДТВЕРДИЛ ИМЕЙЛ

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)) {

                    Text(
                        text = "Чтобы создать свое заведение, тебе нужно авторизоваться",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ------------------- КНОПКА ВОЙТИ ---------------------------------

                    ButtonCustom(
                        buttonText = stringResource(id = R.string.to_login)
                    ) {
                        navController.navigate(LOG_IN_ROOT)
                    }

                }

            } else if (myPlacesList.value == listOf(defaultForCard) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){


                closeButtonCreate.value = false
                    // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                    Text(
                        text = stringResource(id = R.string.empty_meeting),
                        style = Typography.bodySmall,
                        color = WhiteDvij
                    )

                } else if (myPlacesList.value.isNotEmpty() && myPlacesList.value != listOf(default)){

                    closeButtonCreate.value = false

                    // ЗАПУСКАЕМ ЛЕНИВУЮ КОЛОНКУ

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey_Background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){

                        // ШАБЛОН ДЛЯ КАЖДОГО ЭЛЕМЕНТА СПИСКА

                        if (myPlacesList.value.isNotEmpty() && myPlacesList.value != listOf(defaultForCard)){



                            items(myPlacesList.value){ item ->
                                act.placesCard.PlaceCardForNewClass(
                                    navController = navController,
                                    placeItem = item,
                                    placeKeyFromAct = placeKey,
                                    filledPlaceInfoFromAct = filledPlaceInfoFromAct,
                                    openLoadingState = openLoading
                                )
                            }

                        }


                    }
                } else {

                    closeButtonCreate.value = true

                    LoadingScreen(messageText = stringResource(id = R.string.ss_loading))

                    // ------- ЕСЛИ ИДЕТ ЗАГРУЗКА ---------

                    /*Row(
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

                    }*/

                }
            }

            // -------- ПЛАВАЮЩАЯ КНОПКА СОЗДАНИЯ ЗАВЕДЕНИЯ --------------

            if (!openLoading.value && closeButtonCreate.value != true) {

                if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                    FloatingButton { navController.navigate(CREATE_PLACES_SCREEN) }
                }

            }

        }

        if (openLoading.value){

            LoadingScreen(act.resources.getString(R.string.ss_loading))

        }

    }
}
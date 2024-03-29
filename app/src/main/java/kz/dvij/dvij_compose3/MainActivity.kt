package kz.dvij.dvij_compose3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.accounthelper.SIGN_IN
import kz.dvij.dvij_compose3.adminpages.CityListScreen
import kz.dvij.dvij_compose3.callandwhatsapp.CallAndWhatsapp
import kz.dvij.dvij_compose3.meetingscreens.CreateMeeting
import kz.dvij.dvij_compose3.placescreens.CreatePlace
import kz.dvij.dvij_compose3.stockscreens.CreateStock
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.CategoryDialog
import kz.dvij.dvij_compose3.elements.MeetingsCard
import kz.dvij.dvij_compose3.elements.PlacesCard
import kz.dvij.dvij_compose3.elements.StockCard
import kz.dvij.dvij_compose3.filters.FilterFunctions
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.navigation.ChooseCityNavigation
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.photohelper.PhotoHelper
import kz.dvij.dvij_compose3.tapesscreens.*
import kz.dvij.dvij_compose3.meetingscreens.MeetingViewScreen
import kz.dvij.dvij_compose3.meetingscreens.MeetingsScreens
import kz.dvij.dvij_compose3.placescreens.PlaceViewScreen
import kz.dvij.dvij_compose3.placescreens.PlacesScreens
import kz.dvij.dvij_compose3.stockscreens.StockScreen
import kz.dvij.dvij_compose3.stockscreens.StockViewScreen
import kz.dvij.dvij_compose3.userscreens.AccountScreens
import kz.dvij.dvij_compose3.userscreens.CreateProfileInfoScreen
import kz.dvij.dvij_compose3.userscreens.ProfileScreen
import java.util.*

// https://www.youtube.com/watch?v=AlSjt_2GU5A - регистрация с имейлом и паролем
// https://ericampire.com/firebase-auth-with-jetpack-compose - тоже надо почитать, много полезного. Наверное даже предпочтительнее
// https://www.youtube.com/watch?v=ZECjMRINJkk
// https://www.youtube.com/watch?v=fEFuF1dnWNk
// https://firebase.blog/posts/2022/05/adding-firebase-auth-to-jetpack-compose-app


// ОТПРАВКА ИМЕЙЛА (ДЛЯ РЕАЛИЗАЦИИ ОБРАТНОЙ СВЯЗИ) https://www.geeksforgeeks.org/send-email-in-an-android-application-using-jetpack-compose/

class MainActivity : ComponentActivity() {

    val mAuth = FirebaseAuth.getInstance()  // берем из файрбаз аутентикейшн

    private val accountScreens = AccountScreens(act = this)
    private val accountHelper = AccountHelper(this)
    val chooseCityNavigation = ChooseCityNavigation (this)
    private val createMeeting = CreateMeeting (this)
    private val sideComponents = SideComponents (this)
    val meetingDatabaseManager = MeetingDatabaseManager( this)
    val meetingsScreens = MeetingsScreens(this)
    val stockScreen = StockScreen(this)
    val placesScreens = PlacesScreens(this)
    val photoHelper = PhotoHelper(this)
    private val meetingViewScreen = MeetingViewScreen(this)
    val callAndWhatsapp = CallAndWhatsapp(this)
    val categoryDialog = CategoryDialog(this)
    val meetingsCard = MeetingsCard(this)
    val placesCard = PlacesCard(this)
    private val createPlace = CreatePlace(this)
    val placesDatabaseManager = PlacesDatabaseManager(this)
    private val placeViewScreen = PlaceViewScreen(this)
    private val createStock = CreateStock (this)
    val stockCard = StockCard (this)
    val stockDatabaseManager = StockDatabaseManager(this)
    private val stockViewScreen = StockViewScreen(this)
    private val createProfileInfoScreen = CreateProfileInfoScreen(this)
    val userDatabaseManager = UserDatabaseManager(this)
    val filterFunctions = FilterFunctions(this)
    private val cityListScreen = CityListScreen(this)

    var googleSignInResultLauncher: ActivityResultLauncher<Intent>? = null






    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RememberReturnType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // слушатель регистрации через гугл синг ин

        googleSignInResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {

                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {

                        accountHelper.signInFirebaseWithGoogle(account.idToken!!)

                    }

                } catch (e: ApiException) {
                    Log.d("MyLog", "ApiError: ${e.message}")
                }
            }
        }

        setContent {

            // Список, в который поместим города из БД

            val citiesList = remember {
                mutableStateOf(listOf<CitiesList>())
            }

            // Данные о пользователе с БД

            val userInfo = remember {
                mutableStateOf(UserInfoClass(
                    name = "",
                    surname = "",
                    email = "",
                    city = "Выбери город",
                    avatar = "",
                    phoneNumber = "",
                    whatsapp = "",
                    instagram = "",
                    telegram = "",
                    userKey = ""
                ))
            }

            // Данные о мероприятии. Используется для переходов на разные экраны

            val meetingInfo = remember {
                mutableStateOf(MeetingsAdsClass(
                    key = "",
                    category = "Выбери категорию",
                    headline = "",
                    description = "",
                    price = "",
                    phone = "",
                    whatsapp = "",
                    data = "",
                    startTime = "",
                    finishTime = "",
                    image1 = "",
                    city = "Выбери город",
                    instagram = "",
                    telegram = "",
                    placeKey = "",
                    headlinePlaceInput = "",
                    addressPlaceInput = "",
                    ownerKey = "",
                    createdTime = "",
                    dateInNumber = ""
                )
                )
            }

            // Данные о заведении. Используется для переходов на разные экраны

            val placeInfo = remember {
                mutableStateOf(PlacesCardClass(

                    logo = "",
                    placeKey = "",
                    placeName = "Выбери заведение",
                    placeDescription = "",
                    phone = "",
                    whatsapp = "",
                    telegram = "",
                    instagram = "",
                    category = "",
                    city = "",
                    address = "",
                    owner = "",
                    mondayOpenTime = "",
                    mondayCloseTime = "",
                    tuesdayOpenTime = "",
                    tuesdayCloseTime = "",
                    wednesdayOpenTime = "",
                    wednesdayCloseTime = "",
                    thursdayOpenTime = "",
                    thursdayCloseTime = "",
                    fridayOpenTime = "",
                    fridayCloseTime = "",
                    saturdayOpenTime = "",
                    saturdayCloseTime = "",
                    sundayOpenTime = "",
                    sundayCloseTime = ""

                )
                )
            }

            // Данные о акции. Используется для переходов на разные экраны

            val stockInfo = remember {
                mutableStateOf(StockAdsClass(

                    image = "",
                    headline = "",
                    description = "",
                    category = "",
                    keyStock = "",
                    keyPlace = "",
                    keyCreator = "",
                    city = "",
                    startDate = "",
                    finishDate = "",
                    inputHeadlinePlace = "",
                    inputAddressPlace = ""

                )
                )
            }


            // Читаем список городов
            chooseCityNavigation.readCityDataFromDb(citiesList)

            // Переменные ключей для МЗА. Используются для переходов на разные страницы

            val meetingKey = remember { mutableStateOf("") }
            val placeKey = remember { mutableStateOf("") }
            val stockKey = remember { mutableStateOf("") }
            val startPage = remember { mutableStateOf(MEETINGS_ROOT) }

            // Переменные для запоминания фильтра в МЕРОПРИЯТИЯХ

            val meetingCategoryForFilter = remember { mutableStateOf("Выбери категорию") }
            val meetingStartDateForFilter = remember { mutableStateOf("Выбери дату") }
            val meetingFinishDateForFilter = remember { mutableStateOf("Выбери дату") }
            val meetingSortingForFilter = remember { mutableStateOf("По умолчанию") }

            // Переменные для запоминания фильтра в АКЦИЯХ

            val stockCategoryForFilter = remember { mutableStateOf("Выбери категорию") }
            val stockStartDateForFilter = remember { mutableStateOf("Выбери дату") }
            val stockFinishDateForFilter = remember { mutableStateOf("Выбери дату") }
            val stockSortingForFilter = remember { mutableStateOf("По умолчанию") }

            // Переменные для запоминания фильтра в Заведениях

            val placeCategoryForFilter = remember { mutableStateOf("Выбери категорию") }
            val placeIsOpenForFilter = remember { mutableStateOf(false) }
            val placeSortingForFilter = remember { mutableStateOf("По умолчанию") }


            if (meetingFinishDateForFilter.value == "Выбери дату" && meetingStartDateForFilter.value != "Выбери дату" ){

                meetingFinishDateForFilter.value = meetingStartDateForFilter.value

            } else if (meetingStartDateForFilter.value == "Выбери дату"){

                meetingFinishDateForFilter.value = meetingStartDateForFilter.value

            } else if (meetingFinishDateForFilter.value != "Выбери дату" && meetingStartDateForFilter.value != "Выбери дату"){

                val meetingStartDateForFilterNumber = filterFunctions.splitData(meetingStartDateForFilter.value)
                val meetingFinishDateForFilterNumber = filterFunctions.splitData(meetingFinishDateForFilter.value)

                val startNumberDate = filterFunctions.getDataNumber(meetingStartDateForFilterNumber)
                val finishNumberDate = filterFunctions.getDataNumber(meetingFinishDateForFilterNumber)

                if (startNumberDate > finishNumberDate){
                    meetingFinishDateForFilter.value = meetingStartDateForFilter.value
                }

            }

            if (stockFinishDateForFilter.value == "Выбери дату" && stockStartDateForFilter.value != "Выбери дату" ){

                stockFinishDateForFilter.value = stockStartDateForFilter.value

            } else if (stockStartDateForFilter.value == "Выбери дату"){

                stockFinishDateForFilter.value = stockStartDateForFilter.value

            } else if (stockFinishDateForFilter.value != "Выбери дату" && stockStartDateForFilter.value != "Выбери дату"){

                val stockStartDateForFilterNumber = filterFunctions.splitData(stockStartDateForFilter.value)
                val stockFinishDateForFilterNumber = filterFunctions.splitData(stockFinishDateForFilter.value)

                val startNumberDate = filterFunctions.getDataNumber(stockStartDateForFilterNumber)
                val finishNumberDate = filterFunctions.getDataNumber(stockFinishDateForFilterNumber)

                if (startNumberDate > finishNumberDate){
                    stockFinishDateForFilter.value = stockStartDateForFilter.value
                }

            }


            // Город по умолчанию
            val cityName = remember { mutableStateOf("Выбери город") }

            val navController = rememberNavController() // обязательная строчка для того, чтобы нижнее меню и боковое меню работало. Инициализируем navController
            // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.

            // --- Если пользователь вошел и подтвердил почту, в общем авторизован

            if (mAuth.currentUser != null && mAuth.currentUser!!.isEmailVerified && userInfo.value.userKey != mAuth.uid) {

                // Считываем данные пользователя с БД

                mAuth.uid?.let { uid ->
                    userDatabaseManager.readOneUserFromDataBase(userInfo, uid) { result ->

                        if (result) {
                            // Если успешно, то в переменную userInfo функция автоматически добавит информацию о пользователе

                            // так же сменим город по умолчанию на город, который указан в данных пользователя
                            cityName.value = userInfo.value.city.toString()

                        }
                    }
                }
            }



            val coroutineScope = rememberCoroutineScope() // инициализируем Корутину
            val scaffoldState = rememberScaffoldState() // Инициализируем состояние Scaffold

            val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
            val currentRoute = navBackStackEntry?.destination?.route // Получаем доступ к корню страницы



            // Помещаем все меню в Scaffold

            Scaffold(

                scaffoldState = scaffoldState, // Передаем инициализированный ScaffoldState

                bottomBar = { // ------- НА ОПРЕДЕЛЕННЫХ СТРАНИЦАХ НЕ ПОКАЗЫВАТЬ НИЖНЕЕ МЕНЮ ----------

                    if ( // если текущий путь - хоть одна из эти страниц
                        currentRoute == REG_ROOT
                        || currentRoute == LOG_IN_ROOT
                        || currentRoute == FORGOT_PASSWORD_ROOT
                        || currentRoute == THANK_YOU_PAGE_ROOT
                        || currentRoute == RESET_PASSWORD_SUCCESS
                    ) {

                        // НЕ ПОКАЗЫВАТЬ НИКАКОЕ НИЖНЕЕ МЕНЮ

                    } else { // ----- ЕСЛИ ТЕКУЩИЙ ПУТЬ ДРУГОЙ---------

                        BottomNavigationMenu(navController = navController) // в секции нижнего меню вызываем наше созданное нижнее меню и передаем туда NavController

                    }
                },

                topBar = { // ------- НА ОПРЕДЕЛЕННЫХ СТРАНИЦАХ НЕ ПОКАЗЫВАТЬ ТОПБАР ----------

                    when (currentRoute) {

                        // если текущий путь - хоть одна из эти страниц
                        REG_ROOT, LOG_IN_ROOT, FORGOT_PASSWORD_ROOT, THANK_YOU_PAGE_ROOT, RESET_PASSWORD_SUCCESS -> {

                            // НЕ ПОКАЗЫВАТЬ ТОПБАР

                        }

                        MEETING_VIEW -> {

                            // ----- ЕСЛИ ПУТЬ - СТРАНИЦА ПРОСМОТРА МЕРОПРИЯТИЯ ------------

                            TopBarWithBackButton(navController = navController, text = R.string.meetings)

                        }

                        PLACE_VIEW -> {

                            // ----- ЕСЛИ ПУТЬ - СТРАНИЦА ПРОСМОТРА ЗАВЕДЕНИЯ ------------

                            TopBarWithBackButton(navController = navController, text = R.string.places)

                        }

                        STOCK_VIEW -> {

                            // ----- ЕСЛИ ПУТЬ - СТРАНИЦА ПРОСМОТРА АКЦИИ ------------

                            TopBarWithBackButton(navController = navController, text = R.string.stock)

                        }

                        else -> { // ----- ЕСЛИ ТЕКУЩИЙ ПУТЬ ДРУГОЙ---------

                            // в секцию верхнего меню вызываем наше созданное верхнее меню

                            TopBarInApp(
                                topBarRoute = currentRoute, // Заголовок меняется в зависимости от текущего пути. Функция определения названия внутри
                                onNavigationIconClick = {
                                    // в действие на клик передаем корутину для запуска открытия бокового меню
                                    coroutineScope.launch { scaffoldState.drawerState.open() }
                                }
                            )
                        }
                    }
                },

                drawerContent = {

                    // ---------- СОДЕРЖИМОЕ БОКОВОГО МЕНЮ ---------

                    sideComponents.AllSideComponents(
                        navController = navController,
                        scaffoldState = scaffoldState,
                        userInfo = userInfo,
                        cityName = cityName,
                        citiesList = citiesList
                    )
                }
                )

            // начиная с какой то версии materials требуется указывать паддинги.
            // Это реализуется путем передачи через лямду paddingValues (т.е вынести в фигурные скобки как будет ниже), а затем
            // в paddingValues мы передаем Column.


            { paddingValues ->

                Column(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                        )
                {

                    // Навигационный хост. Здесь мы казываем,
                    // какой элемент нажат (пример MEETINGS_ROOT), и куда нужно перейти {например MeetingScreen()},

                    NavHost(
                        navController = navController, // указываем navController
                        startDestination = startPage.value //MEETINGS_ROOT // При первом открытии приложения какой элемент будет выбран по умолчанию сразу
                    ) {

                        // прописываем путь элемента, нажав на который куда нужно перейти

                        // --- СТРАНИЦЫ МЕРОПРИЯТИЙ -----

                        composable(MEETINGS_ROOT) {meetingsScreens.MeetingsScreen(navController = navController, meetingKey = meetingKey, cityForFilter = cityName, meetingCategoryForFilter = meetingCategoryForFilter, meetingStartDateForFilter = meetingStartDateForFilter, meetingFinishDateForFilter = meetingFinishDateForFilter, meetingSortingForFilter = meetingSortingForFilter, filledMeeting = meetingInfo, filledPlace = placeInfo)}
                        composable(EDIT_MEETINGS_SCREEN) { createMeeting.CreateMeetingScreen(navController = navController, citiesList, filledUserInfo = userInfo.value, filledMeeting = meetingInfo.value, createOrEdit = "1", filledPlace = placeInfo.value)}
                        composable(CREATE_MEETINGS_SCREEN) { createMeeting.CreateMeetingScreen(navController = navController, citiesList, filledUserInfo = userInfo.value, createOrEdit = "0")}
                        composable(MEETING_VIEW) {meetingViewScreen.MeetingViewScreen(meetingKey, navController, placeKey, meetingInfo, placeInfo)}

                        // ---- СТРАНИЦЫ ЗАВЕДЕНИЙ ----

                        composable(PLACES_ROOT) { placesScreens.PlacesScreen(navController, placeKey = placeKey, cityForFilter = cityName, placeSortingForFilter = placeSortingForFilter, placeCategoryForFilter = placeCategoryForFilter, placeIsOpenForFilter = placeIsOpenForFilter, filledPlaceInfoFromAct = placeInfo)}
                        composable(CREATE_PLACES_SCREEN) { createPlace.CreatePlaceScreen(navController = navController, citiesList = citiesList, filledUserInfo = userInfo.value ,createOrEdit = "0")}
                        composable(EDIT_PLACES_SCREEN) { createPlace.CreatePlaceScreen(navController = navController, citiesList = citiesList, filledUserInfo = userInfo.value, filledPlace = placeInfo.value, createOrEdit = EDIT_PLACES_SCREEN)}
                        composable(PLACE_VIEW) {placeViewScreen.PlaceViewScreen(key = placeKey.value, navController = navController, meetingKey, stockKey, placeInfo, meetingInfo, stockInfo)}

                        // ----- СТРАНИЦЫ АКЦИЙ ------

                        composable(STOCK_ROOT) { stockScreen.StockScreen(navController, this@MainActivity, stockKey = stockKey, cityForFilter = cityName, stockCategoryForFilter = stockCategoryForFilter, stockStartDateForFilter = stockStartDateForFilter, stockFinishDateForFilter = stockFinishDateForFilter, stockSortingForFilter = stockSortingForFilter, filledStockInfoFromAct = stockInfo, filledPlace = placeInfo)}
                        composable(CREATE_STOCK_SCREEN) {createStock.CreateStockScreen(navController = navController,citiesList = citiesList, filledUserInfo = userInfo.value, createOrEdit = "0")}
                        composable(EDIT_STOCK_SCREEN) {createStock.CreateStockScreen(navController = navController,citiesList = citiesList, filledUserInfo = userInfo.value, filledStock = stockInfo.value, filledPlace = placeInfo.value, createOrEdit = "1")}
                        composable(STOCK_VIEW) {stockViewScreen.StockViewScreen(key = stockKey.value, navController = navController, placeKey = placeKey, stockInfo, placeInfo)}

                        // ----- СТРАНИЦЫ ПРОФИЛЯ ПОЛЬЗОВАТЕЛЯ -----

                        composable(PROFILE_ROOT) { ProfileScreen(mAuth.currentUser, navController, this@MainActivity, userInfo) }
                        composable(CREATE_USER_INFO_SCREEN) {createProfileInfoScreen.CreateUserInfoScreen(navController = navController, citiesList = citiesList, userInfo.value, CREATE_USER_INFO_SCREEN)}

                        // ---- СТРАНИЦЫ РЕГИСТРАЦИИ / АВТОРИЗАЦИИ ----

                        composable(REG_ROOT) {accountScreens.SignInUpPage(switch = REGISTRATION, navController = navController)}
                        composable(LOG_IN_ROOT) {accountScreens.SignInUpPage(switch = SIGN_IN, navController = navController)}
                        composable(THANK_YOU_PAGE_ROOT) {accountScreens.ThankYouPage(navController = navController)}
                        composable(FORGOT_PASSWORD_ROOT) {accountScreens.ForgotPasswordPage(navController = navController)}
                        composable(RESET_PASSWORD_SUCCESS) {accountScreens.ResetPasswordSuccess(navController = navController)}

                        // ---- ПРОЧИЕ СТРАНИЦЫ -------

                        composable(ABOUT_ROOT) { AboutScreen(navController = navController)}
                        composable(CALLBACK_ROOT) { CallbackScreen(act = this@MainActivity, filledUserInfo = userInfo.value, navController = navController) }
                        composable(CALLBACK_LIST_ROOT) { CallbackListScreen(
                            navController = navController,
                            act = this@MainActivity
                        ) }
                        composable(POLICY_ROOT) { PrivatePolicyScreen()}
                        composable(ADS_ROOT) { AdsScreen() }
                        composable(BUGS_ROOT) { BugsScreen(act = this@MainActivity, filledUserInfo = userInfo.value, navController = navController) }
                        composable(BUGS_LIST_ROOT) { BugsListScreen(navController = navController, act = this@MainActivity) }
                        composable(CITIES_LIST_ROOT) { cityListScreen.CityListScreen(act = this@MainActivity, navController = navController) }


                    }
                }
            }
        }
    }
}
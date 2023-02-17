package kz.dvij.dvij_compose3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import kz.dvij.dvij_compose3.callandwhatsapp.CallAndWhatsapp
import kz.dvij.dvij_compose3.meetingscreens.CreateMeeting
import kz.dvij.dvij_compose3.placescreens.CreatePlace
import kz.dvij.dvij_compose3.stockscreens.CreateStock
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.CategoryDialog
import kz.dvij.dvij_compose3.elements.MeetingsCard
import kz.dvij.dvij_compose3.elements.PlacesCard
import kz.dvij.dvij_compose3.elements.StockCard
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.navigation.ChooseCityNavigation
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.photohelper.PhotoHelper
import kz.dvij.dvij_compose3.tapesscreens.*
import kz.dvij.dvij_compose3.meetingscreens.MeetingViewScreen
import kz.dvij.dvij_compose3.meetingscreens.MeetingsScreens
import kz.dvij.dvij_compose3.placescreens.PlaceViewScreen
import kz.dvij.dvij_compose3.stockscreens.StockScreen
import kz.dvij.dvij_compose3.stockscreens.StockViewScreen
import kz.dvij.dvij_compose3.userscreens.AccountScreens
import kz.dvij.dvij_compose3.userscreens.CreateProfileInfoScreen

// https://www.youtube.com/watch?v=AlSjt_2GU5A - регистрация с имейлом и паролем
// https://ericampire.com/firebase-auth-with-jetpack-compose - тоже надо почитать, много полезного. Наверное даже предпочтительнее
// https://www.youtube.com/watch?v=ZECjMRINJkk
// https://www.youtube.com/watch?v=fEFuF1dnWNk
// https://firebase.blog/posts/2022/05/adding-firebase-auth-to-jetpack-compose-app


// ОТПРАВКА ИМЕЙЛА (ДЛЯ РЕАЛИЗАЦИИ ОБРАТНОЙ СВЯЗИ) https://www.geeksforgeeks.org/send-email-in-an-android-application-using-jetpack-compose/

class MainActivity : ComponentActivity() {

    val mAuth = FirebaseAuth.getInstance()  // берем из файрбаз аутентикейшн

    val accountScreens = AccountScreens(act = this)
    val accountHelper = AccountHelper(this)
    val chooseCityNavigation = ChooseCityNavigation (this)
    val createMeeting = CreateMeeting (this)
    val sideComponents = SideComponents (this)
    val meetingDatabaseManager = MeetingDatabaseManager( this)
    val meetingsScreens = MeetingsScreens(this)
    val stockScreen = StockScreen(this)
    val placesScreens = PlacesScreens(this)
    val photoHelper = PhotoHelper(this)
    val meetingViewScreen = MeetingViewScreen(this)
    val callAndWhatsapp = CallAndWhatsapp(this)
    val categoryDialog = CategoryDialog(this)
    val meetingsCard = MeetingsCard(this)
    val placesCard = PlacesCard(this)
    val createPlace = CreatePlace(this)
    val placesDatabaseManager = PlacesDatabaseManager(this)
    val placeViewScreen = PlaceViewScreen(this)
    val createStock = CreateStock (this)
    val stockCard = StockCard (this)
    val stockDatabaseManager = StockDatabaseManager(this)
    val stockViewScreen = StockViewScreen(this)
    val choosePlaceDialog = ChoosePlaceDialog(this)
    val createProfileInfoScreen = CreateProfileInfoScreen(this)
    val userDatabaseManager = UserDatabaseManager(this)

    var googleSignInResultLauncher: ActivityResultLauncher<Intent>? = null
    var callOnPhoneResultLauncher: ActivityResultLauncher<Intent>? = null






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

            val citiesList = remember {
                mutableStateOf(listOf<CitiesList>())
            }

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
                    ownerKey = "")
                )
            }

            val placeInfo = remember {
                mutableStateOf(PlacesAdsClass(

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
                    openTime = "",
                    closeTime = ""

                )
                )
            }

            chooseCityNavigation.readCityDataFromDb(citiesList)

            val meetingKey = remember { mutableStateOf("") }
            val placeKey = remember { mutableStateOf("") }
            val stockKey = remember { mutableStateOf("") }
            val startPage = remember { mutableStateOf(MEETINGS_ROOT) }

            val cityName = remember { mutableStateOf(userInfo.value.city!!) }

            val context = LocalContext.current // контекст для тостов

            val navController = rememberNavController() // обязательная строчка для того, чтобы нижнее меню и боковое меню работало. Инициализируем navController
            // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.

            // --- Если пользователь вошел и подтвердил почту, в общем авторизован

            if (mAuth.currentUser != null && mAuth.currentUser!!.isEmailVerified) {

                mAuth.uid?.let {
                    userDatabaseManager.readOneUserFromDataBase(userInfo, it) { result ->

                        if (!result) {

                            startPage.value = CREATE_USER_INFO_SCREEN


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

                            // ------- ТО НИЧЕ НЕ ДЕЛАТЬ))) -----------

                    } else { // ----- ЕСЛИ ТЕКУЩИЙ ПУТЬ ДРУГОЙ---------

                        BottomNavigationMenu(navController = navController) // в секции нижнего меню вызываем наше созданное нижнее меню и передаем туда NavController

                    }
                },

                topBar = { // ------- НА ОПРЕДЕЛЕННЫХ СТРАНИЦАХ НЕ ПОКАЗЫВАТЬ ТОПБАР ----------

                    if ( // если текущий путь - хоть одна из эти страниц
                        currentRoute == REG_ROOT
                        || currentRoute == LOG_IN_ROOT
                        || currentRoute == FORGOT_PASSWORD_ROOT
                        || currentRoute == THANK_YOU_PAGE_ROOT
                        || currentRoute == RESET_PASSWORD_SUCCESS

                    ){

                            // ------- ТО НИЧЕ НЕ ДЕЛАТЬ))) -----------

                    } else if (currentRoute == MEETING_VIEW){

                        // ----- ЕСЛИ ПУТЬ - СТРАНИЦА ПРОСМОТРА МЕРОПРИЯТИЯ ------------

                        TopBarWithBackButton(navController = navController, text = R.string.meetings)

                    } else if (currentRoute == PLACE_VIEW){

                        // ----- ЕСЛИ ПУТЬ - СТРАНИЦА ПРОСМОТРА ЗАВЕДЕНИЯ ------------

                        TopBarWithBackButton(navController = navController, text = R.string.places)

                    } else if (currentRoute == STOCK_VIEW){

                        // ----- ЕСЛИ ПУТЬ - СТРАНИЦА ПРОСМОТРА АКЦИИ ------------

                        TopBarWithBackButton(navController = navController, text = R.string.stock)

                    } else { // ----- ЕСЛИ ТЕКУЩИЙ ПУТЬ ДРУГОЙ---------

                        // в секцию верхнего меню вызываем наше созданное верхнее меню

                        TopBarInApp(
                            topBarName = stringResource(id =
                            //Заголовок меню постоянно меняется. Указываем, какие должны быть заголовки исходя из того,
                            // какая страница открыта

                            // Условие выбора заголовка
                            when (currentRoute) {
                                null -> R.string.meetings
                                MEETINGS_ROOT -> R.string.meetings
                                PLACES_ROOT -> R.string.places
                                STOCK_ROOT -> R.string.stock
                                PROFILE_ROOT -> R.string.profile
                                ABOUT_ROOT -> R.string.side_about
                                POLICY_ROOT -> R.string.side_private_policy
                                ADS_ROOT -> R.string.side_ad
                                BUGS_ROOT -> R.string.side_report_bug
                                CREATE_MEETINGS_SCREEN -> R.string.create_meeting
                                EDIT_MEETINGS_SCREEN -> R.string.edit_meeting
                                CREATE_PLACES_SCREEN -> R.string.create_place
                                CREATE_STOCK_SCREEN -> R.string.create_stock
                                MEETING_VIEW -> R.string.meetings
                                PLACE_VIEW -> R.string.places
                                STOCK_VIEW -> R.string.stock
                                else -> R.string.app_name
                                }
                            ),
                            onNavigationIconClick = {
                                // в действие на клик передаем корутину для запуска открытия бокового меню
                                coroutineScope.launch { scaffoldState.drawerState.open() }
                            }
                        )
                    }
                },

                drawerContent = { // ---------- СОДЕРЖИМОЕ БОКОВОГО МЕНЮ ---------

                        sideComponents.HeaderSideNavigation() // HEADER - Логотип

                        sideComponents.AvatarBoxSideNavigation(
                            //user = mAuth.currentUser,
                            navController = navController, scaffoldState = scaffoldState, userInfo = userInfo) // Аватарка

                        chooseCityNavigation.CityHeaderSideNavigation(cityName,citiesList) // Меню с выбором города находится теперь в отдельном классе

                        sideComponents.BodySideNavigation( // вызываем тело бокового меню, где расположены перечень страниц
                            navController = navController, // Передаем NavController
                            scaffoldState // Передаем состояние Scaffold, для реализации функции автоматического закрывания бокового меню при нажатии на элемент
                        )

                        sideComponents.SubscribeBoxSideNavigation() // строка "ПОДПИШИСЬ НА ДВИЖ"



                }
                )

            // начиная с какой то версии materials требуется указывать паддинги.
            // Это реализуется путем передачи через лямду paddingValues (т.е вынести в фигурные скобки как будет ниже), а затем
            // в paddingValues мы передаем Column.


            { paddingValues ->



                Column(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxWidth())
                {

                    // Навигационный хост. Здесь мы казываем,
                    // какой элемент нажат (пример MEETINGS_ROOT), и куда нужно перейти {например MeetingScreen()},

                    NavHost(
                        navController = navController, // указываем navController
                        startDestination = MEETINGS_ROOT //MEETINGS_ROOT // При первом открытии приложения какой элемент будет выбран по умолчанию сразу
                    ) {

                        // прописываем путь элемента, нажав на который куда нужно перейти

                        composable(MEETINGS_ROOT) {meetingsScreens.MeetingsScreen(navController = navController, meetingKey = meetingKey)}
                        composable(PLACES_ROOT) { placesScreens.PlacesScreen(navController, placeKey = placeKey)}
                        composable(STOCK_ROOT) { stockScreen.StockScreen(navController, this@MainActivity, stockKey = stockKey)}
                        composable(PROFILE_ROOT) { ProfileScreen(mAuth.currentUser, navController, this@MainActivity, userInfo)}
                        composable(ABOUT_ROOT) { AboutScreen()}
                        composable(POLICY_ROOT) { PrivatePolicyScreen()}
                        composable(ADS_ROOT) { AdsScreen() }
                        composable(BUGS_ROOT) { BugsScreen() }
                        composable(REG_ROOT) {accountScreens.SignInUpPage(switch = REGISTRATION, navController = navController)}
                        composable(LOG_IN_ROOT) {accountScreens.SignInUpPage(switch = SIGN_IN, navController = navController)}
                        composable(THANK_YOU_PAGE_ROOT) {accountScreens.ThankYouPage(navController = navController)}
                        composable(FORGOT_PASSWORD_ROOT) {accountScreens.ForgotPasswordPage(navController = navController)}
                        composable(RESET_PASSWORD_SUCCESS) {accountScreens.ResetPasswordSuccess(navController = navController)}
                        composable(EDIT_MEETINGS_SCREEN) { createMeeting.CreateMeetingScreen(navController = navController, citiesList, filledUserInfo = userInfo.value, filledMeeting = meetingInfo.value, createOrEdit = meetingKey.value, filledPlace = placeInfo.value)}
                        composable(CREATE_MEETINGS_SCREEN) { createMeeting.CreateMeetingScreen(navController = navController, citiesList, filledUserInfo = userInfo.value, createOrEdit = meetingKey.value)}
                        composable(MEETING_VIEW) {meetingViewScreen.MeetingViewScreen(meetingKey, navController, placeKey, meetingInfo, placeInfo)}
                        composable(CREATE_PLACES_SCREEN) { createPlace.CreatePlaceScreen(navController = navController, citiesList = citiesList)}
                        composable(PLACE_VIEW) {placeViewScreen.PlaceViewScreen(key = placeKey.value, navController = navController, meetingKey, stockKey)}
                        composable(CREATE_STOCK_SCREEN) {createStock.CreateStockScreen(navController = navController,citiesList = citiesList)}
                        composable(STOCK_VIEW) {stockViewScreen.StockViewScreen(key = stockKey.value, navController = navController, placeKey = placeKey)}
                        composable(CREATE_USER_INFO_SCREEN) {createProfileInfoScreen.CreateUserInfoScreen(navController = navController, citiesList = citiesList, userInfo.value)}

                    }
                }
            }
        }
    }
}
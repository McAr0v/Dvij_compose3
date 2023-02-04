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
import kz.dvij.dvij_compose3.createscreens.CreateMeeting
import kz.dvij.dvij_compose3.createscreens.CreatePlace
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.CategoryDialog
import kz.dvij.dvij_compose3.elements.MeetingsCard
import kz.dvij.dvij_compose3.elements.PlacesCard
import kz.dvij.dvij_compose3.firebase.MeetingDatabaseManager
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.navigation.ChooseCityNavigation
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.photohelper.PhotoHelper
import kz.dvij.dvij_compose3.tapesscreens.*
import kz.dvij.dvij_compose3.viewscreens.MeetingViewScreen
import kz.dvij.dvij_compose3.viewscreens.PlaceViewScreen

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

            chooseCityNavigation.readCityDataFromDb(citiesList)



            val meetingKey = remember { mutableStateOf("") }
            val placeKey = remember { mutableStateOf("") }

            val context = LocalContext.current // контекст для тостов

            val navController = rememberNavController() // обязательная строчка для того, чтобы нижнее меню и боковое меню работало. Инициализируем navController
            // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.


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

                        // ----- ЕСЛИ ПУТЬ - СТРАНИЦА МЕРОПРИЯТИЯ ------------

                        TopBarWithBackButton(navController = navController, text = R.string.meetings)

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
                                MEETING_VIEW -> R.string.meetings
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
                            navController = navController, scaffoldState = scaffoldState) // Аватарка

                        chooseCityNavigation.CityHeaderSideNavigation(citiesList) // Меню с выбором города находится теперь в отдельном классе

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
                        composable(STOCK_ROOT) { stockScreen.StockScreen(navController, this@MainActivity)}
                        composable(PROFILE_ROOT) { ProfileScreen(mAuth.currentUser, navController, this@MainActivity)}
                        composable(ABOUT_ROOT) { AboutScreen()}
                        composable(POLICY_ROOT) { PrivatePolicyScreen()}
                        composable(ADS_ROOT) { AdsScreen() }
                        composable(BUGS_ROOT) { BugsScreen() }
                        composable(REG_ROOT) {accountScreens.SignInUpPage(switch = REGISTRATION, navController = navController)}
                        composable(LOG_IN_ROOT) {accountScreens.SignInUpPage(switch = SIGN_IN, navController = navController)}
                        composable(THANK_YOU_PAGE_ROOT) {accountScreens.ThankYouPage(navController = navController)}
                        composable(FORGOT_PASSWORD_ROOT) {accountScreens.ForgotPasswordPage(navController = navController)}
                        composable(RESET_PASSWORD_SUCCESS) {accountScreens.ResetPasswordSuccess(navController = navController)}
                        composable(CREATE_MEETINGS_SCREEN) { createMeeting.CreateMeetingScreen(navController = navController, citiesList)}
                        composable(MEETING_VIEW) {meetingViewScreen.MeetingViewScreen(key = meetingKey.value, navController)}
                        composable(CREATE_PLACES_SCREEN) { createPlace.CreatePlaceScreen(
                            navController = navController,
                            citiesList = citiesList
                        )}
                        composable(PLACE_VIEW) {placeViewScreen.PlaceViewScreen(
                            key = placeKey.value,
                            navController = navController
                        )}

                    }
                }
            }
        }
    }
}
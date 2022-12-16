package kz.dvij.dvij_compose3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.GOOGLE_SIGN_IN_REQUEST_CODE
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.accounthelper.SIGN_IN
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.screens.*

// https://www.youtube.com/watch?v=AlSjt_2GU5A - регистрация с имейлом и паролем
// https://ericampire.com/firebase-auth-with-jetpack-compose - тоже надо почитать, много полезного. Наверное даже предпочтительнее
// https://www.youtube.com/watch?v=ZECjMRINJkk
// https://www.youtube.com/watch?v=fEFuF1dnWNk
// https://firebase.blog/posts/2022/05/adding-firebase-auth-to-jetpack-compose-app


// ОТПРАВКА ИМЕЙЛА (ДЛЯ РЕАЛИЗАЦИИ ОБРАТНОЙ СВЯЗИ) https://www.geeksforgeeks.org/send-email-in-an-android-application-using-jetpack-compose/

class MainActivity : ComponentActivity() {

    val mAuth = FirebaseAuth.getInstance() // берем из файрбаз аутентикейшн
    private val accountScreens = AccountScreens(act = this)
    private val accountHelper = AccountHelper(this)



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE){
            //Log.d("MyLog", "SignInDone")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    accountHelper.signInFirebaseWithGoogle(account.idToken!!)
                }

            } catch (e: ApiException) {
                Log.d("MyLog", "ApiError: ${e.message}")
            }

        }
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val context = LocalContext.current

            val navController = rememberNavController() // обязательная строчка для того, чтобы нижнее меню и боковое меню работало. Инициализируем navController
            // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.



            val coroutineScope = rememberCoroutineScope() // инициализируем Корутину
            val scaffoldState = rememberScaffoldState() // Инициализируем состояние Scaffold

            val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
            val currentRoute = navBackStackEntry?.destination?.route // Получаем доступ к корню страницы


            // Помещаем все меню в Scaffold

            Scaffold(

                scaffoldState = scaffoldState, // Передаем инициализированный ScaffoldState
                bottomBar = {

                    if (
                        currentRoute == REG_ROOT
                        || currentRoute == LOG_IN_ROOT
                        || currentRoute == FORGOT_PASSWORD_ROOT
                        || currentRoute == THANK_YOU_PAGE_ROOT
                        || currentRoute == RESET_PASSWORD_SUCCESS
                    ) {

                    } else {
                        BottomNavigationMenu(navController = navController) // в секции нижнего меню вызываем наше созданное нижнее меню и передаем туда NavController
                    }


                            },
                topBar = {

                    if (
                        currentRoute == REG_ROOT
                        || currentRoute == LOG_IN_ROOT
                        || currentRoute == FORGOT_PASSWORD_ROOT
                        || currentRoute == THANK_YOU_PAGE_ROOT
                        || currentRoute == RESET_PASSWORD_SUCCESS
                    ){

                    } else {
                        // в секцию верхнего меню вызываем наше созданное верхнее меню

                        TopBar(
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

                drawerContent = {

                        // собственно содержимое бокового меню
                        HeaderSideNavigation() // вызываем Header
                        AvatarBoxSideNavigation(user = mAuth.currentUser, navController = navController, scaffoldState = scaffoldState)
                        CityHeaderSideNavigation("Усть-Каменогорск")
                        BodySideNavigation( // вызываем тело бокового меню, где расположены перечень страниц
                            navController = navController, // Передаем NavController
                            scaffoldState // Передаем состояние Scaffold, для реализации функции автоматического закрывания бокового меню при нажатии на элемент
                        )
                        SubscribeBoxSideNavigation()


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
                        startDestination = MEETINGS_ROOT // При первом открытии приложения какой элемент будет выбран по умолчанию сразу
                    ) {

                        // прописываем путь элемента, нажав на который куда нужно перейти

                        composable(MEETINGS_ROOT) { MeetingsScreen()}
                        composable(PLACES_ROOT) { PlacesScreen()}
                        composable(STOCK_ROOT) { StockScreen()}
                        composable(PROFILE_ROOT) { ProfileScreen(mAuth.currentUser, navController, this@MainActivity)}
                        composable(ABOUT_ROOT) { AboutScreen()}
                        composable(POLICY_ROOT) { PrivatePolicyScreen()}
                        composable(ADS_ROOT) { AdsScreen() }
                        composable(BUGS_ROOT) { BugsScreen() }
                        composable(REG_ROOT) {accountScreens.RegistrScreen(navController, scaffoldState, REGISTRATION)}
                        composable(LOG_IN_ROOT) {accountScreens.RegistrScreen(navController, scaffoldState, SIGN_IN)}
                        composable(THANK_YOU_PAGE_ROOT) {accountScreens.ThankYouPage(navController = navController)}
                        composable(FORGOT_PASSWORD_ROOT) {accountScreens.ForgotPasswordPage(navController = navController)}
                        composable(RESET_PASSWORD_SUCCESS) {accountScreens.ResetPasswordSuccess(navController = navController)}

                    }
                }
            }
        }

    }
}
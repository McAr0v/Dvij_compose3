package kz.dvij.dvij_compose3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.screens.*

// https://www.youtube.com/watch?v=AlSjt_2GU5A - регистрация с имейлом и паролем
// https://ericampire.com/firebase-auth-with-jetpack-compose - тоже надо почитать, много полезного. Наверное даже предпочтительнее
// https://www.youtube.com/watch?v=ZECjMRINJkk
// https://www.youtube.com/watch?v=fEFuF1dnWNk
// https://firebase.blog/posts/2022/05/adding-firebase-auth-to-jetpack-compose-app

class MainActivity : ComponentActivity() {

    companion object {
        const val RC_SIGN_IN = 100
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // firebase auth instance

        mAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        setContent {

            val navController = rememberNavController() // обязательная строчка для того, чтобы нижнее меню и боковое меню работало. Инициализируем navController
            // он нужен для того, чтобы определять, куда вернуться, если нажать кнопку "Назад", какой элемент сейчас выбран и тд.

            val coroutineScope = rememberCoroutineScope() // инициализируем Корутину
            val scaffoldState = rememberScaffoldState() // Инициализируем состояние Scaffold

            val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
            val currentRoute = navBackStackEntry?.destination?.route // Получаем доступ к корню страницы

            // Помещаем все меню в Scaffold

            androidx.compose.material.Scaffold(

                scaffoldState = scaffoldState, // Передаем инициализированный ScaffoldState
                bottomBar = {
                    BottomNavigationMenu(navController = navController) // в секции нижнего меню вызываем наше созданное нижнее меню и передаем туда NavController
                            },
                topBar = {

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
                             }),
                             onNavigationIconClick = {
                                 // в действие на клик передаем корутину для запуска открытия бокового меню
                                 coroutineScope.launch { scaffoldState.drawerState.open() }
                             }
                         )
                         },

                drawerContent = {
                    // собственно содержимое бокового меню
                    HeaderSideNavigation() // вызываем Header

                    if (mAuth.currentUser == null) {
                        SideGoogleButton {
                            signIn()
                        }
                    } else {
                        AvatarBoxSideNavigation(true, navController, scaffoldState)
                    }


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
                        composable(PROFILE_ROOT) { ProfileScreen()}
                        composable(ABOUT_ROOT) { AboutScreen()}
                        composable(POLICY_ROOT) { PrivatePolicyScreen()}
                        composable(ADS_ROOT) { AdsScreen() }
                        composable(BUGS_ROOT) { BugsScreen() }

                    }
                }
            }
        }
    }

    private  fun signIn() {
        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // result returned from launching the intent from GoogleSignInApi.getSignInIntent()

        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    // Google Sign In was successful, authebticate with firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)

                } catch (e: Exception) {
                    // Google SignIn Failed
                    Log.d("MyLog", "Google Sign In is failed")
                }
            } else {
                Log.d("MyLog", exception.toString())
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    // SignIn Successful
                    Toast.makeText(this, "SignIn Done", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign In Failed
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                }

            }
    }

}
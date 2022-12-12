package kz.dvij.dvij_compose3.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

// функция превью экрана


@Composable
fun ProfileScreen (
    user: FirebaseUser?,
    navController: NavController,
    activity: MainActivity
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val accountHelper = AccountHelper(activity)

    if (user!=null) {

        Column(
            modifier = Modifier
                .background(Grey95)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // АВАТАРКА ПОЛЬЗОВАТЕЛЯ

            if (user.photoUrl != null) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape))
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.zhanna_avatar), // по идее сюда надо будет передавать из гугла, или иметь возможность загружать
                    contentDescription = stringResource(id = R.string.cd_avatar), // описание для слабовидящих
                    modifier = Modifier
                        .size(130.dp) // размер аватарки
                        .clip(CircleShape) // делаем ее круглой
                )
            }

            Spacer(modifier = Modifier.height(30.dp)) // разделитель между заголовком и полями для ввода

            if (user.displayName != null) {

                Text(  // заголовок зависит от switch
                    text = user.displayName!!,
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00, // цвет заголовка
                    textAlign = TextAlign.Center
                )

            }

            Spacer(modifier = Modifier.height(10.dp)) // разделитель между заголовком и полями для ввода

            if (user.email != null){
                Text(
                    text = user.email!! ,
                    style = Typography.labelMedium, // стиль текста
                    textAlign = TextAlign.Center,
                    color = Grey40
                )

                Spacer(modifier = Modifier.height(50.dp)) // разделитель между заголовком и полями для ввода

                Button(
                    onClick = {
                        activity.mAuth.signOut()
                        accountHelper.signOutGoogle()
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                        navController.navigate(MEETINGS_ROOT)
                        Toast.makeText(activity, "Вы успешно вышли из системы", Toast.LENGTH_SHORT).show()

                    },
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Grey00,
                        contentColor = Grey100
                    )) {

                    Text(text = "Выйти из аккаунта")

                }

            }






        }

    }
}


// экран профиля

@Composable
fun ProfileScreenContent (){

    Column (
        modifier = Modifier
            .background(Grey10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "ProfileScreen", style = Typography.titleLarge)
        Text(text = "ProfileScreen", style = Typography.titleMedium)
        Text(text = "ProfileScreen", style = Typography.titleSmall)
        Text(text = "ProfileScreen", style = Typography.bodyLarge)
        Text(text = "ProfileScreen", style = Typography.bodyMedium)
        Text(text = "ProfileScreen", style = Typography.bodySmall)
        Text(text = "ProfileScreen", style = Typography.labelLarge)
        Text(text = "ProfileScreen", style = Typography.labelMedium)
        Text(text = "ProfileScreen", style = Typography.labelSmall)

    }
}





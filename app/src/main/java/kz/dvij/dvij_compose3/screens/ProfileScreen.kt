package kz.dvij.dvij_compose3.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.navigation.LOG_IN_ROOT
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.REG_ROOT
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

    // ------ ЕСЛИ USER НЕ РАВЕН NULL И  ПОДТВЕРДИЛ EMAIL ------

    if (user!=null && user.isEmailVerified) {

        Column(
            modifier = Modifier
                .background(Grey95)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // -------------- АВАТАРКА ПОЛЬЗОВАТЕЛЯ ------------------

            if (user.photoUrl != null) {

                // ----- ЕСЛИ ЕСТЬ ФОТОГРАФИЯ ---------

                AsyncImage(
                    model = user.photoUrl, // фотография пользователя из Google аккаунта
                    contentScale = ContentScale.Crop, // увеличение изображения - либо по ширине либо по высоте выступающее за края части будут обрезаны
                    contentDescription = stringResource(id = R.string.icon_user_image), // описание для слабовидящих
                    modifier = Modifier
                        .size(150.dp) // размер аватарки
                        .border(
                            BorderStroke(4.dp, PrimaryColor), CircleShape
                        ) // рамка вокруг иконки
                        .clip(CircleShape)) // делаем аватарку круглой
            } else {

                // ------- ЕСЛИ НЕТ ФОТОГРАФИИ -------------

                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.no_user_image), // заглушка
                    contentScale = ContentScale.Crop, // увеличение изображения - либо по ширине либо по высоте выступающее за края части будут обрезаны
                    contentDescription = stringResource(id = R.string.cd_avatar), // описание для слабовидящих
                    modifier = Modifier
                        .size(150.dp) // размер аватарки
                        .border(
                            BorderStroke(4.dp, PrimaryColor),
                            CircleShape
                        ) // рамка вокруг иконки
                        .clip(CircleShape) // делаем ее круглой
                )
            }

            Spacer(modifier = Modifier.height(30.dp)) // разделитель


            // -------------- ИМЯ ПОЛЬЗОВАТЕЛЯ -----------------------


            if (user.displayName != null) {

                Text(
                    text = user.displayName!!, // имя из базы данных firebase
                    style = Typography.titleLarge, // стиль текста
                    color = Grey00, // цвет
                    textAlign = TextAlign.Center // выравнивание по центру
                )

            }

            Spacer(modifier = Modifier.height(10.dp)) // разделитель


            // --------------- EMAIL ПОЛЬЗОВАТЕЛЯ ----------------

            if (user.email != null) {

                Text(
                    text = user.email!!, // email из базы данных firebase
                    style = Typography.labelMedium, // стиль текста
                    textAlign = TextAlign.Center, // выравнивание по центру
                    color = Grey40 // цвет
                )
            }

                Spacer(modifier = Modifier.height(50.dp)) // разделитель



            // --------- КНОПКА ВЫХОДА ИЗ АККАУНТА ------------

                Button(
                    onClick = {
                        // функции на нажатие

                        activity.mAuth.signOut() // выход из аккаунта, если вошел по Email

                        accountHelper.signOutGoogle() // выход из аккауна, если вошел через Google

                        navController.navigate(MEETINGS_ROOT) // после выхода отправляем на страницу мероприятий


                        // показываем ТОСТ что все готово

                        Toast.makeText(
                            activity,
                            activity.getString(R.string.sign_out_success),
                            Toast.LENGTH_SHORT
                        ).show()

                    },
                    modifier = Modifier
                        .fillMaxWidth() // кнопка занимает всю ширину
                        .height(50.dp)// высота - 50
                        .padding(horizontal = 30.dp), // отступы от краев
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(

                        // цвета кнопки
                        backgroundColor = PrimaryColor,
                        contentColor = Grey100

                    )) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                    Text(
                        text = stringResource(id = R.string.sign_out_button), // текст кнопки
                        style = Typography.labelMedium // стиль текста
                    )


            }

        }

    }

    // -------- ЕСЛИ USER ЗАРЕГИСТРИРОВАН НО НЕ ПОДТВЕРДИЛ ПОЧТУ ----------

    else if (user!=null && !user.isEmailVerified){

        Column(
            modifier = Modifier
                .background(Grey95)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ----- ИЛЛЮСТРАЦИЯ ---------

            Image(
                //modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.verifi_email),
                contentDescription = stringResource(id = R.string.cd_illustration)
            )


            Spacer(modifier = Modifier.height(40.dp)) // разделитель


            // ------- ТЕКСТ НА СТРАНИЦЕ --------------

            Text(
                text = stringResource(id = R.string.email_verify_toast), // имя из базы данных firebase
                style = Typography.bodyLarge, // стиль текста
                color = Grey00, // цвет
                textAlign = TextAlign.Center // выравнивание по центру
            )

            Spacer(modifier = Modifier.height(40.dp)) // разделитель между текстом и иконкой

            // -------- КНОПКА ПЕРЕХОДА НА СТРАНИЦУ ВХОДА -------------

            Button(
                onClick = {
                    // функции на нажатие

                    navController.navigate(LOG_IN_ROOT)

                },
                modifier = Modifier
                    .fillMaxWidth() // кнопка занимает всю ширину
                    .height(50.dp)// высота - 50
                    .padding(horizontal = 30.dp), // отступы от краев
                shape = RoundedCornerShape(50), // скругление углов
                colors = ButtonDefaults.buttonColors(

                    // цвета кнопки
                    backgroundColor = PrimaryColor,
                    contentColor = Grey100

                )) {

                // Иконка в кнопке

                Icon(
                    painter = painterResource(id = R.drawable.ic_login), // иконка
                    contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                    tint = Grey100 // цвет иконки
                )

                Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                // Текст кнопки

                Text(
                    text = stringResource(id = R.string.to_login), // текст кнопки
                    style = Typography.labelMedium // стиль текста
                )
            }
        }
    }

    // ------- ЕСЛИ USER НЕ ЗАРЕГИСТРИРОВАН И НЕ ВОШЕЛ

    else if (user == null) {

        Column(
            modifier = Modifier
                .background(Grey95)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ----- ИЛЛЮСТРАЦИЯ ---------

            Image(
                //modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.need_create_account),
                contentDescription = stringResource(id = R.string.cd_illustration)
            )


            Spacer(modifier = Modifier.height(40.dp)) // разделитель

            // ------ ТЕКСТ НА СТРАНИЦЕ ------------

            Text(
                text = stringResource(id = R.string.need_registration_toast), // текст сообщения
                style = Typography.bodyLarge, // стиль текста
                color = Grey00, // цвет
                textAlign = TextAlign.Center // выравнивание по центру
            )

            Spacer(modifier = Modifier.height(40.dp)) // разделитель между текстом и иконкой

            // -------- КНОПКА НА РЕГИСТРАЦИЮ -----------

            Button(
                onClick = {
                    // функции на нажатие

                    navController.navigate(REG_ROOT)

                },
                modifier = Modifier
                    .fillMaxWidth() // кнопка занимает всю ширину
                    .height(50.dp)// высота - 50
                    .padding(horizontal = 30.dp), // отступы от краев
                shape = RoundedCornerShape(50), // скругление углов
                colors = ButtonDefaults.buttonColors(

                    // цвета кнопки
                    backgroundColor = PrimaryColor,
                    contentColor = Grey100

                )) {

                // Иконка кнопки

                Icon(
                    painter = painterResource(id = R.drawable.ic_login), // иконка
                    contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                    tint = Grey100 // цвет иконки
                )

                Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                // Текст кнопки

                Text(
                    text = stringResource(id = R.string.to_registration), // текст кнопки
                    style = Typography.labelMedium // стиль текста
                )
            }
        }
    }
}





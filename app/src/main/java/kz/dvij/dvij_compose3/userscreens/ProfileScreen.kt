package kz.dvij.dvij_compose3.tapesscreens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.elements.IconText
import kz.dvij.dvij_compose3.elements.SpacerTextWithLine
import kz.dvij.dvij_compose3.firebase.UserInfoClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

// функция превью экрана

@Composable
fun ProfileScreen (
    user: FirebaseUser?,
    navController: NavController,
    activity: MainActivity,
    userInfo: MutableState<UserInfoClass>
) {



    val accountHelper = AccountHelper(activity)

    // ------ ЕСЛИ USER НЕ РАВЕН NULL И  ПОДТВЕРДИЛ EMAIL ------

    if (user!=null && user.isEmailVerified) {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Grey100)
            .verticalScroll(rememberScrollState())
        ){

            Box(modifier = Modifier
                .fillMaxWidth()
                .background(Grey90)
                .fillMaxSize()) {

                if (user.photoUrl != null && userInfo.value.avatar == "") {

                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "Аватар пользователя",
                        imageLoader = ImageLoader(activity),
                        modifier = Modifier
                            .height(260.dp), // заполнить картинкой весь контейнер
                        contentScale = ContentScale.FillWidth, // обрезать картинку, что не вмещается
                    )

                } else if (userInfo.value.avatar != "") {

                    AsyncImage(
                        model = userInfo.value.avatar, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТНОГО ЗАВЕДЕНИЯ ИЗ БД
                        contentDescription = "Аватар пользователя", // описание изображения для слабовидящих
                        modifier = Modifier
                            .height(260.dp), // заполнить картинкой весь контейнер
                        contentScale = ContentScale.FillWidth, // обрезать картинку, что не вмещается
                        //alignment = Alignment.Center
                        placeholder = painterResource(id = R.drawable.no_user_image)
                    )

                } else {

                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        painter = painterResource(id = R.drawable.no_user_image),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )

                }

                // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .padding(top = 245.dp, end = 0.dp, start = 0.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Grey100,
                                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                            ),
                        shape = RoundedCornerShape(
                            topStart = 15.dp,
                            topEnd = 15.dp,
                            bottomEnd = 15.dp,
                            bottomStart = 15.dp
                        ),
                        elevation = CardDefaults.cardElevation(5.dp),
                        colors = CardDefaults.cardColors(Grey100)
                    ) {

                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                        ) {

                            SpacerTextWithLine(headline = "Твое имя")

                            if (userInfo.value.name != "" && userInfo.value.surname != ""){

                                androidx.compose.material3.Text(
                                    text = "${userInfo.value.name} ${userInfo.value.surname}",
                                    style = Typography.titleMedium,
                                    color = Grey10
                                )

                            } else if (userInfo.value.name == "" && userInfo.value.surname == ""){

                                user.displayName?.let { name ->
                                    androidx.compose.material3.Text(
                                        text = name,
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )
                                }

                            } else {

                                androidx.compose.material3.Text(
                                    text = "Новый пользователь",
                                    style = Typography.titleSmall,
                                    color = Grey10
                                )

                            }

                            SpacerTextWithLine(headline = "Email для входа")

                            if (user.email != null) {

                                androidx.compose.material3.Text(
                                    text = user.email!!,
                                    style = Typography.titleMedium,
                                    color = Grey10
                                )

                            } else {

                                androidx.compose.material3.Text(
                                    text = "Email не указан",
                                    style = Typography.titleSmall,
                                    color = Grey40
                                )

                            }

                            SpacerTextWithLine(headline = "Город")

                            if (userInfo.value.city != "Выбери город" && userInfo.value.city != null){

                                userInfo.value.city?.let { city ->
                                    androidx.compose.material3.Text(
                                        text = city,
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )
                                }

                            } else {

                                androidx.compose.material3.Text(
                                    text = "Город не выбран",
                                    style = Typography.titleSmall,
                                    color = Grey40
                                )

                            }

                            SpacerTextWithLine(headline = "Телефон для звонков")

                            if (userInfo.value.phoneNumber != "7"){

                                userInfo.value.phoneNumber?.let { phone ->
                                    androidx.compose.material3.Text(
                                        text = "+7${phone}",
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )
                                }

                            } else {

                                androidx.compose.material3.Text(
                                    text = "Телефон не указан",
                                    style = Typography.titleSmall,
                                    color = Grey40
                                )

                            }

                            SpacerTextWithLine(headline = "Whatsapp")

                            if (userInfo.value.whatsapp != "7") {

                                userInfo.value.whatsapp?.let {
                                    androidx.compose.material3.Text(
                                        text = "+7${it}",
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )
                                }

                            } else {

                                androidx.compose.material3.Text(
                                    text = "Номер телефона не указан",
                                    style = Typography.titleSmall,
                                    color = Grey40
                                )

                            }

                            SpacerTextWithLine(headline = "Instagram")

                            if (userInfo.value.instagram != "") {

                                userInfo.value.instagram?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )
                                }

                            } else {

                                androidx.compose.material3.Text(
                                    text = "Номер телефона не указан",
                                    style = Typography.titleSmall,
                                    color = Grey40
                                )

                            }

                            SpacerTextWithLine(headline = "Telegram")

                            if (userInfo.value.telegram != "") {

                                userInfo.value.telegram?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = Typography.titleMedium,
                                        color = Grey10
                                    )
                                }

                            } else {

                                androidx.compose.material3.Text(
                                    text = "Telegram не указан",
                                    style = Typography.titleSmall,
                                    color = Grey40
                                )

                            }



                            Spacer(modifier = Modifier.height(50.dp)) // разделитель

                            Button(

                                onClick = {

                                    navController.navigate(CREATE_USER_INFO_SCREEN)

                                },
                                modifier = Modifier
                                    .fillMaxWidth() // кнопка на всю ширину
                                    .height(50.dp)// высота - 50
                                    .padding(horizontal = 30.dp), // отступы от краев
                                shape = RoundedCornerShape(50), // скругление углов
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = SuccessColor, // цвет кнопки
                                    contentColor = Grey100 // цвет контента на кнопке
                                )
                            ) {
                                Text(
                                    text = "Редактировать",
                                    style = Typography.labelMedium
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_publish),
                                    contentDescription = stringResource(id = R.string.cd_publish_button),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp)) // разделитель


                            // --------- КНОПКА ВЫХОДА ИЗ АККАУНТА ------------

                            Button(
                                onClick = {
                                    // функции на нажатие

                                    try {

                                        navController.navigate(MEETINGS_ROOT) { popUpTo(0) } // после выхода отправляем на страницу мероприятий

                                        accountHelper.signOutGoogle() // выход из аккауна, если вошел через Google

                                        activity.mAuth.signOut() // выход из аккаунта, если вошел по Email


                                    } catch (e: ApiException) {
                                        Log.d("MyLog", "ApiError: ${e.message}")
                                    }

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

                                )
                            ) {

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
                }

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





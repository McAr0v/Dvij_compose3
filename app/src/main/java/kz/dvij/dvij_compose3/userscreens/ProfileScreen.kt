package kz.dvij.dvij_compose3.userscreens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.constants.SECONDARY
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.UserInfoClass
import kz.dvij.dvij_compose3.functions.returnMeetingWord
import kz.dvij.dvij_compose3.functions.returnPlaceWord
import kz.dvij.dvij_compose3.functions.returnStockWord
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

    val openLoading = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА

    // ------ Считываем данные о пользователе -----

    if (activity.mAuth.uid != null){

        activity.userDatabaseManager.readOneUserFromDataBase(userInfo, activity.mAuth.uid!!){

        }

    }

    val accountHelper = AccountHelper(activity)

    // ------ ЕСЛИ USER НЕ РАВЕН NULL И  ПОДТВЕРДИЛ EMAIL ------

    if (user!=null && user.isEmailVerified) {

        if (userInfo.value != UserInfoClass() && !openLoading.value){

            Column(modifier = Modifier
                .fillMaxSize()
                .background(Grey_Background)
                .verticalScroll(rememberScrollState())
            ){

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey_Background)
                    .fillMaxSize()) {

                    // --- ЕСЛИ ЕСТЬ ФОТОГРАФИЯ В ГУГЛ ПРОФИЛЕ И НЕ ЗАГРУЖЕНА СВОЯ АВАТАРКА ----

                    if (user.photoUrl != null && userInfo.value.avatar == "") {

                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = stringResource(id = R.string.cd_avatar),
                            imageLoader = ImageLoader(activity),
                            modifier = Modifier
                                .height(300.dp),
                            contentScale = ContentScale.Crop,
                        )

                    } else if (userInfo.value.avatar != "") {

                        // ----- ЕСЛИ ЗАГРУЖЕНА СВОЯ АВАТАРКА -----

                        AsyncImage(
                            model = userInfo.value.avatar,
                            contentDescription = stringResource(id = R.string.cd_avatar),
                            modifier = Modifier
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        // ----- ЕСЛИ НЕТ АВАТАРКИ ------

                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            painter = painterResource(id = R.drawable.no_user_image),
                            contentDescription = stringResource(id = R.string.cd_avatar),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )

                    }

                    // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .padding(top = 280.dp, end = 0.dp, start = 0.dp, bottom = 0.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {

                        // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                        androidx.compose.material3.Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Grey_Background,
                                    shape = RoundedCornerShape(topStart = 150.dp, topEnd = 0.dp)
                                ),
                            shape = RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            ),
                            elevation = CardDefaults.cardElevation(5.dp),
                            colors = CardDefaults.cardColors(Grey_Background)
                        ) {

                            Column(modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 30.dp, horizontal = 20.dp)
                            ) {


                                // --- КОЛОНКА С ИМЕНЕМ, ГОРОД, РЕДАКТИРОВАТЬ -----

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    Column {

                                        // ----- ИМЯ И ФАМИЛИЯ -----

                                        if (userInfo.value.name != "" && userInfo.value.surname != "") {

                                            androidx.compose.material3.Text(
                                                text = "${userInfo.value.name} ${userInfo.value.surname}",
                                                style = Typography.titleSmall,
                                                color = WhiteDvij
                                            )

                                        } else if (userInfo.value.name == "" && userInfo.value.surname == "") {

                                            user.displayName?.let { name ->
                                                androidx.compose.material3.Text(
                                                    text = name,
                                                    style = Typography.titleSmall,
                                                    color = WhiteDvij
                                                )
                                            }

                                        } else {

                                            androidx.compose.material3.Text(
                                                text = "Новый пользователь",
                                                style = Typography.titleSmall,
                                                color = WhiteDvij
                                            )

                                        }

                                        // ------ ГОРОД -------

                                        if (userInfo.value.city != "Выбери город" && userInfo.value.city != null) {

                                            userInfo.value.city?.let { city ->
                                                androidx.compose.material3.Text(
                                                    text = city,
                                                    style = Typography.bodySmall,
                                                    color = Grey_Text
                                                )
                                            }

                                        } else {

                                            androidx.compose.material3.Text(
                                                text = "Город не выбран",
                                                style = Typography.bodySmall,
                                                color = Grey_Text
                                            )

                                        }
                                    }


                                    // ----- КНОПКА РЕДАКТИРОВАТЬ ------

                                    SocialButtonCustom(icon = R.drawable.ic_edit) {

                                        openLoading.value = true

                                        navController.navigate(CREATE_USER_INFO_SCREEN)

                                    }

                                }

                                Spacer(modifier = Modifier.height(20.dp))


                                // --- ТЕЛЕФОН -----

                                TextAndDesc(
                                    headline = if (userInfo.value.phoneNumber != "7"){

                                        "+7${userInfo.value.phoneNumber}"

                                    } else {

                                        "Телефон не указан"

                                    },
                                    description = "Телефон для звонков",
                                    size = "Small"
                                )


                                Spacer(modifier = Modifier.height(20.dp))

                                // --- Email -----

                                TextAndDesc(
                                    headline = if (user.email != null){

                                        user.email!!

                                    } else {

                                        "Email не указан"

                                    },
                                    description = "Email для входа в аккаунт",
                                    size = "Small"
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // --- Whatsapp -----

                                TextAndDesc(
                                    headline = if (userInfo.value.whatsapp != "7"){

                                        "+7${userInfo.value.whatsapp}"

                                    } else {

                                        "Телефон не указан"

                                    },
                                    description = "Whatsapp",
                                    size = "Small"
                                )


                                Spacer(modifier = Modifier.height(20.dp))

                                // --- Instagram -----

                                TextAndDesc(
                                    headline = if (userInfo.value.instagram != ""){

                                        userInfo.value.instagram!!

                                    } else {

                                        "Instagram не указан"

                                    },
                                    description = "Instagram",
                                    size = "Small"
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // --- Telegram -----

                                TextAndDesc(
                                    headline = if (userInfo.value.telegram != ""){

                                        userInfo.value.telegram!!

                                    } else {

                                        "Telegram не указан"

                                    },
                                    description = "Telegram",
                                    size = "Small"
                                )

                                Spacer(modifier = Modifier.height(30.dp))


                                // ---- КНОПКА ВЫЙТИ ИЗ АККАУНТА -----

                                ButtonCustom(
                                    buttonText = stringResource(id = R.string.sign_out_button),
                                    typeButton = SECONDARY,
                                    rightIcon = R.drawable.ic_logout
                                ) {

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

                                }
                            }
                        }
                    }
                }
            }

        } else {

            Column(modifier = Modifier.fillMaxSize().background(Grey100)) {
                LoadingScreen("Идет загрузка")
            }

        }


    }

    // -------- ЕСЛИ USER ЗАРЕГИСТРИРОВАН НО НЕ ПОДТВЕРДИЛ ПОЧТУ ----------

    else if (user!=null && !user.isEmailVerified){

        Column(
            modifier = Modifier
                .background(Grey_Background)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            // -------------  ЗАГОЛОВОК ПРОВЕРЬ ПОЧТУ И АКТИВИРУЙ АККАУНТ ---------------

            Text(
                text = stringResource(id = R.string.verify_email_title), //"Активируй аккаунт"
                style = Typography.titleLarge, // стиль заголовка
                color = WhiteDvij, // цвет заголовка
                textAlign = TextAlign.Start
            )


            Spacer(modifier = Modifier.height(30.dp)) // разделитель между заголовком и полями для ввода


            // ------ ДОПОЛНИТЕЛЬНОЕ ОПИСАНИЕ ПОД ЗАГОЛОВКОМ ДЛЯ ПОДТВЕРЖДЕНИЯ ПОЧТЫ ---------------

            Text(
                text = stringResource(id = R.string.email_verify_toast),
                style = Typography.bodySmall, // стиль текста
                color = WhiteDvij, // цвет текста
                textAlign = TextAlign.Start
            )


            Spacer(modifier = Modifier.height(60.dp)) // разделитель между полями

            // --------- КНОПКА ВХОД -------------


            ButtonCustom(
                buttonText = stringResource(id = R.string.i_activate_profile)
            ) {
                navController.navigate(LOG_IN_ROOT)
            }


            Spacer(modifier = Modifier.height(20.dp)) // разделитель


            // --------- КНОПКА ВЕРНУТЬСЯ НА ГЛАВНУЮ -------------

            ButtonCustom(
                buttonText = stringResource(id = R.string.go_to_home),
                typeButton = SECONDARY
            ) {
                navController.navigate(MEETINGS_ROOT)
            }

        }
    }

    // ------- ЕСЛИ USER НЕ ЗАРЕГИСТРИРОВАН И НЕ ВОШЕЛ

    else if (user == null) {

        Column(
            modifier = Modifier
                .background(Grey_Background)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            // -------------  ЗАГОЛОВОК Создай аккаунт ---------------

            Text(
                text = stringResource(id = R.string.create_or_sign_in), // Создай аккаунт
                style = Typography.titleLarge, // стиль заголовка
                color = WhiteDvij, // цвет заголовка
                textAlign = TextAlign.Start
            )


            Spacer(modifier = Modifier.height(30.dp)) // разделитель между заголовком и полями для ввода


            // ------ ДОПОЛНИТЕЛЬНОЕ ОПИСАНИЕ ПОД ЗАГОЛОВКОМ ДЛЯ ПОДТВЕРЖДЕНИЯ ПОЧТЫ ---------------

            Text(
                text = stringResource(id = R.string.you_must_create_or_sign_in),
                style = Typography.bodySmall, // стиль текста
                color = WhiteDvij, // цвет текста
                textAlign = TextAlign.Start
            )


            Spacer(modifier = Modifier.height(60.dp)) // разделитель между полями

            // --------- КНОПКА ВХОД -------------


            ButtonCustom(
                buttonText = stringResource(id = R.string.to_login)
            ) {
                navController.navigate(LOG_IN_ROOT)
            }


            Spacer(modifier = Modifier.height(20.dp)) // разделитель


            // --------- КНОПКА ВЕРНУТЬСЯ НА ГЛАВНУЮ -------------

            ButtonCustom(
                buttonText = stringResource(id = R.string.to_registration),
                typeButton = SECONDARY
            ) {
                navController.navigate(REG_ROOT)
            }

        }
    }
}





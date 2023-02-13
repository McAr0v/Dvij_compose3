package kz.dvij.dvij_compose3.userscreens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.INSTAGRAM_URL
import kz.dvij.dvij_compose3.constants.TELEGRAM_URL
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.UserDatabaseManager
import kz.dvij.dvij_compose3.firebase.UserInfoClass
import kz.dvij.dvij_compose3.functions.checkDataOnCreateMeeting
import kz.dvij.dvij_compose3.navigation.CREATE_USER_INFO_SCREEN
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.PROFILE_ROOT
import kz.dvij.dvij_compose3.photohelper.chooseImageDesign
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.Grey95
import kz.dvij.dvij_compose3.ui.theme.SuccessColor
import kz.dvij.dvij_compose3.ui.theme.Typography

class CreateProfileInfoScreen (val act: MainActivity) {

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ
    private val userDatabaseManager = UserDatabaseManager(act)

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun CreateUserInfoScreen (
        navController: NavController,
        citiesList: MutableState<List<CitiesList>>,
        filledUserInfo: UserInfoClass? = UserInfoClass()
    ) {

        var phoneNumber by rememberSaveable { mutableStateOf("7") } // инициализируем переменную телефонного номера
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") } // инициализируем переменную номера с whatsapp
        var openLoading = remember {mutableStateOf(false)} // инициализируем переменную, открывающую диалог ИДЕТ ЗАГРУЗКА
        val openCityDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ГОРОДА

        Column(
            modifier = Modifier
                .fillMaxSize() // занять весь размер экрана
                .background(Grey95) // цвет фона
                .verticalScroll(rememberScrollState()) // говорим, что колонка скролится вверх и вниз
                .padding(top = 0.dp, end = 20.dp, start = 20.dp, bottom = 20.dp) // паддинги
            ,
            verticalArrangement = Arrangement.Top, // выравнивание по вертикали
            horizontalAlignment = Alignment.Start // выравнивание по горизонтали
        ) {

            SpacerTextWithLine(headline = "Аватар") // подпись перед формой

            val avatar = chooseImageDesign(filledUserInfo?.avatar) // Изображение акции

            SpacerTextWithLine(headline = "Имя")

            val name = fieldTextComponent("Введи своё имя") // ТЕКСТОВОЕ ПОЛЕ НАЗВАНИЯ МЕСТА

            SpacerTextWithLine(headline = "Фамилия")

            val surname = fieldTextComponent("Введи свою фамилию") // ТЕКСТОВОЕ ПОЛЕ АДРЕСА МЕСТА

            SpacerTextWithLine(headline = "Email")

            val email = fieldEmailComponent(act = act)

            SpacerTextWithLine(headline = "Телефон")

            val phone = fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it }) // форма телефона

            // --- ФОРМА WHATSAPP ----

            SpacerTextWithLine(headline = "Whatsapp")

            val whatsapp = fieldPhoneComponent(
                phoneNumberWhatsapp,
                onPhoneChanged = { phoneNumberWhatsapp = it },
                icon = painterResource(id = R.drawable.whatsapp)
            )

            SpacerTextWithLine(headline = stringResource(id = R.string.social_instagram)) // подпись перед формой

            val instagram = fieldInstagramComponent(act = act, icon = R.drawable.instagram) // форма инстаграма

            SpacerTextWithLine(headline = stringResource(id = R.string.social_telegram)) // подпись перед формой

            val telegram = fieldInstagramComponent(act = act, icon = R.drawable.telegram) // форма телеграма

            SpacerTextWithLine(headline = stringResource(id = R.string.city_with_star)) // подпись перед формой

            val city = act.chooseCityNavigation.citySelectButton {openCityDialog.value = true}.cityName.toString() // Кнопка выбора города

            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            if (openCityDialog.value) {
                act.chooseCityNavigation.CityChooseDialog(citiesList) {
                    openCityDialog.value = false
                }

                Log.d ("MyLog", "Avatar = $avatar")
            }

            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------

            Button(

                onClick = {

                    // если все права есть и все обязательные поля заполнены

                    openLoading.value = true // открываем диалог загрузки

                    // запускаем корутину

                    GlobalScope.launch(Dispatchers.IO){

                        // запускаем сжатие изображения
                        val compressedImage = act.photoHelper.compressImage(act, avatar!!)

                        // после сжатия запускаем функцию загрузки сжатого фота в Storage

                        act.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", CREATE_USER_INFO_SCREEN){ avatarUrl ->

                            // В качестве колбака придет ссылка на изображение в Storage

                            // Запускаем корутину и публикуем данные пользователя

                            GlobalScope.launch(Dispatchers.Main) {

                                // заполняем

                                val filledUser = UserInfoClass(
                                    avatar = avatarUrl,
                                    name = name,
                                    surname = surname,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    whatsapp = whatsapp,
                                    instagram = instagram,
                                    telegram = telegram,
                                    userKey = auth.uid,
                                    city = city
                                )

                                // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                if (auth.uid != null) {

                                    // Если зарегистрирован, то запускаем функцию публикации мероприятия

                                    userDatabaseManager.publishUser(filledUser = filledUser){ result ->

                                        // в качестве колбака придет булин. Если опубликовано, то:

                                        if (result){

                                            navController.navigate(PROFILE_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                                            // показываем ТОСТ
                                            Toast.makeText(
                                                act,
                                                "Данные пользователя успешно опубликованы",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {

                                            // если произошла ошибка и мероприятие не опубликовалось то:

                                            // Показываем тост
                                            Toast.makeText(
                                                act,
                                                act.resources.getString(R.string.error_text),
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }
                                }
                            }
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth() // кнопка на всю ширину
                    .height(50.dp),// высота - 50
                shape = RoundedCornerShape(50), // скругление углов
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SuccessColor, // цвет кнопки
                    contentColor = Grey100 // цвет контента на кнопке
                )
            ) {
                Text(
                    text = stringResource(id = R.string.push_button),
                    style = Typography.labelMedium
                )

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_publish),
                    contentDescription = stringResource(id = R.string.cd_publish_button),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))



        }

        // --- ЭКРАН ИДЕТ ЗАГРУЗКА ----

        if (openLoading.value) {
            LoadingScreen(act.resources.getString(R.string.ss_loading))
        }

    }

}
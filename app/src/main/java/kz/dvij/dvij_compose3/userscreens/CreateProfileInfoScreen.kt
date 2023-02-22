package kz.dvij.dvij_compose3.userscreens

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
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.UserDatabaseManager
import kz.dvij.dvij_compose3.firebase.UserInfoClass
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
        filledUserInfo: UserInfoClass = UserInfoClass(
            avatar = "",
            name = "",
            surname = "",
            email = "",
            phoneNumber = "",
            whatsapp = "",
            instagram = "",
            telegram = "",
            userKey = "",
            city = "Выбери город"
        ),
        // Тип страницы - редактирование или создание
        createOrEdit: String
    ) {

        val nameFromDb = remember {mutableStateOf(filledUserInfo.name)}
        val surnameFromDb = remember {mutableStateOf(filledUserInfo.surname)}
        val emailFromDb = remember {mutableStateOf(filledUserInfo.email)}
        val instagramFromDb = remember {mutableStateOf(filledUserInfo.instagram)}
        val telegramFromDb = remember {mutableStateOf(filledUserInfo.telegram)}

        // --- ПЕРЕМЕННЫЕ ГОРОДА ---


        // Значение города по умолчанию
        val chosenCityCreateWithoutUser = remember {mutableStateOf("Выбери город")}

        // Выбранный город из данных мероприятия. Используется при редактировании
        val chosenCity = remember {mutableStateOf(filledUserInfo.city!!)}

        // Переменная, передаваемая в БД
        var city by rememberSaveable { mutableStateOf("Выбери город") }

        var phoneNumber by rememberSaveable { mutableStateOf("7") } // инициализируем переменную телефонного номера
        var phoneNumberFromDb by rememberSaveable {
            mutableStateOf(filledUserInfo.phoneNumber)
        }
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") } // инициализируем переменную номера с whatsapp
        var phoneNumberWhatsappFromDb by rememberSaveable {
            mutableStateOf(filledUserInfo.whatsapp)
        }

        val openLoading = remember {mutableStateOf(false)} // инициализируем переменную, открывающую диалог ИДЕТ ЗАГРУЗКА
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

            // ---- АВАТАР ------

            SpacerTextWithLine(headline = "Аватар") // подпись перед формой

            val avatar = if (filledUserInfo.avatar != null && filledUserInfo.avatar != "" && createOrEdit != "0"){

                // Если при редактировании есть картинка, подгружаем картинку
                chooseImageDesign(filledUserInfo.avatar)

            } else {

                // Если нет - стандартный выбор картинки
                chooseImageDesign()

            }


            // ---- ИМЯ ------

            SpacerTextWithLine(headline = "Имя")

            val name = if (filledUserInfo.name != null && filledUserInfo.name != "" && createOrEdit != "0" ){

                fieldTextComponent("Введи своё имя", nameFromDb.value)

            } else {

                fieldTextComponent("Введи своё имя")

            }


            // ---- ФАМИЛИЯ -------

            SpacerTextWithLine(headline = "Фамилия")

            val surname = if (filledUserInfo.surname != null && filledUserInfo.surname != "" && createOrEdit != "0" ){

                fieldTextComponent("Введи свою фамилию", surnameFromDb.value)

            } else {

                fieldTextComponent("Введи свою фамилию")

            }


            // ---- EMAIL ---------

            SpacerTextWithLine(headline = "Email")

            val email = if (filledUserInfo.email != null && filledUserInfo.email != "" && createOrEdit != "0" ) {

                fieldEmailComponent(act = act, emailFromDb.value)

            } else {

                fieldEmailComponent(act = act)

            }


            // ----- ТЕЛЕФОН --------

            SpacerTextWithLine(headline = "Телефон")

            val phone = if (phoneNumberFromDb != null && phoneNumberFromDb != "+7" && phoneNumberFromDb != "+77" && phoneNumberFromDb != ""){

                fieldPhoneComponent(phoneNumberFromDb!!, onPhoneChanged = { phoneNumberFromDb = it }) // форма телефона

            } else {

                fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it }) // форма телефона

            }
            



            // --- ФОРМА WHATSAPP ----

            SpacerTextWithLine(headline = "Whatsapp")

            val whatsapp = if (phoneNumberFromDb != null && phoneNumberFromDb != "+7" && phoneNumberFromDb != "+77" && phoneNumberFromDb != ""){

                fieldPhoneComponent(
                    phoneNumberWhatsappFromDb!!,
                    onPhoneChanged = { phoneNumberWhatsappFromDb = it },
                    icon = painterResource(id = R.drawable.whatsapp),
                    mask = "+7 (XXX) XXX XX XX"
                )

            } else {

                fieldPhoneComponent(
                    phoneNumberWhatsapp,
                    onPhoneChanged = { phoneNumberWhatsapp = it },
                    icon = painterResource(id = R.drawable.whatsapp)
                )

            }



            // ------ ИНСТАГРАМ -------

            SpacerTextWithLine(headline = stringResource(id = R.string.social_instagram)) // подпись перед формой

            val instagram = if (filledUserInfo.instagram != null && filledUserInfo.instagram != "" && createOrEdit != "0" ){

                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = instagramFromDb.value) // форма инстаграма

            } else {

                fieldInstagramComponent(act = act, icon = R.drawable.instagram)

            }


            // ------ ТЕЛЕГРАМ -------

            SpacerTextWithLine(headline = stringResource(id = R.string.social_telegram)) // подпись перед формой

            val telegram = if (filledUserInfo.telegram != null && filledUserInfo.telegram != "" && createOrEdit != "0" ) {

                fieldInstagramComponent(act = act, icon = R.drawable.telegram, inputText = telegramFromDb.value) // форма телеграма

            } else {

                fieldInstagramComponent(act = act, icon = R.drawable.telegram)

            }



            // ---- ГОРОД ------

            SpacerTextWithLine(headline = stringResource(id = R.string.city_with_star)) // подпись перед формой


            // Если при редактировании в пользователе есть город

            if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && filledUserInfo.city != "Empty") {

                // Передаем в кнопку выбора города ГОРОД ИЗ МЕРОПРИЯТИЯ ДЛЯ РЕДАКТИРОВАНИЯ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCity) {openCityDialog.value = true}

            } else {

                // В ОСТАЛЬНЫХ СЛУЧАЯХ - ПЕРЕДАЕМ ГОРОД ПО УМОЛЧАНИЮ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityCreateWithoutUser) {openCityDialog.value = true}

            }


            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            if (openCityDialog.value) {

                if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && filledUserInfo.city != "Empty"){

                    // Если при редактировании в мероприятии есть город, Передаем ГОРОД ИЗ МЕРОПРИЯТИЯ ДЛЯ РЕДАКТИРОВАНИЯ
                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCity, citiesList) {
                        openCityDialog.value = false
                    }

                } else {

                    // В ОСТАЛЬНЫХ СЛУЧАЯХ - ПЕРЕДАЕМ ГОРОД ПО УМОЛЧАНИЮ
                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityCreateWithoutUser, citiesList) {
                        openCityDialog.value = false
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------

            Button(

                onClick = {

                    // если все права есть и все обязательные поля заполнены

                    openLoading.value = true // открываем диалог загрузки

                    // запускаем корутину

                    GlobalScope.launch(Dispatchers.IO){

                        if (avatar != null){

                            // запускаем сжатие изображения
                            val compressedImage = act.photoHelper.compressImage(act, avatar)

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
                                        phoneNumber = phone,
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

                                                act.chooseCityNavigation.chosenCity = CitiesList("Выбери город", "default_city")

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

                        } else {

                            // Запускаем корутину и публикуем данные пользователя

                            GlobalScope.launch(Dispatchers.Main) {

                                // заполняем

                                val filledUser = UserInfoClass(
                                    avatar = filledUserInfo.avatar,
                                    name = name,
                                    surname = surname,
                                    email = email,
                                    phoneNumber = phone,
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

                                            act.chooseCityNavigation.chosenCity = CitiesList("Выбери город", "default_city")

                                            navController.navigate(PROFILE_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                                            // показываем ТОСТ
                                            Toast.makeText(
                                                act,
                                                "Данные пользователя успешно отредактированы",
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
package kz.dvij.dvij_compose3.meetingscreens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import kotlinx.coroutines.*
import kz.dvij.dvij_compose3.pickers.dataPicker
import kz.dvij.dvij_compose3.pickers.timePicker
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.functions.checkDataOnCreateMeeting
import kz.dvij.dvij_compose3.navigation.ChoosePlaceDialog
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.photohelper.chooseImageDesign
import kz.dvij.dvij_compose3.ui.theme.*

class CreateMeeting(private val act: MainActivity) {

    private val meetingDatabase = MeetingDatabaseManager(act)

    // ------ КЛАСС СОЗДАНИЯ МЕРОПРИЯТИЯ ----------

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    // ---- МЕРОПРИЯТИЕ ПО УМОЛЧАНИЮ ----------

    val default = MeetingsAdsClass (
        description = "Default"
    )

    private val choosePlaceDialog = ChoosePlaceDialog(act)


    // ------- ЭКРАН СОЗДАНИЯ МЕРОПРИЯТИЯ ------------

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RememberReturnType")
    @Composable
    fun CreateMeetingScreen(
        navController: NavController,
        citiesList: MutableState<List<CitiesList>>,
        filledUserInfo: UserInfoClass = UserInfoClass(),
        filledMeeting: MeetingsAdsClass = MeetingsAdsClass(
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
            ownerKey = ""
        ),
        meetingKey: String
    ) {

        Log.d ("MyLog", "$filledMeeting")

        val activity = act
        val meetingDatabaseManager = MeetingDatabaseManager(activity) // инициализируем класс с функциями базы данных ИНИЦИАЛИЗИРОВАТЬ НУЖНО ИМЕННО ТАК, ИНАЧЕ НАЛ
        val placesDatabaseManager = PlacesDatabaseManager(act = activity)

        // КАЛЕНДАРЬ - https://www.geeksforgeeks.org/date-picker-in-android-using-jetpack-compose/
        // https://stackoverflow.com/questions/60417233/jetpack-compose-date-time-picker

        var phoneNumber by rememberSaveable { mutableStateOf("7") } // инициализируем переменную телефонного номера
        var userPhoneNumber by rememberSaveable { mutableStateOf(filledUserInfo.phoneNumber) } // инициализируем переменную телефонного номера пользователя с БД
        var phoneNumberFromDb by rememberSaveable {
            mutableStateOf(filledMeeting.phone)
        }


        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") } // инициализируем переменную номера с whatsapp
        var userWhatsappNumber by rememberSaveable { mutableStateOf(filledUserInfo.whatsapp) }
        var phoneNumberWhatsappFromDb by rememberSaveable {
            mutableStateOf(filledMeeting.whatsapp)
        }

        val headlinePlace = remember {mutableStateOf(filledMeeting.headlinePlaceInput)} // инициализируем переменную заголовка места, введенного вручную
        val addressPlace = remember {mutableStateOf(filledMeeting.addressPlaceInput)} // инициализирууем переменную адреса места, введенного вручную

        val chosenMeetingCategoryCreate = remember {mutableStateOf("Выбери категорию")}
        val chosenMeetingCategoryEdit = remember {mutableStateOf<String>(filledMeeting.category!!)}
        var category by rememberSaveable { mutableStateOf("Выбери категорию") }


        var headline = "" // инициализируем заголовок
        var description = "" // инициализируем описание
        var price = "" // инициализируем цену
        var phone = "" // инициализируем телефон
        var whatsapp = "" // инициализируем whatsapp

        var dataResult = "" // инициализируем выбор даты
        var timeStartResult = "" // инициализируем выбор времени начала мероприятия
        var timeFinishResult = "" // инициализируем выбор времени конца мероприятия


        var placeInfo = PlacesAdsClass (placeName = "Выбери заведение") // инициализируем ЗАВЕДЕНИЕ ПО УМОЛЧАНИЮ

        val openLoading = remember {mutableStateOf(false)} // инициализируем переменную, открывающую диалог ИДЕТ ЗАГРУЗКА
        val openCategoryDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог КАТЕГОРИИ
        val openCityDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ГОРОДА
        val openPlaceDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ЗАВЕДЕНИЙ
        val openFieldPlace = remember { mutableStateOf(false) } // инициализируем переменную, открывающую формы ЗАВЕДЕНИЙ

        val chosenCityCreateWithUser = remember {mutableStateOf(filledUserInfo.city!!)}
        val chosenCityCreateWithoutUser = remember {mutableStateOf("Выбери город")}
        val chosenCityEdit = remember {mutableStateOf<String>(filledMeeting.city!!)}
        var city by rememberSaveable { mutableStateOf("Выбери город") }

        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------

        // Инициализируем переменную списка категорий
        val categoriesList = remember {
            mutableStateOf(listOf<CategoriesList>())
        }

        // Инициализируем переменную списка мест
        val placesList = remember {
            mutableStateOf(listOf<PlacesAdsClass>())
        }

        // Запускаем функцию считывания списка категорий с базы данных
        act.categoryDialog.readMeetingCategoryDataFromDb(categoriesList)

        // Считываем список моих заведений
        placesDatabaseManager.readPlaceMyDataFromDb(placesList)

        // --------- САМ КОНТЕНТ СТРАНИЦЫ -----------------

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

            // ---- СОДЕРЖИМОЕ СТРАНИЦЫ СОЗДАНИЯ ---------

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_image)) // подпись перед формой

            val image1 = if (filledMeeting.image1 != null && filledMeeting.image1 != "" && meetingKey != "0"){
                chooseImageDesign(filledMeeting.image1) // Изображение мероприятия
            } else {
                chooseImageDesign()
            }

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_headline)) // подпись перед формой

            headline = if (filledMeeting.headline != null && filledMeeting.headline != "" && meetingKey != "0"){
                fieldHeadlineComponent(act = activity, filledMeeting.headline) // форма заголовка
            } else {
                fieldHeadlineComponent(act = activity) // форма заголовка
            }

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_category)) // подпись перед формой

            if (filledMeeting.category != null && filledMeeting.category != "Выбери категорию" && filledMeeting.category != "" && meetingKey != "0") {

                category = act.categoryDialog.meetingCategorySelectButton(categoryName = chosenMeetingCategoryEdit) { openCategoryDialog.value = true }.toString()


            } else {
                category = act.categoryDialog.meetingCategorySelectButton (categoryName = chosenMeetingCategoryCreate) { openCategoryDialog.value = true }.toString() // Кнопка выбора категории
        }

            // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

            if (openCategoryDialog.value) {

                if (meetingKey != "0"){

                    act.categoryDialog.CategoryMeetingChooseDialog(categoryName = chosenMeetingCategoryEdit, categoriesList) {
                        openCategoryDialog.value = false
                    }

                } else {

                    act.categoryDialog.CategoryMeetingChooseDialog(categoryName = chosenMeetingCategoryCreate, categoriesList) {
                        openCategoryDialog.value = false
                    }

                }


            }





            SpacerTextWithLine(headline = "Заведение*") // подпись перед формой

            // --- КНОПКИ ВЫБОРА - ВЫБРАТЬ ЗАВЕДЕНИЕ ИЗ СПИСКА ИЛИ ВВВЕСТИ ВРУЧНУЮ ------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // ---- КНОПКА ВЫБОРА ЗАВЕДЕНИЯ ИЗ ДИАЛОГА --------

                placeInfo = choosePlaceDialog.placeSelectButton {
                    openPlaceDialog.value = true
                    openFieldPlace.value = false // Сбрасываем отображение форм адреса и названия заведения ВРУЧНУЮ, а так же цвета кнопки выбора вручную
                    headlinePlace.value = "" // Сбрасываем значения заголовка, введенного вручную
                    addressPlace.value = "" // Сбрасываем значения адреса, введенного вручную
                }

                Spacer(modifier = Modifier.width(10.dp))

                // --- КНОПКА ВКЛЮЧЕНИЯ ВВОДА АДРЕСА ВРУЧНУЮ --------

                Button(
                    onClick = {

                        if (openFieldPlace.value){
                            openFieldPlace.value = false
                        } else {
                            openFieldPlace.value = true

                            // если выбираем ввести вручную, а уже выбрано заведение из списка
                            // то сбрасываем выбранное заведение из списка
                            choosePlaceDialog.chosenPlace = PlacesAdsClass(placeName = "Выбери заведение")
                        }

                    },

                    // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

                    border = BorderStroke(
                        width = if (!openFieldPlace.value) {
                            2.dp
                        } else {
                            0.dp
                        }, color = if (!openFieldPlace.value) {
                            Grey60
                        } else {
                            Grey95
                        }
                    ),

                    // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (!openFieldPlace.value) {
                            Grey95
                        } else {
                            PrimaryColor
                        },
                        contentColor = if (!openFieldPlace.value) {
                            Grey60
                        } else {
                            Grey100
                        },
                    ),
                    shape = RoundedCornerShape(50) // скругленные углы кнопки
                ) {

                    Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

                    androidx.compose.material3.Text(
                        text = "Ввести адрес вручную", // текст кнопки
                        style = Typography.labelMedium, // стиль текста
                        color = if (!openFieldPlace.value) {
                            Grey60
                        } else {
                            Grey100
                        }
                    )
                }
            }

            // --- КОНТЕНТ, ЕСЛИ МЫ ВЫБРАЛИ ВВЕСТИ АДРЕС И НАЗВАНИЕ ЗАВЕДЕНИЯ ВРУЧНУЮ ----

            if (openFieldPlace.value){
                
                Spacer(modifier = Modifier.height(10.dp))

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Grey90, shape = RoundedCornerShape(15.dp))
                        .padding(top = 0.dp, start = 10.dp, end = 10.dp, bottom = 20.dp)

                ) {
                    SpacerTextWithLine(headline = "Название места проведения")
                    headlinePlace.value = fieldTextComponent("Введите название места") // ТЕКСТОВОЕ ПОЛЕ НАЗВАНИЯ МЕСТА
                    SpacerTextWithLine(headline = "Адрес места проведения")
                    addressPlace.value = fieldTextComponent("Введите адрес места") // ТЕКСТОВОЕ ПОЛЕ АДРЕСА МЕСТА
                }
            }



            // --- САМ ДИАЛОГ ВЫБОРА Заведения -----

            if (openPlaceDialog.value) {
                choosePlaceDialog.PlaceChooseDialog(placesList = placesList) {
                    openPlaceDialog.value = false
                }
            }

            SpacerTextWithLine(headline = stringResource(id = R.string.city_with_star)) // подпись перед формой

            if (filledMeeting.city != null && filledMeeting.city != "Выбери город" && filledMeeting.city != "" && meetingKey != "0") {

                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityEdit) {openCityDialog.value = true}.toString() // Кнопка выбора города


            } else if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && meetingKey == "0") {

            city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityCreateWithUser) {openCityDialog.value = true}.toString() // Кнопка выбора города


        } else {
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityCreateWithoutUser) {openCityDialog.value = true}.toString() // Кнопка выбора города
            }

            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            if (openCityDialog.value) {

                if (filledMeeting.city != null && filledMeeting.city != "Выбери город" && filledMeeting.city != "" && meetingKey != "0"){

                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityEdit, citiesList) {
                        openCityDialog.value = false
                    }

                } else if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && meetingKey == "0"){

                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityCreateWithUser, citiesList) {
                        openCityDialog.value = false
                    }

                } else {

                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityCreateWithoutUser, citiesList) {
                        openCityDialog.value = false
                    }

                }


            }

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_phone)) // подпись перед формой

            phone = if (phoneNumberFromDb != "+7" && phoneNumberFromDb != "+77" && phoneNumberFromDb != "" && phoneNumberFromDb != null && meetingKey != "0"){

                fieldPhoneComponent(phoneNumberFromDb!!, onPhoneChanged = { phoneNumberFromDb = it }) // форма телефона

            } else if (filledUserInfo.phoneNumber != "+7" && filledUserInfo.phoneNumber != "+77" && filledUserInfo.phoneNumber != "" && filledUserInfo.phoneNumber != null && meetingKey == "0") {

                fieldPhoneComponent(userPhoneNumber!!, onPhoneChanged = { userPhoneNumber = it }) // форма телефона

            } else {

                fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it }) // форма телефона

            }


            SpacerTextWithLine(headline = stringResource(id = R.string.cm_whatsapp)) // подпись перед формой


            // --- ФОРМА WHATSAPP ----

            whatsapp = if (phoneNumberWhatsappFromDb != null && phoneNumberWhatsappFromDb != "+7" && phoneNumberWhatsappFromDb != "+77" && phoneNumberWhatsappFromDb != "" && meetingKey != "0"){

                fieldPhoneComponent(phoneNumberWhatsappFromDb!!, onPhoneChanged = { phoneNumberWhatsappFromDb = it }) // форма телефона

            } else if (userWhatsappNumber != "+7" && userWhatsappNumber != "+77" && userWhatsappNumber != "" && userWhatsappNumber != null && meetingKey == "0") {

                fieldPhoneComponent(userWhatsappNumber!!, onPhoneChanged = { userWhatsappNumber = it }) // форма телефона

            } else {

                fieldPhoneComponent(phoneNumberWhatsapp, onPhoneChanged = { phoneNumberWhatsapp = it }, icon = painterResource(id = R.drawable.whatsapp))

            }

            SpacerTextWithLine(headline = stringResource(id = R.string.social_instagram)) // подпись перед формой

            val instagram = if (filledMeeting.instagram != "" && filledMeeting.instagram != null && meetingKey != "0") {

                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledMeeting.instagram) // форма инстаграма

            } else if (filledUserInfo.instagram != "" && filledUserInfo.instagram != null && meetingKey == "0") {

                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledUserInfo.instagram) // форма инстаграма

            } else {

                fieldInstagramComponent(act = act, icon = R.drawable.instagram)

            }


            SpacerTextWithLine(headline = stringResource(id = R.string.social_telegram)) // подпись перед формой

            val telegram = if (filledMeeting.telegram != "" && filledMeeting.telegram != null && meetingKey != "0") {

                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledMeeting.telegram) // форма телеграма

            } else if (filledUserInfo.telegram != "" && filledUserInfo.telegram != null && meetingKey == "0") {

                fieldInstagramComponent(act = act, icon = R.drawable.telegram, inputText = filledUserInfo.telegram) // форма телеграма

            } else {

                fieldInstagramComponent(act = act, icon = R.drawable.telegram)

            }

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_date)) // подпись перед формой

            dataResult = dataPicker(act) // ВЫБОР ДАТЫ

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_start_time)) // подпись перед формой

            timeStartResult = timePicker() // ВЫБОР ВРЕМЕНИ - Начало мероприятия

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_finish_time)) // подпись перед формой

            timeFinishResult = timePicker() // ВЫБОР ВРЕМЕНИ - Конец мероприятия

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_price)) // подпись перед формой

            price = if (filledMeeting.price != "" && filledMeeting.price != null && meetingKey != "0") {

                fieldPriceComponent(act = activity, filledMeeting.price) // Форма цены за билет

            } else {

                fieldPriceComponent(act = activity) // Форма цены за билет

            }


            SpacerTextWithLine(headline = stringResource(id = R.string.cm_description)) // подпись перед формой

            description = if (filledMeeting.description != "" && filledMeeting.description != null && meetingKey != "0"){

                fieldDescriptionComponent(act = activity, filledMeeting.description) // ФОРМА ОПИСАНИЯ МЕРОПРИЯТИЯ

            } else {

                fieldDescriptionComponent(act = activity)
            }


            Spacer(modifier = Modifier.height(30.dp)) // РАЗДЕЛИТЕЛЬ


            // -------------- КНОПКИ ОТМЕНА И ОПУБЛИКОВАТЬ ------------


            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------

                Button(

                    onClick = {

                        // действие на нажатие

                        // --- ФУНКЦИЯ ПРОВЕРКИ НА ЗАПОЛНЕНИЕ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ ---------

                        val checkData = checkDataOnCreateMeeting(image1, headline, phone, dataResult, timeStartResult, description, category, city)

                        if (checkData != 0) {

                            // если checkData вернет какое либо число, то это число будет ID сообщения в тосте

                            Toast.makeText(activity, act.resources.getString(checkData), Toast.LENGTH_SHORT).show()

                        } else if (ContextCompat.checkSelfPermission(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                            // так же проверка, если нет разрешения на запись картинок в память, то запрос на эти права

                            ActivityCompat.requestPermissions(act, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 888)

                        } else {

                            // если все права есть и все обязательные поля заполнены

                            openLoading.value = true // открываем диалог загрузки

                            // запускаем корутину

                            GlobalScope.launch(Dispatchers.IO){

                                // запускаем сжатие изображения
                                val compressedImage = activity.photoHelper.compressImage(activity, image1!!)

                                // после сжатия запускаем функцию загрузки сжатого фота в Storage

                                activity.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", MEETINGS_ROOT){

                                    // В качестве колбака придет ссылка на изображение в Storage

                                    // Запускаем корутину и публикуем мероприятие

                                    GlobalScope.launch(Dispatchers.Main) {

                                        // заполняем мероприятие

                                        val filledMeeting = MeetingsAdsClass(
                                            key = meetingDatabaseManager.meetingDatabase.push().key, // генерируем уникальный ключ мероприятия
                                            category = category,
                                            headline = headline,
                                            description = description,
                                            price = price,
                                            phone = phone,
                                            whatsapp = whatsapp,
                                            data = dataResult,
                                            startTime = timeStartResult,
                                            finishTime = timeFinishResult,
                                            image1 = it,
                                            city = city,
                                            instagram = instagram,
                                            telegram = telegram,
                                            placeKey = placeInfo.placeKey ?: "Empty",
                                            headlinePlaceInput = headlinePlace.value,
                                            addressPlaceInput = addressPlace.value,
                                            ownerKey = act.mAuth.uid
                                        )

                                        // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                        if (auth.uid != null) {

                                            // Если зарегистрирован, то запускаем функцию публикации мероприятия

                                            meetingDatabaseManager.publishMeeting(filledMeeting){ result ->

                                                // в качестве колбака придет булин. Если опубликовано мероприятие то:

                                                if (result){

                                                    // сбрасываем выбранное заведение, чтобы потом не отображался последний выбор
                                                    choosePlaceDialog.chosenPlace = PlacesAdsClass(placeName = "Выбери заведение")
                                                    placeInfo = PlacesAdsClass (placeName = "Выбери заведение")

                                                    // сбрасываем выбранную категорию, чтобы потом не отображался последний выбор категории
                                                    act.categoryDialog.chosenMeetingCategory = CategoriesList ("Выбери категорию", "Default")

                                                    // сбрасываем выбранный город, чтобы потом не отображался последний выбор города
                                                    act.chooseCityNavigation.chosenCity = CitiesList("Выбери город", "default_city")

                                                    navController.navigate(MEETINGS_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                                                    // показываем ТОСТ
                                                    Toast.makeText(
                                                        activity,
                                                        act.resources.getString(R.string.cm_success),
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                } else {

                                                    // если произошла ошибка и мероприятие не опубликовалось то:

                                                    // Показываем тост
                                                    Toast.makeText(
                                                        activity,
                                                        act.resources.getString(R.string.error_text),
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }
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

            TextButton(
                onClick = { Toast.makeText(activity, "СДЕЛАТЬ ДИАЛОГ - ДЕЙСТВИТЕЛЬНО ХОТИТЕ ВЫЙТИ?", Toast.LENGTH_SHORT).show() },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.cansel_button),
                    style = Typography.labelMedium,
                    color = Grey40
                )
            }
        }

        // --- ЭКРАН ИДЕТ ЗАГРУЗКА ----

        if (openLoading.value) {
            LoadingScreen(act.resources.getString(R.string.ss_loading))
        }
    }

}
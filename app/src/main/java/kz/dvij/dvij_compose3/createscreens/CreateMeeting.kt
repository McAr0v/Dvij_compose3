package kz.dvij.dvij_compose3.createscreens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
import kz.dvij.dvij_compose3.constants.INSTAGRAM_URL
import kz.dvij.dvij_compose3.constants.TELEGRAM_URL
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.MeetingDatabaseManager
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.functions.checkDataOnCreateMeeting
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.photohelper.chooseImageDesign
import kz.dvij.dvij_compose3.ui.theme.*

class CreateMeeting(private val act: MainActivity) {

    // ------ КЛАСС СОЗДАНИЯ МЕРОПРИЯТИЯ ----------

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    val default = MeetingsAdsClass (
        description = "def"
    )


    // ------- ЭКРАН СОЗДАНИЯ МЕРОПРИЯТИЯ ------------

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RememberReturnType")
    @Composable
    fun CreateMeetingScreen(navController: NavController, citiesList: MutableState<List<CitiesList>>) {

        val activity = act
        val meetingDatabaseManager = MeetingDatabaseManager(activity) // инициализируем класс с функциями базы данных ИНИЦИАЛИЗИРОВАТЬ НУЖНО ИМЕННО ТАК, ИНАЧЕ НАЛ

        // КАЛЕНДАРЬ - https://www.geeksforgeeks.org/date-picker-in-android-using-jetpack-compose/
        // https://stackoverflow.com/questions/60417233/jetpack-compose-date-time-picker

        var phoneNumber by rememberSaveable { mutableStateOf("7") } // инициализируем переменную телефонного номера
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") } // инициализируем переменную номера с whatsapp


        var headline = "" // инициализируем заголовок
        var description = "" // инициализируем описание
        var price = "" // инициализируем цену
        var phone = "" // инициализируем телефон
        var whatsapp = "" // инициализируем whatsapp

        var dataResult = "" // инициализируем выбор даты
        var timeStartResult = "" // инициализируем выбор времени начала мероприятия
        var timeFinishResult = "" // инициализируем выбор времени конца мероприятия
        var category: String // категория

        var openLoading = remember {mutableStateOf(false)} // инициализируем переменную, открывающую диалог ИДЕТ ЗАГРУЗКА
        val openCategoryDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог КАТЕГОРИИ
        val openCityDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ГОРОДА


        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------

        // Инициализируем переменную списка категорий
        val categoriesList = remember {
            mutableStateOf(listOf<CategoriesList>())
        }


        // Запускаем функцию считывания списка категорий с базы данных
        act.categoryDialog.readMeetingCategoryDataFromDb(categoriesList)


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

            val image1 = chooseImageDesign() // Изображение мероприятия

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_headline)) // подпись перед формой

            headline = fieldHeadlineComponent(act = activity) // форма заголовка

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_category)) // подпись перед формой

            category = act.categoryDialog.meetingCategorySelectButton { openCategoryDialog.value = true }.categoryName.toString() // Кнопка выбора категории

            SpacerTextWithLine(headline = stringResource(id = R.string.city_with_star)) // подпись перед формой

            val city = act.chooseCityNavigation.citySelectButton {openCityDialog.value = true}.cityName.toString() // Кнопка выбора города

            // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

            if (openCategoryDialog.value) {
                act.categoryDialog.CategoryMeetingChooseDialog(categoriesList) {
                    openCategoryDialog.value = false
                }
            }

            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            if (openCityDialog.value) {
                act.chooseCityNavigation.CityChooseDialog(citiesList) {
                    openCityDialog.value = false
                }
            }


            SpacerTextWithLine(headline = stringResource(id = R.string.cm_phone)) // подпись перед формой

            phone = fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it }) // форма телефона

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_whatsapp)) // подпись перед формой


            // --- ФОРМА WHATSAPP ----

            whatsapp = fieldPhoneComponent(
                phoneNumberWhatsapp,
                onPhoneChanged = { phoneNumberWhatsapp = it },
                icon = painterResource(id = R.drawable.whatsapp)
            )

            SpacerTextWithLine(headline = stringResource(id = R.string.social_instagram)) // подпись перед формой
            
            val instagram = fieldInstagramComponent(act = act, icon = R.drawable.instagram) // форма инстаграма

            SpacerTextWithLine(headline = stringResource(id = R.string.social_telegram)) // подпись перед формой

            val telegram = fieldInstagramComponent(act = act, icon = R.drawable.telegram) // форма телеграма

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_date)) // подпись перед формой

            dataResult = dataPicker(act) // ВЫБОР ДАТЫ

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_start_time)) // подпись перед формой

            timeStartResult = timePicker() // ВЫБОР ВРЕМЕНИ - Начало мероприятия

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_finish_time)) // подпись перед формой

            timeFinishResult = timePicker() // ВЫБОР ВРЕМЕНИ - Конец мероприятия

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_price)) // подпись перед формой

            price = fieldPriceComponent(act = activity) // Форма цены за билет

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_description)) // подпись перед формой

            description = fieldDescriptionComponent(act = activity) // ФОРМА ОПИСАНИЯ МЕРОПРИЯТИЯ

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
                                            instagram = INSTAGRAM_URL + instagram,
                                            telegram = TELEGRAM_URL + telegram
                                        )

                                        // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                        if (auth.uid != null) {

                                            // Если зарегистрирован, то запускаем функцию публикации мероприятия

                                            meetingDatabaseManager.publishMeeting(filledMeeting){ result ->

                                                // в качестве колбака придет булин. Если опубликовано мероприятие то:

                                                if (result){

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
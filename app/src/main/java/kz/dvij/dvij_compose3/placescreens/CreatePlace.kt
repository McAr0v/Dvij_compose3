package kz.dvij.dvij_compose3.placescreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.functions.checkDataOnCreatePlace
import kz.dvij.dvij_compose3.navigation.PLACES_ROOT
import kz.dvij.dvij_compose3.photohelper.chooseImageDesign
import kz.dvij.dvij_compose3.pickers.timePicker
import kz.dvij.dvij_compose3.ui.theme.*

class CreatePlace (val act: MainActivity) {

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО заведение

    val default = PlacesAdsClass (
        placeDescription = "Default"
    )

    private val placesDatabaseManager = PlacesDatabaseManager(act) // ИНИЦИАЛИЗИРОВАТЬ НУЖНО ИМЕННО ТАК, ИНАЧЕ НАЛ

    // ----- ЭКРАН СОЗДАНИЯ ЗАВЕДЕНИЯ --------

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RememberReturnType")
    @Composable
    fun CreatePlaceScreen (navController: NavController, citiesList: MutableState<List<CitiesList>>) {

        var phoneNumber by rememberSaveable { mutableStateOf("7") } // инициализируем переменную телефонного номера
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") } // инициализируем переменную номера с whatsapp

        var openLoading = remember {mutableStateOf(false)} // инициализируем переменную, открывающую диалог ИДЕТ ЗАГРУЗКА
        val openCategoryDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог КАТЕГОРИИ
        val openCityDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ГОРОДА

        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------

        // Инициализируем переменную списка категорий
        val categoriesList = remember {
            mutableStateOf(listOf<CategoriesList>())
        }

        // Запускаем функцию считывания списка категорий с базы данных
        act.categoryDialog.readPlaceCategoryDataFromDb(categoriesList)

        // ---- СОДЕРЖИМОЕ СТРАНИЦЫ СОЗДАНИЯ ---------

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

            SpacerTextWithLine(headline = "Логотип заведения") // подпись перед формой

            val image1 = chooseImageDesign() // Изображение заведения

            SpacerTextWithLine(headline = "Название заведения") // подпись перед формой

            val headline = fieldHeadlineComponent(act = act) // форма заголовка

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_category)) // подпись перед формой

            val category = act.categoryDialog.placeCategorySelectButton { openCategoryDialog.value = true }.categoryName.toString() // Кнопка выбора категории

            SpacerTextWithLine(headline = stringResource(id = R.string.city_with_star)) // подпись перед формой

            // val city = act.chooseCityNavigation.citySelectButton {openCityDialog.value = true}.cityName.toString() // Кнопка выбора города

            SpacerTextWithLine(headline = "Адрес") // подпись перед формой

            val address = fieldHeadlineComponent(act = act) // форма заголовка

            // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

            if (openCategoryDialog.value) {
                act.categoryDialog.CategoryPlaceChooseDialog(categoriesList) {
                    openCategoryDialog.value = false
                }
            }

            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            /*if (openCityDialog.value) {
                act.chooseCityNavigation.CityChooseDialog(citiesList) {
                    openCityDialog.value = false
                }
            }*/


            SpacerTextWithLine(headline = stringResource(id = R.string.cm_phone)) // подпись перед формой

            var phone = fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it }) // форма телефона

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_whatsapp)) // подпись перед формой


            // --- ФОРМА WHATSAPP ----

            var whatsapp = fieldPhoneComponent(
                phoneNumberWhatsapp,
                onPhoneChanged = { phoneNumberWhatsapp = it },
                icon = painterResource(id = R.drawable.whatsapp)
            )

            SpacerTextWithLine(headline = stringResource(id = R.string.social_instagram)) // подпись перед формой

            val instagram = fieldInstagramComponent(act = act, icon = R.drawable.instagram) // форма инстаграма

            SpacerTextWithLine(headline = stringResource(id = R.string.social_telegram)) // подпись перед формой

            val telegram = fieldInstagramComponent(act = act, icon = R.drawable.telegram) // форма телеграма

            SpacerTextWithLine(headline = "Начало рабочего дня") // подпись перед формой

            var openTimeResult = timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            SpacerTextWithLine(headline = "Конец рабочего дня") // подпись перед формой

            var closeTimeResult = timePicker() // ВЫБОР ВРЕМЕНИ - Когда закрывается заведение

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_description)) // подпись перед формой

            var description = fieldDescriptionComponent(act = act) // ФОРМА ОПИСАНИЯ ЗАВЕДЕНИЯ

            Spacer(modifier = Modifier.height(30.dp)) // РАЗДЕЛИТЕЛЬ


            // -------------- КНОПКИ ОТМЕНА И ОПУБЛИКОВАТЬ ------------


            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------

            Button(

                onClick = {

                    // действие на нажатие

                    // --- ФУНКЦИЯ ПРОВЕРКИ НА ЗАПОЛНЕНИЕ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ ---------

                    val checkData = checkDataOnCreatePlace(
                        image1 = image1,
                        headline = headline,
                        phone = phone,
                        openTime = openTimeResult,
                        closeTime = closeTimeResult,
                        description = description,
                        category = category,
                        city = "Empty",//city,
                        address = address
                    )

                    if (checkData != 0) {

                        // если checkData вернет какое либо число, то это число будет ID сообщения в тосте

                        Toast.makeText(act, act.resources.getString(checkData), Toast.LENGTH_SHORT).show()

                    } else if (ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        // так же проверка, если нет разрешения на запись картинок в память, то запрос на эти права

                        ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 888)

                    } else {

                        // если все права есть и все обязательные поля заполнены

                        openLoading.value = true // открываем диалог загрузки

                        // запускаем корутину

                        GlobalScope.launch(Dispatchers.IO){

                            // запускаем сжатие изображения
                            val compressedImage = act.photoHelper.compressImage(act, image1!!)

                            // после сжатия запускаем функцию загрузки сжатого фота в Storage

                            act.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", PLACES_ROOT){


                                Log.d("MyLog", it)
                                // В качестве колбака придет ссылка на изображение в Storage

                                // Запускаем корутину и публикуем заведение

                                    GlobalScope.launch(Dispatchers.Main) {

                                        // заполняем заведение

                                        val filledPlace = PlacesAdsClass(

                                            logo = it,
                                            placeName = headline,
                                            placeDescription = description,
                                            phone = phone,
                                            whatsapp = whatsapp,
                                            telegram = TELEGRAM_URL + telegram,
                                            instagram = INSTAGRAM_URL + instagram,
                                            category = category,
                                            //city = city,
                                            address = address,
                                            placeKey = placesDatabaseManager.placeDatabase.push().key,
                                            owner = auth.uid,
                                            openTime = openTimeResult,
                                            closeTime = closeTimeResult

                                        )

                                        // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                        if (auth.uid != null) {

                                            // Если зарегистрирован, то запускаем функцию публикации заведения

                                            placesDatabaseManager.publishPlace(filledPlace) { result ->

                                                // в качестве колбака придет булин. Если опубликовано заведение то:

                                                if (result) {

                                                    // сбрасываем выбранную категорию, чтобы потом не отображался последний выбор категории
                                                    act.categoryDialog.chosenPlaceCategory = CategoriesList ("Выбери категорию", "Default")

                                                    // сбрасываем выбранный город, чтобы потом не отображался последний выбор города
                                                    act.chooseCityNavigation.chosenCity = CitiesList("Выбери город", "default_city")

                                                    navController.navigate(PLACES_ROOT) {popUpTo(0)} // переходим на страницу заведений

                                                    // показываем ТОСТ
                                                    Toast.makeText(
                                                        act,
                                                        "Твое заведение успешно опубликовано",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                } else {

                                                    // если произошла ошибка и заведение не опубликовалось то:

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
                onClick = { Toast.makeText(act, "СДЕЛАТЬ ДИАЛОГ - ДЕЙСТВИТЕЛЬНО ХОТИТЕ ВЫЙТИ?", Toast.LENGTH_SHORT).show() },
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
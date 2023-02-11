@file:Suppress("IMPLICIT_CAST_TO_ANY")

package kz.dvij.dvij_compose3.stockscreens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.firebase.StockDatabaseManager
import kz.dvij.dvij_compose3.functions.checkDataOnCreateStock
import kz.dvij.dvij_compose3.navigation.ChoosePlaceDialog
import kz.dvij.dvij_compose3.navigation.STOCK_ROOT
import kz.dvij.dvij_compose3.photohelper.chooseImageDesign
import kz.dvij.dvij_compose3.pickers.dataPicker
import kz.dvij.dvij_compose3.ui.theme.*

class CreateStock (val act: MainActivity) {

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО АКЦИЮ

    val choosePlaceDialog = ChoosePlaceDialog(act)


    // ---- АКЦИЯ ПО УМОЛЧАНИЮ -------

    val defaultStock = StockAdsClass (
        description = "Default"
    )

    private val stockDatabaseManager = StockDatabaseManager(act) // ИНИЦИАЛИЗИРОВАТЬ НУЖНО ИМЕННО ТАК, ИНАЧЕ НАЛ

    // ----- ЭКРАН СОЗДАНИЯ АКЦИИ --------

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun CreateStockScreen (navController: NavController, citiesList: MutableState<List<CitiesList>>) {

        val placesDatabaseManager = PlacesDatabaseManager(act = act)


        var openLoading = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ИДЕТ ЗАГРУЗКА
        val openCategoryDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог КАТЕГОРИИ
        val openCityDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ГОРОДА
        val openPlaceDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог ЗАВЕДЕНИЙ
        val openFieldPlace = remember { mutableStateOf(false) } // инициализируем переменную, открывающую формы ЗАВЕДЕНИЙ
        var headlinePlace = remember {mutableStateOf("")} // инициализируем переменную заголовка места, введенного вручную
        var addressPlace = remember {mutableStateOf("")} // инициализирууем переменную адреса места, введенного вручную

        var placeInfo = PlacesAdsClass (placeName = "Выбери заведение") // инициализируем ЗАВЕДЕНИЕ ПО УМОЛЧАНИЮ

        // Инициализируем переменную списка мест
        val placesList = remember {
            mutableStateOf(listOf<PlacesAdsClass>())
        }

        // Считываем список моих заведений
        placesDatabaseManager.readPlaceMyDataFromDb(placesList)

        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------

        // Инициализируем переменную списка категорий

        val categoriesList = remember {
            mutableStateOf(listOf<CategoriesList>())
        }

        // Запускаем функцию считывания списка категорий с базы данных

        act.categoryDialog.readStockCategoryDataFromDb(categoriesList)



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

            SpacerTextWithLine(headline = "Фото акции") // подпись перед формой

            val image1 = chooseImageDesign() // Изображение акции

            SpacerTextWithLine(headline = "Заголовок акции") // подпись перед формой

            val headline = fieldHeadlineComponent(act = act) // форма заголовка

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_category)) // подпись перед формой

            val category = act.categoryDialog.stockCategorySelectButton { openCategoryDialog.value = true }.categoryName.toString() // Кнопка выбора категории

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

            val city = act.chooseCityNavigation.citySelectButton {openCityDialog.value = true}.cityName.toString() // Кнопка выбора города


            // СДЕЛАТЬ ДИАЛОГ ВЫБОРА ЗАВЕДЕНИЯ


            // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

            if (openCategoryDialog.value) {
                act.categoryDialog.CategoryStockChooseDialog(categoriesList) {
                    openCategoryDialog.value = false
                }
            }

            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            if (openCityDialog.value) {
                act.chooseCityNavigation.CityChooseDialog(citiesList) {
                    openCityDialog.value = false
                }
            }

            SpacerTextWithLine(headline = "Дата начала акции") // подпись перед формой

            var startDay = dataPicker(act = act) // выбор даты начала

            SpacerTextWithLine(headline = "Дата завершения акции") // подпись перед формой

            var finishDay = dataPicker(act = act) // выбор даты завершения

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_description)) // подпись перед формой

            var description = fieldDescriptionComponent(act = act) // ФОРМА ОПИСАНИЯ Акции

            Spacer(modifier = Modifier.height(30.dp)) // РАЗДЕЛИТЕЛЬ



            // -------------- КНОПКИ ОТМЕНА И ОПУБЛИКОВАТЬ ------------


            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------

            Button(

                onClick = {

                    // действие на нажатие

                    // --- ФУНКЦИЯ ПРОВЕРКИ НА ЗАПОЛНЕНИЕ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ ---------

                    val checkData = checkDataOnCreateStock(
                        image1 = image1,
                        headline = headline,
                        startDay = startDay,
                        finishDay = finishDay,
                        description = description,
                        category = category,
                        city = city
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

                            act.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", STOCK_ROOT){


                                Log.d("MyLog", it)
                                // В качестве колбака придет ссылка на изображение в Storage

                                // Запускаем корутину и публикуем акцию

                                GlobalScope.launch(Dispatchers.Main) {

                                    // заполняем акцию

                                    val filledStock = StockAdsClass (

                                        image = it,
                                        headline = headline,
                                        description = description,
                                        category = category,
                                        keyStock = stockDatabaseManager.stockDatabase.push().key,
                                        keyPlace = placeInfo.placeKey ?: "Empty",
                                        keyCreator = auth.uid,
                                        city = city,
                                        startDate = startDay,
                                        finishDate = finishDay,
                                        inputHeadlinePlace = headlinePlace.value,
                                        inputAddressPlace = addressPlace.value

                                            )

                                    // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                    if (auth.uid != null) {

                                        // Если зарегистрирован, то запускаем функцию публикации акции

                                        stockDatabaseManager.publishStock(filledStock = filledStock) { result ->

                                            // в качестве колбака придет булин. Если опубликована акция то:

                                            if (result) {

                                                // сбрасываем выбранное заведение, чтобы потом не отображался последний выбор

                                                choosePlaceDialog.chosenPlace = PlacesAdsClass(placeName = "Выбери заведение")
                                                placeInfo = PlacesAdsClass (placeName = "Выбери заведение")

                                                // сбрасываем выбранную категорию, чтобы потом не отображался последний выбор категории
                                                act.categoryDialog.chosenStockCategory = CategoriesList ("Выбери категорию", "Default")

                                                // сбрасываем выбранный город, чтобы потом не отображался последний выбор города
                                                act.chooseCityNavigation.chosenCity = CitiesList("Выбери город", "default_city")

                                                navController.navigate(STOCK_ROOT) {popUpTo(0)} // переходим на страницу акций

                                                // показываем ТОСТ
                                                Toast.makeText(
                                                    act,
                                                    "Твоя акция успешно опубликована",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            } else {

                                                // если произошла ошибка и акция не опубликовалась то:

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
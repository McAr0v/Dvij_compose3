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
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.filters.FilterFunctions
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.functions.checkDataOnCreateStock
import kz.dvij.dvij_compose3.navigation.ChoosePlaceDialog
import kz.dvij.dvij_compose3.navigation.STOCK_ROOT
import kz.dvij.dvij_compose3.photohelper.chooseImageDesign
import kz.dvij.dvij_compose3.pickers.dataPicker
import kz.dvij.dvij_compose3.ui.theme.*

class CreateStock (val act: MainActivity) {

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО АКЦИЮ

    private val choosePlaceDialog = ChoosePlaceDialog(act)

    private val filterFunctions = FilterFunctions(act)

    // ----- ЭКРАН СОЗДАНИЯ АКЦИИ --------

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun CreateStockScreen (
        navController: NavController,
        citiesList: MutableState<List<CitiesList>>,
        filledUserInfo: UserInfoClass = UserInfoClass(),
        filledStock: StockAdsClass = StockAdsClass(

            image = "",
            headline = "",
            description = "",
            category = "",
            keyStock = "",
            keyPlace = "",
            keyCreator = "",
            city = "",
            startDate = "",
            finishDate = "",
            inputHeadlinePlace = "",
            inputAddressPlace = ""

        ),
        // Заполненое заведение, подаваемое извне. Если не передать, значения по умолчанию:
        filledPlace: PlacesCardClass = PlacesCardClass(
            placeName = "Выбери заведение",
            placeKey = ""
        ),

        // Тип страницы - редактирование или создание
        createOrEdit: String
    ) {

        val stockDatabaseManager = StockDatabaseManager(act = act)
        val placesDatabaseManager = PlacesDatabaseManager(act = act)

        // ------ ПЕРЕМЕННАЯ ДЛЯ ВЫБОРА ЗАВЕДЕНИЯ ---------

        // КЛЮЧ ЗАВЕДЕНИЯ ИЗ ПОДАННОГО ИЗВНЕ ЗАВЕДЕНИЯ
        var placeKey by rememberSaveable { mutableStateOf(filledPlace.placeKey) }

        // ДАННЫЕ ВЫБРАННОГО МЕСТА
        val chosenPlace = remember {mutableStateOf(filledPlace)}

        // ЗАГОЛОВОК ЗАВЕДЕНИЯ ВВЕДЕННОГО ВРУЧНУЮ ИЗ БД
        val headlinePlace = remember {mutableStateOf(filledStock.inputHeadlinePlace)}

        // АДРЕС ЗАВЕДЕНИЯ ВВЕДЕННОГО ВРУЧНУЮ ИЗ БД
        val addressPlace = remember {mutableStateOf(filledStock.inputAddressPlace)}

        // ПЕРЕКЛЮЧЕНИЕ ТИПА ЗАВЕДЕНИЯ - ИЗ СПИСКА ИЛИ НАПИСАТЬ АДРЕС ВРУЧНУЮ
        val changeTypePlace = remember {mutableStateOf(false)}

        // ЗАГОЛОВОК ЗАВЕДЕНИЯ, ПЕРЕДАВАЕМЫЙ ПРИ СОЗДАНИИ МЕРОПРИЯТИЯ
        var finishHeadlinePlace by rememberSaveable { mutableStateOf("") }

        // АДРЕС ЗАВЕДЕНИЯ, ПЕРЕДАВАЕМЫЙ ПРИ СОЗДАНИИ МЕРОПРИЯТИЯ
        var finishAddressPlace by rememberSaveable { mutableStateOf("") }

        // ПОКАЗАТЬ / СКРЫТЬ ФОРМЫ ДЛЯ ВВОДА ВРУЧНУЮ ЗАГОЛОВКА И АДРЕСА ЗАВЕДЕНИЯ
        val openFieldPlace = remember { mutableStateOf(false) }

        // ----- СПИСКИ -----

        // Список категорий
        val categoriesList = remember {mutableStateOf(listOf<CategoriesList>())}

        // Список мест
        val placesList = remember {
            mutableStateOf(listOf<PlacesCardClass>())
        }

        // --------- ПЕРЕМЕННЫЕ ДЛЯ ВЫБОРА КАТЕГОРИИ АКЦИИ ------------

        // КАТЕГОРИЯ АКЦИИ ПО УМОЛЧАНИЮ ПРИ СОЗДАНИИ
        val chosenStockCategoryCreate = remember {mutableStateOf("Выбери категорию")}

        // КАТЕГОРИЯ АКЦИИ ПРИШЕДШАЯ ИЗ БД
        val chosenStockCategoryEdit = remember {mutableStateOf(filledStock.category!!)}

        // КАТЕГОРИЯ АКЦИИ, ПЕРЕДАВАЕМАЯ В БД ПРИ СОЗДАНИИ МЕРОПРИЯТИЯ
        var category by rememberSaveable { mutableStateOf("Выбери категорию") }

        var startDay = ""
        var finishDay = ""


        // --- ПЕРЕМЕННЫЕ ДИАЛОГОВ ---

        val openLoading = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА
        val openCategoryDialog = remember { mutableStateOf(false) } // диалог КАТЕГОРИИ
        val openCityDialog = remember { mutableStateOf(false) } // диалог ГОРОДА
        val openPlaceDialog = remember { mutableStateOf(false) } // диалог ЗАВЕДЕНИЙ


        // --- ПЕРЕМЕННЫЕ ГОРОДА ---

        // Выбранный город из данных пользователя. Используется при создании
        val chosenCityCreateWithUser = remember {mutableStateOf(filledUserInfo.city!!)}

        // Значение города по умолчанию
        val chosenCityCreateWithoutUser = remember {mutableStateOf("Выбери город")}

        // Выбранный город из данных мероприятия. Используется при редактировании
        val chosenCityEdit = remember {mutableStateOf(filledStock.city!!)}

        // Переменная, передаваемая в БД
        var city by rememberSaveable { mutableStateOf("Выбери город") }


        // Считываем список моих заведений
        placesDatabaseManager.readPlaceMyDataFromDb(placesList)


        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------

        // Запускаем функцию считывания списка категорий с базы данных

        act.categoryDialog.readStockCategoryDataFromDb(categoriesList)

        // ------- СЧИТЫВАЕМ ПОЛНУЮ ИНФОРМАЦИЮ О ВЫБРАННОМ ЗАВЕДЕНИИ------

        // Если у выбранного места есть ключ, и он не равен ключу, ранее выбранного заведения, то...

        if (chosenPlace.value.placeKey != null && chosenPlace.value.placeKey != "" && chosenPlace.value.placeKey != "null" && chosenPlace.value.placeKey != placeKey ){

            // --- ЧИТАЕМ ДАННЫЕ О ЗАВЕДЕНИИ ---
            placesDatabaseManager.readOnePlaceFromDataBase(chosenPlace, chosenPlace.value.placeKey!!){

                //Если заведение считалось, указываем ключ выбранного заведения в отдельную переменную
                placeKey = chosenPlace.value.placeKey
            }

            // --- Закрываем формы для ввода заведения вручную
            openFieldPlace.value = false

        }

        // Если ключа у выбранного места нет, то указываем значение по умолчанию

        if (chosenPlace.value.placeKey == null || chosenPlace.value.placeKey == "" || chosenPlace.value.placeKey == "null"){

            // Доп условие, если выбранное место так же не имеет и названия.
            // ЕСЛИ ЕСТЬ НАЗВАНИЕ, ТО ТОГДА ЭТО ЗАВЕДЕНИЕ, ВВЕДЕННОЕ ВРУЧНУЮ!

            if (chosenPlace.value.placeName != ""){
                chosenPlace.value = PlacesCardClass(
                    placeName = "Выбери заведение",
                    placeKey = ""
                )
            }

            // --- Открываем формы для ввода заведения вручную
            openFieldPlace.value = true

        }

        var placeInfo = "Выбери заведение"



        // ---- СОДЕРЖИМОЕ СТРАНИЦЫ СОЗДАНИЯ ---------

        Column(
            modifier = Modifier
                .fillMaxSize() // занять весь размер экрана
                .background(Grey_Background) // цвет фона
                .verticalScroll(rememberScrollState()) // говорим, что колонка скролится вверх и вниз
                .padding(top = 0.dp, end = 20.dp, start = 20.dp, bottom = 20.dp) // паддинги
            ,
            verticalArrangement = Arrangement.Top, // выравнивание по вертикали
            horizontalAlignment = Alignment.Start // выравнивание по горизонтали
        ) {

            // ----- КАРТИНКА -----

            Text(
                text = "Фото акции",
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val image1 = if (filledStock.image != null && filledStock.image != "" && createOrEdit != "0"){
                // Если при редактировании есть картинка, подгружаем картинку
                chooseImageDesign(filledStock.image)
            } else {
                // Если нет - стандартный выбор картинки
                chooseImageDesign()
            }


            // ----- ЗАГОЛОВОК -----

            Text(
                text = "Заголовок",
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val headline = if (filledStock.headline != null && filledStock.headline != "" && createOrEdit != "0"){
                // Если при редактировании есть заголовок, заполняем его в форму
                fieldHeadlineComponent(filledStock.headline)
            } else {
                // Если нет - поле ввода пустое
                fieldHeadlineComponent() // форма заголовка
            }

            // --- ОПИСАНИЕ -----
            Text(
                text = stringResource(id = R.string.cm_description),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val description = if (filledStock.description != null && filledStock.description != "null" && filledStock.description != ""){

                fieldDescriptionComponent(description = filledStock.description) // ФОРМА ОПИСАНИЯ Акции

            } else {

                fieldDescriptionComponent() // ФОРМА ОПИСАНИЯ Акции

            }


            // ----- КАТЕГОРИЯ -----

            Text(
                text = stringResource(id = R.string.cm_category),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            category = if (filledStock.category != null && filledStock.category != "Выбери категорию" && filledStock.category != "" && createOrEdit != "0") {
                // Если при редактировании есть категория, передаем ее в кнопку
                act.categoryDialog.categorySelectButton(categoryName = chosenStockCategoryEdit) { openCategoryDialog.value = true }

            } else {
                // Если нет - передаем пустое значение
                act.categoryDialog.categorySelectButton (categoryName = chosenStockCategoryCreate) { openCategoryDialog.value = true }
            }

            // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

            if (openCategoryDialog.value) {

                // ЕСЛИ РЕДАКТИРОВАНИЕ
                if (createOrEdit != "0"){
                    // Передаем переменную, содержащую название категории из БД
                    act.categoryDialog.CategoryChooseDialog(categoryName = chosenStockCategoryEdit, categoriesList) {
                        openCategoryDialog.value = false
                    }

                } else { // Если создание

                    // Передаем переменную, в которую поместим категорию по умолчанию
                    act.categoryDialog.CategoryChooseDialog(categoryName = chosenStockCategoryCreate, categoriesList) {
                        openCategoryDialog.value = false
                    }
                }
            }

            // ----- ГОРОД -----

            Text(
                text = stringResource(id = R.string.city_with_star),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            // Если при редактировании в мероприятии есть город

            if (filledStock.city != null && filledStock.city != "Выбери город" && filledStock.city != "" && createOrEdit != "0") {

                // Передаем в кнопку выбора города ГОРОД ИЗ МЕРОПРИЯТИЯ ДЛЯ РЕДАКТИРОВАНИЯ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityEdit) {openCityDialog.value = true}

            } else if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && createOrEdit == "0") {

                // Если при создании мероприятия в пользователе есть город, передаем ГОРОД ИЗ БД ПОЛЬЗОВАТЕЛЯ ДЛЯ СОЗДАНИЯ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityCreateWithUser) {openCityDialog.value = true}

            } else {

                // В ОСТАЛЬНЫХ СЛУЧАЯХ - ПЕРЕДАЕМ ГОРОД ПО УМОЛЧАНИЮ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityCreateWithoutUser) {openCityDialog.value = true}

            }

            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            if (openCityDialog.value) {

                if (filledStock.city != null && filledStock.city != "Выбери город" && filledStock.city != "" && createOrEdit != "0"){

                    // Если при редактировании в мероприятии есть город, Передаем ГОРОД ИЗ МЕРОПРИЯТИЯ ДЛЯ РЕДАКТИРОВАНИЯ
                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityEdit, citiesList) {
                        openCityDialog.value = false
                    }

                } else if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && createOrEdit == "0"){

                    // Если при создании мероприятия в пользователе есть город, передаем ГОРОД ИЗ БД ПОЛЬЗОВАТЕЛЯ ДЛЯ СОЗДАНИЯ
                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityCreateWithUser, citiesList) {
                        openCityDialog.value = false
                    }

                } else {

                    // В ОСТАЛЬНЫХ СЛУЧАЯХ - ПЕРЕДАЕМ ГОРОД ПО УМОЛЧАНИЮ
                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityCreateWithoutUser, citiesList) {
                        openCityDialog.value = false
                    }
                }
            }



            // ----- ЗАВЕДЕНИЕ -----

            Text(
                text = "Место проведения*",
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            // --- КНОПКИ ВЫБОРА - ВЫБРАТЬ ЗАВЕДЕНИЕ ИЗ СПИСКА ИЛИ ВВВЕСТИ ВРУЧНУЮ ------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // ---- КНОПКА ВЫБОРА ЗАВЕДЕНИЯ ИЗ ДИАЛОГА --------

                placeInfo = choosePlaceDialog.placeSelectButton (chosenOutPlace = chosenPlace) {

                    // ДЕЙСТВИЯ НА НАЖАТИЕ НА КНОПКУ

                    openPlaceDialog.value = true // открываем диалог выбора заведения
                    openFieldPlace.value = false // Сбрасываем отображение форм адреса и названия заведения ВРУЧНУЮ, а так же цвета кнопки выбора вручную

                }.toString()

                Spacer(modifier = Modifier.width(10.dp))

                // --- КНОПКА ВКЛЮЧЕНИЯ ВВОДА АДРЕСА ВРУЧНУЮ --------

                Button(
                    onClick = {

                        // Если открыт диалог ввода заведения вручную
                        if (openFieldPlace.value){
                            openFieldPlace.value = false // закрываем диалог
                        } else {

                            // Если закрыт
                            openFieldPlace.value = true // Открываем)

                            // если выбираем ввести вручную, а уже выбрано заведение из списка
                            // то сбрасываем выбранное заведение из списка
                            chosenPlace.value = PlacesCardClass(placeName = "Выбери заведение", placeKey = "")
                        }
                    },

                    // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

                    border = BorderStroke(
                        width = if (!openFieldPlace.value) {
                            2.dp
                        } else {
                            0.dp
                        }, color = if (!openFieldPlace.value) {
                            Grey_ForCards
                        } else {
                            YellowDvij
                        }
                    ),

                    // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (!openFieldPlace.value) {
                            Grey_ForCards
                        } else {
                            YellowDvij
                        },
                        contentColor = if (!openFieldPlace.value) {
                            WhiteDvij
                        } else {
                            Grey_OnBackground
                        },
                    ),
                    shape = RoundedCornerShape(50) // скругленные углы кнопки
                ) {

                    Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

                    androidx.compose.material3.Text(
                        text = "Ввести адрес", // текст кнопки
                        style = Typography.labelMedium, // стиль текста
                        color = if (!openFieldPlace.value) {
                            WhiteDvij
                        } else {
                            Grey_OnBackground
                        }
                    )
                }
            }

            // --- КОНТЕНТ, ЕСЛИ МЫ ВЫБРАЛИ ВВЕСТИ АДРЕС И НАЗВАНИЕ ЗАВЕДЕНИЯ ВРУЧНУЮ ----

            if (openFieldPlace.value){

                Spacer(modifier = Modifier.height(10.dp))

                // --- ПОДЛОЖКА ПОД ФОРМЫ -----

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Grey_OnBackground, shape = RoundedCornerShape(15.dp))
                        .padding(top = 0.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)

                ) {
                    Text(
                        text = "Название места проведения",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )

                    // ЕСЛИ ИЗ МЕРОПРИЯТИЯ ПРИШЕЛ ВВЕДЕННЫЙ ЗАГОЛОВОК ЗАВЕДЕНИЯ

                    finishHeadlinePlace = if (headlinePlace.value != null && headlinePlace.value != "" && headlinePlace.value != "null" ) {

                        // Передаем заголовок в текстовое поле
                        fieldTextComponent("Введи название места", headlinePlace.value) // ТЕКСТОВОЕ ПОЛЕ НАЗВАНИЯ МЕСТА

                    } else {
                        // Если не пришел - показываем пустое поле
                        fieldTextComponent("Введи название места")
                    }

                    Text(
                        text = "Адрес места",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )

                    // ЕСЛИ ИЗ МЕРОПРИЯТИЯ ПРИШЕЛ ВВЕДЕННЫЙ АДРЕС ЗАВЕДЕНИЯ

                    finishAddressPlace = if (addressPlace.value != null && addressPlace.value != "" && addressPlace.value != "null" ) {

                        // Передаем адрес в текстовое поле
                        fieldTextComponent("Введи адрес места", addressPlace.value)

                    } else {
                        // Если не пришел - показываем пустое поле
                        fieldTextComponent("Введи адрес места") // ТЕКСТОВОЕ ПОЛЕ АДРЕСА МЕСТА

                    }
                }
            }


            // --- САМ ДИАЛОГ ВЫБОРА Заведения -----

            if (openPlaceDialog.value) {

                choosePlaceDialog.PlaceChooseDialog(placesList = placesList, chosenOutPlace = chosenPlace, ifChoose = changeTypePlace) {
                    // Функции при закрытии диалога
                    openFieldPlace.value = false // Закрываем отображение полей ввода вручную
                    openPlaceDialog.value = false // Закрываем сам вспылвающий диалог выбора заведений
                }
            }

            // --- ЕСЛИ ИЗ БД ПРИШЛИ ЗАГОЛОВОК И АДРЕС ЗАВЕДЕНИЯ,
            // НО ПОЛЬЗОВАТЕЛЬ ВЫБРАЛ ЗАВЕДЕНИЕ ИЗ СПИСКА

            if (changeTypePlace.value){
                finishHeadlinePlace = ""
                finishAddressPlace = ""
                headlinePlace.value = "" // Сбрасываем значения заголовка, введенного вручную
                addressPlace.value = "" // Сбрасываем значения адреса, введенного вручную
                !changeTypePlace.value // Говорим, что мы сбросили значения, теперь в них ничего нет
            }


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ){

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(0.5f)
                ) {

                    // ----- ДАТА НАЧАЛА АКЦИИ ---

                    Text(
                        text = "Начало акции",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )

                    startDay = if (filledStock.startDate != null && filledStock.startDate != "null" && filledStock.startDate != ""){

                        dataPicker(act = act, inputDate = filledStock.startDate) // выбор даты начала

                    } else {

                        dataPicker(act = act) // выбор даты начала

                    }

                }

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(0.5f)
                ){

                    // ----- ДАТА ЗАВЕРШЕНИЯ АКЦИИ ---

                    Text(
                        text = "Конец акции",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )

                    finishDay = if (filledStock.finishDate != null && filledStock.finishDate != "null" && filledStock.finishDate != "") {

                        dataPicker(act = act, inputDate = filledStock.finishDate) // выбор даты завершения

                    } else {

                        dataPicker(act = act) // выбор даты завершения

                    }

                }

            }


            Spacer(modifier = Modifier.height(40.dp)) // РАЗДЕЛИТЕЛЬ



            // -------------- КНОПКИ ОТМЕНА И ОПУБЛИКОВАТЬ ------------


            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------
            
            ButtonCustom(buttonText = "Опубликовать") {

                // ЕСЛИ В ЗАКОЛНЕННОЙ АКЦИИ ЕСТЬ КЛЮЧ ЗАВЕДЕНИЯ И В ЗАПОЛНЕННОЙ АКЦИИ ЕСТЬ КЛЮЧ АКЦИИ, ТО:
                // ps - не сработает, если будет создание

                if (filledStock.keyPlace != null && filledStock.keyPlace != "null" && filledStock.keyPlace != ""){

                    if (filledStock.keyStock != null && filledStock.keyStock != "null" && filledStock.keyStock != ""){

                        stockDatabaseManager.deleteStockFromPlace(filledStock.keyStock, filledStock.keyPlace){ deleted ->

                            if (deleted) {

                                Log.d ("MyLog", "Ключ был успешно удален")

                            }
                        }
                    }
                }

                val currentTime = System.currentTimeMillis()/1000 // инициализируем календарь //LocalTime.now().toNanoOfDay()

                // действие на нажатие

                // --- ФУНКЦИЯ ПРОВЕРКИ НА ЗАПОЛНЕНИЕ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ ---------

                Log.d ("MyLog", "${filledStock.image}")

                val checkData = checkDataOnCreateStock(
                    image1 = image1,
                    headline = headline,
                    startDay = startDay,
                    finishDay = finishDay,
                    description = description,
                    category = category,
                    city = city,
                    placeKey = chosenPlace.value.placeKey,
                    inputAddressPlace = finishAddressPlace,
                    inputHeadlinePlace = finishHeadlinePlace,
                    imageUriFromDb = filledStock.image ?: "Empty"
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

                        if (image1 == null && createOrEdit != "0"){

                            GlobalScope.launch(Dispatchers.Main) {

                                val filledStockForDb = StockAdsClass (

                                    image = filledStock.image,
                                    headline = headline,
                                    description = description,
                                    category = category,
                                    keyStock = filledStock.keyStock,
                                    keyPlace = chosenPlace.value.placeKey ?: "Empty",
                                    keyCreator = filledStock.keyCreator ?: "Empty",
                                    city = city,
                                    startDate = startDay,
                                    finishDate = finishDay,
                                    inputHeadlinePlace = finishHeadlinePlace,
                                    inputAddressPlace = finishAddressPlace,
                                    createTime = filledStock.createTime,
                                    startDateNumber = filterFunctions.getSplitDataFromDb(startDay),
                                    finishDateNumber = filterFunctions.getSplitDataFromDb(finishDay)

                                )

                                if (auth.uid != null) {

                                    // Если зарегистрирован, то запускаем функцию публикации акции

                                    stockDatabaseManager.publishStock(filledStock = filledStockForDb) { result ->

                                        // в качестве колбака придет булин. Если опубликована акция то:

                                        if (result) {

                                            startDay = ""
                                            finishDay = ""
                                            city = "Выбери город"
                                            category = "Выбери категорию"
                                            finishAddressPlace = ""
                                            finishHeadlinePlace = ""
                                            chosenStockCategoryCreate.value = "Выбери категорию"
                                            chosenCityCreateWithoutUser.value = "Выбери город"

                                            chosenPlace.value = PlacesCardClass(

                                                logo = "",
                                                placeKey = "",
                                                placeName = "Выбери заведение",
                                                placeDescription = "",
                                                phone = "",
                                                whatsapp = "",
                                                telegram = "",
                                                instagram = "",
                                                category = "",
                                                city = "",
                                                address = "",
                                                owner = "",
                                                mondayOpenTime = "",
                                                mondayCloseTime = "",
                                                tuesdayOpenTime = "",
                                                tuesdayCloseTime = "",
                                                wednesdayOpenTime = "",
                                                wednesdayCloseTime = "",
                                                thursdayOpenTime = "",
                                                thursdayCloseTime = "",
                                                fridayOpenTime = "",
                                                fridayCloseTime = "",
                                                saturdayOpenTime = "",
                                                saturdayCloseTime = "",
                                                sundayOpenTime = "",
                                                sundayCloseTime = ""

                                            )

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

                        } else {

                            // запускаем сжатие изображения
                            val compressedImage = act.photoHelper.compressImage(act, image1!!)

                            // после сжатия запускаем функцию загрузки сжатого фота в Storage

                            act.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", STOCK_ROOT){


                                if (filledStock.image != null && filledStock.image != ""){

                                    act.photoHelper.deleteStockImage(filledStock.image){

                                        if (it){

                                            Log.d("MyLog", "Старая картинка акции удалена")

                                        } else {

                                            Log.d("MyLog", "Старая картинка акции НЕ удалена")

                                        }

                                    }

                                }


                                // В качестве колбака придет ссылка на изображение в Storage

                                // Запускаем корутину и публикуем акцию

                                GlobalScope.launch(Dispatchers.Main) {

                                    // заполняем акцию

                                    val filledStockForDb = if (createOrEdit != "0"){

                                        StockAdsClass (

                                            image = it,
                                            headline = headline,
                                            description = description,
                                            category = category,
                                            keyStock = filledStock.keyStock,
                                            keyPlace = chosenPlace.value.placeKey ?: "Empty",
                                            keyCreator = filledStock.keyCreator,
                                            city = city,
                                            startDate = startDay,
                                            finishDate = finishDay,
                                            inputHeadlinePlace = finishHeadlinePlace,
                                            inputAddressPlace = finishAddressPlace,
                                            createTime = filledStock.createTime,
                                            startDateNumber = filterFunctions.getSplitDataFromDb(startDay),
                                            finishDateNumber = filterFunctions.getSplitDataFromDb(finishDay)

                                        )

                                    } else {

                                        StockAdsClass (

                                            image = it,
                                            headline = headline,
                                            description = description,
                                            category = category,
                                            keyStock = stockDatabaseManager.stockDatabase.push().key,
                                            keyPlace = chosenPlace.value.placeKey ?: "Empty",
                                            keyCreator = auth.uid,
                                            city = city,
                                            startDate = startDay,
                                            finishDate = finishDay,
                                            inputHeadlinePlace = finishHeadlinePlace,
                                            inputAddressPlace = finishAddressPlace,
                                            createTime = currentTime.toString(),
                                            startDateNumber = filterFunctions.getSplitDataFromDb(startDay),
                                            finishDateNumber = filterFunctions.getSplitDataFromDb(finishDay)

                                        )

                                    }


                                    // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                    if (auth.uid != null) {

                                        // Если зарегистрирован, то запускаем функцию публикации акции

                                        stockDatabaseManager.publishStock(filledStock = filledStockForDb) { result ->

                                            // в качестве колбака придет булин. Если опубликована акция то:

                                            if (result) {

                                                startDay = ""
                                                finishDay = ""
                                                city = "Выбери город"
                                                category = "Выбери категорию"
                                                finishAddressPlace = ""
                                                finishHeadlinePlace = ""
                                                chosenStockCategoryCreate.value = "Выбери категорию"
                                                chosenCityCreateWithoutUser.value = "Выбери город"

                                                chosenPlace.value = PlacesCardClass(

                                                    logo = "",
                                                    placeKey = "",
                                                    placeName = "Выбери заведение",
                                                    placeDescription = "",
                                                    phone = "",
                                                    whatsapp = "",
                                                    telegram = "",
                                                    instagram = "",
                                                    category = "",
                                                    city = "",
                                                    address = "",
                                                    owner = "",
                                                    mondayOpenTime = "",
                                                    mondayCloseTime = "",
                                                    tuesdayOpenTime = "",
                                                    tuesdayCloseTime = "",
                                                    wednesdayOpenTime = "",
                                                    wednesdayCloseTime = "",
                                                    thursdayOpenTime = "",
                                                    thursdayCloseTime = "",
                                                    fridayOpenTime = "",
                                                    fridayCloseTime = "",
                                                    saturdayOpenTime = "",
                                                    saturdayCloseTime = "",
                                                    sundayOpenTime = "",
                                                    sundayCloseTime = ""

                                                )

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
                }
                
            }

            Spacer(modifier = Modifier.height(20.dp))

            ButtonCustom(buttonText = stringResource(id = R.string.cansel_button), typeButton = ATTENTION, leftIcon = R.drawable.ic_close) {
                Toast.makeText(act, "СДЕЛАТЬ ДИАЛОГ - ДЕЙСТВИТЕЛЬНО ХОТИТЕ ВЫЙТИ?", Toast.LENGTH_SHORT).show()
            }
        }

        // --- ЭКРАН ИДЕТ ЗАГРУЗКА ----

        if (openLoading.value) {
            LoadingScreen(act.resources.getString(R.string.ss_loading))
        }
    }

}
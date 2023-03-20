package kz.dvij.dvij_compose3.meetingscreens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.filters.FilterFunctions
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.functions.checkDataOnCreateMeeting
import kz.dvij.dvij_compose3.navigation.ChoosePlaceDialog
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.photohelper.chooseImageDesign
import kz.dvij.dvij_compose3.ui.theme.*

class CreateMeeting(private val act: MainActivity) {

    // ------ КЛАСС СОЗДАНИЯ МЕРОПРИЯТИЯ ----------

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    private val choosePlaceDialog = ChoosePlaceDialog(act)

    private val filterFunctions = FilterFunctions(act)


    // ------- ЭКРАН СОЗДАНИЯ МЕРОПРИЯТИЯ ------------

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RememberReturnType")
    @Composable
    fun CreateMeetingScreen(
        navController: NavController,
        citiesList: MutableState<List<CitiesList>>, // список городов
        filledUserInfo: UserInfoClass = UserInfoClass(), // данные пользователя с БД
        // Заполненое мероприятие, подаваемое извне. Если не передать, значения по умолчанию:
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
            image1 = null,
            city = "Выбери город",
            instagram = "",
            telegram = "",
            placeKey = "",
            headlinePlaceInput = "",
            addressPlaceInput = "",
            ownerKey = ""
        ),
        // Заполненое заведение, подаваемое извне. Если не передать, значения по умолчанию:
        filledPlace: PlacesCardClass = PlacesCardClass(
            placeName = "Выбери заведение",
            placeKey = ""
        ),

        // Тип страницы - редактирование или создание
        createOrEdit: String
    ) {

        val activity = act
        val meetingDatabaseManager = MeetingDatabaseManager(activity) // инициализируем класс с функциями базы данных ИНИЦИАЛИЗИРОВАТЬ НУЖНО ИМЕННО ТАК, ИНАЧЕ НАЛ
        val placesDatabaseManager = PlacesDatabaseManager(act = activity) // инициализируем класс с функциями базы данных ИНИЦИАЛИЗИРОВАТЬ НУЖНО ИМЕННО ТАК, ИНАЧЕ НАЛ

        // КАЛЕНДАРЬ - https://www.geeksforgeeks.org/date-picker-in-android-using-jetpack-compose/
        // https://stackoverflow.com/questions/60417233/jetpack-compose-date-time-picker

        // ------- ПЕРЕМЕННЫЕ ДЛЯ ВВОДА НОМЕРА -----------

        // ПУСТОЙ ТЕЛЕФОН // Переменная, если нет телефона ни в пользователе, ни в мероприятии. ОБЫЧНО ТАК В РЕЖИМЕ СОЗДАНИЯ
        var phoneNumber by rememberSaveable { mutableStateOf("7") }

        // АВТОЗАПОЛНЕНИЕ ТЕЛЕФОНА ПРИ СОЗДАНИИ // Переменная, если есть номер в профиле пользователя. Применяется в режиме создания для АВТОЗАПОЛНЕНИЯ
        var userPhoneNumber by rememberSaveable { mutableStateOf(filledUserInfo.phoneNumber) }

        // ПРИ РЕДАКТИРОВАНИИ МЕРОПРИЯТИЯ // Переменная телефона для связи, пришедшая из БД
        var phoneNumberFromDb by rememberSaveable {mutableStateOf(filledMeeting.phone)}



        // ------ ПЕРЕМЕННЫЕ ДЛЯ ВВОДА WHATSAPP -------------

        // WHATSAPP ПУСТОЙ ТЕЛЕФОН // Переменная, если нет телефона ни в пользователе, ни в мероприятии. ОБЫЧНО ТАК В РЕЖИМЕ СОЗДАНИЯ
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") }

        // WHATSAPP АВТОЗАПОЛНЕНИЕ ТЕЛЕФОНА ПРИ СОЗДАНИИ // Переменная, если есть номер в профиле пользователя. Применяется в режиме создания для АВТОЗАПОЛНЕНИЯ
        var userWhatsappNumber by rememberSaveable { mutableStateOf(filledUserInfo.whatsapp) }

        // WHATSAPP ПРИ РЕДАКТИРОВАНИИ МЕРОПРИЯТИЯ // Переменная телефона для связи, пришедшая из БД
        var phoneNumberWhatsappFromDb by rememberSaveable {mutableStateOf(filledMeeting.whatsapp)}



        // ------ ПЕРЕМЕННАЯ ДЛЯ ВЫБОРА ЗАВЕДЕНИЯ ---------

        // КЛЮЧ ЗАВЕДЕНИЯ ИЗ ПОДАННОГО ИЗВНЕ ЗАВЕДЕНИЯ
        var placeKey by rememberSaveable { mutableStateOf(filledPlace.placeKey) }

        // ДАННЫЕ ВЫБРАННОГО МЕСТА
        val chosenPlace = remember {mutableStateOf(filledPlace)}

        // ЗАГОЛОВОК ЗАВЕДЕНИЯ ВВЕДЕННОГО ВРУЧНУЮ ИЗ БД
        val headlinePlace = remember {mutableStateOf(filledMeeting.headlinePlaceInput)}

        // АДРЕС ЗАВЕДЕНИЯ ВВЕДЕННОГО ВРУЧНУЮ ИЗ БД
        val addressPlace = remember {mutableStateOf(filledMeeting.addressPlaceInput)}

        // ПЕРЕКЛЮЧЕНИЕ ТИПА ЗАВЕДЕНИЯ - ИЗ СПИСКА ИЛИ НАПИСАТЬ АДРЕС ВРУЧНУЮ
        val changeTypePlace = remember {mutableStateOf(false)}

        // ЗАГОЛОВОК ЗАВЕДЕНИЯ, ПЕРЕДАВАЕМЫЙ ПРИ СОЗДАНИИ МЕРОПРИЯТИЯ
        var finishHeadlinePlace by rememberSaveable { mutableStateOf("") }

        // АДРЕС ЗАВЕДЕНИЯ, ПЕРЕДАВАЕМЫЙ ПРИ СОЗДАНИИ МЕРОПРИЯТИЯ
        var finishAddressPlace by rememberSaveable { mutableStateOf("") }

        // ПОКАЗАТЬ / СКРЫТЬ ФОРМЫ ДЛЯ ВВОДА ВРУЧНУЮ ЗАГОЛОВКА И АДРЕСА ЗАВЕДЕНИЯ
        val openFieldPlace = remember { mutableStateOf(false) }

        var placeInfo = "Выбери заведение"



        // --------- ПЕРЕМЕННЫЕ ДЛЯ ВЫБОРА КАТЕГОРИИ МЕРОПРИЯТИЯ ------------

        // КАТЕГОРИЯ МЕРОПРИЯТИЯ ПО УМОЛЧАНИЮ ПРИ СОЗДАНИИ
        val chosenMeetingCategoryCreate = remember {mutableStateOf("Выбери категорию")}

        // КАТЕГОРИЯ МЕРОПРИЯТИЯ ПРИШЕДШАЯ ИЗ БД
        val chosenMeetingCategoryEdit = remember {mutableStateOf(filledMeeting.category!!)}

        // КАТЕГОРИЯ МЕРОПРИЯТИЯ, ПЕРЕДАВАЕМАЯ В БД ПРИ СОЗДАНИИ МЕРОПРИЯТИЯ
        var category by rememberSaveable { mutableStateOf("Выбери категорию") }



        // --- ПЕРЕМЕННЫЕ ФОРМ -----

        var headline = "" // инициализируем заголовок
        var description = "" // инициализируем описание
        var price = "" // инициализируем цену
        var phone = "" // инициализируем телефон
        var whatsapp = "" // инициализируем whatsapp

        var dataResult = "" // инициализируем выбор даты

        val chosenDataResult = remember {mutableStateOf("")}


        var timeStartResult = "" // инициализируем выбор времени начала мероприятия
        var timeFinishResult = "" // инициализируем выбор времени конца мероприятия


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
        val chosenCityEdit = remember {mutableStateOf(filledMeeting.city!!)}

        // Переменная, передаваемая в БД
        var city by rememberSaveable { mutableStateOf("Выбери город") }


        // ----- СПИСКИ -----

        // Список категорий
        val categoriesList = remember {mutableStateOf(listOf<CategoriesList>())}

        // Список мест
        val placesList = remember {
            mutableStateOf(listOf<PlacesCardClass>())
        }


        // --- ФУНКЦИИ СЧИТЫВАНИЯ С БД ----

        // Запускаем функцию считывания списка категорий с базы данных
        act.categoryDialog.readMeetingCategoryDataFromDb(categoriesList)

        // Считываем список моих заведений
        placesDatabaseManager.readPlaceMyDataFromDb(placesList)



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




        // --------- САМ КОНТЕНТ СТРАНИЦЫ -----------------

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

            // ---- СОДЕРЖИМОЕ СТРАНИЦЫ СОЗДАНИЯ ---------


            // ---- Картинка ---

            Text(
                text = stringResource(id = R.string.cm_image),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val image1 = if (filledMeeting.image1 != null && filledMeeting.image1 != "" && createOrEdit != "0"){
                // Если при редактировании есть картинка, подгружаем картинку
                chooseImageDesign(filledMeeting.image1)
            } else {
                // Если нет - стандартный выбор картинки
                chooseImageDesign()
            }


            // --- Заголовок ----

            Text(
                text = stringResource(id = R.string.cm_headline),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            headline = if (filledMeeting.headline != null && filledMeeting.headline != "" && createOrEdit != "0"){
                // Если при редактировании есть заголовок, заполняем его в форму
                fieldHeadlineComponent(filledMeeting.headline)
            } else {
                // Если нет - поле ввода пустое
                fieldHeadlineComponent() // форма заголовка
            }

            // ---- Описание ------

            Text(
                text = stringResource(id = R.string.cm_description),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            description = if (filledMeeting.description != "" && filledMeeting.description != null && createOrEdit != "0"){

                // Если при редактировании есть описание, передаем его
                fieldDescriptionComponent(filledMeeting.description) // ФОРМА ОПИСАНИЯ МЕРОПРИЯТИЯ

            } else {

                // Если нет - пустое поле
                fieldDescriptionComponent()

            }

            // ---- ЦЕНА ------

            Text(
                text = stringResource(id = R.string.cm_price),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            price = if (filledMeeting.price != "" && filledMeeting.price != null && createOrEdit != "0") {

                // Если при редактировании есть цена, передаем ее
                fieldPriceComponent(filledMeeting.price)

            } else {

                // Если нет - пустое поле
                fieldPriceComponent() // Форма цены за билет

            }


            // --- Категория мероприятия ----

            Text(
                text = stringResource(id = R.string.cm_category),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            if (filledMeeting.category != null && filledMeeting.category != "Выбери категорию" && filledMeeting.category != "" && createOrEdit != "0") {
                // Если при редактировании есть категория, передаем ее в кнопку
                category = act.categoryDialog.categorySelectButton(categoryName = chosenMeetingCategoryEdit) { openCategoryDialog.value = true }

            } else {
                // Если нет - передаем пустое значение
                category = act.categoryDialog.categorySelectButton (categoryName = chosenMeetingCategoryCreate) { openCategoryDialog.value = true }
        }

            // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

            if (openCategoryDialog.value) {

                // ЕСЛИ РЕДАКТИРОВАНИЕ
                if (createOrEdit != "0"){
                    // Передаем переменную, содержащую название категории из БД
                    act.categoryDialog.CategoryChooseDialog(categoryName = chosenMeetingCategoryEdit, categoriesList) {
                        openCategoryDialog.value = false
                    }

                } else { // Если создание

                    // Передаем переменную, в которую поместим категорию по умолчанию
                    act.categoryDialog.CategoryChooseDialog(categoryName = chosenMeetingCategoryCreate, categoriesList) {
                        openCategoryDialog.value = false
                    }
                }
            }

            // ------- ГОРОД ---------

            Text(
                text = stringResource(id = R.string.city_with_star),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )


            // Если при редактировании в мероприятии есть город

            if (filledMeeting.city != null && filledMeeting.city != "Выбери город" && filledMeeting.city != "" && createOrEdit != "0") {

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

                if (filledMeeting.city != null && filledMeeting.city != "Выбери город" && filledMeeting.city != "" && createOrEdit != "0"){

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


            // --- ВЫБОР ЗАВЕДЕНИЯ -----

            Text(
                text = "Заведение*",
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
                        .padding(top = 0.dp, start = 10.dp, end = 10.dp, bottom = 20.dp)

                ) {
                    // --- ВЫБОР ЗАВЕДЕНИЯ -----

                    Text(
                        text = "Название места проведения*",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )

                    // ЕСЛИ ИЗ МЕРОПРИЯТИЯ ПРИШЕЛ ВВЕДЕННЫЙ ЗАГОЛОВОК ЗАВЕДЕНИЯ

                    finishHeadlinePlace = if (headlinePlace.value != null && headlinePlace.value != "" && headlinePlace.value != "null" ) {

                        // Передаем заголовок в текстовое поле
                        fieldTextComponent("Напиши название места", headlinePlace.value) // ТЕКСТОВОЕ ПОЛЕ НАЗВАНИЯ МЕСТА

                    } else {
                        // Если не пришел - показываем пустое поле
                        fieldTextComponent("Напиши название места")
                    }

                    Text(
                        text = "Адрес места проведения*",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )

                    // ЕСЛИ ИЗ МЕРОПРИЯТИЯ ПРИШЕЛ ВВЕДЕННЫЙ АДРЕС ЗАВЕДЕНИЯ

                    finishAddressPlace = if (addressPlace.value != null && addressPlace.value != "" && addressPlace.value != "null" ) {

                        // Передаем адрес в текстовое поле
                        fieldTextComponent("Напиши адрес места проведения", addressPlace.value)

                    } else {
                        // Если не пришел - показываем пустое поле
                        fieldTextComponent("Напиши адрес места проведения") // ТЕКСТОВОЕ ПОЛЕ АДРЕСА МЕСТА

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
                headlinePlace.value = "" // Сбрасываем значения заголовка, введенного вручную
                addressPlace.value = "" // Сбрасываем значения адреса, введенного вручную
                !changeTypePlace.value // Говорим, что мы сбросили значения, теперь в них ничего нет
            }


            // ---- ДАТА ------

            Text(
                text = stringResource(id = R.string.cm_date),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            dataResult = if (filledMeeting.data != "" && filledMeeting.data != null && createOrEdit != "0"){

                dataPicker(act, filledMeeting.data, chosenDataResult) // Если есть данные о мероприятии , передаем дату из мероприятия

            } else {

                dataPicker(act, chosenDate = chosenDataResult) // Если есть данные о мероприятии , передаем дату из мероприятия

            }



            // ---- ВРЕМЯ НАЧАЛА ------

            Text(
                text = stringResource(id = R.string.cm_start_time),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )


            timeStartResult = if (filledMeeting.startTime != "" && filledMeeting.startTime != null && createOrEdit != "0"){

                timePicker(filledMeeting.startTime) // Если есть данные о мероприятии , передаем время из мероприятия

            } else {

                timePicker()

            }




            // ---- ВРЕМЯ КОНЦА ------

            Text(
                text = stringResource(id = R.string.cm_finish_time),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )


            timeFinishResult = if (filledMeeting.finishTime != "" && filledMeeting.finishTime != null && createOrEdit != "0"){

                timePicker(filledMeeting.finishTime) // Если есть данные о мероприятии , передаем время из мероприятия

            } else {

                timePicker()

            }



            // --- ТЕЛЕФОН -----

            Text(
                text = stringResource(id = R.string.cm_phone),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )


            phone = if (phoneNumberFromDb != "+7" && phoneNumberFromDb != "+77" && phoneNumberFromDb != "" && phoneNumberFromDb != null && createOrEdit != "0"){

                // Если при редактировании у мероприятия есть телефон, передаем ПЕРЕМЕННУЮ НОМЕРА С БД
                fieldPhoneComponent(phoneNumberFromDb!!, onPhoneChanged = { phoneNumberFromDb = it })

            } else if (filledUserInfo.phoneNumber != "+7" && filledUserInfo.phoneNumber != "+77" && filledUserInfo.phoneNumber != "" && filledUserInfo.phoneNumber != null && createOrEdit == "0") {

                // Если при создании у пользователя есть телефон, передаем ПЕРЕМЕННУЮ НОМЕРА ПОЛЬЗОВАТЕЛЯ
                fieldPhoneComponent(userPhoneNumber!!, onPhoneChanged = { userPhoneNumber = it })

            } else {

                // Во всех остальных случаях передаем переменную пустого номера
                fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it })
            }



            // --- WHATSAPP ----

            Text(
                text = stringResource(id = R.string.cm_whatsapp),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )


            whatsapp = if (phoneNumberWhatsappFromDb != null && phoneNumberWhatsappFromDb != "+7" && phoneNumberWhatsappFromDb != "+77" && phoneNumberWhatsappFromDb != "" && createOrEdit != "0"){

                // Если при редактировании у мероприятия есть whatsapp, передаем ПЕРЕМЕННУЮ WHATSAPP С БД
                fieldPhoneComponent(phoneNumberWhatsappFromDb!!, onPhoneChanged = { phoneNumberWhatsappFromDb = it }, icon = painterResource(id = R.drawable.whatsapp))

            } else if (userWhatsappNumber != "+7" && userWhatsappNumber != "+77" && userWhatsappNumber != "" && userWhatsappNumber != null && createOrEdit == "0") {

                // Если при создании у пользователя есть whatsapp, передаем ПЕРЕМЕННУЮ WHATSAPP ПОЛЬЗОВАТЕЛЯ
                fieldPhoneComponent(userWhatsappNumber!!, onPhoneChanged = { userWhatsappNumber = it }, icon = painterResource(id = R.drawable.whatsapp))

            } else {

                // Во всех остальных случаях передаем переменную пустого whatsapp
                fieldPhoneComponent(phoneNumberWhatsapp, onPhoneChanged = { phoneNumberWhatsapp = it }, icon = painterResource(id = R.drawable.whatsapp))

            }


            // ---- INSTAGRAM -----

            Text(
                text = stringResource(id = R.string.social_instagram),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )


            val instagram = if (filledMeeting.instagram != "" && filledMeeting.instagram != null && createOrEdit != "0") {

                // Если при редактировании у мероприятия есть инстаграм, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledMeeting.instagram)

            } else if (filledUserInfo.instagram != "" && filledUserInfo.instagram != null && createOrEdit == "0") {

                // Если при создании у пользователя есть инстаграм, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledUserInfo.instagram)

            } else {

                // Во всех остальных случаях ничего не передаем
                fieldInstagramComponent(act = act, icon = R.drawable.instagram)

            }


            // ---- TELEGRAM -----

            Text(
                text = stringResource(id = R.string.social_telegram),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val telegram = if (filledMeeting.telegram != "" && filledMeeting.telegram != null && createOrEdit != "0") {

                // Если при редактировании у мероприятия есть telegram, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledMeeting.telegram) // форма телеграма

            } else if (filledUserInfo.telegram != "" && filledUserInfo.telegram != null && createOrEdit == "0") {

                // Если при создании у пользователя есть telegram, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.telegram, inputText = filledUserInfo.telegram) // форма телеграма

            } else {

                // Во всех остальных случаях ничего не передаем
                fieldInstagramComponent(act = act, icon = R.drawable.telegram)

            }

            Spacer(modifier = Modifier.height(30.dp)) // РАЗДЕЛИТЕЛЬ


            // -------------- КНОПКИ ОТМЕНА И ОПУБЛИКОВАТЬ ------------


            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------


            ButtonCustom(buttonText = "Опубликовать") {

                // ЕСЛИ В ЗАКОЛНЕННОМ МЕРОПРИЯТИИ ЕСТЬ КЛЮЧ ЗАВЕДЕНИЯ И В ЗАПОЛНЕННОМ МЕРОПРИЯТИИ ЕСТЬ КЛЮЧ МЕРОПРИЯТИЯ, ТО:
                // ps - не сработает, если будет создание

                if (filledMeeting.placeKey != null && filledMeeting.placeKey != "null" && filledMeeting.placeKey != ""){

                    if (filledMeeting.key != null && filledMeeting.key != "null" && filledMeeting.key != ""){

                        meetingDatabaseManager.deleteMeetingFromPlace(filledMeeting.key, filledMeeting.placeKey){ deleted ->

                            if (deleted) {

                                Log.d ("MyLog", "Ключ был успешно удален")

                            }
                        }
                    }
                }

                val currentTime = System.currentTimeMillis()/1000 //calendar.timeInMillis // инициализируем календарь //LocalTime.now().toNanoOfDay()


                // действие на нажатие

                // --- ФУНКЦИЯ ПРОВЕРКИ НА ЗАПОЛНЕНИЕ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ ---------

                val checkData = checkDataOnCreateMeeting(
                    image1,
                    headline,
                    phone,
                    dataResult,
                    timeStartResult,
                    description,
                    category,
                    city,
                    chosenPlace.value.placeKey,
                    finishHeadlinePlace,
                    finishAddressPlace,
                    filledMeeting.image1 ?: "",
                )

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

                        // Если это страница редактирования

                        if (createOrEdit != "0"){

                            // Если пользователь не загрузил новое изображение

                            if (image1 == null){

                                GlobalScope.launch(Dispatchers.Main) {

                                    // заполняем мероприятие

                                    val finishFilledMeeting = MeetingsAdsClass(
                                        key = filledMeeting.key,
                                        category = category,
                                        headline = headline,
                                        description = description,
                                        price = price,
                                        phone = phone,
                                        whatsapp = whatsapp,
                                        data = dataResult,
                                        startTime = timeStartResult,
                                        finishTime = timeFinishResult,
                                        image1 = filledMeeting.image1,
                                        city = city,
                                        instagram = instagram,
                                        telegram = telegram,
                                        placeKey = chosenPlace.value.placeKey ?: "",
                                        headlinePlaceInput = finishHeadlinePlace,
                                        addressPlaceInput = finishAddressPlace,
                                        ownerKey = act.mAuth.uid,
                                        createdTime = filledMeeting.createdTime,
                                        dateInNumber = filterFunctions.getSplitDataFromDb(dataResult)
                                    )

                                    // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                    if (auth.uid != null) {

                                        // Если зарегистрирован, то запускаем функцию публикации мероприятия

                                        meetingDatabaseManager.publishMeeting(finishFilledMeeting){ result ->

                                            // в качестве колбака придет булин. Если опубликовано мероприятие то:

                                            if (result){

                                                dataResult = ""
                                                timeStartResult = ""
                                                timeFinishResult = ""
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

                                                navController.navigate(MEETINGS_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                                                // показываем ТОСТ
                                                Toast.makeText(
                                                    activity,
                                                    "Твое мероприятие успешно отредактировано",
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

                            } else {

                                // Если при редактировании пользователь загрузил фото

                                if (filledMeeting.image1 != null && filledMeeting.image1 != ""){

                                    act.photoHelper.deleteMeetingImage(filledMeeting.image1){

                                        if (it){

                                            Log.d ("MyLog", "Старая картинка при редактировании удалена")

                                        } else {

                                            Log.d ("MyLog", "Старая картинка при редактировании почемуто не удалилась")

                                        }

                                    }

                                }

                                // запускаем сжатие изображения
                                val compressedImage = activity.photoHelper.compressImage(activity, image1)

                                // после сжатия запускаем функцию загрузки сжатого фото в Storage

                                activity.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", MEETINGS_ROOT){

                                    // В качестве колбака придет ссылка на изображение в Storage

                                    // Запускаем корутину и публикуем мероприятие

                                    GlobalScope.launch(Dispatchers.Main) {

                                        // заполняем мероприятие

                                        val finishFilledMeeting = MeetingsAdsClass(
                                            key = filledMeeting.key,
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
                                            placeKey = chosenPlace.value.placeKey ?: "",
                                            headlinePlaceInput = finishHeadlinePlace,
                                            addressPlaceInput = finishAddressPlace,
                                            ownerKey = act.mAuth.uid,
                                            createdTime = filledMeeting.createdTime,
                                            dateInNumber = filterFunctions.getSplitDataFromDb(dataResult)
                                        )

                                        // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                        if (auth.uid != null) {

                                            // Если зарегистрирован, то запускаем функцию публикации мероприятия

                                            meetingDatabaseManager.publishMeeting(finishFilledMeeting){ result ->

                                                // в качестве колбака придет булин. Если опубликовано мероприятие то:

                                                if (result){

                                                    navController.navigate(MEETINGS_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                                                    // показываем ТОСТ

                                                    Toast.makeText(
                                                        activity,
                                                        "Твое мероприятие успешно отредактировано",
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

                        } else if (createOrEdit == "0"){

                            // если страница создания

                            // запускаем сжатие изображения
                            val compressedImage = activity.photoHelper.compressImage(activity, image1!!)

                            // после сжатия запускаем функцию загрузки сжатого фото в Storage

                            activity.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", MEETINGS_ROOT){

                                // В качестве колбака придет ссылка на изображение в Storage

                                // Запускаем корутину и публикуем мероприятие

                                GlobalScope.launch(Dispatchers.Main) {

                                    // заполняем мероприятие

                                    // Если это страница создания
                                    val finishFilledMeeting = MeetingsAdsClass(
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
                                        placeKey = chosenPlace.value.placeKey ?: "",
                                        headlinePlaceInput = finishHeadlinePlace,
                                        addressPlaceInput = finishAddressPlace,
                                        ownerKey = act.mAuth.uid,
                                        createdTime = currentTime.toString(),
                                        dateInNumber = filterFunctions.getSplitDataFromDb(dataResult)
                                    )

                                    // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                    if (auth.uid != null) {

                                        // Если зарегистрирован, то запускаем функцию публикации мероприятия

                                        meetingDatabaseManager.publishMeeting(finishFilledMeeting){ result ->

                                            // в качестве колбака придет булин. Если опубликовано мероприятие то:

                                            if (result){

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
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ButtonCustom(buttonText = stringResource(id = R.string.cansel_button), typeButton = ATTENTION, leftIcon = R.drawable.ic_close) {
                Toast.makeText(activity, "СДЕЛАТЬ ДИАЛОГ - ДЕЙСТВИТЕЛЬНО ХОТИТЕ ВЫЙТИ?", Toast.LENGTH_SHORT).show()
            }
        }

        // --- ЭКРАН ИДЕТ ЗАГРУЗКА ----

        if (openLoading.value) {
            LoadingScreen(act.resources.getString(R.string.ss_loading))
        }
    }

}
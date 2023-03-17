package kz.dvij.dvij_compose3.placescreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesCardClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.firebase.UserInfoClass
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
    fun CreatePlaceScreen (
        navController: NavController,
        citiesList: MutableState<List<CitiesList>>, // список городов
        filledUserInfo: UserInfoClass = UserInfoClass(), // данные пользователя с БД
        // Заполненое заведение, подаваемое извне. Если не передать, значения по умолчанию:
        filledPlace: PlacesCardClass = PlacesCardClass(
            placeName = "",
            placeKey = "",
            logo = "",
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
            sundayCloseTime = "",
            createPlaceTime = ""
        ),

        // Тип страницы - редактирование или создание
        createOrEdit: String
    ) {

        // ------- ПЕРЕМЕННЫЕ ДЛЯ ВВОДА НОМЕРА -----------

        // ПУСТОЙ ТЕЛЕФОН // Переменная, если нет телефона ни в пользователе, ни в заведении. ОБЫЧНО ТАК В РЕЖИМЕ СОЗДАНИЯ
        var phoneNumber by rememberSaveable { mutableStateOf("7") }

        // АВТОЗАПОЛНЕНИЕ ТЕЛЕФОНА ПРИ СОЗДАНИИ // Переменная, если есть номер в профиле пользователя. Применяется в режиме создания для АВТОЗАПОЛНЕНИЯ
        var userPhoneNumber by rememberSaveable { mutableStateOf(filledUserInfo.phoneNumber) }

        // ПРИ РЕДАКТИРОВАНИИ ЗАВЕДЕНИЯ // Переменная телефона для связи, пришедшая из БД
        var phoneNumberFromDb by rememberSaveable {mutableStateOf(filledPlace.phone)}



        // ------ ПЕРЕМЕННЫЕ ДЛЯ ВВОДА WHATSAPP -------------

        // WHATSAPP ПУСТОЙ ТЕЛЕФОН // Переменная, если нет телефона ни в пользователе, ни в заведении. ОБЫЧНО ТАК В РЕЖИМЕ СОЗДАНИЯ
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") }

        // WHATSAPP АВТОЗАПОЛНЕНИЕ ТЕЛЕФОНА ПРИ СОЗДАНИИ // Переменная, если есть номер в профиле пользователя. Применяется в режиме создания для АВТОЗАПОЛНЕНИЯ
        var userWhatsappNumber by rememberSaveable { mutableStateOf(filledUserInfo.whatsapp) }

        // WHATSAPP ПРИ РЕДАКТИРОВАНИИ ЗАВЕДЕНИЯ // Переменная телефона для связи, пришедшая из БД
        var phoneNumberWhatsappFromDb by rememberSaveable {mutableStateOf(filledPlace.whatsapp)}


        // --------- ПЕРЕМЕННЫЕ ДЛЯ ВЫБОРА КАТЕГОРИИ ЗАВЕДЕНИЙ ------------

        // КАТЕГОРИЯ ЗАВЕДЕНИЯ ПО УМОЛЧАНИЮ ПРИ СОЗДАНИИ
        val chosenPlaceCategoryCreate = remember {mutableStateOf("Выбери категорию")}

        // КАТЕГОРИЯ ЗАВЕДЕНИЯ ПРИШЕДШАЯ ИЗ БД
        val chosenPlaceCategoryEdit = remember {mutableStateOf(filledPlace.category!!)}

        // КАТЕГОРИЯ ЗАВЕДЕНИЯ, ПЕРЕДАВАЕМАЯ В БД ПРИ ПУБЛИКАЦИИ
        var category by rememberSaveable { mutableStateOf("Выбери категорию") }



        // --- ПЕРЕМЕННЫЕ ДИАЛОГОВ ---

        val openLoading = remember {mutableStateOf(false)} // диалог ИДЕТ ЗАГРУЗКА
        val openCategoryDialog = remember { mutableStateOf(false) } // диалог КАТЕГОРИИ
        val openCityDialog = remember { mutableStateOf(false) } // диалог ГОРОДА

        // --- ПЕРЕМЕННЫЕ ГОРОДА ---

        // Выбранный город из данных пользователя. Используется при создании
        val chosenCityCreateWithUser = remember {mutableStateOf(filledUserInfo.city!!)}

        // Значение города по умолчанию
        val chosenCityCreateWithoutUser = remember {mutableStateOf("Выбери город")}

        // Выбранный город из данных заведения. Используется при редактировании
        val chosenCityEdit = remember {mutableStateOf(filledPlace.city!!)}

        // Переменная, передаваемая в БД
        var city by rememberSaveable { mutableStateOf("Выбери город") }



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
                .background(Grey_Background) // цвет фона
                .verticalScroll(rememberScrollState()) // говорим, что колонка скролится вверх и вниз
                .padding(top = 0.dp, end = 20.dp, start = 20.dp, bottom = 20.dp) // паддинги
            ,
            verticalArrangement = Arrangement.Top, // выравнивание по вертикали
            horizontalAlignment = Alignment.Start // выравнивание по горизонтали
        ) {

            // ----- ЛОГОТИП ---------

            Text(
                text = "Логотип заведения",
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val image1 = if (filledPlace.logo != null && filledPlace.logo != "" && createOrEdit != "0"){

                // Если при редактировании есть картинка, подгружаем картинку
                chooseImageDesign(filledPlace.logo)

            } else {

                // Если нет - стандартный выбор картинки
                chooseImageDesign()

            }


            // ---- НАЗВАНИЕ ЗАВЕДЕНИЯ -------

            Text(
                text = "Название заведения",
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val headline = if (filledPlace.placeName != null && filledPlace.placeName != "" && createOrEdit != "0"){
                // Если при редактировании есть заголовок, заполняем его в форму
                fieldHeadlineComponent(filledPlace.placeName)
            } else {
                // Если нет - поле ввода пустое
                fieldHeadlineComponent() // форма заголовка
            }


            // --- КАТЕГОРИЯ ЗАВЕДЕНИЯ ------

            Text(
                text = stringResource(id = R.string.cm_category),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            if (filledPlace.category != null && filledPlace.category != "Выбери категорию" && filledPlace.category != "" && createOrEdit != "0") {
                // Если при редактировании есть категория, передаем ее в кнопку
                category = act.categoryDialog.categorySelectButton(categoryName = chosenPlaceCategoryEdit) { openCategoryDialog.value = true }

            } else {
                // Если нет - передаем пустое значение
                category = act.categoryDialog.categorySelectButton (categoryName = chosenPlaceCategoryCreate) { openCategoryDialog.value = true }
            }

            // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

            if (openCategoryDialog.value) {

                // ЕСЛИ РЕДАКТИРОВАНИЕ
                if (createOrEdit != "0"){
                    // Передаем переменную, содержащую название категории из БД
                    act.categoryDialog.CategoryChooseDialog(categoryName = chosenPlaceCategoryEdit, categoriesList) {
                        openCategoryDialog.value = false
                    }

                } else { // Если создание

                    // Передаем переменную, в которую поместим категорию по умолчанию
                    act.categoryDialog.CategoryChooseDialog(categoryName = chosenPlaceCategoryCreate, categoriesList) {
                        openCategoryDialog.value = false
                    }
                }
            }


            // ---- ГОРОД ------

            Text(
                text = stringResource(id = R.string.city_with_star),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            // Если при редактировании в заведении есть город

            if (filledPlace.city != null && filledPlace.city != "Выбери город" && filledPlace.city != "" && createOrEdit != "0") {

                // Передаем в кнопку выбора города ГОРОД ИЗ ЗАВЕДЕНИЯ ДЛЯ РЕДАКТИРОВАНИЯ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityEdit) {openCityDialog.value = true}

            } else if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && createOrEdit == "0") {

                // Если при создании заведения в пользователе есть город, передаем ГОРОД ИЗ БД ПОЛЬЗОВАТЕЛЯ ДЛЯ СОЗДАНИЯ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityCreateWithUser) {openCityDialog.value = true}

            } else {

                // В ОСТАЛЬНЫХ СЛУЧАЯХ - ПЕРЕДАЕМ ГОРОД ПО УМОЛЧАНИЮ
                city = act.chooseCityNavigation.citySelectButton(cityName = chosenCityCreateWithoutUser) {openCityDialog.value = true}

            }

            // --- САМ ДИАЛОГ ВЫБОРА ГОРОДА -----

            if (openCityDialog.value) {

                if (filledPlace.city != null && filledPlace.city != "Выбери город" && filledPlace.city != "" && createOrEdit != "0"){

                    // Если при редактировании в заведении есть город, Передаем ГОРОД ИЗ ЗАВЕДЕНИЯ ДЛЯ РЕДАКТИРОВАНИЯ
                    act.chooseCityNavigation.CityChooseDialog(cityName = chosenCityEdit, citiesList) {
                        openCityDialog.value = false
                    }

                } else if (filledUserInfo.city != null && filledUserInfo.city != "Выбери город" && filledUserInfo.city != "" && createOrEdit == "0"){

                    // Если при создании заведения в пользователе есть город, передаем ГОРОД ИЗ БД ПОЛЬЗОВАТЕЛЯ ДЛЯ СОЗДАНИЯ
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



            // ----- АДРЕС -----

            Text(
                text = "Адрес",
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val address = if (filledPlace.address != null && filledPlace.address != "" && createOrEdit != "0"){
                // Если при редактировании есть заголовок, заполняем его в форму
                fieldHeadlineComponent(filledPlace.address)
            } else {
                // Если нет - поле ввода пустое
                fieldHeadlineComponent() // форма заголовка
            }


            // ---- ТЕЛЕФОН ------

            Text(
                text = stringResource(id = R.string.cm_phone),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val phone = if (phoneNumberFromDb != "+7" && phoneNumberFromDb != "+77" && phoneNumberFromDb != "" && phoneNumberFromDb != null && createOrEdit != "0"){

                // Если при редактировании у заведения есть телефон, передаем ПЕРЕМЕННУЮ НОМЕРА С БД
                fieldPhoneComponent(phoneNumberFromDb!!, onPhoneChanged = { phoneNumberFromDb = it })

            } else if (filledUserInfo.phoneNumber != "+7" && filledUserInfo.phoneNumber != "+77" && filledUserInfo.phoneNumber != "" && filledUserInfo.phoneNumber != null && createOrEdit == "0") {

                // Если при создании у пользователя есть телефон, передаем ПЕРЕМЕННУЮ НОМЕРА ПОЛЬЗОВАТЕЛЯ
                fieldPhoneComponent(userPhoneNumber!!, onPhoneChanged = { userPhoneNumber = it })

            } else {

                // Во всех остальных случаях передаем переменную пустого номера
                fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it })
            }



            // --- ФОРМА WHATSAPP ----

            Text(
                text = stringResource(id = R.string.social_whatsapp),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val whatsapp = if (phoneNumberWhatsappFromDb != null && phoneNumberWhatsappFromDb != "+7" && phoneNumberWhatsappFromDb != "+77" && phoneNumberWhatsappFromDb != "" && createOrEdit != "0"){

                // Если при редактировании у заведения есть whatsapp, передаем ПЕРЕМЕННУЮ WHATSAPP С БД
                fieldPhoneComponent(phoneNumberWhatsappFromDb!!, onPhoneChanged = { phoneNumberWhatsappFromDb = it }, icon = painterResource(id = R.drawable.whatsapp))

            } else if (userWhatsappNumber != "+7" && userWhatsappNumber != "+77" && userWhatsappNumber != "" && userWhatsappNumber != null && createOrEdit == "0") {

                // Если при создании у пользователя есть whatsapp, передаем ПЕРЕМЕННУЮ WHATSAPP ПОЛЬЗОВАТЕЛЯ
                fieldPhoneComponent(userWhatsappNumber!!, onPhoneChanged = { userWhatsappNumber = it }, icon = painterResource(id = R.drawable.whatsapp))

            } else {

                // Во всех остальных случаях передаем переменную пустого whatsapp
                fieldPhoneComponent(phoneNumberWhatsapp, onPhoneChanged = { phoneNumberWhatsapp = it }, icon = painterResource(id = R.drawable.whatsapp))

            }


            // ----- ИНСТАГРАМ -----

            Text(
                text = stringResource(id = R.string.social_instagram),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val instagram = if (filledPlace.instagram != "" && filledPlace.instagram != null && createOrEdit != "0") {

                // Если при редактировании у заведения есть инстаграм, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledPlace.instagram)

            } else if (filledUserInfo.instagram != "" && filledUserInfo.instagram != null && createOrEdit == "0") {

                // Если при создании у пользователя есть инстаграм, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledUserInfo.instagram)

            } else {

                // Во всех остальных случаях ничего не передаем
                fieldInstagramComponent(act = act, icon = R.drawable.instagram)

            }



            // ----- ТЕЛЕГРАМ ---------

            Text(
                text = stringResource(id = R.string.social_telegram),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val telegram = if (filledPlace.telegram != "" && filledPlace.telegram != null && createOrEdit != "0") {

                // Если при редактировании у заведения есть telegram, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.instagram, inputText = filledPlace.telegram) // форма телеграма

            } else if (filledUserInfo.telegram != "" && filledUserInfo.telegram != null && createOrEdit == "0") {

                // Если при создании у пользователя есть telegram, передаем его
                fieldInstagramComponent(act = act, icon = R.drawable.telegram, inputText = filledUserInfo.telegram) // форма телеграма

            } else {

                // Во всех остальных случаях ничего не передаем
                fieldInstagramComponent(act = act, icon = R.drawable.telegram)

            }

            Text(
                text = "Режим работы",
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp)
            )

            Text(
                text = "Если выходной, оставь поле пустым",
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            val timeWorkMonday = createTimeWorkPlace(startTime = filledPlace.mondayOpenTime!!, finishTime = filledPlace.mondayOpenTime, dayName = "Понедельник", createOrEdit)
            Text(
                text = "${timeWorkMonday[0]} - ${timeWorkMonday[1]}",
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            /*val timeWorkTuesday = createTimeWorkPlace("Вторник")
            Text(
                text = timeWorkTuesday[0],
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val timeWorkWednesday = createTimeWorkPlace("Среда")
            Text(
                text = timeWorkWednesday[0],
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val timeWorkThursday = createTimeWorkPlace("Четверг")
            Text(
                text = timeWorkThursday[0],
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val timeWorkFriday = createTimeWorkPlace("Пятница")
            Text(
                text = timeWorkFriday[0],
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val timeWorkSaturday = createTimeWorkPlace("Суббота")
            Text(
                text = timeWorkSaturday[0],
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val timeWorkSunday = createTimeWorkPlace("Воскресенье")
            Text(
                text = timeWorkSunday[0],
                style = Typography.labelMedium,
                color = Grey_Text,
                modifier = Modifier.padding(bottom = 20.dp)
            )*/

            // ---- ПОНЕДЕЛЬНИК ------

            SpacerTextWithLine(headline = "Понедельник начало") // подпись перед формой

            val mondayOpenTimeResult = if (filledPlace.mondayOpenTime != "" && filledPlace.mondayOpenTime != null && createOrEdit != "0"){

                timePicker(filledPlace.mondayOpenTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            }

            SpacerTextWithLine(headline = "Закрываемся в") // подпись перед формой

            val mondayCloseTimeResult = if (filledPlace.mondayCloseTime != "" && filledPlace.mondayCloseTime != null && createOrEdit != "0"){

                timePicker(filledPlace.mondayCloseTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker()

            }

            // ---- Вторник ------

            SpacerTextWithLine(headline = "Вторник начало") // подпись перед формой

            val tuesdayOpenTimeResult = if (filledPlace.tuesdayOpenTime != "" && filledPlace.tuesdayOpenTime != null && createOrEdit != "0"){

                timePicker(filledPlace.tuesdayOpenTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            }

            SpacerTextWithLine(headline = "Закрываемся в") // подпись перед формой

            val tuesdayCloseTimeResult = if (filledPlace.tuesdayCloseTime != "" && filledPlace.tuesdayCloseTime != null && createOrEdit != "0"){

                timePicker(filledPlace.tuesdayCloseTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker()

            }

            // ---- Среда ------

            SpacerTextWithLine(headline = "Среда начало") // подпись перед формой

            val wednesdayOpenTimeResult = if (filledPlace.wednesdayOpenTime != "" && filledPlace.wednesdayOpenTime != null && createOrEdit != "0"){

                timePicker(filledPlace.wednesdayOpenTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            }

            SpacerTextWithLine(headline = "Закрываемся в") // подпись перед формой

            val wednesdayCloseTimeResult = if (filledPlace.wednesdayCloseTime != "" && filledPlace.wednesdayCloseTime != null && createOrEdit != "0"){

                timePicker(filledPlace.wednesdayCloseTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker()

            }

            // ---- Четверг ------

            SpacerTextWithLine(headline = "Четверг начало") // подпись перед формой

            val thursdayOpenTimeResult = if (filledPlace.thursdayOpenTime != "" && filledPlace.thursdayOpenTime != null && createOrEdit != "0"){

                timePicker(filledPlace.thursdayOpenTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            }

            SpacerTextWithLine(headline = "Закрываемся в") // подпись перед формой

            val thursdayCloseTimeResult = if (filledPlace.thursdayCloseTime != "" && filledPlace.thursdayCloseTime != null && createOrEdit != "0"){

                timePicker(filledPlace.thursdayCloseTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker()

            }

            // ---- Пятница ------

            SpacerTextWithLine(headline = "Пятница начало") // подпись перед формой

            val fridayOpenTimeResult = if (filledPlace.fridayOpenTime != "" && filledPlace.fridayOpenTime != null && createOrEdit != "0"){

                timePicker(filledPlace.fridayOpenTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            }

            SpacerTextWithLine(headline = "Закрываемся в") // подпись перед формой

            val fridayCloseTimeResult = if (filledPlace.fridayCloseTime != "" && filledPlace.fridayCloseTime != null && createOrEdit != "0"){

                timePicker(filledPlace.fridayCloseTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker()

            }

            // ---- Суббота ------

            SpacerTextWithLine(headline = "Суббота начало") // подпись перед формой

            val saturdayOpenTimeResult = if (filledPlace.saturdayOpenTime != "" && filledPlace.saturdayOpenTime != null && createOrEdit != "0"){

                timePicker(filledPlace.saturdayOpenTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            }

            SpacerTextWithLine(headline = "Закрываемся в") // подпись перед формой

            val saturdayCloseTimeResult = if (filledPlace.saturdayCloseTime != "" && filledPlace.saturdayCloseTime != null && createOrEdit != "0"){

                timePicker(filledPlace.saturdayCloseTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker()

            }

            // ---- Воскресенье ------

            SpacerTextWithLine(headline = "Воскресенье начало") // подпись перед формой

            val sundayOpenTimeResult = if (filledPlace.sundayOpenTime != "" && filledPlace.sundayOpenTime != null && createOrEdit != "0"){

                timePicker(filledPlace.sundayOpenTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            }

            SpacerTextWithLine(headline = "Закрываемся в") // подпись перед формой

            val sundayCloseTimeResult = if (filledPlace.sundayCloseTime != "" && filledPlace.sundayCloseTime != null && createOrEdit != "0"){

                timePicker(filledPlace.sundayCloseTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

            } else {

                timePicker()

            }

            // ---- ОПИСАНИЕ --------

            Text(
                text = stringResource(id = R.string.cm_description),
                style = Typography.bodySmall,
                color = WhiteDvij,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            val description = if (filledPlace.placeDescription != "" && filledPlace.placeDescription != null && createOrEdit != "0"){

                // Если при редактировании есть описание, передаем его
                fieldDescriptionComponent(filledPlace.placeDescription) // ФОРМА ОПИСАНИЯ МЕРОПРИЯТИЯ

            } else {

                // Если нет - пустое поле
                fieldDescriptionComponent()

            }

            Spacer(modifier = Modifier.height(30.dp)) // РАЗДЕЛИТЕЛЬ


            // -------------- КНОПКИ ОТМЕНА И ОПУБЛИКОВАТЬ ------------


            // ------ КНОПКА ОПУБЛИКОВАТЬ -----------

            ButtonCustom(buttonText = "Опубликовать") {


                val currentTime = System.currentTimeMillis()/1000

                // действие на нажатие

                // --- ФУНКЦИЯ ПРОВЕРКИ НА ЗАПОЛНЕНИЕ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ ---------

                val checkData = checkDataOnCreatePlace(
                    image1 = image1,
                    headline = headline,
                    phone = phone,
                    description = description,
                    category = category,
                    city = city,
                    address = address,
                    imageUriFromDb = filledPlace.logo ?: "",
                    mondayOT = mondayOpenTimeResult,
                    mondayCT = mondayCloseTimeResult,
                    tuesdayOT = tuesdayOpenTimeResult,
                    tuesdayCT = tuesdayCloseTimeResult,
                    wednesdayOT = wednesdayOpenTimeResult,
                    wednesdayCT = wednesdayCloseTimeResult,
                    thursdayOT = thursdayOpenTimeResult,
                    thursdayCT = thursdayCloseTimeResult,
                    fridayOT = fridayOpenTimeResult,
                    fridayCT = fridayCloseTimeResult,
                    saturdayOT = saturdayOpenTimeResult,
                    saturdayCT = saturdayCloseTimeResult,
                    sundayOT = sundayOpenTimeResult,
                    sundayCT = sundayCloseTimeResult
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

                        // ЕСЛИ ИЗОБРАЖЕНИЕ НЕ НАЛ - ТАКОЕ ВОЗМОЖНО ТОЛЬКО ПРИ РЕДАКТИРОВАНИИ

                        if (image1 == null){

                            GlobalScope.launch(Dispatchers.Main) {

                                // заполняем заведение

                                val filledPlaceForDb = PlacesAdsClass(

                                    logo = filledPlace.logo,
                                    placeName = headline,
                                    placeDescription = description,
                                    phone = phone,
                                    whatsapp = whatsapp,
                                    telegram = telegram,
                                    instagram = instagram,
                                    category = category,
                                    city = city,
                                    address = address,
                                    placeKey = filledPlace.placeKey,
                                    owner = filledPlace.owner,
                                    mondayOpenTime = mondayOpenTimeResult,
                                    mondayCloseTime = mondayCloseTimeResult,
                                    tuesdayOpenTime = tuesdayOpenTimeResult,
                                    tuesdayCloseTime = tuesdayCloseTimeResult,
                                    wednesdayOpenTime = wednesdayOpenTimeResult,
                                    wednesdayCloseTime = wednesdayCloseTimeResult,
                                    thursdayOpenTime = thursdayOpenTimeResult,
                                    thursdayCloseTime = thursdayCloseTimeResult,
                                    fridayOpenTime = fridayOpenTimeResult,
                                    fridayCloseTime = fridayCloseTimeResult,
                                    saturdayOpenTime = saturdayOpenTimeResult,
                                    saturdayCloseTime = saturdayCloseTimeResult,
                                    sundayOpenTime = sundayOpenTimeResult,
                                    sundayCloseTime = sundayCloseTimeResult,
                                    createPlaceTime = filledPlace.createPlaceTime

                                )

                                // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                if (auth.uid != null) {

                                    // Если зарегистрирован, то запускаем функцию публикации заведения

                                    placesDatabaseManager.publishPlace(filledPlaceForDb) { result ->

                                        // в качестве колбака придет булин. Если опубликовано заведение то:

                                        if (result) {

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
                        } else {

                            // ---- В СЛУЧАЕ, ЕСЛИ ЕСТЬ ВЫБРАННАЯ ИЗ ГАЛЕРЕИ КАРТИНКА -------

                            // запускаем сжатие изображения
                            val compressedImage = act.photoHelper.compressImage(act, image1)

                            // после сжатия запускаем функцию загрузки сжатого фото в Storage

                            act.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", PLACES_ROOT){

                                // Запускаем корутину и публикуем заведение

                                GlobalScope.launch(Dispatchers.Main) {

                                    // заполняем заведение

                                    val filledPlaceForDb = if(createOrEdit == "0"){

                                        // ---- ЕСЛИ СОЗДАНИЕ ---------

                                        PlacesAdsClass(

                                            logo = it,
                                            placeName = headline,
                                            placeDescription = description,
                                            phone = phone,
                                            whatsapp = whatsapp,
                                            telegram = telegram,
                                            instagram = instagram,
                                            category = category,
                                            city = city,
                                            address = address,
                                            placeKey = placesDatabaseManager.placeDatabase.push().key,
                                            owner = auth.uid,
                                            mondayOpenTime = mondayOpenTimeResult,
                                            mondayCloseTime = mondayCloseTimeResult,
                                            tuesdayOpenTime = tuesdayOpenTimeResult,
                                            tuesdayCloseTime = tuesdayCloseTimeResult,
                                            wednesdayOpenTime = wednesdayOpenTimeResult,
                                            wednesdayCloseTime = wednesdayCloseTimeResult,
                                            thursdayOpenTime = thursdayOpenTimeResult,
                                            thursdayCloseTime = thursdayCloseTimeResult,
                                            fridayOpenTime = fridayOpenTimeResult,
                                            fridayCloseTime = fridayCloseTimeResult,
                                            saturdayOpenTime = saturdayOpenTimeResult,
                                            saturdayCloseTime = saturdayCloseTimeResult,
                                            sundayOpenTime = sundayOpenTimeResult,
                                            sundayCloseTime = sundayCloseTimeResult,
                                            createPlaceTime = currentTime.toString()

                                        )

                                    } else {

                                        // ---- ЕСЛИ РЕДАКТИРОВАНИЕ --------

                                        PlacesAdsClass(

                                            logo = it,
                                            placeName = headline,
                                            placeDescription = description,
                                            phone = phone,
                                            whatsapp = whatsapp,
                                            telegram = telegram,
                                            instagram = instagram,
                                            category = category,
                                            city = city,
                                            address = address,
                                            placeKey = filledPlace.placeKey,
                                            owner = filledPlace.owner,
                                            mondayOpenTime = mondayOpenTimeResult,
                                            mondayCloseTime = mondayCloseTimeResult,
                                            tuesdayOpenTime = tuesdayOpenTimeResult,
                                            tuesdayCloseTime = tuesdayCloseTimeResult,
                                            wednesdayOpenTime = wednesdayOpenTimeResult,
                                            wednesdayCloseTime = wednesdayCloseTimeResult,
                                            thursdayOpenTime = thursdayOpenTimeResult,
                                            thursdayCloseTime = thursdayCloseTimeResult,
                                            fridayOpenTime = fridayOpenTimeResult,
                                            fridayCloseTime = fridayCloseTimeResult,
                                            saturdayOpenTime = saturdayOpenTimeResult,
                                            saturdayCloseTime = saturdayCloseTimeResult,
                                            sundayOpenTime = sundayOpenTimeResult,
                                            sundayCloseTime = sundayCloseTimeResult,
                                            createPlaceTime = filledPlace.createPlaceTime

                                        )

                                    }

                                    // Делаем дополнительную проверку - пользователь зарегистрирован или нет

                                    if (auth.uid != null) {

                                        // Если зарегистрирован, то запускаем функцию публикации заведения

                                        placesDatabaseManager.publishPlace(filledPlaceForDb) { result ->

                                            // в качестве колбака придет булин. Если опубликовано заведение то:

                                            if (result) {

                                                navController.navigate(PLACES_ROOT) {popUpTo(0)} // переходим на страницу заведений

                                                // показываем ТОСТ

                                                if (createOrEdit == "0"){

                                                    // --- ЕСЛИ СОЗДАНИЕ ----

                                                    Toast.makeText(
                                                        act,
                                                        "Твое заведение успешно опубликовано",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                } else {

                                                    // ------ ЕСЛИ РЕДАКТИРОВАНИЕ ----

                                                    Toast.makeText(
                                                        act,
                                                        "Твое заведение успешно отредактировано",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }
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
                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            ButtonCustom(buttonText = "Отменить", typeButton = ATTENTION, leftIcon = R.drawable.ic_close) {
                Toast.makeText(act, "СДЕЛАТЬ ДИАЛОГ - ДЕЙСТВИТЕЛЬНО ХОТИТЕ ВЫЙТИ?", Toast.LENGTH_SHORT).show()
            }
        }

        // --- ЭКРАН ИДЕТ ЗАГРУЗКА ----

        if (openLoading.value) {
            LoadingScreen(act.resources.getString(R.string.ss_loading))
        }
    }
}
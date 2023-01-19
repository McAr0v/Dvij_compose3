package kz.dvij.dvij_compose3.createscreens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.DatabaseManager
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
    fun CreateMeetingScreen(navController: NavController) {

        val activity = act
        val databaseManager = DatabaseManager(activity) // инициализируем класс с функциями базы данных

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
        val openDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог


        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------



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

            // -------- ИЗОБРАЖЕНИЕ МЕРОПРИЯТИЯ -----------

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_image)) // подпись перед формой

            val image1 = chooseImageDesign(activity) // Изображение мероприятия

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_headline)) // подпись перед формой

            headline = fieldHeadlineComponent(act = activity) // форма заголовка

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_category)) // подпись перед формой

            category = activity.getString(act.categoryDialog.categorySelectButton { openDialog.value = true }.categoryName)  // КНОПКА, АКТИВИРУЮЩАЯ ДИАЛОГ выбора категории

            // ДИАЛОГ ВЫБОРА КАТЕГОРИИ

            if (openDialog.value) {
                act.categoryDialog.CategoryChooseDialog {
                    openDialog.value = false
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

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_date)) // подпись перед формой

            dataResult = dataPicker() // ВЫБОР ДАТЫ

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

                        // если какое либо обязательное поле не заполнено

                        val checkData = checkDataOnCreateMeeting(image1, headline, phone, dataResult, timeStartResult, description, category)

                        if (checkData != 0) {

                            Toast.makeText(activity, act.resources.getString(checkData), Toast.LENGTH_SHORT).show()

                        } else if (ContextCompat.checkSelfPermission(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(act, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 888)
                        } else {

                            openLoading.value = true

                            // сделать функцию получения картинок отдельно в датабаз менеджер, и уже после получения всех картинок, вызывать публиш адс

                            GlobalScope.launch(Dispatchers.IO){

                                val compressedImage = activity.photoHelper.compressImage(activity, image1!!)

                                activity.photoHelper.uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg", MEETINGS_ROOT){

                                    GlobalScope.launch(Dispatchers.Main) {

                                        val filledMeeting = MeetingsAdsClass(
                                            key = databaseManager.meetingDatabase.push().key, // генерируем уникальный ключ мероприятия
                                            category = category,
                                            headline = headline,
                                            description = description,
                                            price = price,
                                            phone = phone,
                                            whatsapp = whatsapp,
                                            data = dataResult,
                                            startTime = timeStartResult,
                                            finishTime = timeFinishResult,
                                            image1 = it
                                        )

                                        if (auth.uid != null) {

                                            databaseManager.publishMeeting(filledMeeting){ result ->

                                                if (result){

                                                    act.categoryDialog.chosenCategory = CategoriesList.DefaultCat
                                                    navController.navigate(MEETINGS_ROOT) {popUpTo(0)}

                                                    Toast.makeText(
                                                        activity,
                                                        "мероприятие успешно опубликовано",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                } else {

                                                    Toast.makeText(
                                                        activity,
                                                        "произошла ошибка",
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
                        text = "Опубликовать",
                        style = Typography.labelMedium
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_publish),
                        contentDescription = "",
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
                    text = "Отменить создание",
                    style = Typography.labelMedium,
                    color = Grey40
                )
            }
        }

        if (openLoading.value) {
            LoadingScreen("Мероприятие загружается")
        }
    }

}
package kz.dvij.dvij_compose3.createscreens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kz.dvij.dvij_compose3.pickers.dataPicker
import kz.dvij.dvij_compose3.pickers.timePicker
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.DatabaseManager
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.ui.theme.*

class CreateMeeting(private val act: MainActivity) {

    // ------ КЛАСС СОЗДАНИЯ МЕРОПРИЯТИЯ ----------

    private var chosenCategory: CategoriesList = CategoriesList.DefaultCat // категория по умолчанию (не выбрана категория)
    var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null // Слушатель выбора картинок

    private val storage = Firebase.storage("gs://dvij-compose3-1cf6a.appspot.com").getReference("Meetings")

    private val imageRef = storage
        .child(act.mAuth.uid ?: "empty")
        .child("image_${System.currentTimeMillis()}")

    private val imageRef2 = storage
        .child(act.mAuth.uid ?: "empty")
        .child("image2_${System.currentTimeMillis()}")

    private val imageRef3 = storage
        .child(act.mAuth.uid ?: "empty")
        .child("image3_${System.currentTimeMillis()}")




    val meetingDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Meetings") // Создаем ПАПКУ В БД для мероприятий

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    val default = MeetingsAdsClass (
        description = "def"
    )




    // ------- ЭКРАН СОЗДАНИЯ МЕРОПРИЯТИЯ ------------

    @SuppressLint("RememberReturnType")
    @Composable
    fun CreateMeetingScreen() {

        var uris1 = remember {
            mutableStateOf(listOf<Uri>())
        }

        val activity = act
        val context = LocalContext.current
        val databaseManager = DatabaseManager(activity) // инициализируем класс с функциями базы данных

        // КАЛЕНДАРЬ - https://www.geeksforgeeks.org/date-picker-in-android-using-jetpack-compose/
        // https://stackoverflow.com/questions/60417233/jetpack-compose-date-time-picker

        var phoneNumber by rememberSaveable { mutableStateOf("7") } // инициализируем переменную телефонного номера
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") } // инициализируем переменную номера с whatsapp
        var openDialog = remember { mutableStateOf(false) } // инициализируем переменную, открывающую диалог

        var headline = "" // инициализируем заголовок
        var description = "" // инициализируем описание
        var price = "" // инициализируем цену
        var phone = "" // инициализируем телефон
        var whatsapp = "" // инициализируем whatsapp

        var dataResult = "" // инициализируем выбор даты
        var timeStartResult = "" // инициализируем выбор времени начала мероприятия
        var timeFinishResult = "" // инициализируем выбор времени конца мероприятия
        var category = "" // категория

        var image: Uri? = null
        var image2: Uri? = null
        var image3: Uri? = null

        var downloadUrl: String = ""
        var downloadUrl2: String = ""
        var downloadUrl3: String = ""





        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------



        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey95)
                .verticalScroll(rememberScrollState())
                .padding(top = 0.dp, end = 20.dp, start = 20.dp, bottom = 20.dp)
            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            // -------- ИЗОБРАЖЕНИЕ МЕРОПРИЯТИЯ -----------

            SpacerTextWithLine(headline = "Главное изображение") // подпись перед формой

            image = meetingImage()

            if (image != null){

                SpacerTextWithLine(headline = "Второе изображение")
                image2 = meetingImage()

                if (image2 != null){

                    SpacerTextWithLine(headline = "Третье изображение")
                    image3 = meetingImage()

                }

            }





            SpacerTextWithLine(headline = "Заголовок") // подпись перед формой

            headline = fieldHeadlineComponent(act = activity) // форма заголовка

            SpacerTextWithLine(headline = "Выбери категорию") // подпись перед формой

            // ДИАЛОГ ВЫБОРА КАТЕГОРИИ

            if (openDialog.value) {
                CategoryChooseDialog {
                    openDialog.value = false
                }
            }

            category = activity.getString(categorySelectButton { openDialog.value = true }.categoryName)  // КНОПКА, АКТИВИРУЮЩАЯ ДИАЛОГ выбора категории

            SpacerTextWithLine(headline = "Телефон для кнопки Позвонить") // подпись перед формой

            phone = fieldPhoneComponent(phoneNumber, onPhoneChanged = { phoneNumber = it }) // форма телефона

            SpacerTextWithLine(headline = "Телефон для кнопки Whatsapp") // подпись перед формой


            // --- ФОРМА WHATSAPP ----

            whatsapp = fieldPhoneComponent(
                phoneNumberWhatsapp,
                onPhoneChanged = { phoneNumberWhatsapp = it },
                icon = painterResource(id = R.drawable.whatsapp)
            )

            SpacerTextWithLine(headline = "Выберите дату") // подпись перед формой

            dataResult = dataPicker() // ВЫБОР ДАТЫ

            SpacerTextWithLine(headline = "Начало мероприятия") // подпись перед формой

            timeStartResult = timePicker() // ВЫБОР ВРЕМЕНИ - Начало мероприятия

            SpacerTextWithLine(headline = "Конец мероприятия") // подпись перед формой

            timeFinishResult = timePicker() // ВЫБОР ВРЕМЕНИ - Конец мероприятия

            SpacerTextWithLine(headline = "Цена билета") // подпись перед формой

            price = fieldPriceComponent(act = activity) // Форма цены за билет

            SpacerTextWithLine(headline = "Описание") // подпись перед формой

            description = fieldDescriptionComponent(act = activity) // ФОРМА ОПИСАНИЯ МЕРОПРИЯТИЯ

            Spacer(modifier = Modifier.height(30.dp)) // РАЗДЕЛИТЕЛЬ



            // -------------- КНОПКИ ОТМЕНА И ОПУБЛИКОВАТЬ ------------

            Row(modifier = Modifier.fillMaxWidth()) {


                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f),
                ) {
                    Text(
                        text = "Отмена",
                        style = Typography.labelMedium,
                        color = AttentionColor
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = {

                        // сделать функцию получения картинок отдельно в датабаз менеджер, и уже после получения всех картинок, вызывать публиш адс

                        val uploadImage1 = image?.let { imageRef.putFile(it) }
                        val uploadImage2 = image2?.let { imageRef2.putFile(it) }
                        val uploadImage3 = image3?.let { imageRef3.putFile(it) }

                        uploadImage1?.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let { throw it }
                            }

                            imageRef.downloadUrl
                        }?.addOnCompleteListener { task1 ->

                            if (task1.isSuccessful) {

                                downloadUrl = task1.result.toString()

                                if (image2 == null) {

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
                                        image1 = downloadUrl
                                    )

                                    databaseManager.publishMeeting(filledMeeting) // вызываем функцию публикации мероприятия. Передаем заполненную переменную как класс

                                    Log.d("MyLog", "URL: $downloadUrl")
                                }


                                uploadImage2?.continueWithTask { task2->
                                    if (!task2.isSuccessful) {
                                        task2.exception?.let { throw it }
                                    }

                                    imageRef2.downloadUrl
                                }?.addOnCompleteListener { task1 ->

                                    if (task1.isSuccessful) {

                                        downloadUrl2 = task1.result.toString()

                                        uploadImage3?.continueWithTask { task4->
                                            if (!task4.isSuccessful) {
                                                task4.exception?.let { throw it }
                                            }

                                            imageRef3.downloadUrl
                                        }?.addOnCompleteListener { task5 ->

                                            if (task5.isSuccessful) {

                                                downloadUrl3 = task5.result.toString()

                                                Log.d("MyLog", "URL: $downloadUrl")
                                                Log.d("MyLog", "URL2: $downloadUrl2")
                                                Log.d("MyLog", "URL2: $downloadUrl3")


                                            }

                                        }

                                    }

                                }
                            }

                        }




                        // заполняем в переменную значения, согласно классу мероприятий

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
                            finishTime = timeFinishResult
                        )

                        databaseManager.publishMeeting(filledMeeting) // вызываем функцию публикации мероприятия. Передаем заполненную переменную как класс

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = SuccessColor,
                        contentColor = Grey00
                    ),
                    shape = RoundedCornerShape(50)
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
            }
        }
    }

    // -------- КНОПКА ВЫБОРА КАТЕГОРИИ -----------

    @Composable
    fun categorySelectButton(onClick: ()-> Unit): CategoriesList {

        Button(
            onClick = {
                onClick()
            },

            // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            border = BorderStroke(
                width = if (chosenCategory == CategoriesList.DefaultCat) {
                    2.dp
                } else {
                    0.dp
                }, color = if (chosenCategory == CategoriesList.DefaultCat) {
                    Grey60
                } else {
                    Grey95
                }
            ),

            // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (chosenCategory == CategoriesList.DefaultCat) {
                    Grey95
                } else {
                    PrimaryColor
                },
                contentColor = if (chosenCategory == CategoriesList.DefaultCat) {
                    Grey60
                } else {
                    Grey100
                },
            ),
            shape = RoundedCornerShape(50) // скругленные углы кнопки
        ) {

            Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

            Text(
                text = stringResource(id = chosenCategory.categoryName), // текст кнопки
                style = Typography.labelMedium // стиль текста
            )
        }
        return chosenCategory
    }

    @Composable
    fun meetingImage (): Uri? {

        var selectImage = remember { mutableStateOf<Uri?>(null) }

        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){
            selectImage.value = it
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(15.dp),
            backgroundColor = Grey100
        ) {

            if (selectImage.value == null) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                galleryLauncher.launch("image/*")
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "",
                            tint = Grey10
                        )
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Text(
                            text = "Добавь изображение",
                            color = Grey10,
                            style = Typography.bodyMedium
                        )
                        
                    }
                }
                
            } else {

                Image(
                    modifier = Modifier
                        .background(
                            shape = RoundedCornerShape(20.dp),
                            color = Grey95
                        )
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    painter = (
                            if (selectImage.value == null) {
                                painterResource(id = R.drawable.korn_concert)
                            } else {
                                rememberAsyncImagePainter(model = selectImage.value)
                            }
                            ),
                    contentDescription = "",

                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize().padding(5.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.background(WarningColor, shape = RoundedCornerShape(50))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "",
                            tint = Grey95
                        )
                    }

                    IconButton(
                        onClick = { selectImage.value = null },
                        modifier = Modifier.background(AttentionColor, shape = RoundedCornerShape(50))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "",
                            tint = Grey95
                        )
                    }
                }

                /*Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    /*Text(
                        text = "Главная картинка",
                        color = Grey95,
                        modifier = Modifier
                            .background(SuccessColor, shape = RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        style = Typography.labelSmall
                    )*/



                }*/




            }

            

        }



        return selectImage.value


    }


    // ----- ДИАЛОГ ВЫБОРА КАТЕГОРИИ

    @Composable
    fun CategoryChooseDialog(onDismiss: () -> Unit) {

        // Создаем список городов

        val categoriesList = mutableListOf<CategoriesList>(

            CategoriesList.ConcertsCat,
            CategoriesList.HobieCat
        )

        // ------ САМ ДИАЛОГ ---------

        Dialog(
            onDismissRequest = { onDismiss() } // действие на нажатие за пределами диалога
        ) {

        // -------- СОДЕРЖИМОЕ ДИАЛОГА ---------

            Column(
                modifier = Modifier
                    .border(
                        2.dp, // толщина границы
                        color = Grey80, // цвет границы
                        shape = RoundedCornerShape(20.dp) // скругление углов
                    )
                    .background(
                        Grey95, // цвет фона
                        shape = RoundedCornerShape(20.dp) // скругление углов
                    )
                    .padding(20.dp) // отступы
                    .fillMaxWidth() // занять всю ширину

            ) {


                // ------- ЗАГЛОВОК ВЫБЕРИТЕ КАТЕГОРИЮ и КНОПКА ЗАКРЫТЬ -----------

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание элементов по центру
                    horizontalArrangement = Arrangement.End // выравнивание по горизонтали
                ) {

                    // --------- ЗАГОЛОВОК ----------

                    androidx.compose.material3.Text(
                        text = stringResource(id = R.string.cat_default), // текст заголовка
                        style = Typography.titleMedium, // стиль заголовка
                        color = Grey10, // цвет заголовка
                        modifier = Modifier.weight(1f)
                    ) // занять всю оставшуюся ширину

                    Spacer(modifier = Modifier.height(20.dp)) // разделитель

                    // ------------- ИКОНКА ЗАКРЫТЬ ----------------

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close), // сама иконка
                        contentDescription = stringResource(id = R.string.close_page), // описание для слабовидяших
                        tint = Grey10, // цвет иконки
                        modifier = Modifier.clickable { onDismiss() } // действие на нажатие
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                // ---------- СПИСОК Категорий -------------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth() // занять ширину
                        .background(
                            Grey100, // цвет фона
                            shape = RoundedCornerShape(10.dp) // скругление углов
                        )
                        .padding(20.dp), // отступ
                    verticalArrangement = Arrangement.spacedBy(20.dp)

                ) {

                    // наполнение ленивой колонки

                    // берем каждый item из списка categoriesList и заполняем шаблон

                    items(categoriesList) { category ->

                        // ------------ строка с названием категории -------------

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // действие на нажатие на элемент
                                chosenCategory =
                                    category // выбранная категория теперь та, которую выбрали, а не по умолчанию
                                onDismiss() // закрыть диалог
                            }
                        ) {
                            androidx.compose.material3.Text(
                                text = stringResource(id = category.categoryName), // само название категории
                                color = Grey40, // цвет текста
                                style = Typography.bodyMedium // стиль текста
                            )
                        }
                    }
                }
            }
        }
    }
}
package kz.dvij.dvij_compose3.createscreens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kz.dvij.dvij_compose3.pickers.dataPicker
import kz.dvij.dvij_compose3.pickers.timePicker
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.DatabaseManager
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.ui.theme.*
import java.io.ByteArrayOutputStream
import java.io.File

class CreateMeeting(private val act: MainActivity) {

    // ------ КЛАСС СОЗДАНИЯ МЕРОПРИЯТИЯ ----------

    private var chosenCategory: CategoriesList = CategoriesList.DefaultCat // категория по умолчанию (не выбрана категория)
    var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null // Слушатель выбора картинок

    private val storage = Firebase.storage("gs://dvij-compose3-1cf6a.appspot.com").getReference("Meetings") // инициализируем папку, в которую будет сохраняться картинка мероприятия


    // делаем дополнительные подпапки для более удобного поиска изображений

    private val imageRef = storage
        .child(act.mAuth.uid ?: "empty") // в папке "Meetings" будет еще папка - для каждого пользователя своя
        .child("image_${System.currentTimeMillis()}") // название изображения


    private val meetingDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Meetings") // Создаем ПАПКУ В БД для мероприятий

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

            val image1 = meetingImage() // Изображение мероприятия

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_headline)) // подпись перед формой

            headline = fieldHeadlineComponent(act = activity) // форма заголовка

            SpacerTextWithLine(headline = stringResource(id = R.string.cm_category)) // подпись перед формой



            category = activity.getString(CategoriesList.DefaultCat.categoryName)

            category = activity.getString(categorySelectButton { openDialog.value = true }.categoryName)  // КНОПКА, АКТИВИРУЮЩАЯ ДИАЛОГ выбора категории

            // ДИАЛОГ ВЫБОРА КАТЕГОРИИ

            if (openDialog.value) {
                CategoryChooseDialog {
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

                        Log.d("MyLog", category)

                        if (image1 == null || headline == "" || phone == "+77" || dataResult == "" || timeStartResult == "" || description == "" || category == "Выберите категорию") {

                            if (image1 == null) {Toast.makeText(activity, act.resources.getString(R.string.cm_no_image), Toast.LENGTH_SHORT).show()}
                            if (headline == "") {Toast.makeText(activity, act.resources.getString(R.string.cm_no_headline), Toast.LENGTH_SHORT).show()}
                            if (phone == "+77") {Toast.makeText(activity, act.resources.getString(R.string.cm_no_phone), Toast.LENGTH_SHORT).show()}

                            // по моему не работает телефон. ПРОВЕРИТЬ

                            if (dataResult == "") {Toast.makeText(activity, "Когда начало?", Toast.LENGTH_SHORT).show()}
                            if (timeStartResult == "") {Toast.makeText(activity, "Во сколько начало?", Toast.LENGTH_SHORT).show()}
                            if (description == "") {Toast.makeText(activity, "Где описание?", Toast.LENGTH_SHORT).show()}
                            if (category == "Выберите категорию") {Toast.makeText(activity, "Выбери категорию", Toast.LENGTH_SHORT).show()}

                        } else {

                            openLoading.value = true

                            // сделать функцию получения картинок отдельно в датабаз менеджер, и уже после получения всех картинок, вызывать публиш адс

                            GlobalScope.launch(Dispatchers.IO){

                                val compressedImage = compressImage(activity, image1)
                                uploadPhoto(compressedImage!!, "TestCompressImage", "image/jpg"){

                                    Log.d ("MyLog", "CompressURL: $it")

                                    GlobalScope.launch(Dispatchers.Main){

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
                                            meetingDatabase // записываем в базу данных
                                                //.child(meeting.category ?: "Без категории") // создаем путь категорий
                                                .child(
                                                    filledMeeting.key ?: "empty"
                                                ) // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ МЕРОПРИЯТИЯ
                                                .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего мероприятие
                                                .child("meetingData")
                                                .setValue(filledMeeting).addOnCompleteListener {

                                                    if (it.isSuccessful) {
                                                        Toast.makeText(
                                                            activity,
                                                            "мероприятие успешно опубликовано",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        navController.navigate(MEETINGS_ROOT) {
                                                            popUpTo(
                                                                0
                                                            )
                                                        }

                                                    } else {
                                                        Toast.makeText(
                                                            activity,
                                                            "произошла ошибка",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }// записываем само значение. Передаем целый класс
                                        }

                                        /*openDialog.value = false
                                        navController.navigate(MEETINGS_ROOT)
                                        Toast.makeText(activity, "Мероприятие успешно опубликовано", Toast.LENGTH_SHORT).show()*/

                                    }
                                }

                            }

                            /*val uploadImage1 = image1?.let { imageRef.putFile(it) }

                            uploadImage1?.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let { throw it }
                                }

                                imageRef.downloadUrl
                            }?.addOnCompleteListener { task1 ->*/

                                /*if (task1.isSuccessful) {

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
                                        image1 = task1.result.toString()
                                    )

                                    if (auth.uid != null) {
                                        meetingDatabase // записываем в базу данных
                                            //.child(meeting.category ?: "Без категории") // создаем путь категорий
                                            .child(
                                                filledMeeting.key ?: "empty"
                                            ) // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ МЕРОПРИЯТИЯ
                                            .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего мероприятие
                                            .child("meetingData")
                                            .setValue(filledMeeting).addOnCompleteListener {

                                                if (it.isSuccessful) {
                                                    Toast.makeText(
                                                        activity,
                                                        "мероприятие успешно опубликовано",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    //return@addOnCompleteListener
                                                    navController.navigate(MEETINGS_ROOT) {
                                                        popUpTo(
                                                            0
                                                        )
                                                    }

                                                } else {
                                                    Toast.makeText(
                                                        activity,
                                                        "произошла ошибка",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }// записываем само значение. Передаем целый класс
                                    }

                                }

                            }*/

                        }
                    //

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
                        .fillMaxSize()
                        .padding(5.dp),
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

    private fun compressImage(context: ComponentActivity, uri: Uri): Uri?{

        val bitmap = if (Build.VERSION.SDK_INT < 28){
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }

        val bytes = ByteArrayOutputStream()

        val correctImageSize = getWriteSizeImage(bitmap) // получаем размеры, до которых надо уменьшить картинку
        Log.d (
            "MyLog",
            "Изначальная ширина: ${bitmap.width}, после функции ширина: ${correctImageSize[0]}, Изначальная высота: ${bitmap.height}, после функции высота: ${correctImageSize[1]}"
        )

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, correctImageSize[0], correctImageSize[1], false) // изменение размера картинки

        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes)

        //bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bytes)

        val path: String = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            resizedBitmap,
            "image_${System.currentTimeMillis()}",
            null
        )
        return Uri.parse(path)
    }

    private fun getWriteSizeImage(bitmap: Bitmap): List<Int>{

        var listOfSize = arrayListOf<Int>(bitmap.width, bitmap.height)

        val width = bitmap.width // ширина
        val height = bitmap.height // высота

        val ratio = (width / height).toFloat() // ratio - коэффициент

        val scale: Float = width.toFloat() / height.toFloat()

        Log.d ("MyLog", "$scale")

        // если коэффициент больше или равен 1
        if (ratio >= 1) {
            // если ширина меньше 1000
            if (width <= 1000) {
                listOfSize[0] = width
                listOfSize[1] = height
                //listOfSize.add(width, height)
            } else {
                val resizeHeight = 1000/scale

                listOfSize[0] = 1000
                listOfSize[1] = resizeHeight.toInt()

                //listOfSize.add(1000, resizeHeight.toInt())
            }
        } else {

            if (height <= 1000) {
                //listOfSize.add(height, width)

                listOfSize[0] = height
                listOfSize[1] = width

            } else {
                val resizeWidth = 1000*scale

                listOfSize[0] = resizeWidth.toInt()
                listOfSize[1] = 1000
                //listOfSize.add(1000, resizeWidth.toInt())
            }

        }
            return listOfSize

    }

    private suspend fun uploadPhoto(uri: Uri, name: String, mimeType: String?, callback: (url: String)-> Unit){

        val metadata = mimeType?.let {
            StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()
        }

        if (metadata != null){
            imageRef.putFile(uri, metadata).await()
        } else {
            imageRef.putFile(uri).await()
        }

        callback(imageRef.downloadUrl.await().toString())
    }
}
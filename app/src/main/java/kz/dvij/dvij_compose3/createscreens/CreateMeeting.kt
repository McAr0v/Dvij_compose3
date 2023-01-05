package kz.dvij.dvij_compose3.createscreens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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


    // ------- ЭКРАН СОЗДАНИЯ МЕРОПРИЯТИЯ ------------

    @Composable
    fun CreateMeetingScreen() {

        val activity = act
        val context = LocalContext.current
        val databaseManager = DatabaseManager(activity, activity) // инициализируем класс с функциями базы данных

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


        // -------------- СОДЕРЖИМОЕ СТРАНИЦЫ -----------------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey95)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            // -------- ИЗОБРАЖЕНИЕ МЕРОПРИЯТИЯ -----------

            Image(
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(20.dp),
                        color = Grey95
                    )
                    .fillMaxWidth()
                    .height(200.dp),
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.korn_concert),
                contentDescription = "",

                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

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
package kz.dvij.dvij_compose3.createscreens

import kz.dvij.dvij_compose3.MainActivity
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kz.dvij.dvij_compose3.pickers.dataPicker
import kz.dvij.dvij_compose3.pickers.timePicker
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.ui.theme.*
import java.util.*

class CreateMeeting(act: MainActivity) {

    private var chosenCategory: CategoriesList = CategoriesList.DefaultCat

    @Composable
    fun CreateMeetingScreen(activity: MainActivity) {

        var phoneNumber by rememberSaveable { mutableStateOf("7") }
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") }
        var openDialog = remember { mutableStateOf(false) }

        var headline = ""
        var description = ""
        var price = ""
        var phone = ""
        var whatsapp = ""

        var dataResult = ""
        var timeResult = ""

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
                    .background(shape = RoundedCornerShape(20.dp), color = Grey95)
                    .fillMaxWidth()
                    .height(200.dp),
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.korn_concert),
                contentDescription = "",

                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            SpacerTextWithLine(headline = "Заголовок")

            headline = fieldHeadlineComponent(act = activity)

            SpacerTextWithLine(headline = "Выбери категорию")

            if (openDialog.value) {
                CategoryChooseDialog {
                    openDialog.value = false
                }
            }

            CategorySelectButton { openDialog.value = true }



            SpacerTextWithLine(headline = "Телефон для кнопки Позвонить")

            phone = fieldPhoneComponent(phoneNumber,
                onPhoneChanged = { phoneNumber = it })

            SpacerTextWithLine(headline = "Телефон для кнопки Whatsapp")

            whatsapp = fieldPhoneComponent(
                phoneNumberWhatsapp,
                onPhoneChanged = { phoneNumberWhatsapp = it },
                icon = painterResource(id = R.drawable.whatsapp)
            )

            SpacerTextWithLine(headline = "Выберите дату")

            dataResult = dataPicker()




            SpacerTextWithLine(headline = "Начало мероприятия")

            timeResult = timePicker()

            SpacerTextWithLine(headline = "Конец мероприятия")

            timeResult = timePicker()

            SpacerTextWithLine(headline = "Цена билета")

            price = fieldPriceComponent(act = activity)



            SpacerTextWithLine(headline = "Описание")

            description = fieldDescriptionComponent(act = activity)

            Spacer(modifier = Modifier.height(30.dp))

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
                    onClick = { /*TODO*/ },
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



            // КАЛЕНДАРЬ - https://www.geeksforgeeks.org/date-picker-in-android-using-jetpack-compose/
            // https://stackoverflow.com/questions/60417233/jetpack-compose-date-time-picker

        }
    }

    @Composable
    fun CategorySelectButton(onClick: ()-> Unit) {

        Button(
            onClick = {
                onClick()
            },

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
            shape = RoundedCornerShape(50)
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(id = chosenCategory.categoryName),
                style = Typography.labelMedium
            )
        }

    }

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


                // ------- ЗАГЛОВОК ВЫБЕРИТЕ ГОРОД и КНОПКА ЗАКРЫТЬ -----------
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

                    Spacer(modifier = Modifier.height(20.dp)) // разделител

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

                    // берем каждый item из списка citiesList и заполняем шаблон

                    items(categoriesList) { category ->

                        // ------------ строка с названием города -------------

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            //.padding(vertical = 10.dp)
                            .clickable {
                                // действие на нажатие на элемент
                                chosenCategory =
                                    category // выбранный город теперь тот, который выбрали, а не по умолчанию
                                onDismiss() // закрыть диалог
                            }
                        ) {
                            androidx.compose.material3.Text(
                                text = stringResource(id = category.categoryName), // само название города
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
package kz.dvij.dvij_compose3.createscreens

import kz.dvij.dvij_compose3.MainActivity
import android.annotation.SuppressLint
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.ui.theme.*

class CreateMeeting (act: MainActivity) {

    private var chosenCategory: CategoriesList = CategoriesList.DefaultCat

    @SuppressLint("ResourceType")
    @Composable
    fun CreateMeetingScreen (activity: MainActivity) {

        var phoneNumber by rememberSaveable { mutableStateOf("7") }
        var phoneNumberWhatsapp by rememberSaveable { mutableStateOf("7") }
        var openDialog = remember {mutableStateOf(false)}

        var headline = ""
        var description = ""
        var price = ""
        var phone = ""
        var whatsapp = ""


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

            Spacer(modifier = Modifier.height(20.dp))


            // ------ ЗАГОЛОВОК ---------


            Text(
                text = "Заголовок",
                style = Typography.labelMedium,
                color = Grey40
            )

            Spacer(modifier = Modifier.height(10.dp))

            headline = fieldHeadlineComponent(act = activity)

            Spacer(modifier = Modifier.height(10.dp))


            // ----- КАТЕГОРИЯ ---------

            Text(
                text = "Категория",
                style = Typography.labelMedium,
                color = Grey40
            )

            Spacer(modifier = Modifier.height(5.dp))

            if (openDialog.value) {
                CategoryChooseDialog {
                    openDialog.value = false
                }
            }

            Button(
                onClick = {
                    openDialog.value = true
                },

                border = BorderStroke(
                    width = if (chosenCategory == CategoriesList.DefaultCat) {
                        2.dp
                    } else {
                        0.dp
                    }, color = if (chosenCategory == CategoriesList.DefaultCat) {
                        Grey40
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
                    contentColor = if ( chosenCategory == CategoriesList.DefaultCat) {
                        Grey40
                    } else {
                        Grey100},
                ),
                shape = RoundedCornerShape(50)
            ) {

                Text(
                    text = stringResource(id = chosenCategory.categoryName),
                    style = Typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))




            // ------- ТЕЛЕФОН ДЛЯ ЗВОНКОВ ---------


            Text(
                text = "Телефон для звонков",
                style = Typography.labelMedium,
                color = Grey40
            )

            Spacer(modifier = Modifier.height(10.dp))

            phone = fieldPhoneComponent(phoneNumber,
                onPhoneChanged = { phoneNumber = it })

            Spacer(modifier = Modifier.height(10.dp))


            // ----- ТЕЛЕФОН ДЛЯ WHATSAPP


            Text(
                text = "Телефон для whatsapp",
                style = Typography.labelMedium,
                color = Grey40
            )

            Spacer(modifier = Modifier.height(10.dp))

            whatsapp = fieldPhoneComponent(phoneNumberWhatsapp,
                onPhoneChanged = { phoneNumberWhatsapp = it },
                icon = painterResource(id = R.drawable.whatsapp))

            Spacer(modifier = Modifier.height(10.dp))


            // ------ ОПИСАНИЕ ---------


            Text(
                text = "Описание",
                style = Typography.labelMedium,
                color = Grey40
            )

            Spacer(modifier = Modifier.height(10.dp))

            description = fieldDescriptionComponent(act = activity)

            Spacer(modifier = Modifier.height(10.dp))


            // --------- ЦЕНА -------------


            Text(
                text = "Цена билета",
                style = Typography.labelMedium,
                color = Grey40
            )

            Spacer(modifier = Modifier.height(10.dp))

            price = fieldPriceComponent(act = activity)

        }
    }

    @Composable
    fun CategoryChooseDialog (onDismiss: ()-> Unit){

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
                        modifier = Modifier.weight(1f)) // занять всю оставшуюся ширину

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

                ){

                    // наполнение ленивой колонки

                    // берем каждый item из списка citiesList и заполняем шаблон

                    items (categoriesList) { category->

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
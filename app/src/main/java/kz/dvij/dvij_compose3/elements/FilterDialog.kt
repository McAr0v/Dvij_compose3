package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.pickers.dataPickerWithRemember
import kz.dvij.dvij_compose3.ui.theme.*

class FilterDialog (val act: MainActivity) {

    // ----- ДИАЛОГ ВЫБОРА КАТЕГОРИИ

    @Composable
    fun FilterChooseDialog(
        cityForFilter: MutableState<String>,
        meetingCategoryForFilter: MutableState<String>,
        meetingDateForFilter: MutableState<String>,
        onDismiss: () -> Unit
    ) {

        // Список, в который поместим города из БД

        val citiesList = remember {
            mutableStateOf(listOf<CitiesList>())
        }

        // Читаем список городов
        act.chooseCityNavigation.readCityDataFromDb(citiesList)

        // Список категорий
        val categoriesList = remember {mutableStateOf(listOf<CategoriesList>())}

        val openCategoryDialog = remember { mutableStateOf(false) } // диалог КАТЕГОРИИ

        val openCityDialog = remember { mutableStateOf(false) } // диалог ГОРОДА

        // Запускаем функцию считывания списка категорий с базы данных
        act.categoryDialog.readMeetingCategoryDataFromDb(categoriesList)

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

                    Text(
                        text = "Сортировка", // текст заголовка
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


                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                ) {

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {

                        cityForFilter.value = act.chooseCityNavigation.citySelectButton(cityName = cityForFilter) {openCityDialog.value = true}

                        Spacer(modifier = Modifier.width(10.dp))

                        if (cityForFilter.value != "Выбери город"){

                            IconButton(onClick = { cityForFilter.value = "Выбери город" }) {

                                Icon(painter = painterResource(id = R.drawable.ic_close), contentDescription = "", tint = Grey00)

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {

                        meetingCategoryForFilter.value = act.categoryDialog.categorySelectButton(categoryName = meetingCategoryForFilter) { openCategoryDialog.value = true }

                        Spacer(modifier = Modifier.width(10.dp))

                        if (meetingCategoryForFilter.value != "Выбери категорию"){

                            IconButton(onClick = { meetingCategoryForFilter.value = "Выбери категорию" }) {

                                Icon(painter = painterResource(id = R.drawable.ic_close), contentDescription = "", tint = Grey00)

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {

                        meetingDateForFilter.value = dataPickerWithRemember(act = act, meetingDateForFilter)

                        Spacer(modifier = Modifier.width(10.dp))

                        if (meetingDateForFilter.value != "Выбери дату"){

                            IconButton(onClick = { meetingDateForFilter.value = "Выбери дату" }) {

                                Icon(painter = painterResource(id = R.drawable.ic_close), contentDescription = "", tint = Grey00)

                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                    Button(onClick = { onDismiss() }) {
                        Text(text = "Применить", color = Grey00)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {

                            val query = act.meetingDatabaseManager.createFilter()

                            val removeQuery = act.meetingDatabaseManager.getFilter(query)

                            meetingCategoryForFilter.value = removeQuery[1]
                            meetingDateForFilter.value = removeQuery[2]
                            cityForFilter.value = removeQuery[0]

                        }
                    ) {
                        Text(text = "Сбросить фильтр", color = Grey00)
                    }

                }

                if (openCityDialog.value) {

                    // Если при редактировании в мероприятии есть город, Передаем ГОРОД ИЗ МЕРОПРИЯТИЯ ДЛЯ РЕДАКТИРОВАНИЯ
                    act.chooseCityNavigation.CityChooseDialog(
                        cityName = cityForFilter,
                        citiesList
                    ) {
                        openCityDialog.value = false
                    }

                }

                // --- САМ ДИАЛОГ ВЫБОРА КАТЕГОРИИ -----

                if (openCategoryDialog.value) {

                    act.categoryDialog.CategoryChooseDialog(categoryName = meetingCategoryForFilter, categoriesList) {
                        openCategoryDialog.value = false
                    }

                }

            }
        }
    }
}
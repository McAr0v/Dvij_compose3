package kz.dvij.dvij_compose3.navigation

import androidx.compose.foundation.BorderStroke
import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.FOR_CARDS
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.ui.theme.*

class ChooseCityNavigation (val act: MainActivity) {

    var chosenCity = CitiesList("Выбери город", "default_city") // задаем выбранный город по умолчанию.

    private val cityDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("CitiesList") // Создаем ПАПКУ В БД для списка городов


    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ ГОРОДА С БАЗЫ ДАННЫХ ----------

    fun readCityDataFromDb(citiesList: MutableState<List<CitiesList>>){

        cityDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            // функция при изменении данных в БД

            override fun onDataChange(snapshot: DataSnapshot) {

                val cityArray = ArrayList<CitiesList>() // Создаем пустой список городов

                // добираемся до списка городов

                for (item in snapshot.children){

                    // создаем переменную city, в которую в конце поместим наш ДАТАКЛАСС с городами с БД

                    val city = item.child("CityData").getValue(CitiesList::class.java)

                    if (city != null && city.cityName != act.resources.getString(R.string.cm_no_city)) {cityArray.add(city)} // если city не null и название города не "Выберите город", то добавить в список

                }

                if (cityArray.isEmpty()){
                    citiesList.value = listOf() // если список-черновик городов пустой, то добавить пустой список
                } else {
                    citiesList.value = cityArray // если есть города в списке-черновике, то их и возвращаем
                }

            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }


    // ------ ФУНКЦИЯ ОТОБРАЖЕНИЯ В БОКОВОМ МЕНЮ ГОРОДА -------------

    @Composable
    fun CityHeaderSideNavigation (cityName: MutableState<String>, citiesList: MutableState<List<CitiesList>>) {

        // РАЗДЕЛ БОКОВОГО МЕНЮ С ГОРОДОМ

        Column( // ПОМЕЩАЕМ ВСЕ СОДЕРЖИМОЕ В КОЛОКНКУ
            modifier = Modifier
                .background(Grey_OnBackground) // цвет фона
                .fillMaxWidth() // занимаем всю ширину
                .padding(20.dp) // отступы
        ) {

            // значение отображения диалога с выбором города. По умолчанию false - закрытый диалог
            val openDialog = remember {
                mutableStateOf(false)
            }

            // ---------- ЗАГОЛОВОК "ГОРОД" ---------

            androidx.compose.material.Text( // ЗАГОЛОВОК ГОРОД
                text = stringResource(id = R.string.city), // текст заголовка
                color = Grey_Text, // цвет заголовка
                style = Typography.labelMedium // стиль заголовка
            )


            Spacer(modifier = Modifier.height(10.dp)) // разделитель между заголовком и городом


            // СТРОКА С ИКОНКАМИ И НАЗВАНИЕМ ГОРОДА

            Row (
                modifier = Modifier
                    .fillMaxWidth() // строка должна занимать всю ширину
                    .clickable { // действие на нажатие. Если нажать - откроется диалоговое меню с выбором города

                        openDialog.value =
                            true // помещаем в переменную openDialog значение true - чтобы диалог открылся

                    },
                verticalAlignment = Alignment.CenterVertically // выравнивание по вертикали по центру

            ) {


                // ------------ ВЫЗОВ ДИАЛОГА С ВЫБОРОМ ГОРОДА -------------

                if (openDialog.value) {

                    // если значение диалога true то вызываем открытие диалога и передаем ему
                    // значение для закрытия - это значение false

                    CityChooseDialog(cityName = cityName, citiesList) {openDialog.value = false}

                }


                // -------------  Иконка возле текста ------------------

                androidx.compose.material3.Icon(
                    tint = WhiteDvij, // цвет иконки
                    painter = painterResource(id = R.drawable.ic_baseline_places), // задаем иконку
                    contentDescription = stringResource(id = R.string.cd_location) // описание для слабовидящих
                )

                // разделитель между текстом и иконкой
                Spacer(modifier = Modifier.width(15.dp))

                // -------------- НАЗВАНИЕ ГОРОДА -------------------

                if (cityName.value != "Выбери город" && cityName.value != "" ) {

                    androidx.compose.material.Text(
                        text = cityName.value, // из chosenCity достаем название города
                        style = Typography.bodyMedium, // Стиль текста
                        modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                        color = WhiteDvij // цвет текста
                    )

                } else {

                    androidx.compose.material.Text(
                        text = "Выбери город", // из chosenCity достаем название города
                        style = Typography.bodyMedium, // Стиль текста
                        modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                        color = Grey_Text // цвет текста
                    )

                }

                // разделитель между текстом и иконкой
                Spacer(modifier = Modifier.width(15.dp))

                // ------------- ИКОНКА РЕДАКТИРОВАТЬ -------------------

                androidx.compose.material3.Icon(
                    tint = WhiteDvij, // цвет иконки
                    painter = painterResource(id = R.drawable.ic_right), // задаем иконку
                    contentDescription = stringResource(id = R.string.cd_location) // описание для слабовидящих
                )
            }
        }
    }



    // --------- САМ ВСПЛЫВАЮЩИЙ ДИАЛОГ С ВЫБОРОМ ГОРОДА ------------

    @Composable
    fun CityChooseDialog (cityName: MutableState<String>, citiesList: MutableState<List<CitiesList>>, onDismiss: ()-> Unit){


        // ------ САМ ДИАЛОГ ---------

        Dialog(
            onDismissRequest = { onDismiss() } // действие на нажатие за пределами диалога
        ) {

            // -------- СОДЕРЖИМОЕ ДИАЛОГА ---------

            Column(
                modifier = Modifier
                    .border(
                        2.dp, // толщина границы
                        color = YellowDvij, // цвет границы
                        shape = RoundedCornerShape(15.dp) // скругление углов
                    )
                    .background(
                        Grey_Background, // цвет фона
                        shape = RoundedCornerShape(15.dp) // скругление углов
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

                    Text(
                        text = stringResource(id = R.string.choose_city), // текст заголовка
                        style = Typography.titleMedium, // стиль заголовка
                        color = WhiteDvij, // цвет заголовка
                        modifier = Modifier.weight(1f)) // занять всю оставшуюся ширину

                    Spacer(modifier = Modifier.height(20.dp)) // разделител

                    // ------------- ИКОНКА ЗАКРЫТЬ ----------------

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close), // сама иконка
                        contentDescription = stringResource(id = R.string.close_page), // описание для слабовидяших
                        tint = WhiteDvij, // цвет иконки
                        modifier = Modifier.clickable { onDismiss() } // действие на нажатие
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))



                // ---------- СПИСОК ГОРОДОВ -------------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth() // занять ширину
                        .background(
                            Grey_OnBackground, // цвет фона
                            shape = RoundedCornerShape(15.dp) // скругление углов
                        )
                        .padding(20.dp), // отступ
                verticalArrangement = Arrangement.spacedBy(20.dp) // расстояние между элементами списка

                ){

                    // наполнение ленивой колонки

                    // берем каждый item из списка citiesList и заполняем шаблон

                    items (citiesList.value) { city->

                        // ------------ строка с названием города -------------

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                            // действие на нажатие на элемент
                            cityName.value =
                                city.cityName.toString() // выбранный город теперь тот, который выбрали, а не по умолчанию
                            onDismiss() // закрыть диалог
                            }
                        ) {
                            Text(
                                text = city.cityName!!, // само название города
                                color = WhiteDvij, // цвет текста
                                style = Typography.bodySmall // стиль текста
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun citySelectButton(cityName: MutableState<String>, onClick: ()-> Unit): String {

        Button(
            onClick = {
                onClick()
            },

            // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            border = BorderStroke(
                width = if (cityName.value != "" && cityName.value != "null" && cityName.value != "Выбери город") {
                    0.dp
                } else {
                    2.dp
                }, color = if (cityName.value != "" && cityName.value != "null" && cityName.value != "Выбери город") {
                    YellowDvij
                } else {
                    Grey_ForCards

                }
            ),

            // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (cityName.value != "" && cityName.value != "null" && cityName.value != "Выбери город") {
                    YellowDvij

                } else {
                    Grey_ForCards
                },
                contentColor = if (cityName.value != "" && cityName.value != "null" && cityName.value != "Выбери город") {
                    Grey_OnBackground
                } else {
                    WhiteDvij
                },
            ),
            shape = RoundedCornerShape(50) // скругленные углы кнопки
        ) {

            Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

            if (cityName.value != "" && cityName.value != "null" && cityName.value != "Выбери город") {

                Text(
                    text = cityName.value, // текст кнопки
                    style = Typography.labelMedium, // стиль текста
                    color = if (cityName.value == "Выбери город") {
                        WhiteDvij
                    } else {
                        Grey_OnBackground
                    }
                )

            } else {

                Text(
                    text = "Выбери город", // текст кнопки
                    style = Typography.labelMedium, // стиль текста
                    color = WhiteDvij
                )

            }

        }
        return cityName.value
    }
}
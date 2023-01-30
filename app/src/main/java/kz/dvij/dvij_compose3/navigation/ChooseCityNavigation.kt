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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.ui.theme.*

class ChooseCityNavigation (act: MainActivity) {

    var chosenCity = CitiesList("Выберите город", "default_city") // задаем выбранный город по умолчанию.

    val cityDatabase = FirebaseDatabase // обращаемся к БД
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

                    if (city != null && city.cityName != "Выберите город") {cityArray.add(city)} // если city не null и название города не "Выберите город", то добавить в список

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
    fun CityHeaderSideNavigation (citiesList: MutableState<List<CitiesList>>) {

        // РАЗДЕЛ БОКОВОГО МЕНЮ С ГОРОДОМ

        Column( // ПОМЕЩАЕМ ВСЕ СОДЕРЖИМОЕ В КОЛОКНКУ
            modifier = Modifier
                .background(Grey100) // цвет фона
                .fillMaxWidth() // занимаем всю ширину
                .padding(20.dp) // отступы
        ) {
            val context = LocalContext.current // инициализируем контекст для ТОСТОВ

            // значение отображения диалога с выбором города. По умолчанию false - закрытый диалог
            var openDialog = remember {
                mutableStateOf(false)
            }

            // ---------- ЗАГОЛОВОК "ГОРОД" ---------

            androidx.compose.material.Text( // ЗАГОЛОВОК ГОРОД
                text = stringResource(id = R.string.city), // текст заголовка
                color = Grey40, // цвет заголовка
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

                    CityChooseDialog(citiesList) {openDialog.value = false}

                }


                // -------------  Иконка возле текста ------------------

                androidx.compose.material3.Icon(
                    tint = Grey40, // цвет иконки
                    painter = painterResource(id = R.drawable.ic_baseline_places), // задаем иконку
                    contentDescription = stringResource(id = R.string.cd_location) // описание для слабовидящих
                )

                // разделитель между текстом и иконкой
                Spacer(modifier = Modifier.width(15.dp))

                // -------------- НАЗВАНИЕ ГОРОДА -------------------
                androidx.compose.material.Text(
                    text = chosenCity.cityName!!, // из chosenCity достаем название города
                    style = Typography.labelLarge, // Стиль текста
                    modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                    color = Grey40 // цвет текста
                )

                // разделитель между текстом и иконкой
                Spacer(modifier = Modifier.width(15.dp))

                // ------------- ИКОНКА РЕДАКТИРОВАТЬ -------------------

                androidx.compose.material3.Icon(
                    tint = Grey40, // цвет иконки
                    painter = painterResource(id = R.drawable.ic_edit), // задаем иконку
                    contentDescription = stringResource(id = R.string.to_change_location) // описание для слабовидящих
                )
            }
        }
    }



    // --------- САМ ВСПЛЫВАЮЩИЙ ДИАЛОГ С ВЫБОРОМ ГОРОДА ------------

    @Composable
    fun CityChooseDialog (citiesList: MutableState<List<CitiesList>> ,onDismiss: ()-> Unit){


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
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, // вертикальное выравнивание элементов по центру
                    horizontalArrangement = Arrangement.End // выравнивание по горизонтали
                ) {

                    // --------- ЗАГОЛОВОК ----------

                    Text(
                        text = stringResource(id = R.string.choose_city), // текст заголовка
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



                // ---------- СПИСОК ГОРОДОВ -------------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth() // занять ширину
                        .background(
                            Grey100, // цвет фона
                            shape = RoundedCornerShape(10.dp) // скругление углов
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
                            chosenCity = city // выбранный город теперь тот, который выбрали, а не по умолчанию
                            onDismiss() // закрыть диалог
                            }
                        ) {
                            Text(
                                text = city.cityName!!, // само название города
                                color = Grey40, // цвет текста
                                style = Typography.bodyMedium // стиль текста
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun citySelectButton(onClick: ()-> Unit): CitiesList {

        Button(
            onClick = {
                onClick()
            },

            // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            border = BorderStroke(
                width = if (chosenCity.cityName == "Выберите город") {
                    2.dp
                } else {
                    0.dp
                }, color = if (chosenCity.cityName == "Выберите город") {
                    Grey60
                } else {
                    Grey95
                }
            ),

            // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (chosenCity.cityName == "Выберите город") {
                    Grey95
                } else {
                    PrimaryColor
                },
                contentColor = if (chosenCity.cityName == "Выберите город") {
                    Grey60
                } else {
                    Grey100
                },
            ),
            shape = RoundedCornerShape(50) // скругленные углы кнопки
        ) {

            Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

            Text(
                text = chosenCity.cityName!!, // текст кнопки
                style = Typography.labelMedium, // стиль текста
                color = if (chosenCity.cityName == "Выберите город") {
                    Grey60
                } else {
                    Grey100
                }
            )

        }
        return chosenCity
    }
}
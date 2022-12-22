package kz.dvij.dvij_compose3.navigation

import kz.dvij.dvij_compose3.MainActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.ui.theme.*

class ChooseCityNavigation (act: MainActivity) {

    var chosenCity: CitiesList = CitiesList.Almaty // задаем выбранный город по умолчанию. Это Алматы


    // ------ ФУНКЦИЯ ОТОБРАЖЕНИЯ В БОКОВОМ МЕНЮ ГОРОДА -------------

    @Composable
    fun CityHeaderSideNavigation () {

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

                    CityChooseDialog {openDialog.value = false}

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
                    text = stringResource(id = chosenCity.cityName), // из chosenCity достаем название города
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
    fun CityChooseDialog (onDismiss: ()-> Unit){

        // Создаем список городов

        val citiesList = mutableListOf<CitiesList>(
            CitiesList.Ridder,
            CitiesList.Astana,
            CitiesList.UKa,
            CitiesList.Altay,
            CitiesList.Almaty
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
                        .padding(20.dp) // отступ

                ){

                    // наполнение ленивой колонки

                    // берем каждый item из списка citiesList и заполняем шаблон

                    items (citiesList) { city->

                        // ------------ строка с названием города -------------

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .clickable {
                            // действие на нажатие на элемент
                            chosenCity = city // выбранный город теперь тот, который выбрали, а не по умолчанию
                            onDismiss() // закрыть диалог
                            }
                        ) {
                            Text(
                                text = stringResource(id = city.cityName), // само название города
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
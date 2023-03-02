package kz.dvij.dvij_compose3.navigation

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.ui.theme.*

class ChoosePlaceDialog (val act: MainActivity) {

    // --------- САМ ВСПЛЫВАЮЩИЙ ДИАЛОГ С ВЫБОРОМ Заведений ------------

    @Composable
    fun PlaceChooseDialog (placesList: MutableState<List<PlacesAdsClass>>, chosenOutPlace: MutableState<PlacesAdsClass>, ifChoose: MutableState<Boolean>, onDismiss: ()-> Unit){


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


                // ------- ЗАГЛОВОК ВЫБЕРИТЕ заведение и КНОПКА ЗАКРЫТЬ -----------
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание элементов по центру
                    horizontalArrangement = Arrangement.End // выравнивание по горизонтали
                ) {

                    // --------- ЗАГОЛОВОК ----------

                    Text(
                        text = "Выбери заведение", // текст заголовка
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

                if (placesList.value != listOf<PlacesAdsClass>(
                        PlacesAdsClass(
                            logo=null,
                            placeName=null,
                            placeDescription= "Default",
                            phone=null,
                            whatsapp=null,
                            telegram=null,
                            instagram=null,
                            category=null,
                            city=null,
                            address=null,
                            placeKey=null,
                            owner=null,
                            mondayOpenTime = null,
                            mondayCloseTime = null,
                            tuesdayOpenTime = null,
                            tuesdayCloseTime = null,
                            wednesdayOpenTime = null,
                            wednesdayCloseTime = null,
                            thursdayOpenTime = null,
                            thursdayCloseTime = null,
                            fridayOpenTime = null,
                            fridayCloseTime = null,
                            saturdayOpenTime = null,
                            saturdayCloseTime = null,
                            sundayOpenTime = null,
                            sundayCloseTime = null
                )
                    )) {

                    // ---------- СПИСОК ЗАВЕДЕНИЙ -------------

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth() // занять ширину
                            .background(
                                Grey100, // цвет фона
                                shape = RoundedCornerShape(10.dp) // скругление углов
                            )
                            .padding(20.dp), // отступ
                        verticalArrangement = Arrangement.spacedBy(20.dp) // расстояние между элементами списка

                    ) {


                        // наполнение ленивой колонки

                        // берем каждый item из списка и заполняем шаблон

                        items(placesList.value) { place ->

                            // ------------ строка с названием заведения -------------

                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // действие на нажатие на элемент
                                    chosenOutPlace.value =
                                        place // выбранный город теперь тот, который выбрали, а не по умолчанию
                                    ifChoose.value = true
                                    onDismiss() // закрыть диалог
                                }
                            ) {
                                Text(
                                    text = place.placeName!!, // само название города
                                    color = Grey40, // цвет текста
                                    style = Typography.bodyMedium // стиль текста
                                )
                            }
                        }

                    }

                } else {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Grey100, // цвет фона
                                shape = RoundedCornerShape(10.dp) // скругление углов
                    )
                            .padding(20.dp), // отступ
                        verticalArrangement = Arrangement.spacedBy(20.dp) // расстояние между элементами списка)
                    ){

                        Text(
                            text = "У тебя еще нет созданных заведений", // само название города
                            color = Grey40, // цвет текста
                            style = Typography.bodyMedium // стиль текста
                        )

                    }
                }
            }
        }
    }

    @Composable
    fun placeSelectButton(chosenOutPlace: MutableState<PlacesAdsClass>, onClick: ()-> Unit): String? {

        Button(
            onClick = {
                onClick()
            },

            // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            border = BorderStroke(
                width = if (chosenOutPlace.value.placeName == "Выбери заведение" || chosenOutPlace.value.placeKey == "null") {
                    2.dp
                } else {
                    0.dp
                }, color = if (chosenOutPlace.value.placeName == "Выбери заведение" || chosenOutPlace.value.placeKey == "null") {
                    Grey60
                } else {
                    Grey95
                }
            ),

            // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (chosenOutPlace.value.placeName == "Выбери заведение" || chosenOutPlace.value.placeKey == "null") {
                    Grey95
                } else {
                    PrimaryColor
                },
                contentColor = if (chosenOutPlace.value.placeName == "Выбери заведение" || chosenOutPlace.value.placeKey == "null") {
                    Grey60
                } else {
                    Grey100
                },
            ),
            shape = RoundedCornerShape(50) // скругленные углы кнопки
        ) {

            Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

            Text(
                text = chosenOutPlace.value.placeName!!, // текст кнопки
                style = Typography.labelMedium, // стиль текста
                color = if (chosenOutPlace.value.placeName == "Выбери заведение" || chosenOutPlace.value.placeKey == "null") {
                    Grey60
                } else {
                    Grey100
                }
            )

        }
        return chosenOutPlace.value.placeName
    }

}
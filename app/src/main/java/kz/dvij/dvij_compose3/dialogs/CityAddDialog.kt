package kz.dvij.dvij_compose3.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.SECONDARY
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.elements.fieldTextComponent
import kz.dvij.dvij_compose3.navigation.CITIES_LIST_ROOT
import kz.dvij.dvij_compose3.pickers.dataPickerWithRemember
import kz.dvij.dvij_compose3.ui.theme.*

class CityAddDialog () {

    @Composable
    fun AddCityDialog(
        onDismiss: () -> Unit,
        navController: NavController,
        act: MainActivity,
        openLoadingState: MutableState<Boolean>
    ) {

        var cityName: String = ""
        var cityCode: String = ""


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


                // ------- ЗАГЛОВОК и КНОПКА ЗАКРЫТЬ -----------

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание элементов по центру
                    horizontalArrangement = Arrangement.End // выравнивание по горизонтали
                ) {

                    // --------- ЗАГОЛОВОК ----------

                    Text(
                        text = "Добавить город", // текст заголовка
                        style = Typography.titleMedium, // стиль заголовка
                        color = WhiteDvij, // цвет заголовка
                        modifier = Modifier.weight(1f)
                    ) // занять всю оставшуюся ширину

                    Spacer(modifier = Modifier.height(20.dp)) // разделитель

                    // ------------- ИКОНКА ЗАКРЫТЬ ----------------

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close), // сама иконка
                        contentDescription = stringResource(id = R.string.close_page), // описание для слабовидяших
                        tint = WhiteDvij, // цвет иконки
                        modifier = Modifier.clickable { onDismiss() } // действие на нажатие
                    )
                }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 30.dp),
                ) {


                    cityName = fieldTextComponent(placeHolder = "Название города")

                    Spacer(modifier = Modifier.height(20.dp))

                    cityCode = fieldTextComponent(placeHolder = "Код города")



                }


                Spacer(modifier = Modifier.height(20.dp))




                // ---- КНОПКИ

                ButtonCustom(buttonText = "Опубликовать") {

                    if (cityName != "" && cityCode != "") {

                        val filledCity = CitiesList(
                            cityName = cityName,
                            code = cityCode
                        )

                        onDismiss()

                        openLoadingState.value = true

                        act.chooseCityNavigation.addCityInDb(filledCity = filledCity) { result ->

                            if (result) {

                                Toast.makeText(act, "Город успешно добавлен", Toast.LENGTH_SHORT).show()
                                navController.navigate(CITIES_LIST_ROOT)

                            } else {

                                Toast.makeText(act, "Город не добавлен. Что-то пошло не так(", Toast.LENGTH_SHORT).show()

                            }

                        }

                    } else {

                        Toast.makeText(act, "Не все поля заполнены", Toast.LENGTH_SHORT).show()

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                ButtonCustom(buttonText = "Отменить", typeButton = SECONDARY) {

                    onDismiss()

                }

            }
        }
    }

}
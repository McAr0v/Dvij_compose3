package kz.dvij.dvij_compose3.pickers

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*
import java.util.*

// ------- ВЫБОР ВРЕМЕНИ ----------

@Composable
fun timePicker(inputTime: String = ""): String {

    var timeResult = "" // возвращаемая переменная

    val mContext = LocalContext.current // инициализируем контекст

    val mCalendar = Calendar.getInstance() // инициализируем календарь

    val mHour = mCalendar[Calendar.HOUR_OF_DAY] // инициализируем часы
    val mMinute = mCalendar[Calendar.MINUTE] // инициализируем минуты

    mCalendar.time = Date() // инициализируем текущее время



    val mClock = remember{ mutableStateOf(inputTime) } // создаем переменную, в которую будем записывать время

    // инициализируем диалог выбора времени
    val mClockPickerDialog = TimePickerDialog(
        mContext, // передаем контекст

        // ПРОЧИЕ НАСТРОЙКИ
        { _: TimePicker, mHour: Int, mMinute: Int ->
            mClock.value = "${
                when (mHour) {
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> "$mHour"
                }
            }:${
                when (mMinute) {
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> "$mMinute"
                }
            }"
        }, mHour, mMinute, true
    )

    // помещаем все в колонку
    Column() {

        // ---- САМА КНОПКА ---------
        Button(
            onClick = {
                mClockPickerDialog.show() // при нажатии открываем диалог выбора времени
            },

            // настройки границы
            border = BorderStroke(
                width = if (mClock.value == "") {
                    2.dp
                } else {
                    0.dp
                },

                color = if (mClock.value == "") {
                    Grey_ForCards
                } else {
                    YellowDvij
                }
            ),

            // цвета кнопки

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (mClock.value == "") {
                    Grey_ForCards
                } else {
                    YellowDvij
                },
                contentColor = if (mClock.value == "") {
                    WhiteDvij
                } else {
                    Grey_OnBackground
                },
            ),

            shape = RoundedCornerShape(50) // скругление углов

        ) {

            // СОДЕРЖИМОЕ КНОПКИ

            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = if (mClock.value == "") {
                    stringResource(id = R.string.piker_time)} else {mClock.value},
                style = Typography.labelMedium
            )
        }
    }

    timeResult = mClock.value // записываем в переменную выбранную дату

    return timeResult

}

@Composable
fun timePickerInPlaceCreate(inputTime: String = ""): String {

    var timeResult = "" // возвращаемая переменная

    val mContext = LocalContext.current // инициализируем контекст

    val mCalendar = Calendar.getInstance() // инициализируем календарь

    val mHour = mCalendar[Calendar.HOUR_OF_DAY] // инициализируем часы
    val mMinute = mCalendar[Calendar.MINUTE] // инициализируем минуты

    mCalendar.time = Date() // инициализируем текущее время



    val mClock = remember{ mutableStateOf(inputTime) } // создаем переменную, в которую будем записывать время

    // инициализируем диалог выбора времени
    val mClockPickerDialog = TimePickerDialog(
        mContext, // передаем контекст

        // ПРОЧИЕ НАСТРОЙКИ
        { _: TimePicker, mHour: Int, mMinute: Int ->
            mClock.value = "${
                when (mHour) {
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> "$mHour"
                }
            }:${
                when (mMinute) {
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> "$mMinute"
                }
            }"
        }, mHour, mMinute, true
    )

    // помещаем все в колонку
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        // ---- САМА КНОПКА ---------
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                mClockPickerDialog.show() // при нажатии открываем диалог выбора времени
            },

            // настройки границы
            border = BorderStroke(
                width = if (mClock.value == "") {
                    2.dp
                } else {
                    0.dp
                },

                color = if (mClock.value == "") {
                    Grey_ForCards
                } else {
                    YellowDvij
                }
            ),

            // цвета кнопки

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (mClock.value == "") {
                    Grey_ForCards
                } else {
                    YellowDvij
                },
                contentColor = if (mClock.value == "") {
                    WhiteDvij
                } else {
                    Grey_OnBackground
                },
            ),

            shape = RoundedCornerShape(50) // скругление углов

        ) {

            // СОДЕРЖИМОЕ КНОПКИ

            Text(
                text = if (mClock.value == "") {
                    stringResource(id = R.string.piker_time)} else {mClock.value},
                style = Typography.labelMedium
            )
        }

        if (mClock.value != ""){

            //Spacer(modifier = Modifier.height(5.dp))

            Text(text = "Сбросить время", style = Typography.labelMedium, color = WhiteDvij, modifier = Modifier.clickable { mClock.value = "" })

        }

    }









    timeResult = mClock.value // записываем в переменную выбранную дату

    return timeResult

}
package kz.dvij.dvij_compose3.pickers

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*
import java.util.*


@Composable
fun dataPicker(act: MainActivity, inputDate: String = ""): String{

    // https://www.geeksforgeeks.org/material-design-date-picker-in-android/ - настройка внешнего вида дата пикера

    var dataResult = "" // возвращаемая переменная
    val mContext = LocalContext.current // инициализируем контекст

    val mCalendar = Calendar.getInstance() // инициализируем календарь

    val mYear: Int = mCalendar.get(Calendar.YEAR) // инициализируем год
    val mMonth: Int = mCalendar.get(Calendar.MONTH)// инициализируем месяц
    val mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)// инициализируем день


    mCalendar.time = Date() // берем из календаря текущую дату

    val mDate = remember{ mutableStateOf(inputDate) } // создам переменную дата

    // создаем переменную с диалогом выбора даты

    val mDatePickerDialog = DatePickerDialog(
        mContext, // передаем контекст

        // дополнительные настройки

        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth ${
                when (mMonth+1) {
                    1 -> act.getString(R.string.january)
                    2 -> act.getString(R.string.february)
                    3 -> act.getString(R.string.march)
                    4 -> act.getString(R.string.april)
                    5 -> act.getString(R.string.may)
                    6 -> act.getString(R.string.june)
                    7 -> act.getString(R.string.july)
                    8 -> act.getString(R.string.august)
                    9 -> act.getString(R.string.september)
                    10 -> act.getString(R.string.october)
                    11 -> act.getString(R.string.november)
                    else -> act.getString(R.string.december)
                }
            } $mYear"
        }, mYear, mMonth, mDay
    )

    mDatePickerDialog.datePicker.minDate = mCalendar.timeInMillis // берем минимальную дату для возможности выбора (сегодня)

    // ---- КНОПКА ЗАПУСКА ВЫБОРА ДАТЫ -----

    // Идея в том, чтобы сделать кнопку, которая видоизменяется в зависимости от того, выбрана дата или нет
    // если выбрана, то будет выглядеть как ТАГ, а если не выбрана - то как кнопка Secondary

    Button(
        onClick = {
            mDatePickerDialog.show() // при нажатии запускаем диалог
        },

        // настройки границы

        border = BorderStroke(
            // толщина границы
            width = if (mDate.value == "") {
                2.dp
            } else {
                0.dp
            },
            // цвет границы
            color = if (mDate.value == "") {
                Grey60
            } else {
                Grey95
            }
        ),

        // цвета кнопки
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (mDate.value == "") {
                Grey95
            } else {
                PrimaryColor
            },
            contentColor = if (mDate.value == "") {
                Grey60
            } else {
                Grey100
            },
        ),
        shape = RoundedCornerShape(50) // скругление углов
    ) {

        // ------ СОДЕРЖИМОЕ КНОПКИ --------

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (mDate.value == "") {
                stringResource(id = R.string.piker_date)
            } else {mDate.value},
            style = Typography.labelMedium
        )
    }

    dataResult = mDate.value // помещаем в возвращаемую переменную выбранную дату

    return dataResult
}

@Composable
fun dataPickerWithRemember(act: MainActivity, mDate: MutableState<String>): String{

    // https://www.geeksforgeeks.org/material-design-date-picker-in-android/ - настройка внешнего вида дата пикера

    var dataResult = "" // возвращаемая переменная
    val mContext = LocalContext.current // инициализируем контекст

    val mCalendar = Calendar.getInstance() // инициализируем календарь

    val mYear: Int = mCalendar.get(Calendar.YEAR) // инициализируем год
    val mMonth: Int = mCalendar.get(Calendar.MONTH)// инициализируем месяц
    val mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)// инициализируем день


    mCalendar.time = Date() // берем из календаря текущую дату

    // создаем переменную с диалогом выбора даты

    val mDatePickerDialog = DatePickerDialog(
        mContext, // передаем контекст

        // дополнительные настройки

        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth ${
                when (mMonth+1) {
                    1 -> act.getString(R.string.january)
                    2 -> act.getString(R.string.february)
                    3 -> act.getString(R.string.march)
                    4 -> act.getString(R.string.april)
                    5 -> act.getString(R.string.may)
                    6 -> act.getString(R.string.june)
                    7 -> act.getString(R.string.july)
                    8 -> act.getString(R.string.august)
                    9 -> act.getString(R.string.september)
                    10 -> act.getString(R.string.october)
                    11 -> act.getString(R.string.november)
                    else -> act.getString(R.string.december)
                }
            } $mYear"
        }, mYear, mMonth, mDay
    )

    mDatePickerDialog.datePicker.minDate = mCalendar.timeInMillis // берем минимальную дату для возможности выбора (сегодня)

    // ---- КНОПКА ЗАПУСКА ВЫБОРА ДАТЫ -----

    // Идея в том, чтобы сделать кнопку, которая видоизменяется в зависимости от того, выбрана дата или нет
    // если выбрана, то будет выглядеть как ТАГ, а если не выбрана - то как кнопка Secondary

    Button(
        onClick = {
            mDatePickerDialog.show() // при нажатии запускаем диалог
        },

        // настройки границы

        border = BorderStroke(
            // толщина границы
            width = if (mDate.value == "" || mDate.value == "Выбери дату" ) {
                2.dp
            } else {
                0.dp
            },
            // цвет границы
            color = if (mDate.value == "" || mDate.value == "Выбери дату") {
                Grey60
            } else {
                Grey95
            }
        ),

        // цвета кнопки
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (mDate.value == "" || mDate.value == "Выбери дату") {
                Grey95
            } else {
                PrimaryColor
            },
            contentColor = if (mDate.value == "" || mDate.value == "Выбери дату") {
                Grey60
            } else {
                Grey100
            },
        ),
        shape = RoundedCornerShape(50) // скругление углов
    ) {

        // ------ СОДЕРЖИМОЕ КНОПКИ --------

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (mDate.value == "" || mDate.value == "Выбери дату") {
                stringResource(id = R.string.piker_date)
            } else {mDate.value},
            style = Typography.labelMedium
        )
    }

    dataResult = mDate.value // помещаем в возвращаемую переменную выбранную дату

    return dataResult
}



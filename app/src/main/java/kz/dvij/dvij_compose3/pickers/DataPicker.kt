package kz.dvij.dvij_compose3.pickers

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*
import java.util.*


@Composable
fun dataPicker(): String{

    var dataResult = "" // возвращаемая переменная
    val mContext = LocalContext.current // инициализируем контекст

    val mCalendar = Calendar.getInstance() // инициализируем календарь

    val mYear: Int = mCalendar.get(Calendar.YEAR) // инициализируем год
    val mMonth: Int = mCalendar.get(Calendar.MONTH)// инициализируем месяц
    val mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)// инициализируем день

    mCalendar.time = Date() // берем из календаря текущую дату

    val mDate = remember{ mutableStateOf("") } // создам переменную дата

    // создаем переменную с диалогом выбора даты

    val mDatePickerDialog = DatePickerDialog(
        mContext, // передаем контекст

        // дополнительные настройки

        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth ${
                when (mMonth+1) {
                    1 -> "января"
                    2 -> "февраля"
                    3 -> "марта"
                    4 -> "апреля"
                    5 -> "мая"
                    6 -> "июня"
                    7 -> "июля"
                    8 -> "августа"
                    9 -> "сентября"
                    10 -> "октября"
                    11 -> "ноября"
                    else -> "декабря"
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



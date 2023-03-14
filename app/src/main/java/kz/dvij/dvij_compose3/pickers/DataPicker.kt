package kz.dvij.dvij_compose3.pickers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
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
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun dataPicker(act: MainActivity, inputDate: String = "", chosenDate: MutableState<String>? = null): String{

    // https://www.geeksforgeeks.org/material-design-date-picker-in-android/ - настройка внешнего вида дата пикера

    var chosenDay: String = "" // Инициализируем число, которую мы уже выбрали и которая сразу будет отмечена на календаре
    var chosenMonth: String = "" // Инициализируем месяц, который мы уже выбрали и который сразу будет отмечен на календаре
    var chosenYear: String = "" // Инициализируем год, который мы уже выбрали и который сразу будет отмечен на календаре

    if (chosenDate?.value != null && chosenDate.value != "Выбери дату" && chosenDate.value != ""){

        val splitData = act.filterFunctions.splitData(chosenDate.value) // Разбиваем полученную дату на составляющие

        chosenDay = splitData[0] // указываем число
        chosenMonth = act.filterFunctions.monthToNumber(splitData[1]) // превращаем название месяца в цифру
        chosenYear = splitData[2] // указываем год

    }

    var dataResult = "" // возвращаемая переменная
    val mContext = LocalContext.current // инициализируем контекст

    val mCalendar = Calendar.getInstance() // инициализируем календарь

    // Переменные числа / месяца / года, выбранные при открывании диалога

    val mYear1: Int = if (chosenYear != ""){
        chosenYear.toInt()
    } else {
        mCalendar.get(Calendar.YEAR) // инициализируем год
    }

    val mMonth1: Int = if (chosenMonth != ""){
        chosenMonth.toInt()-1 // здесь -1 обязательно - тут это уебанская система считать с 0. Типа январь это 0, февраль 1 и тд
    } else {
        mCalendar.get(Calendar.MONTH)// инициализируем месяц
    }

    val mDay1: Int = if (chosenDay != ""){
        chosenDay.toInt()
    } else {
        mCalendar.get(Calendar.DAY_OF_MONTH)// инициализируем день
    }

    mCalendar.time = Date() // берем из календаря текущую дату

    val mDate = remember{ mutableStateOf(inputDate) } // создам переменную дата

    // создаем переменную с диалогом выбора даты

    val mDatePickerDialog = DatePickerDialog(
        mContext, // передаем контекст

        // дополнительные настройки

        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth ${act.filterFunctions.numberToNameOfMonth(mMonth)} $mYear"
        }, mYear1, mMonth1, mDay1
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

    if (chosenDate != null){
        chosenDate.value = mDate.value
    }

    dataResult = mDate.value // помещаем в возвращаемую переменную выбранную дату

    return dataResult
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun dataPickerWithRemember(act: MainActivity, chosenDay: MutableState<String>, ifChooseDay: MutableState<String>): String{

    var inputDate: String = "" // Инициализируем число, которую мы уже выбрали и которая сразу будет отмечена на календаре
    var inputMonth: String = "" // Инициализируем месяц, который мы уже выбрали и который сразу будет отмечен на календаре
    var inputYear: String = "" // Инициализируем год, который мы уже выбрали и который сразу будет отмечен на календаре

    if (chosenDay.value != "Выбери дату"){

        val splitData = act.filterFunctions.splitData(ifChooseDay.value) // Разбиваем полученную дату на составляющие

        inputDate = splitData[0] // указываем число
        inputMonth = act.filterFunctions.monthToNumber(splitData[1]) // превращаем название месяца в цифру
        inputYear = splitData[2] // указываем год

    }

    // https://www.geeksforgeeks.org/material-design-date-picker-in-android/ - настройка внешнего вида дата пикера

    var dataResult = "" // возвращаемая переменная
    val mContext = LocalContext.current // инициализируем контекст

    val mCalendar = Calendar.getInstance() // инициализируем календарь

    // Переменные числа / месяца / года, выбранные при открывании диалога

    val mYear1: Int = if (inputYear != ""){
        inputYear.toInt()
    } else {
        mCalendar.get(Calendar.YEAR) // инициализируем год
    }
    val mMonth2: Int = if (inputMonth != ""){
        inputMonth.toInt()-1 // здесь -1 обязательно - тут это уебанская система считать с 0. Типа январь это 0, февраль 1 и тд
    } else {
        mCalendar.get(Calendar.MONTH)// инициализируем месяц
    }
    val mDay3: Int = if (inputDate != ""){
        inputDate.toInt()
    } else {
        mCalendar.get(Calendar.DAY_OF_MONTH)// инициализируем день
    }

    mCalendar.time = Date() // берем из календаря текущую дату

    // создаем переменную с диалогом выбора даты

    val mDatePickerDialog = DatePickerDialog(
        mContext, // передаем контекст

        // дополнительные настройки

        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            chosenDay.value = "$mDayOfMonth ${act.filterFunctions.numberToNameOfMonth(mMonth)} $mYear"
        }, mYear1, mMonth2, mDay3
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
            width = if (chosenDay.value == "" || chosenDay.value == "Выбери дату" ) {
                2.dp
            } else {
                0.dp
            },
            // цвет границы
            color = if (chosenDay.value == "" || chosenDay.value == "Выбери дату") {
                Grey_ForCards
            } else {
                YellowDvij
            }
        ),

        // цвета кнопки
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (chosenDay.value == "" || chosenDay.value == "Выбери дату") {
                Grey_ForCards
            } else {
                YellowDvij
            },
            contentColor = if (chosenDay.value == "" || chosenDay.value == "Выбери дату") {
                WhiteDvij
            } else {
                Grey_OnBackground
            },
        ),
        shape = RoundedCornerShape(50) // скругление углов
    ) {

        // ------ СОДЕРЖИМОЕ КНОПКИ --------

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (chosenDay.value == "" || chosenDay.value == "Выбери дату") {
                stringResource(id = R.string.piker_date)
            } else {chosenDay.value},
            style = Typography.labelMedium
        )
    }

    dataResult = chosenDay.value // помещаем в возвращаемую переменную выбранную дату

    return dataResult
}

@SuppressLint("SimpleDateFormat")
fun convertMillisecondsToDate (timeInMilliseconds: String): String?{

    return try {
        val format = SimpleDateFormat ("dd MM yyyy")
        val netDate = Date(timeInMilliseconds.toLong()*1000)
        format.format(netDate)
    } catch (e: Exception){
        e.toString()
    }

}

fun getTodayInMilliseconds(): Long {

    return System.currentTimeMillis()/1000

}



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
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.ui.theme.*
import java.util.*


@Composable
fun dataPicker(): String{

    var dataResult = ""
    val mContext = LocalContext.current

    val mCalendar = Calendar.getInstance()

    val mYear: Int = mCalendar.get(Calendar.YEAR)
    val mMonth: Int = mCalendar.get(Calendar.MONTH)
    val mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    val mDate = remember{ mutableStateOf("") }

    val mDatePickerDialog = DatePickerDialog(
        mContext,
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

    mDatePickerDialog.datePicker.minDate = mCalendar.timeInMillis

    Button(
        onClick = {
            mDatePickerDialog.show()
        },

        border = BorderStroke(
            width = if (mDate.value == "") {
                2.dp
            } else {
                0.dp
            }, color = if (mDate.value == "") {
                Grey60
            } else {
                Grey95
            }
        ),

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
        shape = RoundedCornerShape(50)
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (mDate.value == "") {"Выберите дату"} else {mDate.value},
            style = Typography.labelMedium
        )
    }

    dataResult = mDate.value

    return dataResult
}



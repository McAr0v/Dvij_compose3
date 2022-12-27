package kz.dvij.dvij_compose3.pickers

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.ui.theme.*
import java.util.*

@Composable
fun timePicker(): String {

    var timeResult = ""

    val mContext = LocalContext.current

    val mCalendar = Calendar.getInstance()

    val mHour = mCalendar[Calendar.HOUR]
    val mMinute = mCalendar[Calendar.MINUTE]

    mCalendar.time = Date()

    val mClock = remember{ mutableStateOf("") }

    val mClockPickerDialog = TimePickerDialog(
        mContext,
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

    Column() {

        Button(
            onClick = {
                mClockPickerDialog.show()
            },

            border = BorderStroke(
                width = if (mClock.value == "") {
                    2.dp
                } else {
                    0.dp
                }, color = if (mClock.value == "") {
                    Grey60
                } else {
                    Grey95
                }
            ),

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (mClock.value == "") {
                    Grey95
                } else {
                    PrimaryColor
                },
                contentColor = if (mClock.value == "") {
                    Grey60
                } else {
                    Grey100
                },
            ),
            shape = RoundedCornerShape(50)

        ) {

            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = if (mClock.value == "") {"Выберите время"} else {mClock.value},
                style = Typography.labelMedium
            )
        }
    }



    timeResult = mClock.value

    return timeResult

}
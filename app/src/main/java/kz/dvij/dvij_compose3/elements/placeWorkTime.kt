package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.constants.SECONDARY
import kz.dvij.dvij_compose3.firebase.PlacesCardClass
import kz.dvij.dvij_compose3.pickers.timePicker
import kz.dvij.dvij_compose3.pickers.timePickerInPlaceCreate
import kz.dvij.dvij_compose3.ui.theme.Grey_Text
import kz.dvij.dvij_compose3.ui.theme.Typography
import kz.dvij.dvij_compose3.ui.theme.WhiteDvij

@Composable
fun WorkTimePlace (
    placeInfo: PlacesCardClass,
    today: String
) {

    Text(
        text = if (placeInfo.mondayOpenTime == "" && placeInfo.mondayCloseTime == "") "Понедельник - выходной" else "Понедельник - ${placeInfo.mondayOpenTime!!} - ${placeInfo.mondayCloseTime!!}",
        style = if (today == "понедельник" || today == "Monday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "понедельник" || today == "Monday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    Text(
        text = if (placeInfo.tuesdayOpenTime == "" && placeInfo.tuesdayCloseTime == "") "Вторник - выходной" else "Вторник - ${placeInfo.tuesdayOpenTime!!} - ${placeInfo.tuesdayCloseTime!!}",
        style = if (today == "вторник" || today == "Tuesday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "вторник" || today == "Tuesday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    Text(
        text = if (placeInfo.wednesdayOpenTime == "" && placeInfo.wednesdayCloseTime == "") "Среда - выходной" else "Среда - ${placeInfo.wednesdayOpenTime!!} - ${placeInfo.wednesdayCloseTime!!}",
        style = if (today == "среда" || today == "Wednesday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "среда" || today == "Wednesday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = if (placeInfo.thursdayOpenTime == "" && placeInfo.thursdayCloseTime == "") "Четверг - выходной" else "Четверг - ${placeInfo.thursdayOpenTime!!} - ${placeInfo.thursdayCloseTime!!}",
        style = if (today == "четверг" || today == "Thursday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "четверг" || today == "Thursday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = if (placeInfo.fridayOpenTime == "" && placeInfo.fridayCloseTime == "") "Пятница - выходной" else "Пятница - ${placeInfo.fridayOpenTime!!} - ${placeInfo.fridayCloseTime!!}",
        style = if (today == "пятница" || today == "Friday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "пятница" || today == "Friday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = if (placeInfo.saturdayOpenTime == "" && placeInfo.saturdayCloseTime == "") "Суббота - выходной" else "Суббота - ${placeInfo.saturdayOpenTime!!} - ${placeInfo.saturdayCloseTime!!}",
        style = if (today == "суббота" || today == "Saturday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "суббота" || today == "Saturday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = if (placeInfo.sundayOpenTime == "" && placeInfo.sundayCloseTime == "") "Воскресенье - выходной" else "Воскресенье - ${placeInfo.sundayOpenTime!!} - ${placeInfo.sundayCloseTime!!}",
        style = if (today == "воскресенье" || today == "Sunday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "воскресенье" || today == "Sunday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )

}

@Composable
fun createTimeWorkPlace (
    //iconForButton: Int
    startTime: String = "",
    finishTime: String = "",
    dayName: String,
    createOrEdit: String
): List<String> {


    var startTimeNumberResult by rememberSaveable { mutableStateOf(startTime) }
    var finishTimeNumberResult by rememberSaveable { mutableStateOf(finishTime) }


    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(0.3f)) {


                Text(
                    text = dayName,
                    style = Typography.labelMedium,
                    color = WhiteDvij
                )

            }


            Spacer(modifier = Modifier.width(5.dp))

            Column(modifier = Modifier.weight(0.4f)) {

                startTimeNumberResult = if (startTime != "" && createOrEdit != "0"){

                    timePickerInPlaceCreate(startTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                } else {

                    timePickerInPlaceCreate() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = " - ",
                style = Typography.bodySmall,
                color = WhiteDvij
            )

            Spacer(modifier = Modifier.width(4.dp))

            Column(modifier = Modifier.weight(0.4f)) {
                //fieldTimeComponent(time = filledTime, onTimeChanged = { filledTime = it })

                finishTimeNumberResult = if (finishTime != "" && createOrEdit != "0"){

                    timePickerInPlaceCreate(finishTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                } else {

                    timePickerInPlaceCreate() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                }

            }

        }

    }

    return listOf(startTimeNumberResult, finishTimeNumberResult)

}
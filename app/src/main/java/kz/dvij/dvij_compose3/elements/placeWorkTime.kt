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
import kz.dvij.dvij_compose3.ui.theme.Grey_Text
import kz.dvij.dvij_compose3.ui.theme.Typography
import kz.dvij.dvij_compose3.ui.theme.WhiteDvij

@Composable
fun WorkTimePlace (
    placeInfo: PlacesCardClass,
    today: String
) {

    Text(
        text = "Понедельник - ${placeInfo.mondayOpenTime!!} - ${placeInfo.mondayCloseTime!!}",
        style = if (today == "понедельник" || today == "Monday" ) Typography.bodySmall else Typography.bodyMedium,
        color = if (today == "понедельник" || today == "Monday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    Text(
        text = "Вторник - ${placeInfo.tuesdayOpenTime!!} - ${placeInfo.tuesdayCloseTime!!}",
        style = if (today == "вторник" || today == "Tuesday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "вторник" || today == "Tuesday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    Text(
        text = "Среда - ${placeInfo.wednesdayOpenTime!!} - ${placeInfo.wednesdayCloseTime!!}",
        style = if (today == "среда" || today == "Wednesday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "среда" || today == "Wednesday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = "Четверг - ${placeInfo.thursdayOpenTime!!} - ${placeInfo.thursdayCloseTime!!}",
        style = if (today == "четверг" || today == "Thursday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "четверг" || today == "Thursday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = "Пятница - ${placeInfo.fridayOpenTime!!} - ${placeInfo.fridayCloseTime!!}",
        style = if (today == "пятница" || today == "Friday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "пятница" || today == "Friday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = "Суббота - ${placeInfo.saturdayOpenTime!!} - ${placeInfo.saturdayCloseTime!!}",
        style = if (today == "суббота" || today == "Saturday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "суббота" || today == "Saturday" ) WhiteDvij else Grey_Text,
        modifier = Modifier.padding(bottom = 5.dp)
    )
    Text(
        text = "Воскресение - ${placeInfo.sundayOpenTime!!} - ${placeInfo.sundayCloseTime!!}",
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

    var filledTime by rememberSaveable { mutableStateOf(listOf(startTimeNumberResult, finishTimeNumberResult)) }

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(0.33f)) {

                /*ButtonCustom(buttonText = dayName, typeButton = SECONDARY) {

                }*/

                Text(
                    text = dayName,
                    style = Typography.bodySmall,
                    color = WhiteDvij
                )

            }


            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(0.33f)) {

                startTimeNumberResult = if (startTime != "" && createOrEdit != "0"){

                    timePicker(startTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                } else {

                    timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                }

                //fieldTimeComponent(time = filledTime, onTimeChanged = { filledTime = it })
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(0.33f)) {
                //fieldTimeComponent(time = filledTime, onTimeChanged = { filledTime = it })

                finishTimeNumberResult = if (finishTime != "" && createOrEdit != "0"){

                    timePicker(finishTime) // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                } else {

                    timePicker() // ВЫБОР ВРЕМЕНИ - Когда открывается заведение

                }

            }

        }

    }

    return filledTime

}
package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.firebase.PlacesCardClass
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
        style = if (today == "пятницу" || today == "Friday" ) Typography.bodyMedium else Typography.bodySmall,
        color = if (today == "пятницу" || today == "Friday" ) WhiteDvij else Grey_Text,
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
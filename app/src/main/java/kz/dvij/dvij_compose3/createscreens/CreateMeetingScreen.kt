package kz.dvij.dvij_compose3.createscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.ui.theme.*

@Preview
@Composable
fun ViewCreateMeetingScreen(){
    //CreateMeetingScreen()
}

@SuppressLint("ResourceType")
@Composable
fun CreateMeetingScreen (activity: MainActivity) {

    var title = ""
    var title2 = ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey95)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Создание мероприятия",
            style = Typography.titleMedium,
            color = Grey00
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.no_user_image),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Заголовок",
            style = Typography.labelMedium,
            color = Grey40
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ------------    ТЕКСТОВОЕ ПОЛЕ С ПАРОЛЕМ ----------------

        title = fieldEmailComponent(act = activity)

        Text(
            text = title,
            style = Typography.labelMedium,
            color = Grey40
        )

        Spacer(modifier = Modifier.height(10.dp))

        title2 = fieldPhoneComponent(act = activity)

        Text(
            text = title2,
            style = Typography.labelMedium,
            color = Grey40
        )

    }
}
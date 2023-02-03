package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

@Preview
@Composable
fun PlacesCardView(){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey100)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        PlaceCard()
        Spacer(modifier = Modifier.height(20.dp))
        PlaceCardSmall()
    }


}

@Composable
fun PlaceCardSmall () {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(Grey100)
    ) {

        Box(modifier = Modifier.fillMaxWidth().height(170.dp)){

            Image(
                modifier = Modifier
                    .width(170.dp)
                    .height(170.dp),
                painter = painterResource(id = R.drawable.rest_logo2),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )


            // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

            Column(
                modifier = Modifier
                    .fillMaxWidth().fillMaxHeight()
                    .padding(top = 0.dp, end = 0.dp, start = 110.dp, bottom = 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                Card(
                    modifier = Modifier
                        .fillMaxWidth().fillMaxHeight(),
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp, bottomStart = 15.dp),
                    elevation = CardDefaults.cardElevation(5.dp),
                    colors = CardDefaults.cardColors(Grey100)
                ) {

                    Column(modifier = Modifier.padding(20.dp)) {

                        // ----- НАЗВАНИЕ ЗАВЕДЕНИЯ --------

                        Text(
                            text = "Пицца Блюз Восток",
                            style = Typography.titleSmall,
                            color = Grey10
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Усть-Каменогорск",
                            style = Typography.bodyMedium,
                            color = Grey40
                        )

                        Text(
                            text = "ул. Назарбаева 32",
                            style = Typography.bodyMedium,
                            color = Grey40
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                shape = RoundedCornerShape(50)
                            ) {

                                // ----- Иконка мероприятий ------

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_celebration),
                                    contentDescription = "",
                                    modifier = Modifier.size(20.dp),
                                    tint = Grey40
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                // ----------- Счетчик мероприятий ----------

                                androidx.compose.material.Text(
                                    text = "34",
                                    style = Typography.labelSmall,
                                    color = Grey40
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                shape = RoundedCornerShape(50)
                            ) {

                                // ----- Иконка акций ------

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_fire),
                                    contentDescription = "",
                                    modifier = Modifier.size(20.dp),
                                    tint = Grey40
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                // ----------- Счетчик акций ----------

                                androidx.compose.material.Text(
                                    text = "5",
                                    style = Typography.labelSmall,
                                    color = Grey40
                                )
                            }

                        }

                    }

                }
            }
        }
    }
}


@Composable
fun PlaceCard () {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(Grey100)
    ) {

        Box(modifier = Modifier.fillMaxWidth()){

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                painter = painterResource(id = R.drawable.rest_logo2),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // ------- КНОПКА КАТЕГОРИИ -----------

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                    shape = RoundedCornerShape(50)
                ) {

                    // ----------- Счетчик избранных ----------

                    androidx.compose.material.Text(
                        text = "Рестораны",
                        style = Typography.labelSmall,
                        color = Grey40
                    )

                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                    shape = RoundedCornerShape(50)
                ) {

                    // ----------- Счетчик избранных ----------

                    androidx.compose.material.Text(
                        text = "10",
                        style = Typography.labelSmall,
                        color = Grey40
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    // ----- Иконка избранное ------

                    Icon(
                        painter = painterResource(id = R.drawable.ic_bookmark),
                        contentDescription = "",
                        modifier = Modifier.size(20.dp),
                        tint = Grey40
                    )

                }

            }

            // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 245.dp, end = 0.dp, start = 0.dp, bottom = 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp, bottomStart = 15.dp),
                    elevation = CardDefaults.cardElevation(5.dp),
                    colors = CardDefaults.cardColors(Grey95)
                ) {

                    Column(modifier = Modifier.padding(20.dp)) {

                        // ----- НАЗВАНИЕ ЗАВЕДЕНИЯ --------

                        Text(
                            text = "Пицца Блюз Восток",
                            style = Typography.titleLarge,
                            color = Grey10
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Усть-Каменогорск",
                            style = Typography.bodyMedium,
                            color = Grey40
                        )

                        Text(
                            text = "ул. Назарбаева 32",
                            style = Typography.bodyMedium,
                            color = Grey40
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        IconText(icon = R.drawable.ic_time, inputText = "9:00 - 18:00")

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                shape = RoundedCornerShape(50)
                            ) {

                                // ----- Иконка мероприятий ------

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_celebration),
                                    contentDescription = "",
                                    modifier = Modifier.size(20.dp),
                                    tint = Grey40
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                // ----------- Счетчик мероприятий ----------

                                androidx.compose.material.Text(
                                    text = "34 мероприятия",
                                    style = Typography.labelSmall,
                                    color = Grey40
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                                shape = RoundedCornerShape(50)
                            ) {

                                // ----- Иконка акций ------

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_fire),
                                    contentDescription = "",
                                    modifier = Modifier.size(20.dp),
                                    tint = Grey40
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                // ----------- Счетчик акций ----------

                                androidx.compose.material.Text(
                                    text = "5 акций",
                                    style = Typography.labelSmall,
                                    color = Grey40
                                )
                            }

                        }

                    }

                }
            }
        }
    }
}

class PlacesCard (val act: MainActivity) {





}
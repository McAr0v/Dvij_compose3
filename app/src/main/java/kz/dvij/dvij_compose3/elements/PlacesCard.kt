package kz.dvij.dvij_compose3.elements

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.firebase.PlacesDialogClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class PlacesCard (val act: MainActivity) {

    @Composable
    fun PlaceCardSmall (navController: NavController, placeItem: PlacesAdsClass, placeKey: MutableState<String>) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(Grey100)
        ) {

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)){

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
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 0.dp, end = 0.dp, start = 110.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {

                    // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
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
    fun PlaceCard (navController: NavController, placeItem: PlacesAdsClass, placeKey: MutableState<String>) {

        val iconFavColor = remember{ mutableStateOf(Grey10) } // Переменная цвета иконки ИЗБРАННОЕ

        // Считываем с базы данных - добавлено ли это мероприятие в избранное?

        act.placesDatabaseManager.favIconPlace(placeItem.placeKey!!){
            // Если колбак тру, то окрашиваем иконку в нужный цвет
            if (it){
                iconFavColor.value = PrimaryColor
            } else {
                // Если колбак фалс, то в обычный цвет
                iconFavColor.value = Grey10
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {

                    // При клике на карточку - передаем на Main Activity placeKey. Ключ берем из дата класса заведения

                    placeKey.value = placeItem.placeKey.toString()

                    // так же при нажатии регистрируем счетчик просмотров - добавляем 1 просмотр

                    placeItem.placeKey?.let {
                        act.placesDatabaseManager.viewCounterPlace(it) {

                            // если колбак тру, то счетчик успешно сработал, значит переходим на страницу заведения
                            if (it) {
                                // РАССКОМЕНТИРОВАТЬ ПОСЛЕ ТОГО, КАК СОЗДАМ СТРАНИЦУ ПРОСМОТРА
                                navController.navigate(PLACE_VIEW)
                            }
                        }
                    }

                }
            ,
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(Grey100)
        ) {

            Box(modifier = Modifier.fillMaxWidth()){

                if (placeItem.logo != null){

                    AsyncImage(
                        model = placeItem.logo, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТНОГО ЗАВЕДЕНИЯ ИЗ БД
                        contentDescription = "Логотип заведения", // описание изображения для слабовидящих
                        modifier = Modifier
                            .height(260.dp), // заполнить картинкой весь контейнер
                        contentScale = ContentScale.FillWidth, // обрезать картинку, что не вмещается
                        //alignment = Alignment.Center
                    )

                } else {

                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        painter = painterResource(id = R.drawable.rest_logo2),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // ------- КНОПКА КАТЕГОРИИ -----------

                    if (placeItem.category != null) {

                        Button(
                            onClick = { Toast.makeText(act, "Cделать фунцию", Toast.LENGTH_SHORT).show()},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                            shape = RoundedCornerShape(50)
                        ) {

                            androidx.compose.material.Text(
                                text = placeItem.category,
                                style = Typography.labelSmall,
                                color = Grey40
                            )

                        }

                    }

                    // ----------- Счетчик избранных ----------

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                        shape = RoundedCornerShape(50)
                    ) {

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
                            tint = iconFavColor.value
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
                        colors = CardDefaults.cardColors(Grey100)
                    ) {

                        Column(modifier = Modifier.padding(20.dp)) {

                            // ----- НАЗВАНИЕ ЗАВЕДЕНИЯ --------

                            if (placeItem.placeName != null) {

                                Text(
                                    text = placeItem.placeName,
                                    style = Typography.titleLarge,
                                    color = Grey10
                                )

                            }



                            Spacer(modifier = Modifier.height(10.dp))

                            if (placeItem.city != null){

                                Text(
                                    text = placeItem.city,
                                    style = Typography.bodyMedium,
                                    color = Grey40
                                )

                            }

                            if (placeItem.address != null){

                                Text(
                                    text = placeItem.address,
                                    style = Typography.bodyMedium,
                                    color = Grey40
                                )

                            }



                            Spacer(modifier = Modifier.height(10.dp))

                            if (placeItem.openTime != null && placeItem.closeTime != null) {

                                IconText(icon = R.drawable.ic_time, inputText = "${placeItem.openTime} - ${placeItem.closeTime}")

                            }

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
}
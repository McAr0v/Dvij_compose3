package kz.dvij.dvij_compose3.viewscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.INSTAGRAM_URL
import kz.dvij.dvij_compose3.constants.TELEGRAM_URL
import kz.dvij.dvij_compose3.elements.HeadlineAndDesc
import kz.dvij.dvij_compose3.elements.SpacerTextWithLine
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.ui.theme.*

class StockViewScreen (val act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun StockViewScreen (key: String, navController: NavController){

        // Переменная, которая содержит в себе информацию об акции
        val stockInfo = remember {
            mutableStateOf(StockAdsClass())
        }

        // Переменная, отвечающая за цвет кнопки избранных
        val buttonFavColor = remember {
            mutableStateOf(Grey90)
        }

        // Переменная, отвечающая за цвет иконки избранных
        val iconTextFavColor = remember {
            mutableStateOf(Grey10)
        }

        // Переменная счетчика людей, добавивших в избранное акции
        val favCounter = remember {
            mutableStateOf(0)
        }

        // Переменная счетчика просмотра акции
        val viewCounter = remember {
            mutableStateOf(0)
        }

        // Считываем данные про акцию и счетчики добавивших в избранное и количество просмотров акции

        act.stockDatabaseManager.readOneStockFromDataBase(stockInfo, key){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров заведения

        }

        // Если пользователь авторизован, проверяем, добавлена ли уже акция в избранное, или нет

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.stockDatabaseManager.favIconStock(key) {
                if (it) {
                    buttonFavColor.value = Grey90_2
                    iconTextFavColor.value = PrimaryColor
                } else {
                    buttonFavColor.value = Grey90
                    iconTextFavColor.value = Grey40
                }
            }
        }


        // ---------- КОНТЕНТ СТРАНИЦЫ --------------

        Column(
            modifier = Modifier
                .background(Grey95)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top

        ) {

            // ------- КАРТИНКА Акции ----------

            AsyncImage(
                model = stockInfo.value.image,
                contentDescription = "Картинка акции",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // --------- КОНТЕНТ ПОД КАРТИНКОЙ ----------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top

            ) {

                // -------- НАЗВАНИЕ АКЦИИ ----------

                if (stockInfo.value.headline != null) {

                    Text(
                        text = stockInfo.value.headline!!,
                        style = Typography.titleLarge,
                        color = Grey10
                    )
                }

                // ------- ГОРОД ------------

                if (stockInfo.value.city != null) {

                    Text(
                        text = stockInfo.value.city!!,
                        style = Typography.bodyMedium,
                        color = Grey40
                    )
                }

                // --------- КАТЕГОРИЯ, СЧЕТЧИКИ, ИЗБРАННОЕ -------------

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    // -------- КАТЕГОРИЯ акции ----------


                    if (stockInfo.value.category != null) {

                        Button(
                            onClick = {
                                Toast
                                    .makeText(act, "Сделать функцию", Toast.LENGTH_SHORT)
                                    .show()
                            },

                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = PrimaryColor,
                                contentColor = Grey95
                            ),

                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text(
                                text = stockInfo.value.category!!,
                                style = Typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))


                    // --------- СЧЕТЧИК КОЛИЧЕСТВА ПРОСМОТРОВ ------------


                    Button(
                        onClick = {
                            Toast.makeText(act, "Количество просмотров завдения",
                                Toast.LENGTH_SHORT).show()},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                        shape = RoundedCornerShape(50)
                    ) {

                        // ----- Иконка просмотра ------

                        androidx.compose.material.Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = stringResource(id = R.string.cd_counter_view_meeting),
                            modifier = Modifier.size(20.dp),
                            tint = Grey40
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        // ----------- Счетчик просмотров ----------

                        Text(
                            text = viewCounter.value.toString(),
                            style = Typography.labelMedium,
                            color = Grey40
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))


                    // -------- ИЗБРАННЫЕ ---------


                    Button(
                        onClick = {

                            // --- Если клиент авторизован, проверяем, добавлена ли уже в избранное эта акция -----
                            // Если не авторизован, условие else

                            if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                                act.stockDatabaseManager.favIconStock(key) {

                                    // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                    if (it) {

                                        // Убираем из избранных

                                        act.stockDatabaseManager.removeFavouriteStock(key) {

                                            // Если пришел колбак, что успешно

                                            if (it) {

                                                iconTextFavColor.value = Grey40 // При нажатии окрашиваем текст и иконку в белый
                                                buttonFavColor.value = Grey80 // При нажатии окрашиваем кнопку в темно-серый

                                                // Выводим ТОСТ
                                                Toast.makeText(act,act.getString(R.string.delete_from_fav),
                                                    Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    } else {

                                        // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                        act.stockDatabaseManager.addFavouriteStock(key) {

                                            // Если пришел колбак, что успешно

                                            if (it) {

                                                iconTextFavColor.value = PrimaryColor // При нажатии окрашиваем текст и иконку в черный
                                                buttonFavColor.value = Grey90_2 // Окрашиваем кнопку в главный цвет

                                                // Выводим ТОСТ
                                                Toast.makeText(act,act.getString(R.string.add_to_fav),
                                                    Toast.LENGTH_SHORT).show()

                                            }
                                        }
                                    }
                                }

                            } else {

                                // Если пользователь не авторизован, то ему выводим ТОСТ

                                Toast
                                    .makeText(act, "Чтобы добавить акцию в избранные, тебе нужно авторизоваться", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonFavColor.value),
                        shape = RoundedCornerShape(50)
                    ) {

                        // --- Иконка СЕРДЕЧКО -----

                        Icon(
                            imageVector = Icons.Filled.Favorite, // сам векторный файл иконки
                            contentDescription = stringResource(id = R.string.cd_add_to_fav), // описание для слабовидящих
                            modifier = Modifier
                                .size(20.dp), // размер иконки
                            tint = iconTextFavColor.value // Цвет иконки
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        // ------ Счетчик добавлено в избранное

                        Text(
                            text = favCounter.value.toString(),
                            style = Typography.labelMedium,
                            color = Grey40
                        )
                    }
                }


                // --------- ДАТА И ВРЕМЯ -----------


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                    // --- ДАТА ----

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                    ) {
                        if (stockInfo.value.startDate != null){
                            HeadlineAndDesc(headline = stockInfo.value.startDate!!, desc = "Начало акции")
                        }

                        if (stockInfo.value.finishDate != null){
                            HeadlineAndDesc(headline = stockInfo.value.finishDate!!, desc = "Конец акции")
                        }

                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                // ---------- ОПИСАНИЕ -------------

                Text(
                    text = "Об акции",
                    style = Typography.titleMedium,
                    color = Grey10
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (stockInfo.value.description !=null){

                    Text(
                        text = stockInfo.value.description!!,
                        style = Typography.bodyMedium,
                        color = Grey10
                    )
                }
            }
        }
    }

}
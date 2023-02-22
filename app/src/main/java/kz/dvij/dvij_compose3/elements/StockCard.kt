package kz.dvij.dvij_compose3.elements

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.navigation.STOCK_VIEW
import kz.dvij.dvij_compose3.ui.theme.*

class StockCard (val act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun StockCard (navController: NavController, stockItem: StockAdsClass, stockKey: MutableState<String>) {

        val iconFavColor = remember{ mutableStateOf(Grey10) } // Переменная цвета иконки ИЗБРАННОЕ

        // Считываем с базы данных - добавлено ли эта акция в избранное?

        act.stockDatabaseManager.favIconStock(stockItem.keyStock!!){
            // Если колбак тру, то окрашиваем иконку в нужный цвет
            if (it){
                iconFavColor.value = PrimaryColor
            } else {
                // Если колбак фалс, то в обычный цвет
                iconFavColor.value = Grey10
            }
        }

        // Переменная счетчика людей, добавивших в избранное акцию
        val favCounter = remember {
            mutableStateOf(0)
        }

        // Переменная счетчика просмотра акции
        val viewCounter = remember {
            mutableStateOf(0)
        }

        val stockInfo = remember {
            mutableStateOf(StockAdsClass())
        }

        // Считываем данные про акцию и счетчики добавивших в избранное и количество просмотров акции

        act.stockDatabaseManager.readOneStockFromDataBase(stockInfo, stockItem.keyStock){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров заведения

        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable {

                    // При клике на карточку - передаем на Main Activity keyStock. Ключ берем из дата класса акции

                    stockKey.value = stockItem.keyStock.toString()

                    // так же при нажатии регистрируем счетчик просмотров - добавляем 1 просмотр

                    act.stockDatabaseManager.viewCounterStock(key = stockItem.keyStock) {

                        if (it) {

                            navController.navigate(STOCK_VIEW)

                        }
                    }
                }
            ,
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(Grey100)
        ) {

            Box(modifier = Modifier.fillMaxWidth()){

                if (stockItem.image != null){

                    AsyncImage(
                        model = stockItem.image, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТОЙ АКЦИИ ИЗ БД
                        contentDescription = "Логотип акции", // описание изображения для слабовидящих
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

                    if (stockItem.category != null) {

                        Button(
                            onClick = { Toast.makeText(act, "Сделать функцию", Toast.LENGTH_SHORT).show()},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Grey90),
                            shape = RoundedCornerShape(50)
                        ) {

                            Text(
                                text = stockItem.category,
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

                        Text(
                            text = favCounter.value.toString(),
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

                            // ----- НАЗВАНИЕ АКЦИИ --------

                            if (stockItem.headline != null) {

                                androidx.compose.material3.Text(
                                    text = stockItem.headline,
                                    style = Typography.titleLarge,
                                    color = Grey10
                                )

                            }



                            Spacer(modifier = Modifier.height(10.dp))

                            if (stockItem.city != null){

                                androidx.compose.material3.Text(
                                    text = stockItem.city,
                                    style = Typography.bodyMedium,
                                    color = Grey40
                                )

                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (stockItem.startDate != null && stockItem.finishDate != null) {

                                IconText(icon = R.drawable.ic_time, inputText = "${stockItem.startDate} - ${stockItem.finishDate}")

                            }
                        }
                    }
                }
            }
        }
    }
}
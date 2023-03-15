package kz.dvij.dvij_compose3.elements

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.FOR_CARDS
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.firebase.StockCardClass
import kz.dvij.dvij_compose3.navigation.EDIT_MEETINGS_SCREEN
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.STOCK_VIEW
import kz.dvij.dvij_compose3.ui.theme.*

class StockCard (val act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun StockCard (
        navController: NavController,
        stockItem: StockCardClass,
        stockKey: MutableState<String>,
        isAd: Boolean = false,
        //filledStock: MutableState<StockAdsClass>,
        //filledPlace: MutableState<PlacesAdsClass>
    ) {

        val iconFavColor = remember{ mutableStateOf(WhiteDvij) } // Переменная цвета иконки ИЗБРАННОЕ

        // Считываем с базы данных - добавлено ли эта акция в избранное?

        act.stockDatabaseManager.favIconStock(stockItem.keyStock!!){
            // Если колбак тру, то окрашиваем иконку в нужный цвет
            if (it){
                iconFavColor.value = YellowDvij
            } else {
                // Если колбак фалс, то в обычный цвет
                iconFavColor.value = WhiteDvij
            }
        }

        // Переменная счетчика людей, добавивших в избранное акцию
        val favCounter = remember {
            mutableStateOf(stockItem.counterInFav?.toInt() ?: 0)
        }

        // Переменная счетчика просмотра акции
        val viewCounter = remember {
            mutableStateOf(stockItem.counterView?.toInt() ?: 0)
        }

        val stockInfo = remember {
            mutableStateOf(StockAdsClass())
        }

        val openConfirmChoose = remember {mutableStateOf(false)} // диалог действительно хотите удалить?

        // Считываем данные про акцию и счетчики добавивших в избранное и количество просмотров акции

        act.stockDatabaseManager.readOneStockFromDataBase(stockInfo, stockItem.keyStock){

            favCounter.value = it[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = it[1] // данные из списка - количество просмотров заведения

        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .border(
                    width = if (isAd) 2.dp else 0.dp,
                    color = if (isAd) YellowDvij else Grey_Background,
                    shape = if (isAd) RoundedCornerShape(15.dp) else RoundedCornerShape(0.dp)
                )
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
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // ------- КНОПКА КАТЕГОРИИ -----------

                    Bubble(
                        buttonText = viewCounter.value.toString(),
                        leftIcon = R.drawable.ic_visibility,
                        typeButton = FOR_CARDS
                    ) {
                        Toast.makeText(act,"Количество просмотров акции",Toast.LENGTH_SHORT).show()
                    }



                    // ----------- Счетчик избранных ----------

                    if (stockItem.counterInFav != null){

                        Bubble(
                            buttonText = favCounter.value.toString(),
                            rightIcon = R.drawable.ic_fav,
                            typeButton = FOR_CARDS,
                            rightIconColor = iconFavColor.value
                        ) {
                            Toast.makeText(act,"Функция добавления в избранное",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // -------- ОТСТУП ДЛЯ НАВИСАЮЩЕЙ КАРТОЧКИ ------------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 235.dp, end = 0.dp, start = 0.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {

                    // ----------- НАВИСАЮЩАЯ КАРТОЧКА ----------------

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
                        elevation = CardDefaults.cardElevation(5.dp),
                        colors = CardDefaults.cardColors(Grey_ForCards)
                    ) {

                        Column(modifier = Modifier.padding(20.dp)) {

                            Row {
                                androidx.compose.material3.Text(
                                    text = "#Акция",
                                    color = Grey_Text,
                                    style = Typography.labelMedium
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                androidx.compose.material3.Text(
                                    text = "#${stockItem.category}",
                                    color = Grey_Text,
                                    style = Typography.labelMedium
                                )
                            }

                            if (isAd){

                                Spacer(modifier = Modifier.height(5.dp))

                                androidx.compose.material3.Text(
                                    text = "#Рекламный пост",
                                    color = Grey_Text,
                                    style = Typography.labelMedium
                                )

                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            // ----- НАЗВАНИЕ АКЦИИ --------

                            if (stockItem.headline != null) {

                                androidx.compose.material3.Text(
                                    text = stockItem.headline,
                                    style = Typography.titleMedium,
                                    color = WhiteDvij
                                )

                            }


                            // ----- ГОРОД --------

                            if (stockItem.city != null){

                                Spacer(modifier = Modifier.height(5.dp))

                                androidx.compose.material3.Text(
                                    text = stockItem.city,
                                    style = Typography.labelMedium,
                                    color = Grey_Text
                                )

                            }

                            Spacer(modifier = Modifier.height(15.dp))



                            // ----- ДАТА ПРОВЕДЕНИЯ ------

                            if (stockItem.startDate != null && stockItem.finishDate != null) {

                                Bubble(buttonText = "${stockItem.startDate} - ${stockItem.finishDate}", typeButton = DARK) {}

                            }

                            Spacer(modifier = Modifier.height(15.dp))


                            if (stockItem.description != null){

                                androidx.compose.material3.Text(
                                    text = stockItem.description,
                                    style = Typography.bodySmall,
                                    color = WhiteDvij
                                )

                            }

                        }
                    }

                    if (stockItem.keyCreator == act.mAuth.uid){

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Grey_OnBackground).padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            // ----- КНОПКА РЕДАКТИРОВАТЬ ------

                            androidx.compose.material.Text(
                                text = stringResource(id = R.string.edit),
                                style = Typography.bodySmall,
                                color = YellowDvij,
                                modifier = Modifier.clickable {

                                    /*filledMeeting.value = MeetingsAdsClass(
                                        key = meetingItem.key,
                                        category = meetingItem.category,
                                        headline = meetingItem.headline,
                                        description = meetingItem.description,
                                        price = meetingItem.price,
                                        phone = meetingItem.phone,
                                        whatsapp = meetingItem.whatsapp,
                                        data = meetingItem.data,
                                        startTime = meetingItem.startTime,
                                        finishTime = meetingItem.finishTime,
                                        image1 = meetingItem.image1,
                                        city = meetingItem.city,
                                        instagram = meetingItem.instagram,
                                        telegram = meetingItem.telegram,
                                        placeKey = meetingItem.placeKey,
                                        headlinePlaceInput = meetingItem.headlinePlaceInput,
                                        addressPlaceInput = meetingItem.addressPlaceInput,
                                        ownerKey = meetingItem.ownerKey,
                                        dateInNumber = meetingItem.dateInNumber,
                                        createdTime = meetingItem.createdTime
                                    )

                                    if (meetingItem.placeKey == null || meetingItem.placeKey == ""){

                                        filledPlace.value = PlacesAdsClass(
                                            placeName = meetingItem.headlinePlaceInput,
                                            address = meetingItem.addressPlaceInput
                                        )

                                        navController.navigate(EDIT_MEETINGS_SCREEN)

                                    } else {

                                        act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo = filledPlace, key = meetingItem.placeKey) {

                                            navController.navigate(EDIT_MEETINGS_SCREEN)

                                        }

                                    }*/

                                }
                            )

                            // ------ КНОПКА УДАЛИТЬ -------

                            androidx.compose.material.Text(
                                text = stringResource(id = R.string.delete),
                                style = Typography.bodySmall,
                                color = AttentionRed,
                                modifier = Modifier.clickable { openConfirmChoose.value = true }
                            )

                        }

                    }

                    if (openConfirmChoose.value) {

                        ConfirmDialog(onDismiss = { openConfirmChoose.value = false }) {

                            /*if (meetingItem.placeKey != null && meetingItem.image1 != null){

                                act.meetingDatabaseManager.deleteMeetingWithPlaceNote(
                                    meetingKey = meetingItem.key,
                                    placeKey = meetingItem.placeKey,
                                    imageUrl = meetingItem.image1
                                ) {

                                    if (it) {

                                        Log.d ("MyLog", "Удалилась и картинка и само мероприятие и запись у заведения")
                                        navController.navigate(MEETINGS_ROOT) {popUpTo(0)}

                                    } else {

                                        Log.d ("MyLog", "Почемуто не удалилось")

                                    }
                                }
                            }*/
                        }
                    }

                }
            }
        }
    }
}
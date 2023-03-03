package kz.dvij.dvij_compose3.stockscreens

import android.annotation.SuppressLint
import android.util.Log
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
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.navigation.EDIT_STOCK_SCREEN
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.STOCK_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

class StockViewScreen (val act: MainActivity) {

    private val placeCard = PlacesCard (act)
    private val ownerCard = OwnerCard(act)

    @SuppressLint("NotConstructor")
    @Composable
    fun StockViewScreen (key: String, navController: NavController, placeKey: MutableState<String>, filledStockInfoFromAct: MutableState<StockAdsClass>, filledPlaceInfoFromAct: MutableState<PlacesAdsClass>){

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

        // Переменная, которая содержит информацию о заведении-организаторе
        val placeInfo = remember {
            mutableStateOf(PlacesAdsClass())
        }

        // --- ПЕРЕМЕННЫЕ ДИАЛОГОВ ---

        val openConfirmChoose = remember {mutableStateOf(false)} // диалог действительно хотите удалить?

        // Считываем данные про акцию и счетчики добавивших в избранное и количество просмотров акции

        act.stockDatabaseManager.readOneStockFromDataBase(stockInfo, key){ list->

            favCounter.value = list[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = list[1] // данные из списка - количество просмотров

            stockInfo.value.keyPlace?.let { nonNullKeyPlace -> act.placesDatabaseManager.readOnePlaceFromDataBase(placeInfo = placeInfo, key = nonNullKeyPlace) {

                }
            }
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

            if (openConfirmChoose.value) {

                ConfirmDialog(onDismiss = { openConfirmChoose.value = false }) {

                    if (stockInfo.value.keyStock != null && stockInfo.value.keyPlace != null && stockInfo.value.image != null) {

                        act.stockDatabaseManager.deleteStockWithPlaceNote(
                            stockKey = stockInfo.value.keyStock!!,
                            imageUrl = stockInfo.value.image!!,
                            placeKey = stockInfo.value.keyPlace!!
                        ) {

                            if (it) {

                                Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")
                                navController.navigate(STOCK_ROOT) {popUpTo(0)}

                            } else {

                                Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                            }

                        }

                    }
                }
            }

            // --------- КОНТЕНТ ПОД КАРТИНКОЙ ----------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top

            ) {

                // --- КНОПКА РЕДАКТИРОВАТЬ -----

                if (stockInfo.value.keyCreator == act.mAuth.uid){

                    Button(

                        onClick = {

                            stockInfo.value.keyStock?.let {
                                act.stockDatabaseManager.readOneStockFromDataBaseReturnClass(it){ stock ->

                                    if (stock.keyPlace != null && stock.keyPlace != "null" && stock.keyPlace != "") {

                                        filledPlaceInfoFromAct.value = placeInfo.value

                                    } else {

                                        filledPlaceInfoFromAct.value = PlacesAdsClass(
                                            placeName = stock.inputHeadlinePlace,
                                            address = stock.inputAddressPlace
                                        )

                                    }

                                    filledStockInfoFromAct.value = stock
                                    navController.navigate(EDIT_STOCK_SCREEN)

                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth() // кнопка на всю ширину
                            .height(50.dp)// высота - 50
                            .padding(horizontal = 30.dp), // отступы от краев
                        shape = RoundedCornerShape(50), // скругление углов
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = SuccessColor, // цвет кнопки
                            contentColor = Grey100 // цвет контента на кнопке
                        )
                    ) {
                        Text(
                            text = "Редактировать",
                            style = Typography.labelMedium
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        androidx.compose.material.Icon(
                            painter = painterResource(id = R.drawable.ic_publish),
                            contentDescription = stringResource(id = R.string.cd_publish_button),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ------ КНОПКА УДАЛЕНИЯ ------

                    Button(

                        onClick = {
                            openConfirmChoose.value = true
                        },
                        modifier = Modifier
                            .fillMaxWidth() // кнопка на всю ширину
                            .height(50.dp)// высота - 50
                            .padding(horizontal = 30.dp), // отступы от краев
                        shape = RoundedCornerShape(50), // скругление углов
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = SuccessColor, // цвет кнопки
                            contentColor = Grey100 // цвет контента на кнопке
                        )
                    ) {
                        Text(
                            text = "Удалить",
                            style = Typography.labelMedium
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        androidx.compose.material.Icon(
                            painter = painterResource(id = R.drawable.ic_publish),
                            contentDescription = "Кнопка удалить",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }




                // -------- НАЗВАНИЕ АКЦИИ ----------

                if (stockInfo.value.headline != null && stockInfo.value.headline != "null" && stockInfo.value.headline != "") {

                    Text(
                        text = stockInfo.value.headline!!,
                        style = Typography.titleLarge,
                        color = Grey10
                    )
                }

                // ------- ГОРОД ------------

                if (stockInfo.value.city != null && stockInfo.value.city != "null" && stockInfo.value.city != "" && stockInfo.value.city != "Выбери город") {

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


                    if (stockInfo.value.category != null && stockInfo.value.category != "null" && stockInfo.value.category != "" && stockInfo.value.category != "Выбери город") {

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

                                        act.stockDatabaseManager.removeFavouriteStock(key) { remove ->

                                            // Если пришел колбак, что успешно

                                            if (remove) {

                                                iconTextFavColor.value = Grey40 // При нажатии окрашиваем текст и иконку в белый
                                                buttonFavColor.value = Grey80 // При нажатии окрашиваем кнопку в темно-серый

                                                // Выводим ТОСТ
                                                Toast.makeText(act,act.getString(R.string.delete_from_fav),
                                                    Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    } else {

                                        // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                        act.stockDatabaseManager.addFavouriteStock(key) { addToFav ->

                                            // Если пришел колбак, что успешно

                                            if (addToFav) {

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
                        if (stockInfo.value.startDate != null && stockInfo.value.startDate != "null" && stockInfo.value.startDate != ""){
                            HeadlineAndDesc(headline = stockInfo.value.startDate!!, desc = "Начало акции")
                        }

                        if (stockInfo.value.finishDate != null && stockInfo.value.finishDate != "null" && stockInfo.value.finishDate != ""){
                            HeadlineAndDesc(headline = stockInfo.value.finishDate!!, desc = "Конец акции")
                        }

                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                // ----- КАРТОЧКА ЗАВЕДЕНИЯ ----------

                placeInfo.value.placeKey?.let {

                    Text(
                        text = "Место проведения",
                        style = Typography.titleMedium,
                        color = Grey10
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    placeCard.PlaceCardSmall(navController = navController, placeItem = placeInfo.value, placeKey = placeKey)

                    Spacer(modifier = Modifier.height(20.dp))

                }

                if (stockInfo.value.keyPlace == "Empty" || stockInfo.value.keyPlace == "" || stockInfo.value.keyPlace == "null" ){

                    Text(
                        text = "Место проведения",
                        style = Typography.titleMedium,
                        color = Grey10
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    stockInfo.value.inputHeadlinePlace?.let {
                        Text(
                            text = it,
                            style = Typography.titleSmall,
                            color = Grey10
                        )
                    }

                    stockInfo.value.inputAddressPlace?.let {
                        Text(
                            text = it,
                            style = Typography.bodyMedium,
                            color = Grey10
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }

                SpacerTextWithLine(headline = "Организитор")

                Spacer(modifier = Modifier.height(10.dp))

                // КАРТОЧКА СОЗДАТЕЛЯ

                if (stockInfo.value.keyCreator != null && stockInfo.value.keyCreator != "null" && stockInfo.value.keyCreator != ""){

                    ownerCard.OwnerCardView(userKey = stockInfo.value.keyCreator!!)

                    Spacer(modifier = Modifier.height(20.dp))

                }

                // ---------- ОПИСАНИЕ -------------


                if (stockInfo.value.description !=null && stockInfo.value.description != "null" && stockInfo.value.description != ""){

                    Text(
                        text = "Об акции",
                        style = Typography.titleMedium,
                        color = Grey10
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
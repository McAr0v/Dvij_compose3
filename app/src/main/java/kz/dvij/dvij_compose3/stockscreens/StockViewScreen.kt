package kz.dvij.dvij_compose3.stockscreens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
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
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.INSTAGRAM_URL
import kz.dvij.dvij_compose3.constants.TELEGRAM_URL
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.navigation.EDIT_MEETINGS_SCREEN
import kz.dvij.dvij_compose3.navigation.EDIT_STOCK_SCREEN
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.STOCK_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

class StockViewScreen (val act: MainActivity) {

    private val placeCard = PlacesCard (act)
    private val ownerCard = OwnerCard(act)

    @SuppressLint("NotConstructor")
    @Composable
    fun StockViewScreen (key: String, navController: NavController, placeKey: MutableState<String>, filledStockInfoFromAct: MutableState<StockAdsClass>, filledPlaceInfoFromAct: MutableState<PlacesAdsClass>) {

        // Переменная, которая содержит в себе информацию об акции
        val stockInfo = remember {
            mutableStateOf(StockAdsClass())
        }

        // Переменная, отвечающая за цвет кнопки избранных
        val buttonFavColor = remember {
            mutableStateOf(Grey_Background)
        }

        // Переменная, отвечающая за цвет иконки избранных
        val iconTextFavColor = remember {
            mutableStateOf(WhiteDvij)
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

        val openConfirmChoose =
            remember { mutableStateOf(false) } // диалог действительно хотите удалить?

        // Считываем данные про акцию и счетчики добавивших в избранное и количество просмотров акции

        act.stockDatabaseManager.readOneStockFromDataBase(stockInfo, key) { list ->

            favCounter.value = list[0] // данные из списка - количество добавивших в избранное
            viewCounter.value = list[1] // данные из списка - количество просмотров

            stockInfo.value.keyPlace?.let { nonNullKeyPlace ->
                act.placesDatabaseManager.readOnePlaceFromDataBase(
                    placeInfo = placeInfo,
                    key = nonNullKeyPlace
                ) {

                }
            }
        }

        // Если пользователь авторизован, проверяем, добавлена ли уже акция в избранное, или нет

        if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
            act.stockDatabaseManager.favIconStock(key) {
                if (it) {
                    buttonFavColor.value = Grey_Background
                    iconTextFavColor.value = YellowDvij
                } else {
                    buttonFavColor.value = Grey_Background
                    iconTextFavColor.value = WhiteDvij
                }
            }
        }

        if (openConfirmChoose.value) {

            ConfirmDialog(onDismiss = { openConfirmChoose.value = false }) {

                if (stockInfo.value.keyStock != null && stockInfo.value.keyPlace != null && stockInfo.value.image != null) {

                    act.stockDatabaseManager.deleteStockWithPlaceNote(
                        stockKey = stockInfo.value.keyStock!!,
                        imageUrl = stockInfo.value.image!!,
                        placeKey = stockInfo.value.keyPlace!!
                    ) {

                        if (it) {

                            Log.d("MyLog", "Удалилась и картинка и сама акция и запись у заведения")
                            navController.navigate(STOCK_ROOT) { popUpTo(0) }

                        } else {

                            Log.d("MyLog", "Почемуто акция не удалилась не удалилось")

                        }

                    }

                }
            }
        }


        // ---------- КОНТЕНТ СТРАНИЦЫ --------------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Grey_Background)
        ) {

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(0.dp)) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    // ------- КАРТИНКА МЕРОПРИЯТИЯ ----------

                    if (stockInfo.value.image != null) {
                        AsyncImage(
                            model = stockInfo.value.image, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТНОГО МЕРОПРИЯТИЯ ИЗ БД
                            contentDescription = "Изображение акции", // описание изображения для слабовидящих
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp), // заполнить картинкой весь контейнер
                            contentScale = ContentScale.Crop // обрезать картинку, что не вмещается
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // ЗДЕСЬ ЛАЙКИ И ПРОСМОТРЫ

                        Bubble(
                            buttonText = viewCounter.value.toString(),
                            leftIcon = R.drawable.ic_visibility,
                            typeButton = DARK
                        ) {
                            Toast.makeText(act, "Количество просмотров акции", Toast.LENGTH_SHORT)
                                .show()
                        }

                        Bubble(
                            buttonText = favCounter.value.toString(),
                            rightIcon = R.drawable.ic_fav,
                            typeButton = DARK,
                            rightIconColor = iconTextFavColor.value
                        ) {

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

                                                act.stockDatabaseManager.readFavCounter(stockInfo.value.keyStock!!){ favCount->

                                                    favCounter.value = favCount

                                                }

                                                iconTextFavColor.value =
                                                    WhiteDvij // При нажатии окрашиваем текст и иконку в белый
                                                buttonFavColor.value =
                                                    Grey_Background // При нажатии окрашиваем кнопку в темно-серый

                                                // Выводим ТОСТ
                                                Toast.makeText(
                                                    act, act.getString(R.string.delete_from_fav),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                    } else {

                                        // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                        act.stockDatabaseManager.addFavouriteStock(key) { addToFav ->

                                            // Если пришел колбак, что успешно

                                            if (addToFav) {

                                                act.stockDatabaseManager.readFavCounter(stockInfo.value.keyStock!!){ favCount->

                                                    favCounter.value = favCount

                                                }

                                                iconTextFavColor.value =
                                                    YellowDvij // При нажатии окрашиваем текст и иконку в черный
                                                buttonFavColor.value =
                                                    Grey_Background // Окрашиваем кнопку в главный цвет

                                                // Выводим ТОСТ
                                                Toast.makeText(
                                                    act, act.getString(R.string.add_to_fav),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }
                                        }
                                    }
                                }

                            } else {

                                // Если пользователь не авторизован, то ему выводим ТОСТ

                                Toast
                                    .makeText(
                                        act,
                                        "Чтобы добавить акцию в избранные, тебе нужно авторизоваться",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
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

                        androidx.compose.material3.Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            ),
                            elevation = CardDefaults.cardElevation(5.dp),
                            colors = CardDefaults.cardColors(Grey_Background)
                        ) {

                            Column(
                                modifier = Modifier.padding(
                                    vertical = 30.dp,
                                    horizontal = 20.dp
                                )
                            ) {

                                Row {
                                    Text(
                                        text = "#Акция",
                                        color = Grey_Text,
                                        style = Typography.labelMedium
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = "#${stockInfo.value.category}",
                                        color = Grey_Text,
                                        style = Typography.labelMedium
                                    )
                                }


                                Spacer(modifier = Modifier.height(10.dp))

                                // -------- НАЗВАНИЕ АКЦИИ ----------

                                if (stockInfo.value.headline != null && stockInfo.value.headline != "null" && stockInfo.value.headline != "") {

                                    Text(
                                        text = stockInfo.value.headline!!,
                                        style = Typography.titleMedium,
                                        color = WhiteDvij
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                if (
                                    stockInfo.value.startDate != null
                                    && stockInfo.value.finishDate != null
                                    && stockInfo.value.startDate != "null"
                                    && stockInfo.value.finishDate != ""
                                ) {

                                    Bubble(
                                        buttonText = "${stockInfo.value.startDate} - ${stockInfo.value.finishDate}"
                                    ) {}

                                }

                                Spacer(modifier = Modifier.height(20.dp))


                                // ------- ГОРОД ------------

                                if (stockInfo.value.city != null && stockInfo.value.city != "null" && stockInfo.value.city != "") {

                                    Text(
                                        text = stockInfo.value.city!!,
                                        style = Typography.labelMedium,
                                        color = WhiteDvij
                                    )
                                }

                                // ----- КАРТОЧКА ЗАВЕДЕНИЯ ----------

                                if (
                                    stockInfo.value.keyPlace != "Empty"
                                    && stockInfo.value.keyPlace != ""
                                    && stockInfo.value.keyPlace != "null"
                                    && stockInfo.value.keyPlace != null
                                    && placeInfo.value.placeKey != "Empty"
                                    && placeInfo.value.placeKey != ""
                                    && placeInfo.value.placeKey != "null"
                                    && placeInfo.value.placeKey != null
                                ) {

                                    Text(
                                        text = "${placeInfo.value.placeName}, ${placeInfo.value.address}",
                                        style = Typography.bodySmall,
                                        color = WhiteDvij
                                    )

                                } else {

                                    Text(
                                        text = "${stockInfo.value.inputHeadlinePlace}, ${stockInfo.value.inputAddressPlace}",
                                        style = Typography.bodySmall,
                                        color = WhiteDvij
                                    )

                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                if (stockInfo.value.description != null && stockInfo.value.description != "null" && stockInfo.value.description != "") {

                                    Text(
                                        text = stockInfo.value.description!!,
                                        style = Typography.bodySmall,
                                        color = WhiteDvij
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // ----- КАРТОЧКА ЗАВЕДЕНИЯ ----------

                                if (
                                    stockInfo.value.keyPlace != "Empty"
                                    && stockInfo.value.keyPlace != ""
                                    && stockInfo.value.keyPlace != "null"
                                    && stockInfo.value.keyPlace != null
                                    && placeInfo.value.placeKey != "Empty"
                                    && placeInfo.value.placeKey != ""
                                    && placeInfo.value.placeKey != "null"
                                    && placeInfo.value.placeKey != null
                                ) {

                                    Spacer(modifier = Modifier.height(30.dp))

                                    placeCard.PlaceCardSmall(
                                        navController = navController,
                                        placeItem = placeInfo.value,
                                        placeKey = placeKey
                                    )

                                }

                                // КАРТОЧКА СОЗДАТЕЛЯ

                                if (stockInfo.value.keyCreator != null && stockInfo.value.keyCreator != "null" && stockInfo.value.keyCreator != "") {

                                    Spacer(modifier = Modifier.height(30.dp))

                                    ownerCard.OwnerCardView(userKey = stockInfo.value.keyCreator!!)

                                }

                                // КНОПКА РЕДАКТИРОВАТЬ

                                if (act.mAuth.uid != null && stockInfo.value.keyCreator == act.mAuth.uid) {

                                    Spacer(modifier = Modifier.height(40.dp))

                                    ButtonCustom(
                                        buttonText = "Редактировать",
                                        leftIcon = R.drawable.ic_edit
                                    ) {
                                        stockInfo.value.keyStock?.let {
                                            act.stockDatabaseManager.readOneStockFromDataBaseReturnClass(
                                                it
                                            ) { stock ->

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
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // ------ КНОПКА УДАЛЕНИЯ ------

                                    ButtonCustom(
                                        buttonText = "Удалить",
                                        typeButton = ATTENTION,
                                        leftIcon = R.drawable.ic_close
                                    ) {
                                        openConfirmChoose.value = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
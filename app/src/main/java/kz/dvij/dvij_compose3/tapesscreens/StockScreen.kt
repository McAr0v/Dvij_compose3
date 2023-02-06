package kz.dvij.dvij_compose3.tapesscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

// функция превью экрана
class StockScreen(val act: MainActivity) {

    private val user = act.mAuth.currentUser

    // создаем акцию по умолчанию
    private val default = StockAdsClass (
        description = "Default"
    )

    @SuppressLint("NotConstructor")
    @Composable
    fun StockScreen(navController: NavController, act: MainActivity, stockKey: MutableState<String>) {

        Column {

            TabMenu(bottomPage = STOCK_ROOT, navController, act, stockKey = stockKey)
        }

    }


// экран акций

    @Composable
    fun StockTapeScreen() {
        Column(
            modifier = Modifier
                .background(Primary70)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "StockScreen TAPE")

        }
    }

    @Composable
    fun StockMyScreen(navController: NavController, stockKey: MutableState<String>) {

        // инициализируем пустой список акций

            val myStockList = remember {
                mutableStateOf(listOf<StockAdsClass>())
            }

            // считываем с БД мои акции !!!!!!!!!!!!!!

            // databaseManager.readPlaceMyDataFromDb(myPlacesList)

            // Surface для того, чтобы внизу отображать кнопочку "ДОБАВИТЬ МЕРОПРИЯТИЕ"

            Surface(modifier = Modifier.fillMaxSize()) {

                // ------- САМ КОНТЕНТ СТРАНИЦЫ ----------

                Column (
                    modifier = Modifier
                        .background(Grey95)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // ----- ЕСЛИ ЗАГРУЗИЛИСЬ МОИ Заведения ---------

                    if (myStockList.value.isNotEmpty() && myStockList.value != listOf(default)){

                        // ЗАПУСКАЕМ ЛЕНИВУЮ КОЛОНКУ

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Grey95),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ){

                            // ШАБЛОН ДЛЯ КАЖДОГО ЭЛЕМЕНТА СПИСКА

                            items(myStockList.value){ item ->
                                // act.placesCard.PlaceCard(navController = navController, placeItem = item, placeKey = placeKey)
                            }
                        }
                    } else if (myStockList.value == listOf(default) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                        // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                        Text(
                            text = stringResource(id = R.string.empty_meeting),
                            style = Typography.bodyMedium,
                            color = Grey10
                        )

                    } else if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

                        // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН ИЛИ НЕ ПОДТВЕРДИЛ ИМЕЙЛ

                        Image(
                            painter = painterResource(
                                id = R.drawable.sign_in_illustration
                            ),
                            contentDescription = stringResource(id = R.string.cd_illustration), // описание для слабовидящих
                            modifier = Modifier.size(200.dp)
                        )


                        Spacer(modifier = Modifier.height(20.dp)) // разделитель

                        Text(
                            modifier = Modifier.padding(20.dp),
                            text = "Чтобы создать свою акцию, тебе нужно авторизоваться",
                            style = Typography.bodyMedium,
                            color = Grey10,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // ------------------- КНОПКА ВОЙТИ ---------------------------------

                        Button(

                            onClick = { navController.navigate(LOG_IN_ROOT) },

                            modifier = Modifier
                                .fillMaxWidth() // кнопка на всю ширину
                                .height(50.dp) // высота - 50
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(50), // скругление углов
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = PrimaryColor, // цвет кнопки
                                contentColor = Grey100 // цвет контента на кнопке
                            )
                        )
                        {

                            // СОДЕРЖИМОЕ КНОПКИ

                            Icon(
                                painter = painterResource(id = R.drawable.ic_login), // иконка
                                contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                                tint = Grey100 // цвет иконки
                            )

                            Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                            Text(
                                text = stringResource(id = R.string.to_login), // если свитч другой, то текст "Войти",
                                style = Typography.labelMedium // стиль текста
                            )
                        }

                    } else {

                        // ------- ЕСЛИ ИДЕТ ЗАГРУЗКА ---------

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            CircularProgressIndicator(
                                color = PrimaryColor,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = stringResource(id = R.string.ss_loading),
                                style = Typography.bodyMedium,
                                color = Grey10
                            )

                        }
                    }
                }

                // -------- ПЛАВАЮЩАЯ КНОПКА СОЗДАНИЯ АКЦИИ --------------

                if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                    FloatingButton { navController.navigate(CREATE_STOCK_SCREEN) }
                }
            }
    }

    @Composable
    fun StockFavScreen() {
        Column(
            modifier = Modifier
                .background(Primary70)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "StockScreen FAV")

        }
    }
}
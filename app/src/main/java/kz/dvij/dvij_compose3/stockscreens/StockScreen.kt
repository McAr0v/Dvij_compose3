package kz.dvij.dvij_compose3.stockscreens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import kz.dvij.dvij_compose3.elements.StockCard
import kz.dvij.dvij_compose3.filters.FilterFunctions
import kz.dvij.dvij_compose3.firebase.StockAdsClass
import kz.dvij.dvij_compose3.firebase.StockDatabaseManager
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

// функция превью экрана
class StockScreen(val act: MainActivity) {

    private val databaseManager = StockDatabaseManager(act = act)
    private val stockCard = StockCard(act)

    private val filterFunctions = FilterFunctions(act)

    // создаем акцию по умолчанию
    private val default = StockAdsClass (
        description = "Default"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotConstructor")
    @Composable
    fun StockScreen(
        navController: NavController,
        act: MainActivity,
        stockKey: MutableState<String>,
        cityForFilter: MutableState<String>,
        stockCategoryForFilter: MutableState<String>,
        stockStartDateForFilter: MutableState<String>,
        stockFinishDateForFilter: MutableState<String>,
        stockSortingForFilter: MutableState<String>
    ) {

        Column {

            TabMenu(
                bottomPage = STOCK_ROOT,
                navController,
                act,
                stockKey = stockKey,
                cityForFilter = cityForFilter,
                stockCategoryForFilter = stockCategoryForFilter,
                stockSortingForFilter = stockSortingForFilter,
                stockStartDateForFilter = stockStartDateForFilter,
                stockFinishDateForFilter = stockFinishDateForFilter
            )
        }

    }

    // ----- ЛЕНТА АКЦИЙ ----------

    @Composable
    fun StockTapeScreen(
        navController: NavController,
        stockKey: MutableState<String>,
        cityForFilter: MutableState<String>,
        stockCategoryForFilter: MutableState<String>,
        stockStartDateForFilter: MutableState<String>,
        stockFinishDateForFilter: MutableState<String>,
        stockSortingForFilter: MutableState<String>
    ) {

        // инициализируем список акций
        val stockList = remember {
            mutableStateOf(listOf<StockAdsClass>())
        }

        val openFilterDialog = remember { mutableStateOf(false) } // диалог ЗАВЕДЕНИЙ

        val filter = filterFunctions.createMeetingFilter(cityForFilter.value, stockCategoryForFilter.value, stockStartDateForFilter.value)

        val removeQuery = filterFunctions.splitFilter(filter)

        val typeFilter = filterFunctions.getTypeOfFilter(removeQuery)

        // обращаемся к базе данных и записываем в список акций
        databaseManager.readStockDataFromDb(stockList)

        // -------- САМ КОНТЕНТ СТРАНИЦЫ ----------

        Column (
            modifier = Modifier
                .background(Grey95)
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ---- ЕСЛИ ЗАГРУЗИЛИСЬ АКЦИИ С БД --------

            if (stockList.value.isNotEmpty() && stockList.value != listOf(default)){

                // ---- ЛЕНИВАЯ КОЛОНКА --------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // для каждого элемента из списка указываем шаблон для отображения

                    items(stockList.value){ item ->

                        // сам шаблон карточки
                        act.stockCard.StockCard(navController = navController, stockItem = item, stockKey = stockKey)
                    }
                }
            } else if (stockList.value == listOf(default)){

                // ----- ЕСЛИ НЕТ АКЦИЙ -------

                Text(
                    text = stringResource(id = R.string.empty_meeting),
                    style = Typography.bodyMedium,
                    color = Grey10
                )

            } else {

                // -------- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    // крутилка индикатор

                    CircularProgressIndicator(
                        color = PrimaryColor,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    // текст рядом с крутилкой

                    Text(
                        text = stringResource(id = R.string.ss_loading),
                        style = Typography.bodyMedium,
                        color = Grey10
                    )

                }
            }
        }

    }

    @Composable
    fun StockMyScreen(navController: NavController, stockKey: MutableState<String>) {

        // инициализируем пустой список акций

            val myStockList = remember {
                mutableStateOf(listOf<StockAdsClass>())
            }

            // считываем с БД мои акции

            databaseManager.readStockMyDataFromDb(myStockList)

            // Surface для того, чтобы внизу отображать кнопочку "ДОБАВИТЬ АКЦИЮ"

            Surface(modifier = Modifier.fillMaxSize()) {

                // ------- САМ КОНТЕНТ СТРАНИЦЫ ----------

                Column (
                    modifier = Modifier
                        .background(Grey95)
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // ----- ЕСЛИ ЗАГРУЗИЛИСЬ МОИ АКЦИИ ---------

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
                                act.stockCard.StockCard(navController = navController, stockItem = item, stockKey = stockKey)
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
    fun StockFavScreen(navController: NavController, stockKey: MutableState<String>) {

        // Инициализируем список акций

        val favStockList = remember {
            mutableStateOf(listOf<StockAdsClass>())
        }

        // Считываем с базы данных избранные акции

        databaseManager.readStockFavDataFromDb(favStockList)


        // --------- САМ КОНТЕНТ СТРАНИЦЫ ----------

        Column (
            modifier = Modifier
                .background(Grey95)
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --------- ЕСЛИ СПИСОК НЕ ПУСТОЙ ----------

            if (favStockList.value.isNotEmpty() && favStockList.value != listOf(default)){

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // Шаблон для каждой акции

                    items(favStockList.value){ item ->
                       stockCard.StockCard(navController = navController, stockItem = item, stockKey = stockKey)
                    }
                }
            } else if (favStockList.value == listOf(default) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

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
                    text = "Чтобы добавить акцию в этот раздел, тебе нужно авторизоваться",
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

                // ---- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

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
    }
}
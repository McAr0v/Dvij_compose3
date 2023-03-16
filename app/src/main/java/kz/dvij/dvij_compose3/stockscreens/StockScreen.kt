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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.elements.FilterDialog
import kz.dvij.dvij_compose3.elements.StockCard
import kz.dvij.dvij_compose3.filters.FilterFunctions
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

// функция превью экрана
class StockScreen(val act: MainActivity) {

    private val databaseManager = StockDatabaseManager(act = act)
    private val stockCard = StockCard(act)

    private val filterFunctions = FilterFunctions(act)
    private val filterDialog = FilterDialog(act)

    // создаем акцию по умолчанию
    private val default = StockAdsClass (
        description = "Default"
    )

    private val defaultStockCard = StockCardClass(
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
        stockSortingForFilter: MutableState<String>,
        filledStockInfoFromAct: MutableState<StockAdsClass>,
        filledPlace: MutableState<PlacesCardClass>
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
                stockFinishDateForFilter = stockFinishDateForFilter,
                filledPlace = filledPlace,
                filledStock = filledStockInfoFromAct
            )
        }

    }

    // ----- ЛЕНТА АКЦИЙ ----------

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun StockTapeScreen(
        navController: NavController,
        stockKey: MutableState<String>,
        cityForFilter: MutableState<String>,
        stockCategoryForFilter: MutableState<String>,
        stockStartDateForFilter: MutableState<String>,
        stockFinishDateForFilter: MutableState<String>,
        stockSortingForFilter: MutableState<String>,
        filledStockInfoFromAct: MutableState<StockAdsClass>,
        filledPlace: MutableState<PlacesCardClass>
    ) {

        // инициализируем список акций
        val stockList = remember {
            mutableStateOf(listOf<StockCardClass>())
        }

        val openFilterDialog = remember { mutableStateOf(false) } // диалог ЗАВЕДЕНИЙ

        val filter = filterFunctions.createStockFilter(cityForFilter.value, stockCategoryForFilter.value, stockStartDateForFilter.value, stockFinishDateForFilter.value)

        val removeQuery = filterFunctions.splitFilter(filter)

        val typeFilter = filterFunctions.getTypeOfStockFilter(removeQuery)

        // обращаемся к базе данных и записываем в список акций

        act.stockDatabaseManager.readFilteredStockDataFromDb(
            stockList = stockList,
            cityForFilter = cityForFilter,
            stockCategoryForFilter = stockCategoryForFilter,
            stockStartDateForFilter = stockStartDateForFilter,
            stockFinishDateForFilter = stockFinishDateForFilter,
            stockSortingForFilter = stockSortingForFilter
        )

        //databaseManager.readStockDataFromDb(stockList)

        if (openFilterDialog.value){

            filterDialog.FilterStockChooseDialog(
                cityForFilter = cityForFilter,
                stockCategoryForFilter = stockCategoryForFilter,
                stockStartDateForFilter = stockStartDateForFilter,
                stockFinishDateForFilter = stockFinishDateForFilter,
                stockSortingForFilter = stockSortingForFilter
            ) {
                openFilterDialog.value = false
            }

        }

        Surface(modifier = Modifier.fillMaxSize()) {

            // -------- САМ КОНТЕНТ СТРАНИЦЫ ----------

            Column (
                modifier = Modifier
                    .background(Grey_Background)
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ---- ЕСЛИ ЗАГРУЗИЛИСЬ АКЦИИ С БД --------

                if (stockList.value.isNotEmpty() && stockList.value != listOf(defaultStockCard)){

                    // ---- ЛЕНИВАЯ КОЛОНКА --------

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey_Background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){

                        // для каждого элемента из списка указываем шаблон для отображения
                        if (stockList.value.isNotEmpty() && stockList.value != listOf(defaultStockCard)){

                            items(stockList.value){ item ->

                                // сам шаблон карточки
                                act.stockCard.StockCard(
                                    navController = navController,
                                    stockItem = item,
                                    stockKey = stockKey,
                                    filledPlace = filledPlace,
                                    filledStock = filledStockInfoFromAct
                                )
                            }

                        }

                    }
                } else if (stockList.value == listOf(defaultStockCard)){

                    // ----- ЕСЛИ НЕТ АКЦИЙ -------

                    Text(
                        text = stringResource(id = R.string.empty_meeting),
                        style = Typography.bodySmall,
                        color = WhiteDvij
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
                            color = YellowDvij,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        // текст рядом с крутилкой

                        Text(
                            text = stringResource(id = R.string.ss_loading),
                            style = Typography.bodySmall,
                            color = WhiteDvij
                        )

                    }
                }
            }

            // -------- ПЛАВАЮЩАЯ КНОПКА ФИЛЬТРА --------------

            FloatingStockFilterButton(
                typeOfFilter = typeFilter,
                city = cityForFilter.value,
                category = stockCategoryForFilter.value,
                startDate = stockStartDateForFilter.value,
                finishDate = stockFinishDateForFilter.value
            ) {
                openFilterDialog.value = true
            }

        }
    }

    @Composable
    fun StockMyScreen(
        navController: NavController,
        stockKey: MutableState<String>,
        filledStockInfoFromAct: MutableState<StockAdsClass>,
        filledPlace: MutableState<PlacesCardClass>
    ) {

        // инициализируем пустой список акций

            val myStockList = remember {
                mutableStateOf(listOf<StockCardClass>())
            }

            // считываем с БД мои акции

            databaseManager.readStockMyDataFromDb(myStockList)

            // Surface для того, чтобы внизу отображать кнопочку "ДОБАВИТЬ АКЦИЮ"

            Surface(modifier = Modifier.fillMaxSize()) {

                // ------- САМ КОНТЕНТ СТРАНИЦЫ ----------

                Column (
                    modifier = Modifier
                        .background(Grey_Background)
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // ----- ЕСЛИ ЗАГРУЗИЛИСЬ МОИ АКЦИИ ---------

                    if (myStockList.value.isNotEmpty() && myStockList.value != listOf(defaultStockCard)){

                        // ЗАПУСКАЕМ ЛЕНИВУЮ КОЛОНКУ

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Grey_Background),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ){

                            // ШАБЛОН ДЛЯ КАЖДОГО ЭЛЕМЕНТА СПИСКА

                            items(myStockList.value){ item ->

                                if (myStockList.value.isNotEmpty() && myStockList.value != listOf(defaultStockCard)){

                                    act.stockCard.StockCard(
                                        navController = navController,
                                        stockItem = item,
                                        stockKey = stockKey,
                                        filledPlace = filledPlace,
                                        filledStock = filledStockInfoFromAct
                                    )

                                }


                            }
                        }
                    } else if (myStockList.value == listOf(defaultStockCard) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                        // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                        Text(
                            text = stringResource(id = R.string.empty_meeting),
                            style = Typography.bodySmall,
                            color = WhiteDvij
                        )

                    } else if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

                        // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН ИЛИ НЕ ПОДТВЕРДИЛ ИМЕЙЛ

                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

                            Text(
                                text = "Чтобы создать свою акцию, тебе нужно авторизоваться",
                                style = Typography.bodySmall,
                                color = WhiteDvij,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // ------------------- КНОПКА ВОЙТИ ---------------------------------

                            ButtonCustom(
                                buttonText = stringResource(id = R.string.to_login)
                            ) {
                                navController.navigate(LOG_IN_ROOT)
                            }

                        }

                    } else {

                        // ------- ЕСЛИ ИДЕТ ЗАГРУЗКА ---------

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            CircularProgressIndicator(
                                color = YellowDvij,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = stringResource(id = R.string.ss_loading),
                                style = Typography.bodySmall,
                                color = WhiteDvij
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
    fun StockFavScreen(
        navController: NavController,
        stockKey: MutableState<String>,
        filledStockInfoFromAct: MutableState<StockAdsClass>,
        filledPlace: MutableState<PlacesCardClass>
    ) {

        // Инициализируем список акций

        val favStockList = remember {
            mutableStateOf(listOf<StockCardClass>())
        }

        // Считываем с базы данных избранные акции

        databaseManager.readStockFavDataFromDb(favStockList)


        // --------- САМ КОНТЕНТ СТРАНИЦЫ ----------

        Column (
            modifier = Modifier
                .background(Grey_Background)
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --------- ЕСЛИ СПИСОК НЕ ПУСТОЙ ----------

            if (favStockList.value.isNotEmpty() && favStockList.value != listOf(defaultStockCard)){

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey_Background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // Шаблон для каждой акции

                    items(favStockList.value){ item ->
                        if (favStockList.value.isNotEmpty() && favStockList.value != listOf(defaultStockCard)){

                            stockCard.StockCard(
                                navController = navController,
                                stockItem = item,
                                stockKey = stockKey,
                                filledPlace = filledPlace,
                                filledStock = filledStockInfoFromAct
                            )

                        }

                    }
                }
            } else if (favStockList.value == listOf(defaultStockCard) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

                // ----- ЕСЛИ СПИСОК ПУСТ, НО ПОЛЬЗОВАТЕЛЬ ЗАРЕГИСТРИРОВАН ----------

                Text(
                    text = stringResource(id = R.string.empty_meeting),
                    style = Typography.bodySmall,
                    color = WhiteDvij
                )

            } else if (act.mAuth.currentUser == null || !act.mAuth.currentUser!!.isEmailVerified){

                // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН ИЛИ НЕ ПОДТВЕРДИЛ ИМЕЙЛ

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

                    Text(
                        text = "Чтобы добавить акцию в этот раздел, тебе нужно авторизоваться",
                        style = Typography.bodySmall,
                        color = WhiteDvij,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ------------------- КНОПКА ВОЙТИ ---------------------------------

                    ButtonCustom(
                        buttonText = stringResource(id = R.string.to_login)
                    ) {
                        navController.navigate(LOG_IN_ROOT)
                    }

                }

            } else {

                // ---- ЕСЛИ ИДЕТ ЗАГРУЗКА ----------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    CircularProgressIndicator(
                        color = YellowDvij,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = stringResource(id = R.string.ss_loading),
                        style = Typography.bodySmall,
                        color = WhiteDvij
                    )
                }
            }
        }
    }
}
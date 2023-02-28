package kz.dvij.dvij_compose3.placescreens

import android.os.Build
import android.util.Log
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
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesDatabaseManager
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// функция превью экрана
class PlacesScreens (val act: MainActivity) {

    private val databaseManager = PlacesDatabaseManager(act) // инициализируем датабаз менеджер

    // создаем заведение по умолчанию
    private val default = PlacesAdsClass (
        placeDescription = "Default"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PlacesScreen(navController: NavController, placeKey: MutableState<String>) {

        Column {

            TabMenu(bottomPage = PLACES_ROOT, navController, activity = act, placesKey = placeKey)

        }
    }


    // ----- ЛЕНТА ЗАВЕДЕНИЙ -------

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun PlacesTapeScreen(navController: NavController, placeKey: MutableState<String>) {

        val date = Date()

        Log.d("MyLog", "$date")

        val timestamp = ZonedDateTime.now(ZoneId.of("Asia/Almaty"))
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE, HH:mm"))

        Log.d("MyLog", timestamp)

        // инициализируем список заведений
        val placeList = remember {
            mutableStateOf(listOf<PlacesAdsClass>())
        }

        // обращаемся к базе данных и записываем в список заведений заведения
        //databaseManager.readPlaceDataFromDb(placeList)
        databaseManager.readPlaceSortedDataFromDb(placeList)

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

            // ---- ЕСЛИ ЗАГРУЗИЛИСЬ Заведения С БД --------

            if (placeList.value.isNotEmpty() && placeList.value != listOf(default)){

                // ---- ЛЕНИВАЯ КОЛОНКА --------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // для каждого элемента из списка указываем шаблон для отображения

                    items(placeList.value){ item ->

                        // сам шаблон карточки
                        act.placesCard.PlaceCard(navController = navController, placeItem = item, placeKey = placeKey)
                    }
                }
            } else if (placeList.value == listOf(default)){

                // ----- ЕСЛИ НЕТ ЗАВЕДЕНИЙ -------

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
    fun PlacesFavScreen(navController: NavController, placeKey: MutableState<String>) {

        // Инициализируем список заведений

        val favPlacesList = remember {
            mutableStateOf(listOf<PlacesAdsClass>())
        }

        // Считываем с базы данных избранные заведения

        databaseManager.readPlacesFavDataFromDb(favPlacesList)


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

            if (favPlacesList.value.isNotEmpty() && favPlacesList.value != listOf(default)){

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey95),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){

                    // Шаблон для каждого заведения

                    items(favPlacesList.value){ item ->
                        act.placesCard.PlaceCard(navController = navController, placeItem = item, placeKey = placeKey)
                    }
                }
            } else if (favPlacesList.value == listOf(default) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

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
                    text = "Чтобы добавить заведение в этот раздел, тебе нужно авторизоваться",
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

    @Composable
    fun PlacesMyScreen(navController: NavController, placeKey: MutableState<String>) {

        // инициализируем пустой список заведений

        val myPlacesList = remember {
            mutableStateOf(listOf<PlacesAdsClass>())
        }

        // считываем с БД мои заведения

        databaseManager.readPlaceMyDataFromDb(myPlacesList)

        // Surface для того, чтобы внизу отображать кнопочку "ДОБАВИТЬ МЕРОПРИЯТИЕ"

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

                // ----- ЕСЛИ ЗАГРУЗИЛИСЬ МОИ Заведения ---------

                if (myPlacesList.value.isNotEmpty() && myPlacesList.value != listOf(default)){

                    // ЗАПУСКАЕМ ЛЕНИВУЮ КОЛОНКУ

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey95),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ){

                        // ШАБЛОН ДЛЯ КАЖДОГО ЭЛЕМЕНТА СПИСКА

                        items(myPlacesList.value){ item ->
                            act.placesCard.PlaceCard(navController = navController, placeItem = item, placeKey = placeKey)
                        }
                    }
                } else if (myPlacesList.value == listOf(default) && act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){

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
                        text = "Чтобы создать свое заведение, тебе нужно авторизоваться",
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

            // -------- ПЛАВАЮЩАЯ КНОПКА СОЗДАНИЯ ЗАВЕДЕНИЯ --------------

            if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                FloatingButton { navController.navigate(CREATE_PLACES_SCREEN) }
            }
        }
    }
}
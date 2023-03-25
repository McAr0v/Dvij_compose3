package kz.dvij.dvij_compose3.navigation

import kz.dvij.dvij_compose3.MainActivity
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.elements.SocialButtonCustom
import kz.dvij.dvij_compose3.firebase.UserInfoClass
import kz.dvij.dvij_compose3.ui.theme.*

class SideComponents (private val act: MainActivity) {

    @Composable
    fun AllSideComponents(
        navController: NavController,
        scaffoldState: ScaffoldState,
        userInfo: MutableState<UserInfoClass>,
        cityName: MutableState<String>,
        citiesList: MutableState<List<CitiesList>>
    ){

        val chooseCityNavigation = act.chooseCityNavigation

        // Инициализируем список элементов бокового меню
        val sideNavigationItemsList = listOf(
            SideNavigationItems.About,
            SideNavigationItems.PrivatePolicy,
            SideNavigationItems.Ads,
            SideNavigationItems.Bugs,
            SideNavigationItems.CallbackScreen
        )

        val coroutineScope = rememberCoroutineScope() // инициализируем корутину
        val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
        val currentRoute = navBackStackEntry?.destination?.route // получаем доступ к корню открытой страницы

        // Инициализируем список элементов бокового меню
        val adminNavigationItemsList = listOf(
            SideNavigationItems.BugsList,
            SideNavigationItems.CallbackListScreen,
        )

        LazyColumn(modifier = Modifier
            .background(Grey_OnBackground)
            .fillMaxSize()){

            item {

                HeaderSideNavigation()

            }

            item {

                AvatarBoxSideNavigation(navController = navController, scaffoldState = scaffoldState, userInfo = userInfo)

            }

            item {

                chooseCityNavigation.CityHeaderSideNavigation(cityName,citiesList) // Меню с выбором города находится теперь в отдельном классе

            }

            if (act.mAuth.uid == "oPgbRuznYcYkneErcqmCSY6Fdsg1") {

                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {

                    androidx.compose.material.Text( // ЗАГОЛОВОК ГОРОД
                        text = "Административная панель", // текст заголовка
                        color = Grey_Text, // цвет заголовка
                        style = Typography.labelMedium, // стиль заголовка
                        modifier = Modifier.padding(start = 20.dp)
                    )


                    Spacer(modifier = Modifier.height(5.dp)) // разделитель между заголовком и городом

                }

                items(adminNavigationItemsList) { item -> // для каждого итема в списке sideNavigationItemsList

                    // Создаем строку (иконка и текст должны идти друг за другом по горизонтали)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // строка должна занимать всю ширину
                            .clickable {
                                // действие на клик
                                navController.navigate(item.navRoute) // открываем нужную страницу

                                // запускаем в корутине действие, чтобы после нажатия на элемент, боковое меню закрывалось
                                coroutineScope.launch {
                                    scaffoldState.drawerState.close()
                                }
                            }
                            .padding(vertical = 10.dp, horizontal = 20.dp), // паддинги элементов
                        verticalAlignment = Alignment.CenterVertically // вертикальное выравнивание элементов по центру
                    ) {

                        // Иконка возле текста
                        Icon(
                            tint = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij, // цвет иконки
                            painter = painterResource(id = item.icon), // задаем иконку, прописанную в sealed class
                            contentDescription = stringResource(id = item.contentDescription) // описание для слабовидящих - вшито тоже в sealed class
                        )

                        // разделитель между текстом и иконкой
                        Spacer(modifier = Modifier.width(15.dp))

                        // Сам текст "Кнопки"
                        Text(
                            text = stringResource(id = item.title), // берем заголовок
                            style = Typography.bodyMedium, // Стиль текста
                            modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                            color = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij // цвет текста
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            items(sideNavigationItemsList) { item -> // для каждого итема в списке sideNavigationItemsList

                // Создаем строку (иконка и текст должны идти друг за другом по горизонтали)

                Row(
                    modifier = Modifier
                        .fillMaxWidth() // строка должна занимать всю ширину
                        .clickable {
                            // действие на клик
                            navController.navigate(item.navRoute) // открываем нужную страницу

                            // запускаем в корутине действие, чтобы после нажатия на элемент, боковое меню закрывалось
                            coroutineScope.launch {
                                scaffoldState.drawerState.close()
                            }
                        }
                        .padding(vertical = 10.dp, horizontal = 20.dp), // паддинги элементов
                    verticalAlignment = Alignment.CenterVertically // вертикальное выравнивание элементов по центру
                ) {

                    // Иконка возле текста
                    Icon(
                        tint = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij, // цвет иконки
                        painter = painterResource(id = item.icon), // задаем иконку, прописанную в sealed class
                        contentDescription = stringResource(id = item.contentDescription) // описание для слабовидящих - вшито тоже в sealed class
                    )

                    // разделитель между текстом и иконкой
                    Spacer(modifier = Modifier.width(15.dp))

                    // Сам текст "Кнопки"
                    Text(
                        text = stringResource(id = item.title), // берем заголовок
                        style = Typography.bodyMedium, // Стиль текста
                        modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                        color = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij // цвет текста
                    )
                }
            }

            item { SubscribeBoxSideNavigation() }

        }

    }

    @Composable
    fun HeaderSideNavigation(){

        // Логотип Движа в боковой навигации

        Box( // создаем контейнер для логотипа
            modifier = Modifier
                .fillMaxWidth() // говорим, чтобы занял всю ширину
                .background(Grey_OnBackground) // цвет фона контейнера
                .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 20.dp), // паддинги контейнера
            contentAlignment = Alignment.CenterStart // выравнивание
        ){
            Icon( // помещаем логотип как векторную иконку
                painter = painterResource(id = R.drawable.dvij_logo), // задаем логотип
                contentDescription = stringResource(id = R.string.cd_logo), // задаем описание для слабослышаших
                tint = YellowDvij // окрашиваем логотип
            )
        }
    }

    @Composable
    fun SubscribeBoxSideNavigation(){

        // Раздел ПОДПИШИСЬ НА ДВИЖ

        Column( // создаем контейнер-стобец
            modifier = Modifier
                .fillMaxSize() // занять максимальный размер (чтобы внизу тоже заполнял осташееся пространство)
                .background(Grey_OnBackground) // цвет фона
                .padding(20.dp), // отступы со всех сторон
            verticalArrangement = Arrangement.Top, // выравнивание по вертикали
            horizontalAlignment = Alignment.Start // выравнивание по горизонтали (слева)

        ) {

            // Заголовок ПОДПИШИСЬ НА ДВИЖ

            Text(
                text = stringResource(id = R.string.subscribe), // текст заголовка
                color = Grey_Text, // цвет заголовка
                style = Typography.labelMedium // стиль заголовка
            )

            // Создаем строку, в строку поместим иконки с ссылками на соц сети

            Row(
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 10.dp) // отступы сверху и снизу
            ) {
                val context = LocalContext.current // инициализируем контекст для отображения ТОСТОВ. Когда уберу тосты, можно удалить по идее

                SocialButtonCustom(icon = R.drawable.instagram) {
                    Toast.makeText(context, "Сделать нужную функцию", Toast.LENGTH_LONG).show()
                }

                Spacer(modifier = Modifier.width(10.dp)) // разделитель между иконками

                SocialButtonCustom(icon = R.drawable.telegram) {
                    Toast.makeText(context, "Сделать нужную функцию", Toast.LENGTH_LONG).show()
                }

                Spacer(modifier = Modifier.width(10.dp)) // разделитель между иконками

                SocialButtonCustom(icon = R.drawable.whatsapp) {
                    Toast.makeText(context, "Сделать нужную функцию", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    @Composable
    fun AvatarBoxSideNavigation(
        navController: NavController, // принимаем навконтроллер чтобы переходить на страницу профиля
        scaffoldState: ScaffoldState, // принимаем скаффолд стейт, чтобы потом можно было после нажатия закрывать боковое меню
        userInfo: MutableState<UserInfoClass>,
    ) {



        // РАЗДЕЛ С АВАТАРКОй
        val loggedUser = act.mAuth.currentUser // получаем пользователя - авторизован он или нет



        val coroutineScope = rememberCoroutineScope() // инициализируем корутину

        // УСЛОВИЕ - ЕСЛИ АВТОРИЗОВАН, ТО КОНТЕНТ ОДИН, ЕСЛИ НЕТ, ТО ДРУГОЙ



        if (loggedUser != null && loggedUser.isEmailVerified) { // КОНТЕНТ ДЛЯ АВТОРИЗОВАННОГО ПОЛЬЗОВАТЕЛЯ

            Row( // используем строку
                modifier = Modifier
                    .fillMaxWidth() // занимаем всю ширину
                    .background(Grey_OnBackground) // цвет фона
                    .padding(20.dp) // отступы
                    .clickable { // действие на нажатие

                        coroutineScope.launch {
                            scaffoldState.drawerState.close() // закрываем боковое меню
                        }
                        navController.navigate(PROFILE_ROOT) // переходим на страницу пользователя


                    },

                verticalAlignment = Alignment.CenterVertically // выравнивание по вертикали (ПО ЦЕНТРУ),


            ) {

                // АВАТАРКА ПОЛЬЗОВАТЕЛЯ

                if (userInfo.value.avatar != ""){

                    // --- Если пользователь поменял аватарку сам --

                    AsyncImage(
                        model = userInfo.value.avatar,
                        contentDescription = stringResource(id = R.string.icon_user_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape))

                } else if (loggedUser.photoUrl != null) {

                    // --- Если пользователь имеет аккаунт гугла но не менял аватарку. Грубо говоря подгрузится изображение из гугл

                    AsyncImage(
                        model = loggedUser.photoUrl,
                        contentDescription = stringResource(id = R.string.icon_user_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape))
                } else {

                    // --- Если нет аватарки -----

                    Image(
                        painter = painterResource(id = R.drawable.no_user_image), // по идее сюда надо будет передавать из гугла, или иметь возможность загружать
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(id = R.string.cd_avatar), // описание для слабовидящих
                        modifier = Modifier
                            .size(60.dp) // размер аватарки
                            .clip(CircleShape) // делаем ее круглой
                    )
                }

                // КОЛОНКА С ИМЕНЕМ И EMAIL

                if (userInfo.value.name != "" && userInfo.value.surname != ""){

                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp) // паддинг слева
                            .weight(1f),// ширина - колонка займет оставшуюся ширину среди всех элементов
                        verticalArrangement = Arrangement.Center
                    ){

                        Text(
                            text = "${userInfo.value.name} ${userInfo.value.surname}", // сюда нужно передавать имя пользователя из БД
                            color = WhiteDvij, // цвет имени
                            style = Typography.bodyMedium // стиль текста
                        )

                        Text(
                            text = loggedUser.email!!, // сюда нужно передавать email пользователя из БД
                            color = Grey_Text, // цвет Email
                            style = Typography.labelMedium // стиль текста
                        )
                    }

                } else if (loggedUser.displayName == null || loggedUser.displayName == ""){

                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp) // паддинг слева
                            .weight(1f),// ширина - колонка займет оставшуюся ширину среди всех элементов
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = loggedUser.email!!, // сюда нужно передавать email пользователя из БД
                            color = Grey_Text, // цвет Email
                            style = Typography.labelMedium // стиль текста
                        )
                    }

                }  else {

                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp) // паддинг слева
                            .weight(1f),// ширина - колонка займет оставшуюся ширину среди всех элементов
                        verticalArrangement = Arrangement.Center
                    ){

                        Text(
                            text = loggedUser.displayName!!, // сюда нужно передавать имя пользователя из БД
                            color = WhiteDvij, // цвет имени
                            style = Typography.bodyMedium // стиль текста
                        )

                        Text(
                            text = loggedUser.email!!, // сюда нужно передавать email пользователя из БД
                            color = Grey_Text, // цвет Email
                            style = Typography.labelMedium // стиль текста
                        )
                    }


                }

                Spacer(modifier = Modifier.width(10.dp)) // разделитель между именем и кнопкой редактировать

                // ИКОНКА РЕДАКТИРОВАТЬ

                Icon(
                    painter = painterResource(id = R.drawable.ic_right), // сама иконка
                    contentDescription = stringResource(id = R.string.cd_move_to_profile), // описание для слабовидящих
                    tint = WhiteDvij // цвет иконки
                )
            }

        } else if (loggedUser != null && !loggedUser.isEmailVerified){

            // если пользователь зарегистрировался, но еще не верифицировал email

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey_OnBackground)
                    .padding(20.dp)
                    .clickable {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                        navController.navigate(THANK_YOU_PAGE_ROOT) { popUpTo(0) }

                    },
                verticalAlignment = Alignment.CenterVertically // выравнивание по вертикали

            ) {

                Column(
                    modifier = Modifier.weight(1f) // колонка займет всю ширину, которая останется после добавления элементов
                ) {

                    Text(
                        text = stringResource(id = R.string.verify_email_and_sign_in), // сам текст
                        color = WhiteDvij, // цвет текста
                        style = Typography.bodyMedium // стиль текста
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = stringResource(id = R.string.verify_email_text), // сам текст активируй аккаунт
                        color = Grey_Text, // цвет текста
                        style = Typography.labelMedium // стиль текста
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // КНОПКА ДЛЯ ПЕРЕХОДА НА СТРАНИЦУ РЕГИСТРАЦИИ

                    ButtonCustom(buttonText = stringResource(id = R.string.i_activate_profile)) {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                        navController.navigate(LOG_IN_ROOT)
                    }


                }
            }

        } else {

            // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ ЗАЛОГИНЕН ------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey_OnBackground)
                    .padding(20.dp)
                    .clickable {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                        navController.navigate(LOG_IN_ROOT)
                    },
                verticalAlignment = Alignment.CenterVertically

            ) {

                Column(
                    modifier = Modifier.weight(1f) // колонка займет всю ширину, которая останется после добавления элементов
                ) {
                    Text(
                        text = stringResource(id = R.string.guest), // текст ГОСТЬ
                        color = WhiteDvij, // цвет текста
                        style = Typography.bodyMedium // стиль текста
                    )
                    Text(
                        text = stringResource(id = R.string.login_or_register), // текст ВОЙДИТЕ ИЛИ ЗАРЕГИСТРИРУЙТЕСЬ
                        color = Grey_Text, // цвет текста
                        style = Typography.labelMedium // стиль текста
                    )
                }

                // ИКОНКА ВХОД

                Icon(
                    painter = painterResource(id = R.drawable.ic_login), // сама иконка
                    contentDescription = stringResource(id = R.string.login_or_register), // описание для слабовидящих
                    tint = WhiteDvij // цвет иконки
                )
            }
        }
    }



    // Функция с элементами бокового меню
    @Composable
    fun BodySideNavigation(
        navController: NavController, // принимаем НавКонтроллер
        scaffoldState: ScaffoldState // Принимаем состояние скаффолда для реализации закрытия бокового меню после нажатия на элемент
    ) {
        // Инициализируем список элементов бокового меню
        val sideNavigationItemsList = listOf(
            SideNavigationItems.About,
            SideNavigationItems.PrivatePolicy,
            SideNavigationItems.Ads,
            SideNavigationItems.Bugs,
            SideNavigationItems.CallbackScreen
        )

        val coroutineScope = rememberCoroutineScope() // инициализируем корутину
        val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
        val currentRoute = navBackStackEntry?.destination?.route // получаем доступ к корню открытой страницы

        LazyColumn(
            Modifier
                .background(color = Grey_OnBackground)
                .padding(vertical = 10.dp)

        ) {
            // Помещаем все в "ленивую" колонку

            // Начинаем создавать элемент меню

            items(sideNavigationItemsList) { item -> // для каждого итема в списке sideNavigationItemsList

                // Создаем строку (иконка и текст должны идти друг за другом по горизонтали)

                Row(
                    modifier = Modifier
                        .fillMaxWidth() // строка должна занимать всю ширину
                        .clickable {
                            // действие на клик
                            navController.navigate(item.navRoute) // открываем нужную страницу

                            // запускаем в корутине действие, чтобы после нажатия на элемент, боковое меню закрывалось
                            coroutineScope.launch {
                                scaffoldState.drawerState.close()
                            }
                        }
                        .padding(vertical = 10.dp, horizontal = 20.dp), // паддинги элементов
                    verticalAlignment = Alignment.CenterVertically // вертикальное выравнивание элементов по центру
                ) {

                    // Иконка возле текста
                    Icon(
                        tint = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij, // цвет иконки
                        painter = painterResource(id = item.icon), // задаем иконку, прописанную в sealed class
                        contentDescription = stringResource(id = item.contentDescription) // описание для слабовидящих - вшито тоже в sealed class
                    )

                    // разделитель между текстом и иконкой
                    Spacer(modifier = Modifier.width(15.dp))

                    // Сам текст "Кнопки"
                    Text(
                        text = stringResource(id = item.title), // берем заголовок
                        style = Typography.bodyMedium, // Стиль текста
                        modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                        color = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij // цвет текста
                    )
                }
            }
        }
    }

    @Composable
    fun AdminSideNavigation(
        navController: NavController, // принимаем НавКонтроллер
        scaffoldState: ScaffoldState // Принимаем состояние скаффолда для реализации закрытия бокового меню после нажатия на элемент
    ) {
        // Инициализируем список элементов бокового меню
        val adminNavigationItemsList = listOf(
            SideNavigationItems.BugsList,
            SideNavigationItems.CallbackListScreen,
        )

        val coroutineScope = rememberCoroutineScope() // инициализируем корутину
        val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
        val currentRoute = navBackStackEntry?.destination?.route // получаем доступ к корню открытой страницы

        LazyColumn(
            Modifier
                .background(color = Grey_OnBackground)
                .padding(vertical = 10.dp)
        ) {
            // Помещаем все в "ленивую" колонку

            // Начинаем создавать элемент меню

            items(adminNavigationItemsList) { item -> // для каждого итема в списке sideNavigationItemsList

                // Создаем строку (иконка и текст должны идти друг за другом по горизонтали)

                Row(
                    modifier = Modifier
                        .fillMaxWidth() // строка должна занимать всю ширину
                        .clickable {
                            // действие на клик
                            navController.navigate(item.navRoute) // открываем нужную страницу

                            // запускаем в корутине действие, чтобы после нажатия на элемент, боковое меню закрывалось
                            coroutineScope.launch {
                                scaffoldState.drawerState.close()
                            }
                        }
                        .padding(vertical = 10.dp, horizontal = 20.dp), // паддинги элементов
                    verticalAlignment = Alignment.CenterVertically // вертикальное выравнивание элементов по центру
                ) {

                    // Иконка возле текста
                    Icon(
                        tint = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij, // цвет иконки
                        painter = painterResource(id = item.icon), // задаем иконку, прописанную в sealed class
                        contentDescription = stringResource(id = item.contentDescription) // описание для слабовидящих - вшито тоже в sealed class
                    )

                    // разделитель между текстом и иконкой
                    Spacer(modifier = Modifier.width(15.dp))

                    // Сам текст "Кнопки"
                    Text(
                        text = stringResource(id = item.title), // берем заголовок
                        style = Typography.bodyMedium, // Стиль текста
                        modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                        color = if (item.navRoute == currentRoute) YellowDvij else WhiteDvij // цвет текста
                    )
                }
            }
        }
    }

}
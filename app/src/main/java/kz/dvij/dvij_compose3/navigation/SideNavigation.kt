package kz.dvij.dvij_compose3.navigation

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
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.ui.theme.*


// https://semicolonspace.com/jetpack-compose-navigation-drawer/
// https://www.youtube.com/watch?v=JLICaBEiJS0


@Composable
fun HeaderSideNavigation(){

    // Логотип Движа в боковой навигации

    Box( // создаем контейнер для логотипа
        modifier = Modifier
            .fillMaxWidth() // говорим, чтобы занял всю ширину
            .background(Grey100) // цвет фона контейнера
            .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 20.dp), // паддинги контейнера
        contentAlignment = Alignment.CenterStart // выравнивание
    ){
        Icon( // помещаем логотип как векторную иконку
            painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.dvij_logo), // задаем логотип
            contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.cd_logo), // задаем описание для слабослышаших
            tint = PrimaryColor // окрашиваем логотип
        )
    }
}

@Composable
fun SubscribeBoxSideNavigation(){

    // Раздел ПОДПИШИСЬ НА ДВИЖ

    Column( // создаем контейнер-стобец
        modifier = Modifier
            .fillMaxSize() // занять максимальный размер (чтобы внизу тоже заполнял осташееся пространство)
            .background(Grey100) // цвет фона
            .padding(20.dp), // отступы со всех сторон
        verticalArrangement = Arrangement.Top, // выравнивание по вертикали
        horizontalAlignment = Alignment.Start // выравнивание по горизонтали (слева)

    ) {

        // Заголовок ПОДПИШИСЬ НА ДВИЖ

        Text(
            text = stringResource(id = kz.dvij.dvij_compose3.R.string.subscribe), // текст заголовка
            color = Grey40, // цвет заголовка
            style = Typography.labelMedium // стиль заголовка
        )

        // Создаем строку, в строку поместим иконки с ссылками на соц сети

        Row(
            modifier = Modifier
                .padding(top = 15.dp, bottom = 10.dp) // отступы сверху и снизу
        ) {
            val context = LocalContext.current // инициализируем контекст для отображения ТОСТОВ. Когда уберу тосты, можно удалить по идее

            // ИКОНКА ИНСТАГРАМ

            IconButton(
                onClick = { // пока заглушка в виде тоста. Надо будет сюда вставить функцию перехода
                Toast.makeText(context, "Сделать нужную функцию", Toast.LENGTH_LONG).show()
            }
            ) {
                Icon( // Сама иконка инстаграма
                    modifier = Modifier
                        .clip(CircleShape) // делаем круглый фон
                        .background(Grey95) // фон иконки
                        .padding(10.dp) // отступ внутри до иконки
                        .size(25.dp), // размер иконки
                    painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.instagram), // сама иконка
                    contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.subscribe_to_instagram), // описание для слабовидящих
                    tint = Grey00 // цвет иконки
                )
            }

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между иконками

            // ИКОНКА ТЕЛЕГРАМА

            IconButton(
                onClick = { // пока заглушка в виде тоста. Надо будет сюда вставить функцию перехода
                    Toast.makeText(context, "Сделать нужную функцию", Toast.LENGTH_LONG).show()
                }
            ) {

                // Сама иконка Телеграма

                Icon(
                    modifier = Modifier
                        .clip(CircleShape) // делаем круглый фон
                        .background(Grey95) // фон иконки
                        .padding(10.dp) // отступ внутри до иконки
                        .size(25.dp), // размер иконки
                    painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.telegram), // сама иконка
                    contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.subscribe_to_telegram), // описание для слабовидящих
                    tint = Grey00 // цвет иконки
                )

            }

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между иконками

            // Иконка WHATSAPP

            IconButton(
                onClick = { // пока заглушка в виде тоста. Надо будет сюда вставить функцию перехода
                    Toast.makeText(context, "Сделать нужную функцию", Toast.LENGTH_LONG).show()
                }
            ) {

                // Сама иконка

                Icon(
                    modifier = Modifier
                        .clip(CircleShape) // делаем круглый фон
                        .background(Grey95) // фон иконки
                        .padding(10.dp) // отступ внутри до иконки
                        .size(25.dp), // размер иконки
                    painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.whatsapp), // сама иконка
                    contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.subscribe_to_whatsapp), // описание для слабовидящих
                    tint = Grey00 // цвет иконки
                )
            }
        }
    }
}

@Composable
fun AvatarBoxSideNavigation(
    user: FirebaseUser?, // принимаем параметр - авторизирован или нет
    navController: NavController, // принимаем навконтроллер чтобы переходить на страницу профиля
    scaffoldState: ScaffoldState // принимаем скаффолд стейт, чтобы потом можно было после нажатия закрывать боковое меню
    ) {

    // РАЗДЕЛ С АВАТАРКОй

    val coroutineScope = rememberCoroutineScope() // инициализируем корутину
    val context = LocalContext.current // инициализируем контекст для ТОСТОВ

    // УСЛОВИЕ - ЕСЛИ АВТОРИЗОВАН, ТО КОНТЕНТ ОДИН, ЕСЛИ НЕТ, ТО ДРУГОЙ

    if (user != null && user.isEmailVerified) { // КОНТЕНТ ДЛЯ АВТОРИЗОВАННОГО ПОЛЬЗОВАТЕЛЯ

        Row( // используем строку
            modifier = Modifier
                .fillMaxWidth() // занимаем всю ширину
                .background(Grey100) // цвет фона
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

            if (user.photoUrl != null) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = stringResource(id = R.string.icon_user_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .border(BorderStroke(2.dp, PrimaryColor), CircleShape)
                        .clip(CircleShape))
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.no_user_image), // по идее сюда надо будет передавать из гугла, или иметь возможность загружать
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.cd_avatar), // описание для слабовидящих
                    modifier = Modifier
                        .size(60.dp) // размер аватарки
                        .border(BorderStroke(2.dp, PrimaryColor), CircleShape)
                        .clip(CircleShape) // делаем ее круглой
                )
            }

            // КОЛОНКА С ИМЕНЕМ И EMAIL
            if (user.displayName == null || user.displayName == ""){

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp) // паддинг слева
                        .weight(1f),// ширина - колонка займет оставшуюся ширину среди всех элементов
                    verticalArrangement = Arrangement.Center
                ){
                    Text(
                        text = user.email!!, // сюда нужно передавать email пользователя из БД
                        color = Grey40, // цвет Email
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
                            text = user.displayName!!, // сюда нужно передавать имя пользователя из БД
                            color = Grey40, // цвет имени
                            style = Typography.titleSmall // стиль текста
                        )

                    Text(
                        text = user.email!!, // сюда нужно передавать email пользователя из БД
                        color = Grey40, // цвет Email
                        style = Typography.labelSmall // стиль текста
                    )
                }


            }

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между именем и кнопкой редактировать

            // ИКОНКА РЕДАКТИРОВАТЬ

            Icon(
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.ic_edit), // сама иконка
                contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.to_change_location), // описание для слабовидящих
                tint = Grey40 // цвет иконки
            )
        }

    } else if (user != null && !user.isEmailVerified){

    // если пользователь зарегистрировался, но еще не верифицировал email
        // КОНТЕНТ если не авторизован пользователь

            Row( // используем строку
                modifier = Modifier
                    .fillMaxWidth() // занимаем всю ширину
                    .background(Grey100) // цвет фона
                    .padding(20.dp) // отступы
                    .clickable { // действие на нажатие. По идее потом надо вести на страницу авторизации
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                        navController.navigate(LOG_IN_ROOT)

                    },
                verticalAlignment = Alignment.CenterVertically // выравнивание по вертикали

            ) {

                // ТЕКСТ ГОСТЬ

                Column(
                    modifier = Modifier.weight(1f) // колонка займет всю ширину, которая останется после добавления элементов
                ) {

                    Text( // текст ГОСТЬ
                        text = stringResource(id = R.string.verify_email_title), // сам текст
                        color = Grey40, // цвет текста
                        style = Typography.titleMedium // стиль текста
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text( // текст ВОЙДИТЕ ИЛИ ЗАРЕГИСТРИРУЙТЕСЬ
                        text = stringResource(id = R.string.verify_email_text), // сам текст
                        color = Grey40, // цвет текста
                        style = Typography.labelSmall // стиль текста
                    )
                }
            }

    } else { // КОНТЕНТ если не авторизован пользователь

        Row( // используем строку
            modifier = Modifier
                .fillMaxWidth() // занимаем всю ширину
                .background(Grey100) // цвет фона
                .padding(20.dp) // отступы
                .clickable { // действие на нажатие. По идее потом надо вести на страницу авторизации
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                    navController.navigate(REG_ROOT)

                },
            verticalAlignment = Alignment.CenterVertically // выравнивание по вертикали

        ) {

            // ТЕКСТ ГОСТЬ

            Column(
                modifier = Modifier.weight(1f) // колонка займет всю ширину, которая останется после добавления элементов
            ) {
                Text( // текст ГОСТЬ
                    text = stringResource(id = kz.dvij.dvij_compose3.R.string.guest), // сам текст
                    color = Grey40, // цвет текста
                    style = Typography.titleMedium // стиль текста
                )
                Text( // текст ВОЙДИТЕ ИЛИ ЗАРЕГИСТРИРУЙТЕСЬ
                    text = stringResource(id = kz.dvij.dvij_compose3.R.string.login_or_register), // сам текст
                    color = Grey40, // цвет текста
                    style = Typography.labelSmall // стиль текста
                )
            }

            // ИКОНКА ВХОД

            Icon(
                painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.ic_login), // сама иконка
                contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.login_or_register), // описание для слабовидящих
                tint = Grey40 // цвет иконки
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
    val sideNavigationItemsList = listOf<SideNavigationItems>(
        SideNavigationItems.About,
        SideNavigationItems.PrivatePolicy,
        SideNavigationItems.Ads,
        SideNavigationItems.Bugs
    )

    val coroutineScope = rememberCoroutineScope() // инициализируем корутину
    val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
    val currentRoute = navBackStackEntry?.destination?.route // получаем доступ к корню открытой страницы

    LazyColumn(
        Modifier
            .background(color = Grey100) // окрашиваем в черный
            //.fillMaxHeight() // занимаем весь размер
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
                    tint = if (item.navRoute == currentRoute) PrimaryColor else Grey40, // цвет иконки
                    painter = painterResource(id = item.icon), // задаем иконку, прописанную в sealed class
                    contentDescription = stringResource(id = item.contentDescription) // описание для слабовидящих - вшито тоже в sealed class
                )

                // разделитель между текстом и иконкой
                Spacer(modifier = Modifier.width(15.dp))

                // Сам текст "Кнопки"
                Text(
                    text = stringResource(id = item.title), // берем заголовок
                    style = Typography.labelLarge, // Стиль текста
                    modifier = Modifier.weight(1f), // Текст займет всю оставшуюся ширину
                    color = if (item.navRoute == currentRoute) PrimaryColor else Grey40 // цвет текста
                )
            }
        }
    }
}

@Composable
fun LogInButton (
    navController: NavController,
    scaffoldState: ScaffoldState
) {

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Grey100)) {
        Button(onClick = {
            navController.navigate("LoginScreen")
            coroutineScope.launch {
                scaffoldState.drawerState.close()
            }
        }){
            Text(text = "Перейти на страницу логина")
        }
    }
}





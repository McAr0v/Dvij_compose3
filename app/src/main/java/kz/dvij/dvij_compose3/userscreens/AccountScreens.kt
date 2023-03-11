package kz.dvij.dvij_compose3.userscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.constants.PRIMARY
import kz.dvij.dvij_compose3.constants.SECONDARY
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.elements.ButtonGoogleCustom
import kz.dvij.dvij_compose3.elements.fieldEmailComponent
import kz.dvij.dvij_compose3.elements.fieldPasswordComponent
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class AccountScreens(val act: MainActivity) {

    // https://storyset.com/illustration/forgot-password/bro -- картинки

    private val accountHelper = AccountHelper(act) // инициализируем класс AccountHelper




    // ------- СТРАНИЦА ВХОДА / РЕГИСТРАЦИИ ---------

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun SignInUpPage(switch: String, navController: NavController){

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Grey_Background)) {

            // помещаем все содержимое страницы в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого


            // ------- КНОПКА ЗАКРЫТЬ ЭКРАН ---------


            // создаем строку, чтобы задать положение кнопки "Закрыть"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey_Background
                    )
                    .padding(top = 10.dp, bottom = 0.dp, start = 20.dp, end = 10.dp),
                verticalAlignment = Alignment.Top, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.SpaceBetween // выравнивание кнопки по правому краю
            ) {

                // -------------- Логотип ------------------

                Icon(
                    painter = painterResource(R.drawable.dvij_logo),
                    contentDescription = stringResource(id = R.string.cd_logo), // описание для слабовидящих
                    modifier = Modifier.size(160.dp),
                    tint = YellowDvij
                )

                // ------- сама кнопка Закрыть ---------


                IconButton(
                    onClick = {
                        navController.navigate(MEETINGS_ROOT) {popUpTo(0)}
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = WhiteDvij
                    )
                }
            }

            // ------ КОЛОНКА С ОСТАВШИМСЯ СОДЕРЖИМЫМ СТРАНИЦЫ ----------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey_Background
                    )
                    .verticalScroll(rememberScrollState()) // Делаем колонку с вертикальным скролом
                    .padding(top = 0.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {

                // создаем переменные email и password - содержимое их будет меняться
                // в зависимости от того, что введет пользователь и это содержимое будет
                // отправляться в Firebase

                val email = remember { mutableStateOf("") }
                val password = remember { mutableStateOf("") }


                // --------------- САМ КОНТЕНТ СТРАНИЦЫ ------------------


                /* Так как страница меняется в зависимости от переключателя switch
            будет много условий, какой switch передан в функцию. Т.е выбрана страница
            регистрации или входа
            */





                // -------------  ЗАГОЛОВОК ---------------


                Text(  // заголовок зависит от switch
                    text = if (switch == REGISTRATION) {
                        stringResource(id = R.string.registration)
                    } else {
                        stringResource(id = R.string.log_in)
                    },
                    style = Typography.titleLarge, // стиль заголовка
                    color = WhiteDvij // цвет заголовка
                )

                Spacer(modifier = Modifier.height(10.dp)) // разделитель


                // --------------- ПЕРЕКЛЮЧАТЕЛЬ МЕЖДУ ВХОДОМ И РЕГИСТРАЦИЕЙ --------------------------


                Row(
                    modifier = Modifier // создаем колонку
                        .fillMaxWidth(), // на всю ширину
                    verticalAlignment = Alignment.CenterVertically, // выравнивание по вертикали
                    horizontalArrangement = Arrangement.Start // выравнивание по центру
                ) {


                    // ------ 1й ТЕКСТ - НЕ ГИПЕРССЫЛКА ----------


                    Text(

                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.have_account)
                        } else {
                            stringResource(id = R.string.no_have_account)
                        },

                        style = Typography.bodySmall, // стиль текста
                        color = WhiteDvij // цвет текста
                    )


                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между словами


                    // ------ 2й ТЕКСТ - ГИПЕРССЫЛКА ------------


                    Text(

                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.to_login)
                        } else {
                            stringResource(id = R.string.to_registration)
                        },

                        style = Typography.bodySmall, // стиль текста
                        color = YellowDvij, // цвет текста
                        modifier = Modifier.clickable { // действие на клик

                            // перейти на страницу входа или регистрации

                            if (switch == REGISTRATION) {

                                navController.navigate(LOG_IN_ROOT)

                            } else {

                                navController.navigate(REG_ROOT)

                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(50.dp)) // разделитель между


                // -------- ТЕКСТОВОЕ ПОЛЕ EMAIL -----------------


                email.value = fieldEmailComponent(act = act)

                Spacer(modifier = Modifier.height(20.dp)) // разделитель между полями


                // ------------    ТЕКСТОВОЕ ПОЛЕ С ПАРОЛЕМ ----------------

                password.value = fieldPasswordComponent(act = act)

                Spacer(modifier = Modifier.height(20.dp)) // раздетиль между формой и кнопкой


                // ------------------- КНОПКА РЕГИСТРАЦИЯ / ВХОД ---------------------------------

                ButtonCustom(

                    buttonText = if (switch == REGISTRATION) {
                        stringResource(id = R.string.to_registration) // если свитч регистрация, то текст "Регистрация"
                    } else {
                        stringResource(id = R.string.to_login) // если свитч другой, то текст "Войти"
                    },

                    typeButton = PRIMARY // Тип кнопки

                ) {

                    // ----------- ДЕЙСТВИЕ НА НАЖАТИЕ ------------

                    // все заключаем в условие - надо чтобы поля имейл и пассворд были обязательно заполнены
                    if (email.value.isNotEmpty() && password.value.isNotEmpty()) {

                        // далее еще уловие - если switch РЕГИСТРАЦИЯ, то функция РЕГИСТРАЦИИ

                        // ----------------- РЕГИСТРАЦИЯ -------------------

                        if (switch == REGISTRATION) {

                            accountHelper.registerWIthEmailAndPassword(email = email.value, password = password.value){ user ->

                                accountHelper.sendEmailVerification(user){ result ->
                                    if (result){
                                        navController.navigate(THANK_YOU_PAGE_ROOT) {popUpTo(0)}
                                        Toast.makeText(act, R.string.send_email_verification_success, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(act, R.string.send_email_verification_error, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        } else {

                            // ------------------ ВХОД -----------------

                            // запускаем функцию signInWithEmailAndPassword и вешаем слушатель, что все завершилось

                            accountHelper.signInWithEmailAndPassword(email = email.value, password = password.value) { result ->

                                if (result){

                                    navController.navigate(PROFILE_ROOT) {popUpTo(0)} // переходим на страницу профиля

                                    Toast.makeText(
                                        act,
                                        act.resources.getString(R.string.log_in_successful),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }

                    } else {

                        // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ ЗАПОЛНИЛ ВСЕ ПОЛЯ -----

                        Toast.makeText(
                            act,
                            act.resources.getString(R.string.em_not_all_fields),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }


                // ----------- НАДПИСЬ ЗАБЫЛИ ПАРОЛЬ? -----------------------

                if (switch != REGISTRATION) {

                    Spacer(modifier = Modifier.height(30.dp)) // Разделитель

                    Row(
                        modifier = Modifier // создаем колонку
                            .fillMaxWidth(), // на всю ширину
                        verticalAlignment = Alignment.CenterVertically, // выравнивание по вертикали
                        horizontalArrangement = Arrangement.Start // выравнивание по центру
                    ) {
                        Text( // 1й текст (НЕ ГИПЕРССЫЛКА)

                            text = stringResource(id = R.string.forgot_password),
                            style = Typography.bodySmall, // стиль текста
                            color = WhiteDvij // цвет текста
                        )

                        Spacer(modifier = Modifier.width(10.dp)) // разделитель между словами

                        Text( // 2й текст, который КЛИКАБЕЛЬНЫЙ

                            text = stringResource(id = R.string.help_change_password),

                            style = Typography.bodySmall, // стиль текста
                            color = YellowDvij, // цвет текста
                            modifier = Modifier.clickable { // действие на клик

                                // перейти на страницу

                                navController.navigate(FORGOT_PASSWORD_ROOT)
                            }
                        )
                    }

                }

                Spacer(modifier = Modifier.height(30.dp)) // Разделитель


                // -------- строка ЕСТЬ АККАУНТ ГУГЛ -------------

                Text( // 1й текст (НЕ ГИПЕРССЫЛКА)

                    text = stringResource(id = R.string.have_google_acc),
                    style = Typography.bodySmall, // стиль текста
                    color = WhiteDvij // цвет текста
                )


                Spacer(modifier = Modifier.height(10.dp)) // разделитель между мини заголовком и кнопкой


                // -------------- КНОПКА ВХОДА ЧЕРЕЗ GOOGLE ------------------

                ButtonGoogleCustom(
                    buttonText = if (switch == REGISTRATION) {
                        stringResource(id = R.string.reg_google_acc)
                    } else {
                        stringResource(id = R.string.log_in_google_acc)
                    },
                    leftIcon = R.drawable.ic_google_color
                ) {

                    // ----- ДЕЙСТВИЕ НА НАЖАТИЕ -------

                    GlobalScope.launch(Dispatchers.IO) {

                        accountHelper.signInWithGoogle() // запускаем функцию регистрации через Google

                    }

                    navController.navigate(MEETINGS_ROOT) {popUpTo(0)} // переходим на  главную страницу

                }

                Spacer(modifier = Modifier.height(30.dp)) // разделитель между кнопкой ГУГЛ


                // ---------- СТРОКА С ПОЛИТИКОЙ КОНФИДЕНЦИАЛЬНОСТИ ----------------------

                Text( // 1й текст (НЕ ГИПЕРССЫЛКА)

                    textAlign = TextAlign.Start,
                    text = stringResource(id = R.string.using_app),

                    style = Typography.labelMedium, // стиль текста
                    color = YellowDvij // цвет текста
                )
            }
        }
    }


    // ------ СТРАНИЦА "СПАСИБО ЗА РЕГИСТРАЦИЮ" --------------

    @Composable
    fun ThankYouPage (navController: NavController){

        Column {

            // ------- КНОПКА ЗАКРЫТЬ ЭКРАН ---------


            // создаем строку, чтобы задать положение кнопки "Закрыть"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey_Background
                    )
                    .padding(top = 10.dp, bottom = 0.dp, start = 20.dp, end = 10.dp),
                verticalAlignment = Alignment.Top, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.SpaceBetween // выравнивание кнопки по правому краю
            ) {

                // -------------- Логотип ------------------

                Icon(
                    painter = painterResource(R.drawable.dvij_logo),
                    contentDescription = stringResource(id = R.string.cd_logo), // описание для слабовидящих
                    modifier = Modifier.size(160.dp),
                    tint = YellowDvij
                )

                // ------- сама кнопка Закрыть ---------


                IconButton(
                    onClick = {
                        navController.navigate(MEETINGS_ROOT) {popUpTo(0)}
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = WhiteDvij
                    )
                }
            }


            // ------------ САМ КОНТЕНТ СТРАНИЦЫ -----------------

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey_Background)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            )

            {

                // -------------  ЗАГОЛОВОК ПРОВЕРЬ ПОЧТУ И АКТИВИРУЙ АККАУНТ ---------------

                Text(
                    text = stringResource(id = R.string.verify_email_title), //"Активируй аккаунт"
                    style = Typography.titleLarge, // стиль заголовка
                    color = WhiteDvij, // цвет заголовка
                    textAlign = TextAlign.Start
                )


                Spacer(modifier = Modifier.height(30.dp)) // разделитель между заголовком и полями для ввода


                // ------ ДОПОЛНИТЕЛЬНОЕ ОПИСАНИЕ ПОД ЗАГОЛОВКОМ ДЛЯ ПОДТВЕРЖДЕНИЯ ПОЧТЫ ---------------

                Text(
                    text = stringResource(id = R.string.verify_email_text),
                    style = Typography.bodySmall, // стиль текста
                    color = WhiteDvij, // цвет текста
                    textAlign = TextAlign.Start
                )


                Spacer(modifier = Modifier.height(60.dp)) // разделитель между полями

                // --------- КНОПКА ВХОД -------------


                ButtonCustom(
                    buttonText = stringResource(id = R.string.i_activate_profile)
                ) {
                    navController.navigate(LOG_IN_ROOT)
                }


                Spacer(modifier = Modifier.height(20.dp)) // разделитель


                // --------- КНОПКА ВЕРНУТЬСЯ НА ГЛАВНУЮ -------------

                ButtonCustom(
                    buttonText = stringResource(id = R.string.go_to_home),
                    typeButton = SECONDARY
                ) {
                    navController.navigate(MEETINGS_ROOT)
                }


            }
        }
    }


    // -------- СТРАНИЦА "ЗАБЫЛИ ПАРОЛЬ?" --------------

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun ForgotPasswordPage (navController: NavController) {

        Column{

            // помещаем все содержимое в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого

            // ------- КНОПКА ЗАКРЫТЬ ЭКРАН ---------


            // создаем строку, чтобы задать положение кнопки "Закрыть"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey_Background
                    )
                    .padding(top = 10.dp, bottom = 0.dp, start = 20.dp, end = 10.dp),
                verticalAlignment = Alignment.Top, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.SpaceBetween // выравнивание кнопки по правому краю
            ) {

                // -------------- Логотип ------------------

                Icon(
                    painter = painterResource(R.drawable.dvij_logo),
                    contentDescription = stringResource(id = R.string.cd_logo), // описание для слабовидящих
                    modifier = Modifier.size(160.dp),
                    tint = YellowDvij
                )

                // ------- сама кнопка Закрыть ---------


                IconButton(
                    onClick = {
                        navController.navigate(LOG_IN_ROOT) {popUpTo(0)}
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = WhiteDvij
                    )
                }
            }

            // ---- САМ КОНТЕНТ СТРАНИЦЫ ---------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey_Background)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            val email = remember { mutableStateOf("") }

            // -------------  ЗАГОЛОВОК ВОССТАНОВИТЬ ПАРОЛЬ  ---------------

            Text(  // заголовок зависит от switch
                text = stringResource(id = R.string.help_change_password),
                style = Typography.titleLarge, // стиль заголовка
                color = WhiteDvij // цвет заголовка
            )


            Spacer(modifier = Modifier.height(30.dp)) // разделитель между заголовком и полями для ввода


            // -------------  ДОПОЛНИТЕЛЬНЫЙ ТЕКСТ ПОД ЗАГОЛОВКОМ ---------------

            Text(  // заголовок зависит от switch
                text = stringResource(id = R.string.forgot_password_text),
                style = Typography.bodySmall, // стиль заголовка
                color = WhiteDvij, // цвет заголовка
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(60.dp)) // разделитель

            // ТЕКСТОВОЕ ПОЛЕ EMAIL

            email.value = fieldEmailComponent(act = act)

            Spacer(modifier = Modifier.height(20.dp)) // разделитель между полями



            // ---------- КНОПКА ВОССТАНОВИТЬ ПАРОЛЬ -------------------

            ButtonCustom(
                buttonText = stringResource(id = R.string.help_change_password),
                typeButton = PRIMARY
            ) {

                // действия при нажатии

                if (email.value != "" ) {

                    act.mAuth.sendPasswordResetEmail(email.value).addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            navController.navigate(RESET_PASSWORD_SUCCESS)
                        } else {
                            task.exception?.let { accountHelper.errorInSignInAndUp(it) }
                        }
                    }
                } else {

                    // ---- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ ЗАПОЛНИЛ ВСЕ ПОЛЯ -----

                    Toast.makeText(
                        act,
                        act.resources.getString(R.string.em_not_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()

                    }
                }

            Spacer(modifier = Modifier.height(50.dp)) // Разделитель


            // -------- строка ЕСТЬ АККАУНТ ГУГЛ -------------

            Text( // 1й текст (НЕ ГИПЕРССЫЛКА)

                text = stringResource(id = R.string.have_google_acc),
                style = Typography.bodySmall, // стиль текста
                color = WhiteDvij // цвет текста
            )


            Spacer(modifier = Modifier.height(10.dp)) // разделитель между мини заголовком и кнопкой


            // -------------- КНОПКА ВХОДА ЧЕРЕЗ GOOGLE ------------------

            ButtonGoogleCustom(
                buttonText = stringResource(id = R.string.log_in_google_acc),
                leftIcon = R.drawable.ic_google_color
            ) {

                // ----- ДЕЙСТВИЕ НА НАЖАТИЕ -------

                GlobalScope.launch(Dispatchers.IO) {

                    accountHelper.signInWithGoogle() // запускаем функцию регистрации через Google

                }

                navController.navigate(MEETINGS_ROOT) {popUpTo(0)} // переходим на  главную страницу

                }

            }
        }
    }


    // ------- СТРАНИЦА "МЫ ОТПРАВИЛИ ССЫЛКУ НА ВОССТАНОВЛЕНИЕ ПАРОЛЯ"

    @Composable
    fun ResetPasswordSuccess (navController: NavController){

        Column{

            // ------- КНОПКА ЗАКРЫТЬ ЭКРАН ---------


            // создаем строку, чтобы задать положение кнопки "Закрыть"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey_Background
                    )
                    .padding(top = 10.dp, bottom = 0.dp, start = 20.dp, end = 10.dp),
                verticalAlignment = Alignment.Top, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.SpaceBetween // выравнивание кнопки по правому краю
            ) {

                // -------------- Логотип ------------------

                Icon(
                    painter = painterResource(R.drawable.dvij_logo),
                    contentDescription = stringResource(id = R.string.cd_logo), // описание для слабовидящих
                    modifier = Modifier.size(160.dp),
                    tint = YellowDvij
                )

                // ------- сама кнопка Закрыть ---------


                IconButton(
                    onClick = {
                        navController.navigate(MEETINGS_ROOT) {popUpTo(0)}
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = WhiteDvij
                    )
                }
            }

            // ---------- САМ КОНТЕНТ СТРАНИЦЫ ---------------

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey_Background)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            )

            {

                // -------------  ЗАГОЛОВОК "Проверь почту и сбрось пароль" ---------------

                Text(  // заголовок
                    text = stringResource(id = R.string.reset_password_title),
                    style = Typography.titleLarge, // стиль заголовка
                    color = WhiteDvij, // цвет заголовка
                    textAlign = TextAlign.Start
                )


                Spacer(modifier = Modifier.height(30.dp)) // разделитель между заголовком и полями для ввода


                // ---------- ВСПОМОГАТЕЛЬНЫЙ ТЕКСТ ПОД ИНСТРУКЦИИ ------------

                Text(
                    text = stringResource(id = R.string.reset_password_text),
                    style = Typography.bodySmall, // стиль текста
                    color = WhiteDvij, // цвет текста
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(60.dp)) // разделитель между полями



                // -------- КНОПКА "ПЕРЕЙТИ НА ГЛАВНУЮ СТРАНИЦУ" --------

                ButtonCustom(
                    buttonText = stringResource(id = R.string.i_change_password)
                ) {
                    // действия при нажатии
                    navController.navigate(LOG_IN_ROOT)
                }

            }
        }
    }
}




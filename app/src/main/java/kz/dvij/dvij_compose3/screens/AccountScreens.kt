package kz.dvij.dvij_compose3.screens

import android.annotation.SuppressLint
import android.icu.text.Transliterator.Position
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.FirebaseAuthConstants
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.accounthelper.SIGN_IN
import kz.dvij.dvij_compose3.elements.fieldEmailComponent
import kz.dvij.dvij_compose3.elements.fieldPasswordComponent
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class AccountScreens(act: MainActivity) {

    // https://storyset.com/illustration/forgot-password/bro -- картинки

    private val act = act // Инициализируем MainActivity
    private val accountHelper = AccountHelper(act) // инициализируем класс AccountHelper

    @Composable
    fun SignInUpPage(switch: String, navController: NavController){

        Column(modifier = Modifier.fillMaxSize().background(color = Grey95)) {

            // помещаем все содержимое страницы в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого


            // ------- КНОПКА ЗАКРЫТЬ ЭКРАН ---------


            // создаем строку, чтобы задать положение кнопки "Закрыть"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey95
                    ),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {


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
                        tint = Grey00
                    )
                }
            }

            // ------ КОЛОНКА С ОСТАВШИМСЯ СОДЕРЖИМЫМ СТРАНИЦЫ ----------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey95
                    )
                    .verticalScroll(rememberScrollState()) // Делаем колонку с вертикальным скролом
                    .padding(top = 0.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // создаем переменные email и password - содержимое их будет меняться
                // в зависимости от того, что введет пользователь и это содержимое будет
                // отправляться в Firebase

                var email = remember { mutableStateOf("") }
                var password = remember { mutableStateOf("") }


                // --------------- САМ КОНТЕНТ СТРАНИЦЫ ------------------


                /* Так как страница меняется в зависимости от переключателя switch
            будет много условий, какой switch передан в функцию. Т.е выбрана страница
            регистрации или входа
            */


                // -------------- ИЛЛЮСТРАЦИЯ ------------------

                Image(
                painter = painterResource(
                    id = if (switch == REGISTRATION){
                        R.drawable.sign_up_illustration // если регистрация, то иллюстрация регистрации
                    } else {
                        R.drawable.sign_in_illustration // если вход, то иллюстрация входа
                    }

                ),
                contentDescription = stringResource(id = R.string.cd_illustration), // описание для слабовидящих
                modifier = Modifier.size(200.dp)
            )


                Spacer(modifier = Modifier.height(20.dp)) // разделитель


                // -------------  ЗАГОЛОВОК ---------------


                Text(  // заголовок зависит от switch
                    text = if (switch == REGISTRATION) {
                        stringResource(id = R.string.registration)
                    } else {
                        stringResource(id = R.string.log_in)
                    },
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00 // цвет заголовка
                )


                // --------------- ПЕРЕКЛЮЧАТЕЛЬ МЕЖДУ ВХОДОМ И РЕГИСТРАЦИЕЙ --------------------------


                Row(
                    modifier = Modifier // создаем колонку
                        .fillMaxWidth(), // на всю ширину
                    verticalAlignment = Alignment.CenterVertically, // выравнивание по вертикали
                    horizontalArrangement = Arrangement.Center // выравнивание по центру
                ) {


                    // ------ 1й ТЕКСТ - НЕ ГИПЕРССЫЛКА ----------


                    Text(

                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.have_account)
                        } else {
                            stringResource(id = R.string.no_have_account)
                        },

                        style = Typography.labelMedium, // стиль текста
                        color = Grey40 // цвет текста
                    )


                    Spacer(modifier = Modifier.width(7.dp)) // разделитель между словами


                    // ------ 2й ТЕКСТ - ГИПЕРССЫЛКА ------------


                    Text(

                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.to_login)
                        } else {
                            stringResource(id = R.string.to_registration)
                        },

                        style = Typography.labelMedium, // стиль текста
                        color = PrimaryColor, // цвет текста
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



                Spacer(modifier = Modifier.height(20.dp)) // разделитель между


                // -------- ТЕКСТОВОЕ ПОЛЕ EMAIL -----------------


                email.value = fieldEmailComponent(act = act)



                Spacer(modifier = Modifier.height(10.dp)) // разделитель между полями


                // ------------    ТЕКСТОВОЕ ПОЛЕ С ПАРОЛЕМ ----------------

                password.value = fieldPasswordComponent(act = act)


                Spacer(modifier = Modifier.height(20.dp)) // раздетиль между формой и кнопкой


                // ------------------- КНОПКА РЕГИСТРАЦИЯ / ВХОД ---------------------------------

                Button(

                    onClick = { // действия при нажатии

                        // все заключаем в условие - надо чтобы поля имейл и пассворд были обязательно заполнены
                        if (email.value.isNotEmpty() && password.value.isNotEmpty()) {

                            // далее еще уловие - если switch РЕГИСТРАЦИЯ, то функция РЕГИСТРАЦИИ

                            // ----------------- РЕГИСТРАЦИЯ -------------------

                            if (switch == REGISTRATION) {

                                // запускаем функцию createUserWithEMailAndPassword и вешаем слушатель, который говорит что действие закончено
                                act.mAuth.createUserWithEmailAndPassword(
                                    email.value,
                                    password.value
                                )
                                    .addOnCompleteListener { task ->

                                        //  если регистрация прошла успешно
                                        if (task.isSuccessful) {

                                            // если пользователь успешно зарегистрировался, mAuth будет содержать всю информацию о пользователе user
                                            //отправляем письмо с подтверждением Email. task.result.user можно взять act.mAuth.currentUser
                                            accountHelper.sendEmailVerification(task.result.user!!)
                                            navController.navigate(THANK_YOU_PAGE_ROOT) {popUpTo(0)} // переходим на диалог СПАСИБО ЗА РЕГИСТРАЦИЮ



                                        } else {

                                            // если регистрация не выполнилась

                                            task.exception?.let {
                                                accountHelper.errorInSignInAndUp(
                                                    it
                                                )
                                            } // отправляем в функцию отслеживания ошибки и вывода нужной информации

                                        }
                                    }

                            } else {

                                // ------------------ ВХОД -----------------

                                // запускаем функцию от FireBase signInWithEmailAndPassword и вешаем слушатель, что все завершилось

                                act.mAuth.signInWithEmailAndPassword(
                                    email.value,
                                    password.value
                                ).addOnCompleteListener { task ->

                                    if (task.isSuccessful) { // если вход выполнен

                                        navController.navigate(PROFILE_ROOT) {popUpTo(0)} // переходим на страницу профиля

                                        Toast.makeText(
                                            act,
                                            act.resources.getString(R.string.log_in_successful),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else { // если вход не выполнен

                                        task.exception?.let {
                                            accountHelper.errorInSignInAndUp(
                                                it
                                            )
                                        } // отправляем в функцию отслеживания ошибки и вывода нужной информации

                                    }
                                }
                            }
                        }
                    },


                    // ------------------ ДЕЙСТВИЕ НА КНОПКУ КОНЧАЕТСЯ ЗДЕСЬ XD --------------------


                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
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
                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.to_registration) // если свитч регистрация, то текст "Регистрация"
                        } else {
                            stringResource(id = R.string.to_login) // если свитч другой, то текст "Войти"
                        },
                        style = Typography.labelMedium // стиль текста
                    )
                }


                // ----------- НАДПИСЬ ЗАБЫЛИ ПАРОЛЬ? -----------------------

                if (switch != REGISTRATION) {

                    Spacer(modifier = Modifier.height(20.dp)) // Разделитель

                    Row(
                        modifier = Modifier // создаем колонку
                            .fillMaxWidth(), // на всю ширину
                        verticalAlignment = Alignment.CenterVertically, // выравнивание по вертикали
                        horizontalArrangement = Arrangement.Center // выравнивание по центру
                    ) {
                        Text( // 1й текст (НЕ ГИПЕРССЫЛКА)

                            text = stringResource(id = R.string.forgot_password),
                            style = Typography.labelMedium, // стиль текста
                            color = Grey40 // цвет текста
                        )

                        Spacer(modifier = Modifier.width(7.dp)) // разделитель между словами

                        Text( // 2й текст, который КЛИКАБЕЛЬНЫЙ

                            text = stringResource(id = R.string.help_change_password),

                            style = Typography.labelMedium, // стиль текста
                            color = PrimaryColor, // цвет текста
                            modifier = Modifier.clickable { // действие на клик

                                // перейти на страницу

                                navController.navigate(FORGOT_PASSWORD_ROOT)
                            }
                        )
                    }

                }

                Spacer(modifier = Modifier.height(20.dp)) // Разделитель


                // -------- строка ЕСТЬ АККАУНТ ГУГЛ с полосами -------------


                Row(
                    // помещаем все в строку

                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Divider( // разделитель полоска
                        color = Grey40, // цвет
                        thickness = 1.dp, // толщина
                        modifier = Modifier.weight(0.3f) // процент, занимаемый в строке
                    )

                    Spacer(modifier = Modifier.width(20.dp)) // разделитель между текстом и разделителем

                    Text(
                        // сам мини заголовок
                        text = stringResource(id = R.string.have_google_acc), // сам текст
                        style = Typography.labelMedium, // стиль текста
                        color = Grey40, // цвет
                    )

                    Spacer(modifier = Modifier.width(20.dp)) // разделитель между текстом и разделителем

                    Divider( // разделитель полоска
                        color = Grey40, // цвет
                        thickness = 1.dp, // толщина
                        modifier = Modifier.weight(0.3f) // процент, занимаемый в строке
                    )
                }


                Spacer(modifier = Modifier.height(15.dp)) // разделитель между мини заголовком и кнопкой


                // -------------- КНОПКА ВХОДА ЧЕРЕЗ GOOGLE ------------------

                Button(
                    onClick = {
                        accountHelper.signInWithGoogle()
                        navController.navigate(MEETINGS_ROOT) {popUpTo(0)}

                        /*act.mAuth.addAuthStateListener {
                            val user = act.mAuth.currentUser




                            if (act.mAuth.currentUser != null) {
                                // navController.navigate(MEETINGS_ROOT) {popUpTo(0)}
                                navController.navigate(PROFILE_ROOT) {popUpTo(0)}
                            } /*else {
                                navController.navigate(PROFILE_ROOT) {popUpTo(0)}
                            }*/
                        }*/


                    },
                    modifier = Modifier
                        .fillMaxWidth() // На всю ширину
                        .height(50.dp), // высота кнопки
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors( // цвета кнопки
                        backgroundColor = Grey00,
                        contentColor = Grey100
                    )
                )
                {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_color), // иконка гугла
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        modifier = Modifier.size(25.dp) // размер иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между иконкой и текстом

                    Text( // текст, в зависимости от switch
                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.reg_google_acc)
                        } else {
                            stringResource(id = R.string.log_in_google_acc)
                        },

                        style = Typography.labelMedium
                    )
                }


                Spacer(modifier = Modifier.height(20.dp)) // разделитель между кнопкой ГУГЛ

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Grey40,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(10.dp))


                // ---------- СТРОКА С ПОЛИТИКОЙ КОНФИДЕНЦИАЛЬНОСТИ ----------------------


                Column(
                    modifier = Modifier // создаем колонку
                        .fillMaxWidth(), // на всю ширину
                    verticalArrangement = Arrangement.Center,// выравнивание по вертикали
                    horizontalAlignment = Alignment.CenterHorizontally // выравнивание по центру
                ) {
                    Text( // 1й текст (НЕ ГИПЕРССЫЛКА)

                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.using_app),

                        style = Typography.labelSmall, // стиль текста
                        color = Grey40 // цвет текста
                    )

                    Spacer(modifier = Modifier.width(7.dp)) // разделитель между словами

                    Text( // 2й текст, который КЛИКАБЕЛЬНЫЙ

                        text = stringResource(id = R.string.private_policy_agree),

                        style = Typography.labelMedium, // стиль текста
                        color = PrimaryColor, // цвет текста
                        modifier = Modifier.clickable { // действие на клик

                            // перейти на страницу политики конфиденциальности

                            navController.navigate(POLICY_ROOT)
                        }
                    )
                }
            }
        }

    }


    // ------ СТРАНИЦА "СПАСИБО ЗА РЕГИСТРАЦИЮ" --------------

    @Composable
    fun ThankYouPage (navController: NavController){

        Column(

        ) {

            // помещаем все содержимое в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого

            // создаем строку, чтобы задать положение кнопки "Закрыть"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey95),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {

                // ------- сама кнопка Закрыть -----------

                IconButton(
                    onClick = {
                        navController.navigate(MEETINGS_ROOT) // действие если нажать на кнопку
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = Grey00
                    )
                }
            }


            // ------------ САМ КОНТЕНТ СТРАНИЦЫ -----------------

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey95)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )

            {

                Spacer(modifier = Modifier.height(20.dp)) // разделитель


                // ---------------- ИЛЛЮСТРАЦИЯ -----------------


                Image(
                    painter = painterResource(id = R.drawable.send_email_illustration),
                    contentDescription = stringResource(id = R.string.cd_illustration)
                )



                Spacer(modifier = Modifier.height(40.dp)) // разделитель



                // -------------  ЗАГОЛОВОК ПРОВЕРЬ ПОЧТУ И АКТИВИРУЙ АККАУНТ ---------------

                Text(
                    text = stringResource(id = R.string.verify_email_title), //"Проверь свой Email и активируй аккаунт"
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00, // цвет заголовка
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(20.dp)) // разделитель между заголовком и полями для ввода


                // ------ ДОПОЛНИТЕЛЬНОЕ ОПИСАНИЕ ПОД ЗАГОЛОВКОМ ДЛЯ ПОДТВЕРЖДЕНИЯ ПОЧТЫ ---------------

                Text(
                    text = stringResource(id = R.string.verify_email_text),
                    style = Typography.bodyMedium, // стиль текста
                    color = Grey40, // цвет текста
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(40.dp)) // разделитель между полями

                // --------- КНОПКА ВХОД -------------

                Button(

                    onClick = { // действия при нажатии
                        navController.navigate(LOG_IN_ROOT)
                    },

                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
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


                    // ТЕКСТ КНОПКИ ВХОД

                    Text(
                        text = stringResource(id = R.string.log_in),
                        style = Typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp)) // разделитель



                // ------ КНОПКА ВЕРНУТЬСЯ НА ГЛАВНУЮ ----------------

                Button(

                    onClick = { // действия при нажатии
                        navController.navigate(MEETINGS_ROOT)
                    },

                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Grey10, // цвет кнопки
                        contentColor = Grey100 // цвет контента на кнопке
                    )
                )
                {

                    // СОДЕРЖИМОЕ КНОПКИ

                    Icon(
                        painter = painterResource(id = R.drawable.ic_home), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой


                    // ТЕКСТ КНОПКИ ВЕРНУТЬСЯ НА ГЛАВНУЮ СТРАНИЦУ

                    Text(
                        text = stringResource(id = R.string.go_to_home),
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }


    // -------- СТРАНИЦА "ЗАБЫЛИ ПАРОЛЬ?" --------------

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun ForgotPasswordPage (navController: NavController) {

        Column(

        ) {

            // помещаем все содержимое в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого

            // создаем строку, чтобы задать положение кнопки "Закрыть"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey95),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {

                // сама кнопка Закрыть
                IconButton(
                    onClick = {
                        navController.navigate(LOG_IN_ROOT) // действие если нажать на кнопку

                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = Grey00
                    )
                }
            }

            // ---- САМ КОНТЕНТ СТРАНИЦЫ ---------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey95)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var email = remember { mutableStateOf("") }

            // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
            // при фокусе на них окрашивались в нужные цвета

            var focusColorEmail = remember { mutableStateOf(Grey40) }

            var isErrorEmail =
                remember { mutableStateOf(false) } // состояние формы - ошибка или нет
            var errorEmailMassage = remember { mutableStateOf("") } // сообщение об ошибке


            val focusManager =
                LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы



                Spacer(modifier = Modifier.height(40.dp)) // разделитель




            // -------------- ИЛЛЮСТРАЦИЯ ------------------


                Image(
                    //modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.forgot_password_illustration),
                    contentDescription = stringResource(id = R.string.cd_illustration)
                )



            Spacer(modifier = Modifier.height(40.dp)) // разделитель



                // -------------  ЗАГОЛОВОК ВОССТАНОВИТЬ ПАРОЛЬ  ---------------

                Text(  // заголовок зависит от switch
                    text = stringResource(id = R.string.help_change_password),
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00 // цвет заголовка
                )




            Spacer(modifier = Modifier.height(20.dp)) // разделитель между заголовком и полями для ввода




            // -------------  ДОПОЛНИТЕЛЬНЫЙ ТЕКСТ ПОД ЗАГОЛОВКОМ ---------------

                Text(  // заголовок зависит от switch
                    text = stringResource(id = R.string.forgot_password_text),
                    style = Typography.bodyMedium, // стиль заголовка
                    color = Grey40, // цвет заголовка
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp)) // разделитель



            // ТЕКСТОВОЕ ПОЛЕ EMAIL

            email.value = fieldEmailComponent(act = act)

            Spacer(modifier = Modifier.height(20.dp)) // разделитель между полями



            // ---------- КНОПКА ВОССТАНОВИТЬ ПАРОЛЬ -------------------

                Button(

                    onClick = { // действия при нажатии
                        act.mAuth.sendPasswordResetEmail(email.value).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate(RESET_PASSWORD_SUCCESS)
                            } else {
                                task.exception?.let { accountHelper.errorInSignInAndUp(it) }
                            }
                        }
                    },

                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor, // цвет кнопки
                        contentColor = Grey100 // цвет контента на кнопке
                    )
                )
                {

                    // СОДЕРЖИМОЕ КНОПКИ

                    Icon(
                        painter = painterResource(id = R.drawable.ic_key), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                    Text(
                        text = stringResource(id = R.string.help_change_password),
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }


    // ------- СТРАНИЦА "МЫ ОТПРАВИЛИ ССЫЛКУ НА ВОССТАНОВЛЕНИЕ ПАРОЛЯ"

    @Composable
    fun ResetPasswordSuccess (navController: NavController){

        Column(

        ) {

            // помещаем все содержимое в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого

            // создаем строку, чтобы задать положение кнопки "Закрыть"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey95),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {

                // сама кнопка Закрыть
                IconButton(
                    onClick = {
                        navController.navigate(MEETINGS_ROOT) // действие если нажать на кнопку
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = Grey00
                    )
                }
            }

            // ---------- САМ КОНТЕНТ СТРАНИЦЫ ---------------

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey95)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )

            {

                Spacer(modifier = Modifier.height(20.dp)) // разделитель


                // ----- ИЛЛЮСТРАЦИЯ ---------

                Image(
                    //modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.send_email_illustration),
                    contentDescription = stringResource(id = R.string.cd_illustration)
                )


                Spacer(modifier = Modifier.height(40.dp)) // разделитель



                // -------------  ЗАГОЛОВОК "Проверь почту и сбрось пароль" ---------------

                Text(  // заголовок
                    text = stringResource(id = R.string.reset_password_title),
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00, // цвет заголовка
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(20.dp)) // разделитель между заголовком и полями для ввода


                // ---------- ВСПОМОГАТЕЛЬНЫЙ ТЕКСТ ПОД ИНСТРУКЦИИ ------------

                Text(
                    text = stringResource(id = R.string.reset_password_text),
                    style = Typography.bodyMedium, // стиль текста
                    color = Grey40, // цвет текста
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp)) // разделитель между полями



                // -------- КНОПКА "ПЕРЕЙТИ НА ГЛАВНУЮ СТРАНИЦУ" --------

                Button(

                    onClick = { // действия при нажатии
                        navController.navigate(MEETINGS_ROOT)
                    },

                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor, // цвет кнопки
                        contentColor = Grey100 // цвет контента на кнопке
                    )
                )
                {

                    // СОДЕРЖИМОЕ КНОПКИ

                    Icon(
                        painter = painterResource(id = R.drawable.ic_home), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                    Text(
                        text = stringResource(id = R.string.go_to_home),
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }
}




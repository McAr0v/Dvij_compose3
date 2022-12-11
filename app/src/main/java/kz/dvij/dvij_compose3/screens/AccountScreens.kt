package kz.dvij.dvij_compose3.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class AccountScreens(act: MainActivity) {

    private val act = act // Инициализируем MainActivity
    private val accountHelper = AccountHelper(act) // инициализируем класс AccountHelper

    @Composable
    fun RegistrScreen(
        navController: NavController,
        scaffoldState: ScaffoldState,
        switch: String
    ){

        val coroutine = rememberCoroutineScope() // инициализируем корутину

        Column() {

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


            // Колонка с остальным содержимым страницы.

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey95)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // создаем переменные email и password - содержимое их будет меняться
                // в зависимости от того, что введет пользователь и это содержимое будет
                // отправляться в Firebase

                var email = remember{ mutableStateOf("") }
                var password = remember{ mutableStateOf("") }

                // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
                // при фокусе на них окрашивались в нужные цвета

                var focusColorEmail = remember { mutableStateOf(Grey40) }
                var focusColorPassword = remember { mutableStateOf(Grey40) }

                // создаем переменную для скрытия или отображения пароля

                var passwordVisible = remember { mutableStateOf(false) }

                // --------------- САМ КОНТЕНТ СТРАНИЦЫ ------------------

                /* Так как страница меняется в зависимости от переключателя switch
                будет много условий, какой switch передан в функцию. Т.е выбрана страница
                регистрации или входа
                */

                // ЗАГОЛОВОК

                Text(  // заголовок зависит от switch
                    text = if (switch == REGISTRATION){
                        stringResource(id = R.string.registration)
                    } else {
                        stringResource(id = R.string.log_in)
                           },
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00 // цвет заголовка
                )

                Spacer(modifier = Modifier.height(10.dp)) // разделитель между заголовком и полями для ввода

                // --------------- Переключатель между регистрацией и входом --------------------------

                Row(modifier = Modifier // создаем колонку
                    .fillMaxWidth(), // на всю ширину
                    verticalAlignment = Alignment.CenterVertically, // выравнивание по вертикали
                    horizontalArrangement = Arrangement.Center // выравнивание по центру
                ) {
                    Text( // 1й текст (НЕ ГИПЕРССЫЛКА)

                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.have_account)
                        } else {
                            stringResource(id = R.string.no_have_account)
                               },

                        style = Typography.labelMedium, // стиль текста
                        color = Grey40 // цвет текста
                    )
                    
                    Spacer(modifier = Modifier.width(7.dp)) // разделитель между словами

                    Text( // 2й текст, который КЛИКАБЕЛЬНЫЙ

                        text = if (switch == REGISTRATION) {
                            stringResource(id = R.string.to_login)
                        } else {
                            stringResource(id = R.string.to_registration)
                               },

                        style = Typography.labelMedium, // стиль текста
                        color = PrimaryColor, // цвет текста
                        modifier = Modifier.clickable { // действие на клик

                            // перейти на страницу входа или регистрации

                            navController.navigate(
                                if (switch == REGISTRATION) {
                                    LOG_IN_ROOT
                                } else {
                                    REG_ROOT
                                }
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp)) // разделитель между 

                // ТЕКСТОВОЕ ПОЛЕ EMAIL

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                            if (it.isFocused) focusColorEmail.value =
                                PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                            else focusColorEmail.value =
                                Grey40 // если нет, то в переменные с цветами передать серый
                        }
                        .border( // настройки самих границ
                            2.dp, // толщина границы
                            color = focusColorEmail.value, // цвет - для этого выше мы создавали переменные с цветом
                            shape = RoundedCornerShape(50.dp) // скругление границ
                        ),
                    value = email.value, // значение email
                    onValueChange = {newText -> email.value = newText}, // когда значение меняется, оно отсюда передается в value
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        // цвета
                        textColor = Grey40,
                        backgroundColor = Grey95,
                        placeholderColor = Grey60,
                        focusedBorderColor = Grey95,
                        unfocusedBorderColor = Grey95,
                        cursorColor = Grey00
                    ),
                    textStyle = Typography.bodyLarge, // стиль текста
                    keyboardOptions = KeyboardOptions(
                        // опции клавиатуры, которая появляется при вводе
                        keyboardType = KeyboardType.Email, // тип клавиатуры (типа удобнее для ввода Email)
                        imeAction = ImeAction.Done // кнопка действия (если не установить это значение, будет перенос на следующую строку. А так действие ГОТОВО)

                    ),
                    placeholder = {
                        // подсказка для пользователей
                        Text(
                            text = stringResource(id = R.string.email_example), // значение подсказки
                            style = Typography.bodyLarge // стиль текста в холдере
                        )
                                  },
                    leadingIcon = {
                        // иконка, расположенная слева от надписи плейсхолдера
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email), // сама иконка
                            contentDescription = stringResource(id = R.string.cd_email_icon), // описание для слабовидящих
                            tint = Grey60, // цвет иконки
                            modifier = Modifier.size(20.dp) // размер иконки
                        )
                                  },
                    )

                Spacer(modifier = Modifier.height(20.dp)) // разделитель между полями

                // ТЕКСТОВОЕ ПОЛЕ С ПАРОЛЕМ

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { it -> // зависимость цвета границы от состояния - есть фокус на форме или нет
                            if (it.isFocused) focusColorPassword.value =
                                PrimaryColor // если фокус - то брендовый цвет
                            else focusColorPassword.value = Grey40 // если не фокус - серый
                        }
                        .border( // настройка границы
                            2.dp, // толщина границы
                            color = focusColorPassword.value, // цвет границы
                            shape = RoundedCornerShape(50.dp) // скругление углов
                        ),
                    value = password.value, // значение пароля
                    onValueChange = {newText -> password.value = newText}, // вводимое значение
                    colors = TextFieldDefaults.outlinedTextFieldColors( // цвета поля
                        textColor = Grey40,
                        backgroundColor = Grey95,
                        placeholderColor = Grey60,
                        focusedBorderColor = Grey95,
                        unfocusedBorderColor = Grey95,
                        cursorColor = Grey00
                    ),
                    trailingIcon = { // иконка ОТКРЫТЬ/СКРЫТЬ пароль
                        IconButton(
                            onClick = {passwordVisible.value = !passwordVisible.value}
                        ) {
                        Icon(
                            painter = painterResource( // разные иконки в зависимости от того, скрыт пароль или открыт
                                if (passwordVisible.value == false) {
                                    R.drawable.ic_visibility_off // скрыт
                                } else {
                                    R.drawable.ic_visibility // открыт
                                }
                            ),
                            contentDescription = stringResource( // разные иконки в зависимости от того, скрыт пароль или открыт
                                if (passwordVisible.value == false) {
                                    R.string.cd_show_password // скрыт, но иконка о том, что показать пароль
                                } else {
                                    R.string.cd_hide_password // открыт, но иконка о том, чтобы закрыть видимость
                                }
                            ),
                            tint = Grey00, // цвет иконки
                            modifier = Modifier.size(20.dp) // размер иконки
                            )
                        }
                    },
                    textStyle = Typography.bodyLarge, // стиль текста

                    keyboardOptions = KeyboardOptions( // опции всплывающей клавиатуры
                        keyboardType = KeyboardType.Password, // тип клавиатуры - пароль
                        imeAction = ImeAction.Done // кнопка завершения - ГОТОВО
                    ),

                    // визуальная трансформация - типа открыть или закрыть символы паролья
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),

                    placeholder = {
                        Text( // текст плейсхолдера
                            text = stringResource(id = R.string.enter_password), // сам текст плейсхолдера
                            style = Typography.bodyLarge // стиль текста
                        )
                                  },

                    leadingIcon = { // иконка, которая слева от поля формы
                        Icon(
                            painter = painterResource(id = R.drawable.ic_key), // сама иконка
                            contentDescription = stringResource(id = R.string.cd_password_icon), // описание для слабовидящаих
                            tint = Grey60, // цвет иконки
                            modifier = Modifier.size(20.dp) // размер иконки
                        )
                                  },
                    )

                Spacer(modifier = Modifier.height(20.dp)) // раздетиль между формой и кнопкой

                // ------------------- КНОПКА ---------------------------------

                Button(

                    onClick = { // действия при нажатии

                        // все заключаем в условие - надо чтобы поля имейл и пассворд были обязательно заполнены
                    if (email.value.isNotEmpty() && password.value.isNotEmpty()) {

                        // далее еще уловие - если switch РЕГИСТРАЦИЯ, то функция РЕГИСТРАЦИИ

                        // ----------------- РЕГИСТРАЦИЯ -------------------

                        if (switch == REGISTRATION) {

                            // запускаем функцию createUserWithEMailAndPassword и вешаем слушатель, который говорит что действие закончено
                            act.mAuth.createUserWithEmailAndPassword(email.value, password.value)
                                .addOnCompleteListener { task ->

                                    //  если регистрация прошла успешно
                                    if (task.isSuccessful) {

                                        // если пользователь успешно зарегистрировался, mAuth будет содержать всю информацию о пользователе user
                                        //отправляем письмо с подтверждением Email. task.result.user можно взять act.mAuth.currentUser
                                        accountHelper.sendEmailVerification(task.result.user!!)

                                        navController.navigate(THANK_YOU_PAGE_ROOT) // переходим на страницу СПАСИБО ЗА РЕГИСТРАЦИЮ

                                    } else { // если регистрация не выполнилась
                                        Toast.makeText(
                                            act,
                                            act.resources.getString(R.string.registr_error),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                        } else {

                            // ------------------ ВХОД -----------------

                            // запускаем функцию от FireBase signInWithEmailAndPassword и вешаем слушатель, что все завершилось

                            act.mAuth.signInWithEmailAndPassword(email.value, password.value).addOnCompleteListener{  task->

                                if(task.isSuccessful) { // если вход выполнен
                                    navController.navigate(PROFILE_ROOT) // переходим на страницу профиля

                                    Toast.makeText(
                                        act,
                                        act.resources.getString(R.string.log_in_successful),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else { // если вход не выполнен

                                    Toast.makeText(
                                        act,
                                        act.resources.getString(R.string.sign_in_error),
                                        Toast.LENGTH_SHORT
                                    ).show()
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

                // ----------- ЗАБЫЛИ ПАРОЛЬ? -----------------------

                if (switch != REGISTRATION) {

                    Spacer(modifier = Modifier.height(20.dp)) // Разделитель

                    Row(modifier = Modifier // создаем колонку
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

                Spacer(modifier = Modifier.height(40.dp)) // Разделитель


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

                    Text( // сам мини заголовок
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
                    onClick = { /*TODO*/ },
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

                Spacer(modifier = Modifier.height(60.dp)) // разделитель между кнопкой ГУГЛ

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Grey40,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ---------- СТРОКА С ПОЛИТИКОЙ КОНФИДЕНЦИАЛЬНОСТИ ----------------------

                Column(modifier = Modifier // создаем колонку
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


    @Composable
    fun ThankYou (navController: NavController){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Grey100), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Спасибо", color = Grey00)
        }
    }

    @Composable
    fun RememberPasswordPage (navController: NavController) {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Grey100), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ){

            var email = remember{ mutableStateOf("") }

            // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
            // при фокусе на них окрашивались в нужные цвета

            var focusColorEmail = remember { mutableStateOf(Grey40) }

            Text(text = "Восстановить пароль", color = Grey00)

            // ТЕКСТОВОЕ ПОЛЕ EMAIL

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                        if (it.isFocused) focusColorEmail.value =
                            PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                        else focusColorEmail.value =
                            Grey40 // если нет, то в переменные с цветами передать серый
                    }
                    .border( // настройки самих границ
                        2.dp, // толщина границы
                        color = focusColorEmail.value, // цвет - для этого выше мы создавали переменные с цветом
                        shape = RoundedCornerShape(50.dp) // скругление границ
                    ),
                value = email.value, // значение email
                onValueChange = {newText -> email.value = newText}, // когда значение меняется, оно отсюда передается в value
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    // цвета
                    textColor = Grey40,
                    backgroundColor = Grey95,
                    placeholderColor = Grey60,
                    focusedBorderColor = Grey95,
                    unfocusedBorderColor = Grey95,
                    cursorColor = Grey00
                ),
                textStyle = Typography.bodyLarge, // стиль текста
                keyboardOptions = KeyboardOptions(
                    // опции клавиатуры, которая появляется при вводе
                    keyboardType = KeyboardType.Email, // тип клавиатуры (типа удобнее для ввода Email)
                    imeAction = ImeAction.Done // кнопка действия (если не установить это значение, будет перенос на следующую строку. А так действие ГОТОВО)

                ),
                placeholder = {
                    // подсказка для пользователей
                    Text(
                        text = stringResource(id = R.string.email_example), // значение подсказки
                        style = Typography.bodyLarge // стиль текста в холдере
                    )
                },
                leadingIcon = {
                    // иконка, расположенная слева от надписи плейсхолдера
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email), // сама иконка
                        contentDescription = stringResource(id = R.string.cd_email_icon), // описание для слабовидящих
                        tint = Grey60, // цвет иконки
                        modifier = Modifier.size(20.dp) // размер иконки
                    )
                },
            )

            Spacer(modifier = Modifier.height(20.dp)) // разделитель между полями

            Button(

                onClick = { // действия при нажатии
                    act.mAuth.sendPasswordResetEmail(email.value)
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

                Text(
                    text = "Восстановить пароль"
                )
            }

        }
    }

}
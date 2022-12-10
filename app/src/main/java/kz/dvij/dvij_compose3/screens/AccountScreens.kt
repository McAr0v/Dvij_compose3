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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.PROFILE_ROOT
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

                Text(
                    text = if (switch == REGISTRATION){"Регистрация"} else {"Вход"}, // заголовок зависит от switch
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00 // цвет заголовка
                )

                Spacer(modifier = Modifier.height(40.dp)) // разделитель между заголовком и полями для ввода

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
                            modifier = Modifier.size(25.dp) // размер иконки
                        )
                                  },
                    )

                Spacer(modifier = Modifier.height(20.dp)) // разделитель между полями

                // ТЕКСТОВОЕ ПОЛЕ С ПАРОЛЕМ

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { it ->
                            if (it.isFocused) focusColorPassword.value = PrimaryColor
                            else focusColorPassword.value = Grey40
                        }
                        .border(
                            2.dp,
                            color = focusColorPassword.value,
                            shape = RoundedCornerShape(50.dp)
                        ),
                    value = password.value,
                    onValueChange = {newText -> password.value = newText},
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Grey40,
                        backgroundColor = Grey95,
                        placeholderColor = Grey60,
                        focusedBorderColor = Grey95,
                        unfocusedBorderColor = Grey95,
                        cursorColor = Grey00
                    ),
                    trailingIcon = { IconButton(onClick = {passwordVisible.value = !passwordVisible.value}) {
                        Icon(painter = painterResource(id = R.drawable.whatsapp), contentDescription = "", tint = Grey00, modifier = Modifier.size(25.dp))
                    }

                    },
                    textStyle = Typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    placeholder = { Text(text = "Введите Пароль", style = Typography.bodyLarge) },
                    leadingIcon = { Icon(painter = painterResource(id = R.drawable.whatsapp), contentDescription = "", tint = Grey60, modifier = Modifier.size(25.dp)) },


                    )

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {

                    if (email.value.isNotEmpty() && password.value.isNotEmpty()){
                        if (switch == REGISTRATION) {

                            act.mAuth.createUserWithEmailAndPassword(email.value, password.value)
                                .addOnCompleteListener { task ->
                                    //  если регистрация прошла успешно
                                    if (task.isSuccessful) {
                                        // если пользователь успешно зарегистрировался, mAuth будет содержать всю информацию о пользователе user
                                        accountHelper.sendEmailVerification(task.result.user!!)
                                        navController.navigate(PROFILE_ROOT)
                                        //отправляем письмо с подтверждением Email. task.result.user можно взять act.mAuth.currentUser
                                    } else { // если регистрация не выполнилась

                                        Toast.makeText(
                                            act,
                                            act.resources.getString(R.string.registr_error),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }

                                }

                        } else {
                            act.mAuth.signInWithEmailAndPassword(email.value, password.value).addOnCompleteListener{  task->
                                if(task.isSuccessful) {
                                    navController.navigate(PROFILE_ROOT)
                                    Toast.makeText(
                                        act,
                                        "Вход успешно выполнен",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        act,
                                        "Произошла ошибка при входе",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }
                    }
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor,
                        contentColor = Grey100
                    )
                )
                {
                    Text(text = if (switch == REGISTRATION){"Зарегистрироваться"} else {"Войти"},
                        style = Typography.labelMedium
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(painter = painterResource(id = R.drawable.ic_login), contentDescription = "", tint = Grey100)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Divider(color = Grey40, thickness = 1.dp, modifier = Modifier.weight(0.3f))
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "Есть аккаунт гугл?",
                        style = Typography.labelMedium,
                        color = Grey40,
                        modifier = Modifier.clickable {  }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Divider(color = Grey40, thickness = 1.dp, modifier = Modifier.weight(0.3f))
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(onClick = { /*TODO*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Grey00,
                        contentColor = Grey100
                    )
                )
                {
                    Icon(painter = painterResource(id = R.drawable.ic_person), contentDescription = "", tint = Grey100)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = if (switch == REGISTRATION) {"Зарегистрироваться через Google"}else{"Войти через Google"},
                        style = Typography.labelMedium)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (switch == REGISTRATION){"Уже создавали аккаунт в движе? "} else {"Еще нет аккаунта?"},
                        style = Typography.labelMedium,
                        color = Grey40
                    )
                    Text(
                        text = if (switch == REGISTRATION){"Войти"} else {"Зарегистрироваться"},
                        style = Typography.labelMedium,
                        color = PrimaryColor,
                        modifier = Modifier.clickable {
                            navController.navigate(
                                if (switch == REGISTRATION) {
                                    "LoginRoot"
                                } else {
                                    "RegRoot"
                                }
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Нажмая кнопку войти вы соглашаетесь с политикой КОНФИДЕНЦИАЛЬНОСТИ ",
                    style = Typography.labelMedium,
                    color = Grey40,
                    modifier = Modifier.clickable {  }
                )

            }
        }

    }


    @Composable
    fun thankYou (navController: NavController){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Grey100), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Спасибо", color = Grey00)
        }
    }

}
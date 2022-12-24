package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun fieldEmailComponent (
    act: MainActivity
): String {

    // создаем переменные email и password - содержимое их будет меняться
    // в зависимости от того, что введет пользователь и это содержимое будет
    // отправляться в Firebase

    var text = remember { mutableStateOf("") }

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey40) }

    // создаем переменную для скрытия или отображения пароля

    var passwordVisible = remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start) {
    }

    // -------- ТЕКСТОВОЕ ПОЛЕ EMAIL -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey40 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text.value.lowercase(), // значение email // lowerCase() - делает все буквы строчными

        // on valueChange - это действие при изменении значения
        onValueChange = { newText ->
            text.value = newText // помещаем в переменную email новый введенный текст

            // проверяем вводимый текст на соответствие правилам:

            // если не содежрит @
            if (!newText.contains("@")) {

                isTextError.value = true // объявляем состояние ошибки
                focusColor.value = AttentionColor // красим границы формы в цвет ошибки
                errorMassage.value = act.resources.getString(R.string.em_dog) // передаем текст ошибки

            } else if (!newText.contains(".")){ // если не содежрит .

                isTextError.value = true // объявляем состояние ошибки
                focusColor.value = AttentionColor // красим границы формы в цвет ошибки
                errorMassage.value = act.resources.getString(R.string.em_dot) // передаем текст ошибки

            } else if (newText.contains(" ")){ // если содержит пробел

                isTextError.value = true // объявляем состояние ошибки
                focusColor.value = AttentionColor // красим границы формы в цвет ошибки
                errorMassage.value = act.resources.getString(R.string.em_space) // передаем текст ошибки

            } else { // когда все нормально

                isTextError.value = false // объявляем, что ошибки нет
                focusColor.value = SuccessColor // цвет фокуса переводим в нормальный
            }

        },

        colors = TextFieldDefaults.outlinedTextFieldColors(
            // цвета
            textColor = Grey40,
            backgroundColor = Grey95,
            placeholderColor = Grey60,
            focusedBorderColor = Grey95,
            unfocusedBorderColor = Grey95,
            cursorColor = Grey00,
            errorBorderColor = Grey95

        ),

        // иконка справа от текста
        trailingIcon = {
            // условие, если состояние ошибки
            if (isTextError.value) {

                Icon(
                    painter = painterResource(R.drawable.ic_error), // иконка ошибки
                    contentDescription = stringResource(R.string.cd_error_icon), // описание для слабослышащих
                    tint = AttentionColor, // цвет иконки
                    modifier = Modifier.size(20.dp) // размер иконки
                )
            } else if (!isTextError.value && text.value != "") {
                Icon(
                    painter = painterResource(R.drawable.ic_check), // иконка ошибки
                    contentDescription = stringResource(R.string.cd_right_icon), // описание для слабослышащих
                    tint = SuccessColor, // цвет иконки
                    modifier = Modifier.size(20.dp) // размер иконки
                )
            }
        },

        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Email, // тип клавиатуры (типа удобнее для ввода Email)
            imeAction = ImeAction.Done // кнопка действия (если не установить это значение, будет перенос на следующую строку. А так действие ГОТОВО)

        ),

        keyboardActions = KeyboardActions(
            // При нажатии на кнопку onDone - снимает фокус с формы
            onDone = {
                focusManager.clearFocus()
            }
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

        singleLine = true, // говорим, что текст в форме будет однострочный

        isError = isTextError.value // в поле isError передаем нашу переменную, которая хранит состояние - ошибка или нет
    )




    // --------- ТЕКСТ ОШИБКИ ----------------


    // условие - если состояние ошибки
    if (isTextError.value) {
        Text(
            text = errorMassage.value, // текст ошибки
            color = AttentionColor, // цвет текста ошибки
            style = Typography.bodySmall, // стиль текста
            modifier = Modifier.padding(top = 5.dp)) // отступы
    }  else {}

    return text.value

}

@Composable
fun fieldPasswordComponent (
    act: MainActivity
): String {

    // создаем переменные email и password - содержимое их будет меняться
    // в зависимости от того, что введет пользователь и это содержимое будет
    // отправляться в Firebase

    var text = remember { mutableStateOf("") }

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey40) }

    // создаем переменную для скрытия или отображения пароля

    var passwordVisible = remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
    }

    // -------- ТЕКСТОВОЕ ПОЛЕ PASSWORD -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey40 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text.value, // значение email // lowerCase() - делает все буквы строчными

        // on valueChange - это действие при изменении значения
        onValueChange = { newText ->
            text.value = newText // помещаем в переменную email новый введенный текст

            // проверяем вводимый текст на соответствие правилам:

            // если пароль меньше 6 символов
            if (newText.length < 6) {

                isTextError.value = true // объявляем состояние ошибки
                focusColor.value = AttentionColor // красим границы формы в цвет ошибки
                errorMassage.value = act.resources.getString(R.string.exception_password_need_more_letters) // передаем текст ошибки

            } else { // когда все нормально

                isTextError.value = false // объявляем, что ошибки нет
                focusColor.value = SuccessColor // цвет фокуса переводим в нормальный
            }

        },

        colors = TextFieldDefaults.outlinedTextFieldColors(
            // цвета
            textColor = Grey40,
            backgroundColor = Grey95,
            placeholderColor = Grey60,
            focusedBorderColor = Grey95,
            unfocusedBorderColor = Grey95,
            cursorColor = Grey00,
            errorBorderColor = Grey95

        ),

        // иконка справа от текста
        trailingIcon = {
            IconButton(
                onClick = {passwordVisible.value = !passwordVisible.value}
            ) {

                Icon(
                    painter = painterResource( // разные иконки в зависимости от того, скрыт пароль или открыт
                        if (!passwordVisible.value) {
                            R.drawable.ic_visibility_off // скрыт
                        } else {
                            R.drawable.ic_visibility // открыт
                        }
                    ),
                    contentDescription = stringResource( // разные иконки в зависимости от того, скрыт пароль или открыт
                        if (!passwordVisible.value) {
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

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Password, // тип клавиатуры (типа удобнее для ввода Email)
            imeAction = ImeAction.Done // кнопка действия (если не установить это значение, будет перенос на следующую строку. А так действие ГОТОВО)

        ),

        keyboardActions = KeyboardActions(
            // При нажатии на кнопку onDone - снимает фокус с формы
            onDone = {
                focusManager.clearFocus()
            }
        ),

        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),

        placeholder = {
            // подсказка для пользователей
            Text(
                text = stringResource(id = R.string.enter_password), // значение подсказки
                style = Typography.bodyLarge // стиль текста в холдере
            )
        },

        leadingIcon = {
            // иконка, расположенная слева от надписи плейсхолдера
            Icon(
                painter = painterResource(id = R.drawable.ic_key), // сама иконка
                contentDescription = stringResource(id = R.string.cd_password_icon), // описание для слабовидящих
                tint = Grey60, // цвет иконки
                modifier = Modifier.size(20.dp) // размер иконки
            )
        },

        singleLine = true, // говорим, что текст в форме будет однострочный

        isError = isTextError.value // в поле isError передаем нашу переменную, которая хранит состояние - ошибка или нет
    )




    // --------- ТЕКСТ ОШИБКИ ----------------


    // условие - если состояние ошибки
    if (isTextError.value) {
        Text(
            text = errorMassage.value, // текст ошибки
            color = AttentionColor, // цвет текста ошибки
            style = Typography.bodySmall, // стиль текста
            modifier = Modifier.padding(top = 5.dp)) // отступы
    }  else {}

    return text.value

}

@Composable
fun fieldHeadlineComponent (
    act: MainActivity
): String {

    // создаем переменные email и password - содержимое их будет меняться
    // в зависимости от того, что введет пользователь и это содержимое будет
    // отправляться в Firebase

    var text = remember { mutableStateOf("") }
    val maxChar = 40
    var counter = "${text.value.length} / ${maxChar.toString()}"

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    //var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey40) }

    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
    }

    // -------- ТЕКСТОВОЕ ПОЛЕ -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey40 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text.value, // значение email // lowerCase() - делает все буквы строчными

        // on valueChange - это действие при изменении значения
        onValueChange = { newText ->

            if (newText.length <= maxChar) {
                isTextError.value = false
                text.value = newText
            }
        },

        colors = TextFieldDefaults.outlinedTextFieldColors(
            // цвета
            textColor = Grey40,
            backgroundColor = Grey95,
            placeholderColor = Grey60,
            focusedBorderColor = Grey95,
            unfocusedBorderColor = Grey95,
            cursorColor = Grey00,
            errorBorderColor = Grey95

        ),

        // иконка справа от текста
        trailingIcon = {
            Text(
                text = counter,
            style = Typography.bodySmall,
            color = Grey40)
        },

        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Text, // тип клавиатуры (типа удобнее для ввода Email)
            imeAction = ImeAction.Done // кнопка действия (если не установить это значение, будет перенос на следующую строку. А так действие ГОТОВО)

        ),

        keyboardActions = KeyboardActions(
            // При нажатии на кнопку onDone - снимает фокус с формы
            onDone = {
                focusManager.clearFocus()
            }
        ),

        placeholder = {
            // подсказка для пользователей
            Text(
                text = stringResource(id = R.string.input_headline), // значение подсказки
                style = Typography.bodyLarge // стиль текста в холдере
            )
        },

        singleLine = true, // говорим, что текст в форме будет однострочный

        isError = isTextError.value // в поле isError передаем нашу переменную, которая хранит состояние - ошибка или нет
    )

    return text.value

}

@Composable
fun fieldDescriptionComponent (
    act: MainActivity
): String {

    // создаем переменные email и password - содержимое их будет меняться
    // в зависимости от того, что введет пользователь и это содержимое будет
    // отправляться в Firebase

    var text = remember { mutableStateOf("") }
    val maxChar = 300
    var counter = "${text.value.length} / ${maxChar.toString()}"

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    //var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey40) }

    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
    }

    // -------- ТЕКСТОВОЕ ПОЛЕ -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey40 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(30.dp) // скругление границ
            ),

        value = text.value, // значение email // lowerCase() - делает все буквы строчными

        // on valueChange - это действие при изменении значения
        onValueChange = { newText ->

            if (newText.length <= maxChar) {
                isTextError.value = false
                text.value = newText
            }
        },

        colors = TextFieldDefaults.outlinedTextFieldColors(
            // цвета
            textColor = Grey40,
            backgroundColor = Grey95,
            placeholderColor = Grey60,
            focusedBorderColor = Grey95,
            unfocusedBorderColor = Grey95,
            cursorColor = Grey00,
            errorBorderColor = Grey95

        ),

        // иконка справа от текста
        trailingIcon = {
            Text(
                text = counter,
                style = Typography.bodySmall,
                color = Grey40)
        },

        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Text, // тип клавиатуры (типа удобнее для ввода Email)
            imeAction = ImeAction.Default // кнопка действия (если не установить это значение, будет перенос на следующую строку. А так действие ГОТОВО)

        ),

        keyboardActions = KeyboardActions(
            // При нажатии на кнопку onDone - снимает фокус с формы
            onDone = {
                focusManager.clearFocus()
            }
        ),

        placeholder = {
            // подсказка для пользователей
            Text(
                text = stringResource(id = R.string.input_description), // значение подсказки
                style = Typography.bodyLarge // стиль текста в холдере
            )
        },

        singleLine = false, // говорим, что текст в форме будет однострочный

        isError = isTextError.value // в поле isError передаем нашу переменную, которая хранит состояние - ошибка или нет
    )

    return text.value

}

@Composable
fun fieldPhoneComponent (
    act: MainActivity
): String {

    // https://ngengesenior.medium.com/how-to-usevisualtransformation-to-create-phone-number-textfield-and-others-in-jetpack-compose-f7a62f8fbe95

    // создаем переменные email и password - содержимое их будет меняться
    // в зависимости от того, что введет пользователь и это содержимое будет
    // отправляться в Firebase

    var number = "+7"
    var inputText = ""

    var text = number + inputText

    val maxChar = 10

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    //var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey40) }

    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
    }

    // -------- ТЕКСТОВОЕ ПОЛЕ -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey40 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text, // значение email // lowerCase() - делает все буквы строчными

        // on valueChange - это действие при изменении значения
        onValueChange = { newText ->

            if (newText.length <= maxChar) {
                isTextError.value = false
                inputText = newText
            }
        },

        colors = TextFieldDefaults.outlinedTextFieldColors(
            // цвета
            textColor = Grey40,
            backgroundColor = Grey95,
            placeholderColor = Grey60,
            focusedBorderColor = Grey95,
            unfocusedBorderColor = Grey95,
            cursorColor = Grey00,
            errorBorderColor = Grey95

        ),


        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Phone, // тип клавиатуры (типа удобнее для ввода Email)
            imeAction = ImeAction.Done // кнопка действия (если не установить это значение, будет перенос на следующую строку. А так действие ГОТОВО)

        ),

        keyboardActions = KeyboardActions(
            // При нажатии на кнопку onDone - снимает фокус с формы
            onDone = {
                focusManager.clearFocus()
            }
        ),

        placeholder = {
            // подсказка для пользователей
            Text(
                text = act.resources.getString(R.string.input_headline), // значение подсказки
                style = Typography.bodyLarge // стиль текста в холдере
            )
        },

        singleLine = true, // говорим, что текст в форме будет однострочный

        isError = isTextError.value // в поле isError передаем нашу переменную, которая хранит состояние - ошибка или нет
    )

    return text

}
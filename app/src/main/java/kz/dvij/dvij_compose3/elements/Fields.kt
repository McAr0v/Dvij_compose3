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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*
import androidx.compose.ui.text.buildAnnotatedString



@Composable
fun fieldEmailComponent (
    act: MainActivity
): String {

    // функция вовзвращает переменную текст, в которую записывается вводимое в поле значение

    var text = remember { mutableStateOf("") }

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey60) }


    // -------- ТЕКСТОВОЕ ПОЛЕ EMAIL -----------------

    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey60 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text.value.lowercase(), // значение text // lowerCase() - делает все буквы строчными

        // on valueChange - это действие при изменении значения

        onValueChange = { newText ->
            text.value = newText // помещаем в переменную text новый введенный текст

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

    // создаем переменную text - она возвращает значение

    var text = remember { mutableStateOf("") }

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey60) }

    // создаем переменную для скрытия или отображения пароля

    var passwordVisible = remember { mutableStateOf(false) }


    // -------- ТЕКСТОВОЕ ПОЛЕ PASSWORD -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey60 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text.value, // значение text

        // on valueChange - это действие при изменении значения

        onValueChange = { newText ->
            text.value = newText // помещаем в переменную text новый введенный текст

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
                onClick = {passwordVisible.value = !passwordVisible.value} // действие на нажатие на иконку видимости
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

        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(), // визуальная трансформация, которая скрывает пароль

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

    // создаем переменную текст - это значение функция возвращает

    var text = remember { mutableStateOf("") }

    val maxChar = 40 // максимальное количество символов

    var counter = maxChar - text.value.length // счетчик, который считает сколько осталось символов

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    //var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey60) }

    // -------- ТЕКСТОВОЕ ПОЛЕ -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey60 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text.value, // значение поля

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

        // счетчик символов справа от текста
        trailingIcon = {
            Text(
                text = counter.toString(),
            style = Typography.bodySmall,
            color = Grey60)
        },

        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Text, // тип клавиатуры
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

    // переменная text возвращает значение при вызове функции

    var text = remember { mutableStateOf("") }

    val maxChar = 300 // максимальное количество вводимых символов

    var counter = "${text.value.length} / ${maxChar.toString()}" // счетчик. Считает, сколько осталось символов для ввода

    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey60) }


    // -------- ТЕКСТОВОЕ ПОЛЕ -----------------

    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey60 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(30.dp) // скругление границ
            ),

        value = text.value, // значение поля

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

        // счетчик символов
        trailingIcon = {
            Text(
                text = counter,
                style = Typography.bodySmall,
                color = Grey60)
        },

        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Text, // тип клавиатуры
            imeAction = ImeAction.Default // кнопка действия (перевод на следующую строку)

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
fun fieldPhoneComponent(
    phone: String,
    mask: String = "+7 (XXX) XXX XX XX",
    maskNumber: Char = 'X',
    onPhoneChanged: (String) -> Unit,
    icon: Painter = painterResource(id = R.drawable.ic_phone)
): String {

    var focusColor = remember { mutableStateOf(Grey40) } // изначальный цвет фокуса
    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    var returnText = "+7$phone" // переменная, которая возвращается из функции

    TextField(
        value = phone, // значение поля

        onValueChange = { it ->
            onPhoneChanged(it.take(mask.count { it == maskNumber }))
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone, // тип клавиатуры
            imeAction = ImeAction.Done // кнопка действия
        ),

        visualTransformation = PhoneVisualTransformation(mask, maskNumber), // визуальное изменение. Передаем маску и символ, который нужно заменять в маске

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey60 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        singleLine = true, // говорим, что в поле только 1 строка

        keyboardActions = KeyboardActions(
            // При нажатии на кнопку onDone - снимает фокус с формы
            onDone = {
                focusManager.clearFocus()
            }
        ),

        textStyle = Typography.bodyLarge, // стиль текста

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

        // иконка слева

        leadingIcon = {
            Icon(
                painter = icon,
                contentDescription = stringResource(id = R.string.cd_phone_icon),
                tint = Grey60,
                modifier = Modifier.size(20.dp) // размер иконки
            )
        }

    )

    return returnText
}


@Composable
fun fieldPriceComponent (
    act: MainActivity
): String {

    // переменная текст - та, которая возвращается из функции

    var text = remember { mutableStateOf("") }

    val maxChar = 8 // максимальное количество символов


    // создаем переменные для проверки на ошибку и вывода текста сообщения ошибки

    var isTextError = remember { mutableStateOf(false) } // состояние формы - ошибка или нет
    //var errorMassage = remember { mutableStateOf("") } // сообщение об ошибке

    val focusManager = LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы

    // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
    // при фокусе на них окрашивались в нужные цвета

    var focusColor = remember { mutableStateOf(Grey60) }


    // -------- ТЕКСТОВОЕ ПОЛЕ -----------------


    TextField(

        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от действия - есть фокус на поле, или нет
                if (it.isFocused) focusColor.value =
                    PrimaryColor // если есть, то в переменные с цветами передать цвет брендовый
                else focusColor.value =
                    Grey60 // если нет, то в переменные с цветами передать серый
            }
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = focusColor.value, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(50.dp) // скругление границ
            ),

        value = text.value, // значение введенного текста

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

        // иконка слева цены
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_tenge),
                contentDescription = stringResource(id = R.string.cd_phone_icon),
                tint = Grey60,
                modifier = Modifier.size(15.dp) // размер иконки
            )
                      },

        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions(
            // опции клавиатуры, которая появляется при вводе
            keyboardType = KeyboardType.Number, // тип клавиатуры (типа удобнее для ввода Email)
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
                text = stringResource(id = R.string.input_price), // значение подсказки
                style = Typography.bodyLarge // стиль текста в холдере
            )
        },

        singleLine = true, // говорим, что текст в форме будет однострочный

        isError = isTextError.value // в поле isError передаем нашу переменную, которая хранит состояние - ошибка или нет
    )

    return text.value

}




// --------- ПРЕОБРАЗОВАНИЕ ТЕЛЕФОНА В НУЖНЫЙ ФОРМАТ ------------


class PhoneVisualTransformation(private val mask: String, private val maskNumber: Char) : VisualTransformation {

    private val maxLength = mask.count { it == maskNumber }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskNumber) {
                    val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        //if (other !is PhonedVisualTransformation) return false
        //if (mask != other.mask) return false
        // (maskNumber != other.maskNumber) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class PhoneOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            if (mask[i++] != numberChar) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}
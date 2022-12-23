package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun fieldComponent (act: MainActivity, dataType: String): String {

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
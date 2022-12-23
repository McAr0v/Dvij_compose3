package kz.dvij.dvij_compose3.createscreens

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.ui.theme.*

@Preview
@Composable
fun ViewCreateMeetingScreen(){
    CreateMeetingScreen()
}

@Composable
fun Field (): String {

    val focusManager =
        LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы
    var focusColorTitle = remember { mutableStateOf(Grey40) }

    var title = remember { mutableStateOf("") }

    //var errorEmailMassage = remember { mutableStateOf("") } // сообщение об ошибке

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { it -> // зависимость цвета границы от состояния - есть фокус на форме или нет
                if (it.isFocused) focusColorTitle.value =
                    PrimaryColor // если фокус - то брендовый цвет
                else focusColorTitle.value = Grey40 // если не фокус - серый
            }
            .border( // настройка границы
                2.dp, // толщина границы
                color = focusColorTitle.value, // цвет границы
                shape = RoundedCornerShape(30.dp) // скругление углов
            ),

        value = title.value, // значение пароля

        onValueChange = {newText -> title.value = newText}, // вводимое значение

        colors = TextFieldDefaults.outlinedTextFieldColors( // цвета поля
            textColor = Grey40,
            backgroundColor = Grey95,
            placeholderColor = Grey60,
            focusedBorderColor = Grey95,
            unfocusedBorderColor = Grey95,
            cursorColor = Grey00
        ),

        keyboardActions = KeyboardActions(
            // При нажатии на кнопку onDone - снимает фокус с формы
            onDone = {

                focusManager.clearFocus()

            }
        ),

        textStyle = Typography.bodyLarge, // стиль текста

        keyboardOptions = KeyboardOptions( // опции всплывающей клавиатуры
            keyboardType = KeyboardType.Text, // тип клавиатуры - пароль
            imeAction = ImeAction.Done // кнопка завершения - ГОТОВО
        ),

        placeholder = {
            Text( // текст плейсхолдера
                text = "Название мероприятия", // сам текст плейсхолдера
                style = Typography.bodyLarge // стиль текста
            )
        },

        singleLine = true, // говорим, что текст в форме будет однострочный
    )

    return title.value
}


@Composable
fun CreateMeetingScreen () {

    var title = ""
    var title2 = ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey95)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Создание мероприятия",
            style = Typography.titleMedium,
            color = Grey00
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = kz.dvij.dvij_compose3.R.drawable.no_user_image),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Заголовок",
            style = Typography.labelMedium,
            color = Grey40
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ------------    ТЕКСТОВОЕ ПОЛЕ С ПАРОЛЕМ ----------------

        title = Field()

        Text(
            text = title,
            style = Typography.labelMedium,
            color = Grey40
        )

        title2 = Field()

        Text(
            text = title2,
            style = Typography.labelMedium,
            color = Grey40
        )

    }
}
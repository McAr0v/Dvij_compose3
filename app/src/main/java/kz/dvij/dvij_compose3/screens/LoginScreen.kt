package kz.dvij.dvij_compose3.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

@Preview
@Composable
fun PreviewLoginScreen(){
    LoginScreen()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(){
    
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Grey95)
        .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var email = remember{ mutableStateOf("") }
        var password = remember{ mutableStateOf("") }

        var focusColorEmail = remember {mutableStateOf(Grey40)}
        var focusColorPassword = remember {mutableStateOf(Grey40)}

        var passwordVisible = remember { mutableStateOf(false) }

        Text(
            text = "Создать аккаунт",
        style = Typography.titleLarge,
        color = Grey00)

        Spacer(modifier = Modifier.height(40.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { it ->
                    if (it.isFocused) focusColorEmail.value = PrimaryColor
                    else focusColorEmail.value = Grey40
                }
                .border(
                    2.dp,
                    color = focusColorEmail.value,
                    shape = RoundedCornerShape(50.dp)
                ),
            value = email.value,
            onValueChange = {newText -> email.value = newText},
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Grey40,
                backgroundColor = Grey95,
                placeholderColor = Grey60,
                focusedBorderColor = Grey95,
                unfocusedBorderColor = Grey95,
                cursorColor = Grey00
            ),
            textStyle = Typography.bodyLarge,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
            placeholder = { Text(text = "Введите Email", style = Typography.bodyLarge)},
            leadingIcon = { Icon(painter = painterResource(id = R.drawable.telegram), contentDescription = "", tint = Grey60, modifier = Modifier.size(25.dp))},

            )

        Spacer(modifier = Modifier.height(20.dp))

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
            placeholder = { Text(text = "Введите Пароль", style = Typography.bodyLarge)},
            leadingIcon = { Icon(painter = painterResource(id = R.drawable.whatsapp), contentDescription = "", tint = Grey60, modifier = Modifier.size(25.dp))},


            )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { /*TODO*/ },
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
            Text(text = "Зарегистрироваться", style = Typography.labelMedium)
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
            Text(text = "Зарегистрироваться через Google", style = Typography.labelMedium)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Уже создавали аккаунт в движе? ",
                style = Typography.labelMedium,
                color = Grey40
            )
            Text(
                text = "Войти",
                style = Typography.labelMedium,
                color = PrimaryColor,
                modifier = Modifier.clickable {  }
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


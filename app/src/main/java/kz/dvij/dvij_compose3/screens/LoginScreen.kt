package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = remember{ mutableStateOf("") }

        Text(
            text = stringResource(id = R.string.login_in_app),
        style = Typography.titleMedium,
        color = Grey00)

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = email.value,
            onValueChange = {newText -> email.value = newText},
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Grey40,
                backgroundColor = Grey100,
                placeholderColor = Grey60
            ),
            textStyle = Typography.bodyLarge,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            placeholder = { Text(text = "Введите Email")}

        )



    }
    
}
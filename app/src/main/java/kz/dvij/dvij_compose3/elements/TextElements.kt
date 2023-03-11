package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.ui.theme.*


@Composable
fun NumberAndDesc (
    number: String,
    desc: String
) {

    Column(
        modifier = Modifier
            .background(
                color = Grey_Background,
                shape = RoundedCornerShape(10.dp)
            )
            .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = YellowDvij),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        
        Column(
            modifier = Modifier.padding(top = 10.dp, bottom = 15.dp, start = 15.dp, end = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ----- Число ------

            Text(
                text = number,
                color = WhiteDvij,
                style = Typography.titleLarge
            )

            // ----- Описание ------

            Text(
                text = desc,
                color = Grey_Text,
                style = Typography.labelMedium
            )

            Spacer(modifier = Modifier.height(5.dp))

        }

    }

}

@Composable
fun TextAndDesc (
    headline: String,
    description: String,
    size: String = "Medium"
){

    Column {

        // ----- ЗАГОЛОВОК ------

        Text(
            text = headline,
            color = WhiteDvij,
            style = when (size) {

                "Medium" -> Typography.titleSmall
                else -> Typography.bodyMedium

            }
        )

        // ----- ПОДПИСЬ ------

        Text(
            text = description,
            color = Grey_Text,
            style = Typography.bodySmall
        )
    }
}

@Composable
fun IconText (icon: Int, inputText: String) {

    // ИКОНКА С ТЕКСТОМ
    // Размещаем в Row чтобы элементы распологались друг за другом по горизонтали

    Row(
        modifier = Modifier
            .fillMaxWidth() // занять всю ширину
            .padding(top = 10.dp), // отступ от ЭЛЕМЕНТА, который распологается ВЫШЕ IconText
        horizontalArrangement = Arrangement.Start, // выравнивание - начало (слева)
        verticalAlignment = Alignment.CenterVertically // вертикальное выравнивание элементов IconText - по центру
    )
    {

        // ИКОНКА

        Icon(
            imageVector = ImageVector.vectorResource(icon), // передаем сам векторный файл иконки !!! ПРИМЕР ИЗ ПАПКИ drawable - kz.dvij.dvij_compose3.R.drawable.ic_time
            contentDescription = stringResource(id = kz.dvij.dvij_compose3.R.string.cd_icon), // описание для слабовидящих
            modifier = Modifier.size(20.dp), // размер иконки
            tint = Grey40 // Цвет иконки
        )

        // Текст

        Text(
            modifier = Modifier.padding(start = 10.dp), // отступ текста от иконки
            text = inputText, // передаем сюда текст, который нужно написать
            color = Grey40, // цвет текста
            style = Typography.bodyMedium // стиль текста
        )
    }
}

// ------ ЗАГОЛОВОК И ПОДПИСЬ СНИЗУ ---------

@Composable
fun HeadlineAndDesc (headline: String, desc: String){

    Column(modifier = Modifier.fillMaxWidth()) {

        // ТЕКСТ ЗАГОЛОВКА

        androidx.compose.material.Text(
            text = headline,
            color = Grey10,
            style = Typography.titleSmall
        )

        // ТЕКСТ ОПИСАНИЯ ПОД ЗАГОЛОВКОМ

        androidx.compose.material.Text(
            text = desc,
            color = Grey10,
            style = Typography.labelSmall
        )
    }
}
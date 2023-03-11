package kz.dvij.dvij_compose3.elements

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.FOR_CARDS
import kz.dvij.dvij_compose3.constants.PRIMARY
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun ButtonCustom (
    typeButton: String = "Primary",
    leftIcon: Int = 0,
    rightIcon: Int = 0,
    buttonText: String,
    onClick: () -> Unit
) {

    Button(

        onClick = onClick, // действие на нажатие передается извне

        // Остальные настройки кнопки

        modifier = Modifier
            .fillMaxWidth() // кнопка на всю ширину
            .height(60.dp) // высота
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = YellowDvij, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(30) // скругление границ
            ),
        shape = RoundedCornerShape(30), // скругление углов
        colors = ButtonDefaults.buttonColors(

            // цвет кнопки
            backgroundColor = if (typeButton == "Primary"){
                YellowDvij
            } else {
                Grey_Background
                   },

            // цвет контента на кнопке
            contentColor = if (typeButton == "Primary"){
                Grey_Background
            } else {
                WhiteDvij
            },
        )
    )
    {

        // СОДЕРЖИМОЕ КНОПКИ

        // --- ЛЕВАЯ ИКОНКА -----

        if (leftIcon != 0) {

            Icon(
                painter = painterResource(id = leftIcon), // иконка
                contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                tint = if (typeButton == "Primary"){
                    Grey_Background // цвет Примари
                } else {
                    WhiteDvij // цвет Секондари
                }
            )

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

        }

        Text(
            text = buttonText, // Текст приходит извне
            style = Typography.bodySmall // стиль текста
        )

        if (rightIcon != 0) {

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

            Icon(
                painter = painterResource(id = rightIcon), // иконка
                contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                tint = if (typeButton == "Primary"){
                    Grey_Background // цвет Примари
                } else {
                    WhiteDvij // цвет Секондари
                }
            )
        }
    }
}

@Composable
fun ButtonGoogleCustom (
    leftIcon: Int = 0,
    rightIcon: Int = 0,
    buttonText: String,
    onClick: () -> Unit
) {

    Button(

        onClick = onClick, // действие на нажатие передается извне

        // Остальные настройки кнопки

        modifier = Modifier
            .fillMaxWidth() // кнопка на всю ширину
            .height(60.dp) // высота
            ,
        shape = RoundedCornerShape(30), // скругление углов
        colors = ButtonDefaults.buttonColors(

            // цвет кнопки
            backgroundColor = WhiteDvij,

            // цвет контента на кнопке
            contentColor = Grey_Background,
        )
    )
    {

        // СОДЕРЖИМОЕ КНОПКИ

        // --- ЛЕВАЯ ИКОНКА -----

        if (leftIcon != 0) {

            Icon(
                painter = painterResource(id = leftIcon), // иконка
                contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                tint = Grey_Background,
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

        }

        Text(
            text = buttonText, // Текст приходит извне
            style = Typography.bodySmall // стиль текста
        )

        if (rightIcon != 0) {

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

            Icon(
                painter = painterResource(id = rightIcon), // иконка
                contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                tint = Grey_Background
            )
        }
    }
}

@Composable
fun SocialButtonCustom (
    typeButton: String = "Primary",
    icon: Int,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape) // делаем круглый фон
                .background(
                    when (typeButton){

                        PRIMARY -> YellowDvij
                        FOR_CARDS -> Grey_ForCards
                        DARK -> Grey_OnBackground
                        else -> Grey_Background

                    }
                ) // фон иконки
                .border( // настройки самих границ

                    2.dp, // толщина границы
                    color = when (typeButton){

                        PRIMARY -> YellowDvij
                        FOR_CARDS -> Grey_ForCards
                        DARK -> Grey_OnBackground
                        else -> YellowDvij

                    }, // цвет - для этого выше мы создавали переменные с цветом
                    shape = RoundedCornerShape(100) // скругление границ
                )
                .padding(10.dp) // отступ внутри до иконки
                .size(25.dp), // размер иконки
            painter = painterResource(id = icon), // сама иконка
            contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
            tint = when (typeButton){

                PRIMARY -> Grey_Background
                FOR_CARDS -> WhiteDvij
                DARK -> WhiteDvij
                else -> WhiteDvij

            } // цвет иконки
        )
    }

}


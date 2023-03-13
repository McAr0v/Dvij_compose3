package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.FOR_CARDS
import kz.dvij.dvij_compose3.constants.PRIMARY
import kz.dvij.dvij_compose3.constants.SECONDARY
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun Bubble (
    typeButton: String = "Primary",
    leftIcon: Int = 0,
    rightIcon: Int = 0,
    buttonText: String,
    leftIconColor: Color = WhiteDvij,
    rightIconColor: Color = WhiteDvij,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(100),
                color = when (typeButton) {
                    PRIMARY -> YellowDvij
                    DARK -> Grey_OnBackground
                    FOR_CARDS -> Grey_ForCards
                    else -> Grey_Background
                }
            )
            .border( // настройки самих границ
                2.dp, // толщина границы
                color = when (typeButton) {
                    PRIMARY -> YellowDvij
                    DARK -> Grey_OnBackground
                    FOR_CARDS -> Grey_ForCards
                    else -> YellowDvij
                }, // цвет - для этого выше мы создавали переменные с цветом
                shape = RoundedCornerShape(100) // скругление границ
            )
            .padding(10.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){

        // СОДЕРЖИМОЕ КНОПКИ

        // --- ЛЕВАЯ ИКОНКА -----

        if (leftIcon != 0) {

            Icon(
                painter = painterResource(id = leftIcon), // иконка
                contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                tint = leftIconColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

        }

        Text(
            text = buttonText, // Текст приходит извне
            style = Typography.labelMedium, // стиль текста
            color = when (typeButton) {
                PRIMARY -> Grey_OnBackground
                SECONDARY -> YellowDvij
                else -> WhiteDvij
            }
        )

        if (rightIcon != 0) {

            Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

            Icon(
                painter = painterResource(id = rightIcon), // иконка
                contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                tint = rightIconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
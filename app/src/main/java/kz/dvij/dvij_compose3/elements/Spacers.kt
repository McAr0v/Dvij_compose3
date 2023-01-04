package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.ui.theme.Grey40
import kz.dvij.dvij_compose3.ui.theme.Grey80
import kz.dvij.dvij_compose3.ui.theme.Typography


// ---- РАЗДЕЛИТЕЛИ С ТЕКСТОМ И ПОЛОСКОЙ

@Composable
fun SpacerTextWithLine(headline: String){

    // Помещаем все в колонку

    Column(modifier = Modifier.fillMaxWidth()) {

        Spacer(modifier = Modifier.height(20.dp)) // отступ сверху 20 дп

        // помещаем в строку текст и спейсер

        Row(
            modifier = Modifier.fillMaxWidth(), // строка на всю ширину
            verticalAlignment = Alignment.CenterVertically // вертикальное выравнивание
        ) {

            // ------- ЗАГЛОВОК ---------

            Text(
                text = headline,
                style = Typography.labelMedium,
                color = Grey40
            )

            // ------ РАЗДЕЛИТЕЛЬ МЕЖДУ ЗАГОЛОВКОМ И ПОЛОСОЙ --------

            Spacer(modifier = Modifier.width(20.dp))

            // ----- САМА ПОЛОСА -----------

            Divider(
                modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                color = Grey80
            )
        }

        // ----- ОТСТУП СНИЗУ ----------

        Spacer(modifier = Modifier.height(10.dp))

    }
}
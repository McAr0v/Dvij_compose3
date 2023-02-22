package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun ConfirmDialog(onDismiss: () -> Unit, onClick: () -> Unit) {

    // ------ САМ ДИАЛОГ ---------

    Dialog(
        onDismissRequest = { onDismiss() } // действие на нажатие за пределами диалога
    ) {

        // -------- СОДЕРЖИМОЕ ДИАЛОГА ---------

        Column(
            modifier = Modifier
                .border(
                    2.dp, // толщина границы
                    color = Grey80, // цвет границы
                    shape = RoundedCornerShape(20.dp) // скругление углов
                )
                .background(
                    Grey95, // цвет фона
                    shape = RoundedCornerShape(20.dp) // скругление углов
                )
                .padding(20.dp) // отступы
                .fillMaxWidth() // занять всю ширину

        ) {


            // ------- ЗАГЛОВОК ВЫБЕРИТЕ КАТЕГОРИЮ и КНОПКА ЗАКРЫТЬ -----------

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание элементов по центру
                horizontalArrangement = Arrangement.End // выравнивание по горизонтали
            ) {

                // --------- ЗАГОЛОВОК ----------

                Text(
                    text = "Действительно хочешь удалить?", // текст заголовка
                    style = Typography.titleMedium, // стиль заголовка
                    color = Grey10, // цвет заголовка
                    modifier = Modifier.weight(1f)
                ) // занять всю оставшуюся ширину

                Spacer(modifier = Modifier.height(20.dp)) // разделитель

                // ------------- ИКОНКА ЗАКРЫТЬ ----------------

                Icon(
                    painter = painterResource(id = R.drawable.ic_close), // сама иконка
                    contentDescription = stringResource(id = R.string.close_page), // описание для слабовидяших
                    tint = Grey10, // цвет иконки
                    modifier = Modifier.clickable { onDismiss() } // действие на нажатие
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {

                Button(onClick = { onClick() }) {

                    Text(text = "Да, удалить")

                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = { onDismiss() }) {

                    Text(text = "Нет, отмена")

                }
            }


        }
    }
}
package kz.dvij.dvij_compose3.photohelper

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.*

// --- ФУНКЦИЯ ВЫБОРА КАРТИНКИ В СОЗДАНИИ МЕРОПРИЯТИЯ / ЗАВЕДЕНИЯ / АКЦИЙ  --------

@Composable
fun chooseImageDesign (inputImageUrl: String? = ""): Uri? {

    val imageUrl = remember {
        mutableStateOf(inputImageUrl)
    }
    val selectImage = remember { mutableStateOf<Uri?>(null) } // создаем пустое значение Uri

    // запускаем Галерею и получаем Uri нашей картинки
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){
        selectImage.value = it // собственно результат it помещаем в переменную изображения выше
    }

    // ------- ПОМЕЩАЕМ ВСЕ СОДЕРЖИМОЕ В КАРТОЧКУ -------

    Card(
        modifier = Modifier
            .fillMaxWidth() // занять всю ширину
            .height(220.dp), // высота 220
        shape = RoundedCornerShape(15.dp), // скругление углов
        backgroundColor = Grey100 // цвет фона
    ) {

        // ---- ЕСЛИ ИЗОБРАЖЕНИЕ ЕЩЕ НЕ ВЫБРАНО -------

        if (selectImage.value == null && imageUrl.value == "") {

            Column(
                modifier = Modifier
                    .fillMaxWidth() // занять всю ширину
                    .fillMaxHeight(), // занять всю высоту
                horizontalAlignment = Alignment.CenterHorizontally, // выравнивание по горизонтали по центру
                verticalArrangement = Arrangement.Center // выравнивание по вертикали по центру
            ) {

                // ------- ИКОНКА И НАДПИСЬ ДОБАВИТЬ ИЗОБРАЖЕНИЕ ---------

                Row(
                    modifier = Modifier
                        .fillMaxWidth() // занять всю ширину
                        .clickable {

                            // запускаем на нажатие функции выбора картинки
                            galleryLauncher.launch("image/*")

                        },
                    verticalAlignment = Alignment.CenterVertically, // выравнивание по вертикали по центру
                    horizontalArrangement = Arrangement.Center // выравнивание по горизонтали по центру
                ) {

                    // ------ ИКОНКА ДОБАВИТЬ --------

                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = stringResource(id = R.string.cd_add_image),
                        tint = Grey10
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // -------- ТЕКСТ ВЫБЕРИ ИЗОБРАЖЕНИЕ -----------

                    Text(
                        text = stringResource(id = R.string.cm_choose_image),
                        color = Grey10,
                        style = Typography.bodyMedium
                    )
                }
            }

        } else {

            // -------- ЕСЛИ ИЗОБРАЖЕНИЕ ВЫБРАНО ------------

            Image(
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(20.dp), // скругление углов
                        color = Grey95 // цвет фона
                    )
                    .fillMaxWidth() // занять всю ширину
                    .fillMaxHeight(), // занять всю высоту

                painter = (
                        if (selectImage.value != null) {

                            // изображение-ЗАГЛУШКА
                            rememberAsyncImagePainter(model = selectImage.value)

                        } else {

                            // ВЫБРАННОЕ ИЗОБРАЖЕНИЕ
                            rememberAsyncImagePainter(model = imageUrl.value)

                        }
                        ),
                contentDescription = stringResource(id = R.string.cd_chosen_image), // Описание для слабовидящих

                contentScale = ContentScale.Crop, // Поместить изображение
                alignment = Alignment.Center // выравнивание по центру
            )

            // --- РЕДАКТИРОВАТЬ ИЛИ УДАЛИТЬ ИЗОБРАЖЕНИЕ --------

            Column(
                modifier = Modifier
                    .fillMaxSize() // занять весь размер
                    .padding(5.dp), // отступ
                horizontalAlignment = Alignment.End, // выравнивание по горизонтали справа
                verticalArrangement = Arrangement.SpaceBetween // выравнивание по вертикали - раскидать элементы сверху и снизу
            ) {

                // ---- ИКОНКА РЕДАКТИРОВАТЬ -------

                IconButton(

                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .background(
                            WarningColor,
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = stringResource(id = R.string.cd_edit_image),
                        tint = Grey95
                    )
                }

                // --- ИКОНКА УДАЛИТЬ -----------

                IconButton(
                    onClick = {
                        selectImage.value = null
                        imageUrl.value = ""
                              },
                    modifier = Modifier
                        .background(
                            AttentionColor,
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(id = R.string.cd_delete_image),
                        tint = Grey95
                    )
                }
            }
        }
    }

    return selectImage.value

}
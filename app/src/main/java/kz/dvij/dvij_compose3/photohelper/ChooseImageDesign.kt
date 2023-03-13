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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.ui.theme.*

// --- ФУНКЦИЯ ВЫБОРА КАРТИНКИ В СОЗДАНИИ МЕРОПРИЯТИЯ / ЗАВЕДЕНИЯ / АКЦИЙ  --------

@Composable
fun chooseImageDesign (inputImageUrl: String? = ""): Uri? {

    val clearImage: Uri? = null

    val imageUrl = remember {
        mutableStateOf(inputImageUrl)
    }
    val selectImage = remember { mutableStateOf<Uri?>(clearImage) } // создаем пустое значение Uri

    // запускаем Галерею и получаем Uri нашей картинки
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){
        selectImage.value = it // собственно результат it помещаем в переменную изображения выше
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(
            color = Grey_ForCards,
            shape = RoundedCornerShape(15.dp)
        )
    ) {
        
        // ---- ЕСЛИ ИЗОБРАЖЕНИЕ НЕ ВЫБРАНО -------

        if (selectImage.value == null && imageUrl.value == "") {

            Column(
                modifier = Modifier
                    .fillMaxWidth() // занять всю ширину
                    .height(220.dp)
                    .padding(horizontal = 50.dp), // высота 220
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                // КНОПКА ВЫБРАТЬ ИЗОБРАЖЕНИЕ

                ButtonCustom(
                    buttonText = stringResource(id = R.string.choose_image),
                    leftIcon = R.drawable.ic_add
                ) {
                    // запускаем на нажатие функции выбора картинки
                    galleryLauncher.launch("image/*")
                }
            }
        } else {

            // -------- ЕСЛИ УЖЕ ЕСТЬ ИЗОБРАЖЕНИЕ ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Grey_OnBackground,
                        shape = RoundedCornerShape(15.dp)
                    ),

            ) {

                // -------- ЕСЛИ ИЗОБРАЖЕНИЕ ВЫБРАНО, ПОМЕЩАЕМ В КАРТОЧКУ ------------

                Card(
                    modifier = Modifier
                        .fillMaxWidth() // занять всю ширину
                        .height(220.dp), // высота 220
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 0.dp, bottomEnd = 0.dp), // скругление углов
                    backgroundColor = Grey_ForCards // цвет фона
                ) {

                    // -------- САМО ИЗОБРАЖЕНИЕ -------- 

                    Image(
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(
                                    topStart = 15.dp,
                                    topEnd = 15.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                ), // скругление углов
                                color = Grey_ForCards // цвет фона
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
                }


                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // ----- КНОПКА РЕДАКТИРОВАТЬ ------

                    Text(
                        text = stringResource(id = R.string.edit),
                        style = Typography.labelMedium,
                        color = YellowDvij,
                        modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
                    )

                    // ------ КНОПКА УДАЛИТЬ -------

                    Text(
                        text = stringResource(id = R.string.delete),
                        style = Typography.labelMedium,
                        color = AttentionRed,
                        modifier = Modifier.clickable {
                            selectImage.value = null
                            imageUrl.value = ""
                        }
                    )

                }
            }
        }
    }

    return selectImage.value

}
package kz.dvij.dvij_compose3.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Карточка мероприятий
@Preview
@Composable
fun MeetingCard () {
    Card(
        modifier = Modifier
            .fillMaxWidth() // растягиваем на всю ширину
            .padding(10.dp) // отступ от краев экрана
            .height(300.dp),
        shape = RoundedCornerShape(15.dp), // shape - форма. Скругляем углы
        colors = CardDefaults.cardColors(Grey95), // цвет карточки под картинкой, по идее можно убрать
        elevation = CardDefaults.cardElevation(5.dp) // "левитация" над фоном
        
    ) {

        // начало работы с карточкой

        Box(modifier = Modifier.fillMaxWidth()){

            // работа с картинкой

            Image(
                painter = painterResource(kz.dvij.dvij_compose3.R.drawable.korn_concert), // картинка карточки. Потом сюда надо подставлять картинку с базы данных
                modifier = Modifier
                    //.height(300.dp)
                    .fillMaxSize(), // заполнить картинку максимально по контейнеру
                contentScale = ContentScale.Crop, // обрезать картинку, если не вмещается
                contentDescription = "Изображение мероприятия" // описание изображения для слабовидящих
            )

            // Помещаем еще контейнер

            Box(modifier = Modifier
                .fillMaxWidth() // занять максимальный размер
                .padding(20.dp) // отступ от краев карточки
            ){

                // Колонка с содержимым 

               Column() {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 150.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        CustomButton(buttonText = "Спорт")
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Отправить",
                            modifier = Modifier.size(20.dp),
                            tint = Grey00
                        )
                    }
                    Spacer(Modifier.size(10.dp))
                    CustomText(inputText = "Hi")
                }
            }
        }
    }
}



@Composable
fun CustomText (inputText: String) {
    Text(
        text = inputText,
        color = Grey00)
}


@Composable
fun CustomButton (buttonText: String) {
    Button(
        onClick = {  },
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 12.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            contentColor = Grey00
        )

    ) {
        /* Icon(
            iconLeft,
            contentDescription = "Отправить",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(5.dp))*/
        Text(buttonText)
        /*Spacer(Modifier.size(5.dp))
        Icon(
            iconRight,
            contentDescription = "Отправить",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )*/
    }
}
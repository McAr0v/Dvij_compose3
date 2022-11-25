package kz.dvij.dvij_compose3.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

@Composable
fun MeetingCard (category: String, title: String, time: String, date: String) {

    // Принимаем категорию и заголовок. Надо еще принимать дату, время и картинку
    // Карточка мероприятий

    Card(
        modifier = Modifier
            .fillMaxWidth() // растягиваем карточку на всю ширину экрана
            .padding(10.dp) // отступ от краев экрана
            ,
        shape = RoundedCornerShape(15.dp), // shape - форма. Скругляем углы
        colors = CardDefaults.cardColors(Grey95), // цвет карточки под картинкой, по идее можно убрать
        elevation = CardDefaults.cardElevation(5.dp) // "левитация" над фоном
        
    ) {

        // Помещаем в карточку Box чтобы туда вложить картинку мероприятия и сделать ее фоном

        Box(modifier = Modifier
            .fillMaxWidth() // растягиваем на всю ширину
            .height(280.dp) // задаем высоту карточки. ЕСЛИ НЕ ВМЕЩАЕТСЯ КОНТЕНТ, СДЕЛАТЬ БОЛЬШЕ
            ) {

            // Картинка - все настройки тут

            Image(
                painter = painterResource(kz.dvij.dvij_compose3.R.drawable.korn_concert), // картинка карточки. Потом сюда надо подставлять картинку с базы данных
                modifier = Modifier.fillMaxSize(), // заполнить картинкой весь контейнер
                contentScale = ContentScale.Crop, // обрезать картинку, что не вмещается
                contentDescription = "Изображение мероприятия" // описание изображения для слабовидящих
            )

            // Помещаем еще контейнер поверх картинки
            // В нем уже находится все ТЕКСТОВОЕ содержимое
            // выбираем Column чтобы все элементы шли друг за другом по высоте

            Column(modifier = Modifier
                .fillMaxSize() // занять всю карточку
                .background(Grey100_50) // помещаем поверх картинки черный полупрозрачный цвет
                .padding(20.dp), // отступ от краев карточки
                verticalArrangement = Arrangement.SpaceBetween // раздвигаем элементы между собой к верху и низу
            )
            {

                // Верхняя панель, КАТЕГОРИЯ И ИЗБРАННОЕ
                // Row - чтобы элементы добавлялись друг за другом по ширине

                Row(
                    modifier = Modifier.fillMaxWidth(), // Растянуть на всю ширину
                    horizontalArrangement = Arrangement.SpaceBetween, // выравнивание - развести элементы по краям
                    verticalAlignment = Alignment.Top // вертикальное выравнивание элементов - по верху
                ) {

                    // КНОПКА КАТЕГОРИИ

                    Text(
                        text = category, // category - название категории. Нужно сюда передавать категорию из базы данных
                        color = Grey00, // цвет текста
                        style = Typography.bodySmall, // стиль текста
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(15.dp)) // скругляем углы
                            .background(PrimaryColor) // цвет кнопки
                            .padding(
                                horizontal = 8.dp, // отступ слева/справа внутри категории
                                vertical = 4.dp // отступ снизу / сверху внутри категории
                            ),

                        )

                    // ИКОНКА ИЗБРАННОЕ

                    Icon(
                        imageVector = Icons.Filled.Favorite, // сам векторный файл иконки
                        contentDescription = "Иконка добавить в избранные", // описание для слабовидящих
                        modifier = Modifier.size(24.dp), // размер иконки
                        tint = Grey00 // Цвет иконки
                    )
                }

                // Заголовок, дата, время

                Column() {

                    // Заголовок мероприятия

                    Text(
                        text = title, // title - заголовок мероприятия, который должен приходить из базы данных
                        color = Grey00, // цвет текста
                        style = Typography.titleLarge // стиль текста
                    )

                    // Панель ДАТА И ВРЕМЯ

                    Column(
                        modifier = Modifier.padding(top=10.dp), // отступ сверху
                    ) {

                        // Вывод даты мероприятия
                        // date - дата проведения мероприятия.
                        // date надо будет передавать из базы данных. Иконку не надо, уже передаю

                        IconText(kz.dvij.dvij_compose3.R.drawable.ic_calendar, date)

                        // Вывод времени начала мероприятия
                        // time - время начала мероприятия.
                        // time надо будет передавать из базы данных. Иконку не надо, уже передаю

                        IconText(kz.dvij.dvij_compose3.R.drawable.ic_time, time)

                    }
                }

            }

        }
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
            contentDescription = "Иконка", // описание для слабовидящих
            modifier = Modifier.size(25.dp), // размер иконки
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


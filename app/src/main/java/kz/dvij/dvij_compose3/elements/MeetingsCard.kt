package kz.dvij.dvij_compose3.elements

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.navigation.BottomNavigationItem
import kz.dvij.dvij_compose3.navigation.MEETING_VIEW
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingsCard(val act: MainActivity) {

    @Composable
    fun MeetingCard (navController: NavController, meetingItem: MeetingsAdsClass, meetingKey: MutableState<String>) {

        // Принимаем категорию и заголовок. Надо еще принимать дату, время и картинку
        // Карточка мероприятий

        val linear = Brush.verticalGradient(listOf(Grey100_50, Grey100))

        val favIconColor = remember{ mutableStateOf(Grey10) }

        act.databaseManager.favIconMeeting(meetingItem.key!!){
            if (it){
                favIconColor.value = PrimaryColor
            } else {
                favIconColor.value = Grey10
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth() // растягиваем карточку на всю ширину экрана
                .padding(10.dp) // отступ от краев экрана
                .clickable {
                    meetingKey.value = meetingItem.key.toString()


                    navController.navigate(MEETING_VIEW)
                },
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

                if (meetingItem.image1 !=null){
                    AsyncImage(
                        model = meetingItem.image1, // БЕРЕМ ИЗОБРАЖЕНИЕ ИЗ ПРИНЯТНОГО МЕРОПРИЯТИЯ ИЗ БД
                        contentDescription = stringResource(id = R.string.cd_meeting_image), // описание изображения для слабовидящих
                        modifier = Modifier.fillMaxSize(), // заполнить картинкой весь контейнер
                        contentScale = ContentScale.Crop // обрезать картинку, что не вмещается
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.korn_concert), // картинка карточки. Потом сюда надо подставлять картинку с базы данных
                        modifier = Modifier.fillMaxSize(), // заполнить картинкой весь контейнер
                        contentScale = ContentScale.Crop, // обрезать картинку, что не вмещается
                        contentDescription = stringResource(id = R.string.cd_meeting_image) // описание изображения для слабовидящих
                    )
                }


                // Помещаем еще контейнер поверх картинки
                // В нем уже находится все ТЕКСТОВОЕ содержимое
                // выбираем Column чтобы все элементы шли друг за другом по высоте

                Column(
                    modifier = Modifier
                        .fillMaxSize() // занять всю карточку
                        .background(linear) // помещаем поверх картинки черный полупрозрачный цвет
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
                        if (meetingItem.category != null) Text(
                            text = meetingItem.category, // category - название категории. Нужно сюда передавать категорию из базы данных
                            color = Grey95, // цвет текста
                            style = Typography.bodySmall, // стиль текста
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(15.dp)) // скругляем углы
                                .background(PrimaryColor) // цвет кнопки
                                .padding(
                                    horizontal = 10.dp, // отступ слева/справа внутри категории
                                    vertical = 5.dp // отступ снизу / сверху внутри категории
                                ),

                            )


                        // ИКОНКА ИЗБРАННОЕ

                        Icon(
                            imageVector = Icons.Filled.Favorite, // сам векторный файл иконки
                            contentDescription = "Иконка добавить в избранные", // описание для слабовидящих
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {

                                    if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified){
                                        act.databaseManager.favIconMeeting(meetingItem.key!!){
                                            if (it){
                                                favIconColor.value = Grey10
                                                act.databaseManager.removeFavouriteMeeting(meetingItem.key!!){
                                                    if (it){
                                                        Toast.makeText(act, "Удалено из избранных", Toast.LENGTH_SHORT).show()

                                                    }
                                                }
                                            } else {
                                                favIconColor.value = PrimaryColor
                                                act.databaseManager.addFavouriteMeeting(meetingItem.key!!){

                                                    if (it){
                                                        Toast.makeText(act, "Добавлено в избранные", Toast.LENGTH_SHORT).show()

                                                    }

                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(act, "Сначала зарегайся", Toast.LENGTH_SHORT).show()
                                    }



                            }, // размер иконки
                            tint = favIconColor.value // Цвет иконки
                        )
                    }

                    // Заголовок, дата, время

                    Column() {

                        // Заголовок мероприятия

                        if (meetingItem.headline!=null){
                            Text(
                                text = meetingItem.headline, // title - заголовок мероприятия, который должен приходить из базы данных
                                color = Grey00, // цвет текста
                                style = Typography.titleLarge // стиль текста
                            )
                        }

                        // Панель ДАТА И ВРЕМЯ

                        Column(
                            modifier = Modifier.padding(top=10.dp), // отступ сверху
                        ) {

                            // Вывод даты мероприятия
                            // date - дата проведения мероприятия.
                            // date надо будет передавать из базы данных. Иконку не надо, уже передаю

                            IconText(R.drawable.ic_calendar, meetingItem.data!!)


                            // Вывод времени начала мероприятия
                            // time - время начала мероприятия.
                            // time надо будет передавать из базы данных. Иконку не надо, уже передаю

                            IconText(
                                R.drawable.ic_time,
                                if (meetingItem.finishTime == ""){
                                    "Начало в ${meetingItem.startTime}"
                                }else {
                                    "${meetingItem.startTime} - ${meetingItem.finishTime}"
                                })

                            IconText(
                                R.drawable.ic_tenge,
                                if(meetingItem.price == ""){
                                    stringResource(id = R.string.free_price)
                                }else {
                                    "${meetingItem.price} тенге"
                                })


                        }
                    }
                }

            }
        }
    }
}
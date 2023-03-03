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
import kz.dvij.dvij_compose3.firebase.MeetingsCardClass
import kz.dvij.dvij_compose3.navigation.MEETING_VIEW
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingsCard(val act: MainActivity) {

    @Composable
    fun MeetingCard (navController: NavController, meetingItem: MeetingsCardClass, meetingKey: MutableState<String>) {

        val linear = Brush.verticalGradient(listOf(Grey100_50, Grey100)) // Переменная полупрозрачного градиента

        val iconFavColor = remember{ mutableStateOf(Grey10) } // Переменная цвета иконки ИЗБРАННОЕ

        // Считываем с базы данных - добавлено ли это мероприятие в избранное?

        act.meetingDatabaseManager.favIconMeeting(meetingItem.key!!){
            // Если колбак тру, то окрашиваем иконку в нужный цвет
            if (it){
                iconFavColor.value = PrimaryColor
            } else {
                // Если колбак фалс, то в обычный цвет
                iconFavColor.value = Grey10
            }
        }

        // ------ САМ КОНТЕНТ КАРТОЧКИ ----------

        Card(
            modifier = Modifier
                .fillMaxWidth() // растягиваем карточку на всю ширину экрана
                .padding(vertical = 10.dp) // отступ от краев экрана
                .clickable {
                    // При клике на карточку - передаем на Main Activity meetingKey. Ключ берем из дата класса мероприятия

                    meetingKey.value = meetingItem.key

                    // так же при нажатии регистрируем счетчик просмотров - добавляем 1 просмотр

                    act.meetingDatabaseManager.viewCounterMeeting(meetingItem.key) {

                        // если колбак тру, то счетчик успешно сработал, значит переходим на страницу мероприятия
                        if (it) {
                            navController.navigate(MEETING_VIEW)
                        }
                    }
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

                        // ------- КНОПКА КАТЕГОРИИ -----------
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


                        // ---- ИКОНКА ИЗБРАННОЕ ---------

                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Favorite, // сам векторный файл иконки
                            contentDescription = stringResource(id = R.string.cd_fav_icon), // описание для слабовидящих
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    // --- Если клиент авторизован, проверяем, добавлено ли уже в избранное это мероприятие -----
                                    // Если не авторизован, условие else

                                    if (act.mAuth.currentUser != null && act.mAuth.currentUser!!.isEmailVerified) {
                                        act.meetingDatabaseManager.favIconMeeting(meetingItem.key) {

                                            // Если уже добавлено в избранные, то при нажатии убираем из избранных

                                            if (it) {

                                                // Убираем из избранных
                                                act.meetingDatabaseManager.removeFavouriteMeeting(meetingItem.key) { result ->

                                                    // Если пришел колбак, что успешно

                                                    if (result) {

                                                        iconFavColor.value = Grey00 // При нажатии окрашиваем кнопку в темно-серый

                                                        // Выводим ТОСТ
                                                        Toast.makeText(act,act.getString(R.string.delete_from_fav),
                                                            Toast.LENGTH_SHORT).show()
                                                    }
                                                }

                                            } else {

                                                // Если не добавлено в избранные, то при нажатии добавляем в избранные

                                                act.meetingDatabaseManager.addFavouriteMeeting(meetingItem.key) { inFav ->

                                                    // Если пришел колбак, что успешно

                                                    if (inFav) {

                                                        iconFavColor.value = PrimaryColor // Окрашиваем кнопку в главный цвет

                                                        // Выводим ТОСТ
                                                        Toast.makeText(act,act.getString(R.string.add_to_fav),
                                                            Toast.LENGTH_SHORT).show()

                                                    }
                                                }
                                            }
                                        }

                                    } else {

                                        // Если пользователь не авторизован, то ему выводим ТОСТ

                                        Toast
                                            .makeText(act, act.getString(R.string.need_reg_meeting_to_fav), Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }, // размер иконки
                            tint = iconFavColor.value // Цвет иконки
                        )

                    }

                    // ------ ЗАГОЛОВОК, ДАТА, ВРЕМЯ, ЦЕНА -----------

                    Column() {

                        // Заголовок мероприятия

                        if (meetingItem.headline!=null){
                            Text(
                                text = meetingItem.headline, // заголовок мероприятия, который должен приходить из базы данных
                                color = Grey00, // цвет текста
                                style = Typography.titleLarge // стиль текста
                            )
                        }

                        // -------- ДАТА, ВРЕМЯ, ЦЕНА ----------

                        Column(
                            modifier = Modifier.padding(top=10.dp), // отступ сверху
                        ) {

                            // ДАТА

                            IconText(R.drawable.ic_calendar, meetingItem.data!!)


                            // ВРЕМЯ

                            val startIn = act.getString(R.string.cm_start_in) // Переменная слова Начинаем в

                            IconText(
                                R.drawable.ic_time,
                                if (meetingItem.finishTime == ""){
                                    "$startIn ${meetingItem.startTime}" // текст - начинаем в и время начала
                                }else {
                                    "${meetingItem.startTime} - ${meetingItem.finishTime}" // просто время начала и время окончания мероприятия
                                })


                            // ЦЕНА

                            val tenge = act.getString(R.string.ss_tenge)

                            IconText(
                                R.drawable.ic_tenge,
                                if(meetingItem.price == ""){
                                    stringResource(id = R.string.free_price)
                                } else {
                                    "${meetingItem.price} $tenge"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
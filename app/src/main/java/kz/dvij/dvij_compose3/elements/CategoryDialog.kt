package kz.dvij.dvij_compose3.elements

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.dialogs.CategoriesList
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.ui.theme.*

class CategoryDialog (act: MainActivity) {

    val categoryDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("CategoryList") // Создаем ПАПКУ В БД для мероприятий

    var chosenCategory: CategoriesList = CategoriesList("Выберите категорию", "Default") // категория по умолчанию (не выбрана категория)
    var chosenCity = CitiesList("Выберите город", "default_city") // задаем выбранный город по умолчанию. Это Алматы

    fun readCategoryDataFromDb(categoriesList: MutableState<List<CategoriesList>>){

        // Обращаемся к базе данных и вешаем слушатель addListenerForSingleValueEvent.
        // У этого слушателя функция такая - он один раз просматривает БД при запуске и все, ждет, когда мы его снова запустим
        // Есть другие типы слушателей, которые работают в режиме реального времени, т.е постоянно обращаются к БД
        // Это приводит к нагрузке на сервер и соответственно будем платить за большое количество обращений к БД

        // У самого объекта слушателя ValueEventListener есть 2 стандартные функции - onDataChange и onCancelled
        // их нужно обязательно добавить и заполнить нужным кодом

        categoryDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val categoryArray = ArrayList<CategoriesList>()

                // запускаем цикл и пытаемся добраться до наших данных
                // snapshot - по сути это JSON файл, в котором нам нужно как в папках прописать путь до наших данных
                // ниже используем итератор и некст для того, чтобы войти в папку, название которой мы не знаем
                // так как на нашем пути куча уникальных ключей, которые мы не можем знать
                // где знаем точный путь (как в "meetingData"), там пишем .child()

                // добираемся

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val category = item.child("CategoryData").getValue(CategoriesList::class.java)


                        /*item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .children.iterator().next() // добираемся до следующей папки внутри УКМероприятия - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ*/

                    if (category != null && category.categoryName != "Выберите категорию") {categoryArray.add(category)}


                }

                if (categoryArray.isEmpty()){
                    categoriesList.value = listOf(chosenCategory)
                } else {
                    categoriesList.value = categoryArray
                }

            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }



    // -------- КНОПКА ВЫБОРА КАТЕГОРИИ -----------

    @Composable
    fun categorySelectButton(onClick: ()-> Unit): CategoriesList {

        Button(
            onClick = {
                onClick()
            },

            // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            border = BorderStroke(
                width = if (chosenCategory.categoryName == "Выберите категорию") {
                    2.dp
                } else {
                    0.dp
                }, color = if (chosenCategory.categoryName == "Выберите категорию") {
                    Grey60
                } else {
                    Grey95
                }
            ),

            // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (chosenCategory.categoryName == "Выберите категорию") {
                    Grey95
                } else {
                    PrimaryColor
                },
                contentColor = if (chosenCategory.categoryName == "Выберите категорию") {
                    Grey60
                } else {
                    Grey100
                },
            ),
            shape = RoundedCornerShape(50) // скругленные углы кнопки
        ) {

            Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

            Text(
                    text = chosenCategory.categoryName!!, // текст кнопки
                    style = Typography.labelMedium, // стиль текста
                    color = if (chosenCategory.categoryName == "Выберите категорию") {
                        Grey60
                    } else {
                        Grey100
                    }
                )

        }
        return chosenCategory
    }

    // ----- ДИАЛОГ ВЫБОРА КАТЕГОРИИ

    @Composable
    fun CategoryChooseDialog(categoriesList: MutableState<List<CategoriesList>> ,onDismiss: () -> Unit) {

        // Создаем список категорий



        /*val categoriesList = mutableListOf<CategoriesList>(

            CategoriesList.ConcertsCat,
            CategoriesList.HobieCat
        )*/

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
                        text = stringResource(id = R.string.cat_default), // текст заголовка
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


                // ---------- СПИСОК Категорий -------------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth() // занять ширину
                        .background(
                            Grey100, // цвет фона
                            shape = RoundedCornerShape(10.dp) // скругление углов
                        )
                        .padding(20.dp), // отступ
                    verticalArrangement = Arrangement.spacedBy(20.dp)

                ) {

                    items(categoriesList.value) { category ->

                        Log.d("MyLog", "Data: ${category.categoryName}")
                        // ------------ строка с названием категории -------------

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // действие на нажатие на элемент
                                chosenCategory =
                                    category // выбранная категория теперь та, которую выбрали, а не по умолчанию
                                onDismiss() // закрыть диалог
                            }
                        ) {

                                Text(
                                    text = category.categoryName!!, // само название категории
                                    color = Grey00, // цвет текста
                                    style = Typography.bodyMedium // стиль текста
                                )

                        }
                    }
                }
            }
        }
    }

}
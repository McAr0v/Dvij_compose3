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
import kz.dvij.dvij_compose3.ui.theme.*

class CategoryDialog (val act: MainActivity) {

    private val meetingCategoryDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("CategoryList") // Создаем ПАПКУ В БД для мероприятий

    private val placeCategoryDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("PlaceCategoryList") // Создаем ПАПКУ В БД для Заведений

    private val stockCategoryDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("StockCategoryList") // Создаем ПАПКУ В БД для АКЦИЙ


    fun readMeetingCategoryDataFromDb(categoriesList: MutableState<List<CategoriesList>>){

        // Обращаемся к базе данных и вешаем слушатель addListenerForSingleValueEvent.
        // У этого слушателя функция такая - он один раз просматривает БД при запуске и все, ждет, когда мы его снова запустим
        // Есть другие типы слушателей, которые работают в режиме реального времени, т.е постоянно обращаются к БД
        // Это приводит к нагрузке на сервер и соответственно будем платить за большое количество обращений к БД

        // У самого объекта слушателя ValueEventListener есть 2 стандартные функции - onDataChange и onCancelled
        // их нужно обязательно добавить и заполнить нужным кодом

        meetingCategoryDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val categoryArray = ArrayList<CategoriesList>() // Создаем пустой список категорий

                // добираемся до списка категорий

                for (item in snapshot.children){

                    // создаем переменную category, в которую в конце поместим наш ДАТАКЛАСС с категорией с БД

                    // пишем путь до категорий и забираем данные согласно Дата Класса категорий
                    val category = item.child("CategoryData").getValue(CategoriesList::class.java)

                    // если категория не нал и название категории не "Выберите категорию", то добавить в наш список
                    if (category != null && category.categoryName != act.resources.getString(R.string.cm_no_category)) {categoryArray.add(category)}

                }

                if (categoryArray.isEmpty()){
                    // если список пустой, то тогда в список добавить созданную категорию по умолчанию
                    categoriesList.value = listOf(CategoriesList("Выбери категорию", "Default"))
                } else {
                    // если список не пустой, то тогда добавить данные с БД
                    categoriesList.value = categoryArray
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }

    fun readPlaceCategoryDataFromDb(categoriesList: MutableState<List<CategoriesList>>){

        placeCategoryDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val categoryArray = ArrayList<CategoriesList>() // Создаем пустой список категорий

                // добираемся до списка категорий

                for (item in snapshot.children){

                    // создаем переменную category, в которую в конце поместим наш ДАТАКЛАСС с категорией с БД

                    // пишем путь до категорий и забираем данные согласно Дата Класса категорий
                    val category = item.child("CategoryData").getValue(CategoriesList::class.java)

                    // если категория не нал и название категории не "Выберите категорию", то добавить в наш список
                    if (category != null && category.categoryName != act.resources.getString(R.string.cm_no_category)) {categoryArray.add(category)}

                }

                if (categoryArray.isEmpty()){
                    // если список пустой, то тогда в список добавить созданную категорию по умолчанию
                    categoriesList.value = listOf(CategoriesList("Выбери категорию", "Default"))
                } else {
                    // если список не пустой, то тогда добавить данные с БД
                    categoriesList.value = categoryArray
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }

    fun readStockCategoryDataFromDb(categoriesList: MutableState<List<CategoriesList>>){

        stockCategoryDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val stockArray = ArrayList<CategoriesList>() // Создаем пустой список акций

                // добираемся до списка акций

                for (item in snapshot.children){

                    // создаем переменную category, в которую в конце поместим наш ДАТАКЛАСС с категорией с БД

                    // пишем путь до категорий и забираем данные согласно Дата Класса категорий
                    val category = item.child("CategoryData").getValue(CategoriesList::class.java)

                    // если категория не нал и название категории не "Выберите категорию", то добавить в наш список
                    if (category != null && category.categoryName != act.resources.getString(R.string.cm_no_category)) {stockArray.add(category)}

                }

                if (stockArray.isEmpty()){
                    // если список пустой, то тогда в список добавить созданную категорию по умолчанию
                    categoriesList.value = listOf(CategoriesList("Выбери категорию", "Default"))
                } else {
                    // если список не пустой, то тогда добавить данные с БД
                    categoriesList.value = stockArray
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }



    // -------- КНОПКА ВЫБОРА КАТЕГОРИИ -----------

    @Composable
    fun categorySelectButton(categoryName: MutableState<String>, onClick: ()-> Unit): String {

        Button(
            onClick = {
                onClick() // действие на нажатие (передаем извне, когда обращаемся к функции)
            },

            // ----- ГРАНИЦА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            border = BorderStroke(
                width = if (categoryName.value != "" && categoryName.value != "null" && categoryName.value != "Выбери категорию") {
                    0.dp
                } else {
                    2.dp
                }, color = if (categoryName.value != "" && categoryName.value != "null" && categoryName.value != "Выбери категорию") {
                    Grey95
                } else {
                    Grey60
                }
            ),

            // ----- ЦВЕТА В ЗАВИСИМОСТИ ОТ СОСТОЯНИЯ КАТЕГОРИИ ------

            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (categoryName.value != "" && categoryName.value != "null" && categoryName.value != "Выбери категорию") {
                    PrimaryColor
                } else {
                    Grey95

                },
                contentColor = if (categoryName.value != "" && categoryName.value != "null" && categoryName.value != "Выбери категорию") {
                    Grey100
                } else {
                    Grey60

                },
            ),
            shape = RoundedCornerShape(50) // скругленные углы кнопки
        ) {

            Spacer(modifier = Modifier.height(30.dp)) // ЧТОБЫ КНОПКА БЫЛА ПОБОЛЬШЕ

            if (categoryName.value != "" && categoryName.value != "null" && categoryName.value != "Выбери категорию") {

                Text(
                    text = categoryName.value, // текст кнопки
                    style = Typography.labelMedium, // стиль текста
                    color = if (categoryName.value == "Выбери категорию") {
                        Grey60
                    } else {
                        Grey100
                    }
                )

            } else {

                Text(
                    text = "Выбери категорию", // текст кнопки
                    style = Typography.labelMedium, // стиль текста
                    color = Grey60
                )
            }
        }
        return categoryName.value
    }


    // ----- ДИАЛОГ ВЫБОРА КАТЕГОРИИ

    @Composable
    fun CategoryChooseDialog(categoryName: MutableState<String>, categoriesList: MutableState<List<CategoriesList>>, onDismiss: () -> Unit) {

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
                    verticalArrangement = Arrangement.spacedBy(20.dp) // расстояние между элементами

                ) {

                    items(categoriesList.value) { category ->

                        // ------------ строка с названием категории -------------

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // действие на нажатие на элемент
                                categoryName.value =
                                    category.categoryName.toString() // выбранная категория теперь та, которую выбрали, а не по умолчанию
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
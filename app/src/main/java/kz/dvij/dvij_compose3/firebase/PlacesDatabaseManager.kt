package kz.dvij.dvij_compose3.firebase

import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity

class PlacesDatabaseManager (val act: MainActivity) {

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    val placeDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Places") // Создаем ПАПКУ В БД для Заведений

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    val default = PlacesAdsClass (
        placeDescription = "def"
    )

    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ ЗАВЕДЕНИЯ --------

    suspend fun publishPlace(filledPLace: PlacesAdsClass, callback: (result: Boolean)-> Unit){

        placeDatabase // записываем в базу данных
            .child(filledPLace.placeKey ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ ЗАВЕДЕНИЯ
            .child("info") // помещаем данные в папку info
            .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего заведение
            .child("placeData") // помещаем в папку
            .setValue(filledPLace).addOnCompleteListener {

                if (it.isSuccessful) {
                    // если мероприятие опубликовано, возвращаем колбак тру
                    callback (true)

                } else {
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ ЗАВЕДЕНИЙ С БАЗЫ ДАННЫХ --------

    fun readPlaceDataFromDb(placeList: MutableState<List<PlacesAdsClass>>){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val placeArray = ArrayList<PlacesAdsClass>() // создаем пустой список заведений

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // следующая папка с информацией о заведении
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("placeData") // добираесся до следующей папки внутри УКПользователя - папка с данными о заведении
                        .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведений

                    if (place != null) {placeArray.add(place)} // если заведение не пустое, добавляем в список

                }

                if (placeArray.isEmpty()){
                    placeList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем мероприятие по умолчанию
                } else {
                    placeList.value = placeArray // если добавились мероприятия в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // --- ФУНКЦИЯ ДОБАВЛЕНИЯ ЗАВЕДЕНИЯ В ИЗБРАННОЕ ---------

    fun addFavouritePlace(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        act.mAuth.uid?.let {
            placeDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем заведения
                .child("AddedToFavorites") // заходим в папку добавивших в избранное
                .child(it) // заходим в папку с названием как наш ключ
                .setValue(act.mAuth.uid) // записываем наш ключ
        }?.addOnCompleteListener {
            // слушаем выполнение. Если успешно сделано, то...
            if (it.isSuccessful){
                // возвращаем колбак ТРУ
                callback (true)
            }
        }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ ЗАВЕДЕНИЯ ИЗ ИЗБРАННОГО ----------

    fun removeFavouritePlace(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        act.mAuth.uid?.let {
            placeDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем заведения
                .child("AddedToFavorites") // заходим в папку добавивших в избранное
                .child(it) // заходим в папку с названием как наш ключ
                .removeValue() // удаляем значение
        }?.addOnCompleteListener {
            // слушаем выполнение. Если успешно сделано, то...
            if (it.isSuccessful){
                // возвращаем колбак ТРУ
                callback (true)
            }
        }
    }

    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ - ЗАВЕДЕНИЕ В ИЗБРАННОМ УЖЕ ИЛИ НЕТ

    fun favIconPlace(key: String, callback: (result: Boolean)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    if (auth.uid !=null) {

                        // считываем данные заведения

                        val place = item
                            .child("info")
                            .children.iterator().next()
                            .child("placeData")
                            .getValue(PlacesAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        val placeFav = item
                            .child("AddedToFavorites")
                            .child(auth.uid!!)
                            .getValue(String::class.java)

                        // проверка заведения на Null

                        if (place != null) {

                            // если ключ заведения равен переданному ключу
                            if (place.placeKey == key) {

                                // если в списке добавивших в избранное есть мой ключ, то вернуть колбак тру
                                if (placeFav == auth.uid){
                                    callback (true)
                                } else {
                                    // если в списке нет, то фалс
                                    callback (false)
                                }
                            }
                        }
                    }
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }

    // --- ФУНКЦИЯ СЧЕТЧИКА ПРОСМОТРА ЗАВЕДЕНИЯ ---------

    fun viewCounterPlace(key: String, callback: (result: Boolean)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // считываем данные заведения

                    val place = item
                        .child("info")
                        .children.iterator().next()
                        .child("placeData")
                        .getValue(PlacesAdsClass::class.java)

                    // считываем список добавивших в избранное пользователей

                    var placeCount = item
                        .child("viewCounter")
                        .child("viewCounter")
                        .getValue(Int::class.java)

                    // проверка заведения на Null

                    if (place != null) {

                        // если ключ заведения равен переданному ключу
                        if (place.placeKey == key) {

                            // Если счетчик просмотров не нал
                            if (placeCount != null) {
                                placeCount ++ // добавляем к счетчику 1

                                // Перезаписываем новое значение счетчика

                                placeDatabase
                                    .child(key)
                                    .child("viewCounter")
                                    .child("viewCounter")
                                    .setValue(placeCount)

                                callback (true) // возвращаем колбак тру

                            } else {
                                // если счетчик еще не создан, то создаем и устанавливаем значение 1
                                placeDatabase
                                    .child(key)
                                    .child("viewCounter")
                                    .child("viewCounter")
                                    .setValue(1)

                                callback (true) // возвращаем колбак тру
                            }
                        }
                    }
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ МОИХ ЗАВЕДЕНИЙ --------

    fun readPlaceMyDataFromDb(placesList: MutableState<List<PlacesAdsClass>>){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val placeArray = ArrayList<PlacesAdsClass>()

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    if (auth.uid !=null) {
                        val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info") // следующая папка с информацией о заведении
                            .child(auth.uid!!) // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                            .child("placeData") // добираемся до следующей папки внутри УКПользователя - папка с данными о заведении
                            .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведения

                        if (place != null) {placeArray.add(place)} //  если заведение не нал, то добавляем в список-черновик
                    }
                }

                if (placeArray.isEmpty()){
                    placesList.value = listOf(default) // если в списке ничего нет, то добавляем заведение по умолчанию
                } else {
                    placesList.value = placeArray // если список не пустой, то возвращаем мои заведения с БД
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ ИЗБРАННЫХ Заведений --------

    fun readPlacesFavDataFromDb(placesList: MutableState<List<PlacesAdsClass>>){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val placeArray = ArrayList<PlacesAdsClass>()

                for (item in snapshot.children){

                    // Считываем каждое заведение для сравнения

                    if (auth.uid !=null) {
                        val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА заведения
                            .child("info") // папка с информацией о заведении
                            .children.iterator().next() // папка уникального ключа пользователя. Пропускаем ее
                            .child("placeData") // добираесся до следующей папки внутри УКПользователя - папка с данными о заведении
                            .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведения

                        // Считываем папку, в которую попадают ключи добавивших в избранное

                        val placeFav = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА заведения
                            .child("AddedToFavorites") // Папка со списком добавивших в избранное
                            .child(auth.uid!!) // ищем папку с ключом пользователя
                            .getValue(String::class.java) // забираем данные из БД если они есть

                        // сравниваем ключи

                        if (placeFav == auth.uid) {
                            // если ключи совпали, проверяем заведение на нал
                            if (place != null) {

                                //  если заведение не нал, то добавляем в список-черновик
                                placeArray.add(place)
                            }
                        }
                    }
                }

                if (placeArray.isEmpty()){
                    placesList.value = listOf(default) // если в списке ничего нет, то добавляем заведение по умолчанию
                } else {
                    placesList.value = placeArray // если список не пустой, то возвращаем избранные заведения с БД
                }
            }
            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ МЕРОПРИЯТИИ --------

    fun readOnePlaceFromDataBase(placeInfo: MutableState<PlacesAdsClass>, key: String, callback: (result: List<Int>)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА заведения
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("placeData") // добираесся до следующей папки внутри - папка с данными о заведении
                        .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведеиня

                    // считываем данные для счетчика - количество добавивших в избранное
                    val placeFav = item.child("AddedToFavorites").childrenCount

                    // считываем данные для счетчика - количество просмотров объявления
                    var placeViewCount = item
                        .child("viewCounter").child("viewCounter").getValue(Int::class.java)

                    // если мероприятие не нал и ключ завдения совпадает с ключем из БД, то...
                    if (place != null && place.placeKey == key) {

                        // передаем в переменную нужное заведение

                        placeInfo.value = place

                        // если счетчик просмотров заведение не нал, то...
                        if (placeViewCount != null) {
                            // Возвращаем калбак в виде списка счетчиков
                            callback (listOf(placeFav.toInt(), placeViewCount.toInt()))
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
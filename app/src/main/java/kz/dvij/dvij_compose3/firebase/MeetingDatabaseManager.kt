package kz.dvij.dvij_compose3.firebase

import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity

class MeetingDatabaseManager (private val activity: MainActivity) {

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    val meetingDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Meetings") // Создаем ПАПКУ В БД для мероприятий

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    val default = MeetingsAdsClass (
        description = "Default"
    )

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ МЕРОПРИЯТИИ ВОЗВРАЩАЮЩАЯ СПИСОК СЧЕТЧИКИ МЕРОПРИЯТИЯ --------

    fun readOneMeetingFromDataBase(meetingInfo: MutableState<MeetingsAdsClass>, key: String, callback: (result: List<Int>)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    // считываем данные для счетчика - количество добавивших в избранное
                    val meetingFav = item.child("AddedToFavorites").childrenCount

                    // считываем данные для счетчика - количество просмотров объявления
                    val meetingCount = item
                        .child("viewCounter").child("viewCounter").getValue(Int::class.java)

                    // если мероприятие не нал и ключ мероприятия совпадает с ключем из БД, то...
                    if (meeting != null && meeting.key == key) {

                        // передаем в переменную нужное мероприятие

                        meetingInfo.value = meeting

                        // если счетчик просмотров мероприятия не нал, то...
                        if (meetingCount != null) {
                            // Возвращаем калбак в виде списка счетчиков
                            callback (listOf(meetingFav.toInt(), meetingCount.toInt()))
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ МЕРОПРИЯТИИ, ВОЗВРАЩАЮЩАЯ ДАТА КЛАСС --------

    fun readOneMeetingFromDBReturnClass(key: String, callback: (result: MeetingsAdsClass)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    // если мероприятие не нал и ключ мероприятия совпадает с ключем из БД, то...
                    if (meeting != null && meeting.key == key) {

                        // передаем в переменную нужное мероприятие

                        callback (meeting)


                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ МЕРОПРИЯТИЙ С БАЗЫ ДАННЫХ --------

    fun readMeetingDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>){

        // Обращаемся к базе данных и вешаем слушатель addListenerForSingleValueEvent.
        // У этого слушателя функция такая - он один раз просматривает БД при запуске и все, ждет, когда мы его снова запустим
        // Есть другие типы слушателей, которые работают в режиме реального времени, т.е постоянно обращаются к БД
        // Это приводит к нагрузке на сервер и соответственно будем платить за большое количество обращений к БД

        // У самого объекта слушателя ValueEventListener есть 2 стандартные функции - onDataChange и onCancelled
        // их нужно обязательно добавить и заполнить нужным кодом

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>() // создаем пустой список мероприятий

                // запускаем цикл и пытаемся добраться до наших данных
                // snapshot - по сути это JSON файл, в котором нам нужно как в папках прописать путь до наших данных
                // ниже используем итератор и некст для того, чтобы войти в папку, название которой мы не знаем
                // так как на нашем пути куча уникальных ключей, которые мы не можем знать
                // где знаем точный путь (как в "meetingData"), там пишем .child()

                // добираемся

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // следующая папка с информацией о мероприятии
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null) {meetingArray.add(meeting)} // если мероприятие не пустое, добавляем в список

                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray // если добавились мероприятия в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }



    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ МОИХ МЕРОПРИЯТИЙ --------

    fun readMeetingMyDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>()

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    if (auth.uid !=null) {
                        val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info") // следующая папка с информацией о мероприятии
                            .child(auth.uid!!) // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                            .child("meetingData") // добираемся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                            .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                        if (meeting != null) {meetingArray.add(meeting)} //  если мероприятие не нал, то добавляем в список-черновик
                    }
                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в списке ничего нет, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray // если список не пустой, то возвращаем мои мероприятия с БД
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ ИЗБРАННЫХ МЕРОПРИЯТИЙ --------

    fun readMeetingFavDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>()

                for (item in snapshot.children){

                    // Считываем каждое мероприятие для сравнения

                    if (auth.uid !=null) {
                        val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info") // папка с информацией о мероприятии
                            .children.iterator().next() // папка уникального ключа пользователя. Пропускаем ее
                            .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                            .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                        // Считываем папку, в которую попадают ключи добавивших в избранное

                        val meetingFav = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("AddedToFavorites") // Папка со списком добавивших в избранное
                            .child(auth.uid!!) // ищем папку с ключом пользователя
                            .getValue(String::class.java) // забираем данные из БД если они есть

                        // сравниваем ключи мероприятия

                        if (meetingFav == auth.uid) {
                            // если ключи совпали, проверяем мероприятие на нал
                            if (meeting != null) {

                                //  если мероприятие не нал, то добавляем в список-черновик
                                meetingArray.add(meeting)
                            }
                        }
                    }
                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в списке ничего нет, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray // если список не пустой, то возвращаем избранные мероприятия с БД
                }
            }
            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }


    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ МЕРОПРИЯТИЯ --------

    suspend fun publishMeeting(filledMeeting: MeetingsAdsClass, callback: (result: Boolean)-> Unit){

        meetingDatabase // записываем в базу данных
            .child(filledMeeting.key ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ МЕРОПРИЯТИЯ
            .child("info") // помещаем данные в папку info
            .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего мероприятие
            .child("meetingData") // помещаем в папку
            .setValue(filledMeeting).addOnCompleteListener {

                if (it.isSuccessful) {
                    // если мероприятие опубликовано, возвращаем колбак тру
                    callback (true)

                } else {
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // --- ФУНКЦИЯ ДОБАВЛЕНИЯ МЕРОПРИЯТИЯ В ИЗБРАННОЕ ---------

    fun addFavouriteMeeting(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        activity.mAuth.uid?.let {
            meetingDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем мероприятия
                .child("AddedToFavorites") // заходим в папку добавших в избранное
                .child(it) // заходим в папку с названием как наш ключ
                .setValue(activity.mAuth.uid) // записываем наш ключ
        }?.addOnCompleteListener {
            // слушаем выполнение. Если успешно сделано, то...
            if (it.isSuccessful){
                // возвращаем колбак ТРУ
                callback (true)
            }
        }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ МЕРОПРИЯТИЯ ИЗ ИЗБРАННОГО ----------

    fun removeFavouriteMeeting(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        activity.mAuth.uid?.let {
            meetingDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем мероприятия
                .child("AddedToFavorites") // заходим в папку добавших в избранное
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

    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ - МЕРОПРИЯТИЕ В ИЗБРАННОМ УЖЕ ИЛИ НЕТ

    fun favIconMeeting(key: String, callback: (result: Boolean)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    if (auth.uid !=null) {

                        // считываем данные мероприятия

                        val meeting = item
                            .child("info")
                            .children.iterator().next()
                            .child("meetingData")
                            .getValue(MeetingsAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        val meetingFav = item
                            .child("AddedToFavorites")
                            .child(auth.uid!!)
                            .getValue(String::class.java)

                        // проверка мероприятия на Null

                        if (meeting != null) {

                            // если ключ мероприятия равен переданному ключу
                            if (meeting.key == key) {

                                // если в списке добавивших в избранное есть мой ключ, то вернуть колбак тру
                                if (meetingFav == auth.uid){
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

    // --- ФУНКЦИЯ СЧЕТЧИКА ПРОСМОТРА МЕРОПРИЯТИЯ ---------

    fun viewCounterMeeting(key: String, callback: (result: Boolean)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                        // считываем данные мероприятия

                        val meeting = item
                            .child("info")
                            .children.iterator().next()
                            .child("meetingData")
                            .getValue(MeetingsAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        var meetingCount = item
                            .child("viewCounter")
                            .child("viewCounter")
                            .getValue(Int::class.java)

                        // проверка мероприятия на Null

                        if (meeting != null) {

                            // если ключ мероприятия равен переданному ключу
                            if (meeting.key == key) {

                                // Если счетчик просмотров не нал
                                if (meetingCount != null) {
                                    meetingCount ++ // добавляем к счетчику 1

                                    // Перезаписываем новое значение счетчика

                                    meetingDatabase
                                        .child(key)
                                        .child("viewCounter")
                                        .child("viewCounter")
                                        .setValue(meetingCount)

                                    callback (true) // возвращаем колбак тру

                                } else {
                                    // если счетчик еще не создан, то создаем и устанавливаем значение 1
                                    meetingDatabase
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

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ МЕРОПРИЯТИЙ КОНКРЕТНОГО ЗАВЕДЕНИЯ С БАЗЫ ДАННЫХ --------

    fun readMeetingInPlaceDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>, placeKey: String){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>() // создаем пустой список мероприятий

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // следующая папка с информацией о мероприятии
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null && meeting.placeKey == placeKey) {meetingArray.add(meeting)} // если мероприятие не пустое, добавляем в список

                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray // если добавились мероприятия в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ КОЛИЧЕСТВА МЕРОПРИЯТИЙ КОНКРЕТНОГО ЗАВЕДЕНИЯ С БАЗЫ ДАННЫХ --------

    fun readMeetingCounterInPlaceDataFromDb(placeKey: String, callback: (meetingsCounter: Int)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>() // создаем пустой список мероприятий

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // следующая папка с информацией о мероприятии
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null && meeting.placeKey == placeKey) {meetingArray.add(meeting)} // если мероприятие не пустое, добавляем в список

                }

                if (meetingArray.isEmpty()){
                    callback (0) // выдаем колбак, что список пустой
                } else {
                    callback (meetingArray.size) // выдаем размер списка мероприятий
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }



}
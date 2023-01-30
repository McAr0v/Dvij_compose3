package kz.dvij.dvij_compose3.firebase

import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity

class DatabaseManager (private val activity: MainActivity) {

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    val meetingDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Meetings") // Создаем ПАПКУ В БД для мероприятий



    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    val default = MeetingsAdsClass (
        description = "def"
    )

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ МЕРОПРИЯТИИ --------

    fun readOneMeetingFromDataBase(meetingInfo: MutableState<MeetingsAdsClass>, key: String, callback: (result: List<Int>)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info")
                        .children.iterator().next() // добираемся до следующей папки внутри УКМероприятия - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    val meetingFav = item
                        .child("AddedToFavorites").childrenCount

                    var meetingCount = item
                        .child("viewCounter").child("viewCounter").getValue(Int::class.java)

                    if (meeting != null && meeting.key == key) {
                        meetingInfo.value = meeting
                        if (meetingCount != null) {
                            callback (listOf(meetingFav.toInt(), meetingCount.toInt()))
                        }
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

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
                        .child("info")
                        .children.iterator().next() // добираемся до следующей папки внутри УКМероприятия - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null) {meetingArray.add(meeting)} // если мероприятие не пустое, добавляем в список

                    //Log.d("MyLog", "Data: $item")
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

        // Обращаемся к базе данных и вешаем слушатель addListenerForSingleValueEvent.
        // У этого слушателя функция такая - он один раз просматривает БД при запуске и все, ждет, когда мы его снова запустим
        // Есть другие типы слушателей, которые работают в режиме реального времени, т.е постоянно обращаются к БД
        // Это приводит к нагрузке на сервер и соответственно будем платить за большое количество обращений к БД

        // У самого объекта слушателя ValueEventListener есть 2 стандартные функции - onDataChange и onCancelled
        // их нужно обязательно добавить и заполнить нужным кодом

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>()

                // запускаем цикл и пытаемся добраться до наших данных
                // snapshot - по сути это JSON файл, в котором нам нужно как в папках прописать путь до наших данных
                // ниже используем итератор и некст для того, чтобы войти в папку, название которой мы не знаем
                // так как на нашем пути куча уникальных ключей, которые мы не можем знать
                // где знаем точный путь (как в "meetingData"), там пишем .child()

                // добираемся

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    if (auth.uid !=null) {
                        val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info").child(auth.uid!!) // добираемся до следующей папки внутри УКМероприятия - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                            .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                            .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                        if (meeting != null) {meetingArray.add(meeting)} //  если мероприятие не нал, то добавляем в список-черновик
                    }

                    //Log.d("MyLog", "Data: $item")
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

        // Обращаемся к базе данных и вешаем слушатель addListenerForSingleValueEvent.
        // У этого слушателя функция такая - он один раз просматривает БД при запуске и все, ждет, когда мы его снова запустим
        // Есть другие типы слушателей, которые работают в режиме реального времени, т.е постоянно обращаются к БД
        // Это приводит к нагрузке на сервер и соответственно будем платить за большое количество обращений к БД

        // У самого объекта слушателя ValueEventListener есть 2 стандартные функции - onDataChange и onCancelled
        // их нужно обязательно добавить и заполнить нужным кодом

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>()

                // запускаем цикл и пытаемся добраться до наших данных
                // snapshot - по сути это JSON файл, в котором нам нужно как в папках прописать путь до наших данных
                // ниже используем итератор и некст для того, чтобы войти в папку, название которой мы не знаем
                // так как на нашем пути куча уникальных ключей, которые мы не можем знать
                // где знаем точный путь (как в "meetingData"), там пишем .child()

                // добираемся

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    if (auth.uid !=null) {
                        val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info")
                            .children.iterator().next().child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                            .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                        val meetingFav = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("AddedToFavorites")
                            .child(auth.uid!!) // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                            .getValue(String::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                        if (meetingFav == auth.uid) {
                            if (meeting != null) {
                                meetingArray.add(meeting)
                            }
                        } //  если мероприятие не нал, то добавляем в список-черновик
                    }

                    //Log.d("MyLog", "Data: $item")
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



    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ МЕРОПРИЯТИЯ --------

    suspend fun publishMeeting(filledMeeting: MeetingsAdsClass, callback: (result: Boolean)-> Unit){

        meetingDatabase // записываем в базу данных
            .child(
                filledMeeting.key ?: "empty"
            ) // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ МЕРОПРИЯТИЯ
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

        activity.mAuth.uid?.let {
            meetingDatabase
                .child(key)
                .child("AddedToFavorites")
                .child(it)
                .setValue(activity.mAuth.uid)
        }?.addOnCompleteListener {

            if (it.isSuccessful){
                callback (true)
            }

        }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ МЕРОПРИЯТИЯ ИЗ ИЗБРАННОГО ----------

    fun removeFavouriteMeeting(key: String, callback: (result: Boolean)-> Unit){

        activity.mAuth.uid?.let {
            meetingDatabase
                .child(key)
                .child("AddedToFavorites")
                .child(it)
                .removeValue()
        }?.addOnCompleteListener {

            if (it.isSuccessful){
                callback (true)
            }

        }
    }

    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ - МЕРОПРИЯТИЕ В ИЗБРАННОМ УЖЕ ИЛИ НЕТ

    fun favIconMeeting(key: String, callback: (result: Boolean)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{


            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    if (auth.uid !=null) {

                        // считываем данные мероприятия

                        val meeting = item
                            .child("info")
                            .children.iterator().next().child("meetingData")
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


            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                        // считываем данные мероприятия

                        val meeting = item
                            .child("info")
                            .children.iterator().next().child("meetingData")
                            .getValue(MeetingsAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        var meetingCount = item
                            .child("viewCounter").child("viewCounter").getValue(Int::class.java)

                        // проверка мероприятия на Null

                        if (meeting != null) {

                            // если ключ мероприятия равен переданному ключу
                            if (meeting.key == key) {

                                if (meetingCount != null) {
                                    meetingCount ++
                                    meetingDatabase.child(key).child("viewCounter").child("viewCounter").setValue(meetingCount)
                                    callback (true)
                                } else {

                                    meetingDatabase.child(key).child("viewCounter").child("viewCounter").setValue(1)
                                    callback (true)
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



}
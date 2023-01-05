package kz.dvij.dvij_compose3.firebase

import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity

class DatabaseManager (val activity: MainActivity) {



    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    val meetingDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Meetings") // Создаем ПАПКУ В БД для мероприятий

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ



    // --- ФУНКЦИЯ ПУБЛИКАЦИИ МЕРОПРИЯТИЙ -------

    fun publishMeeting(meeting: MeetingsAdsClass){

        // ОПЕРАТОР ЭЛВИСА ?: ГОВОРИТ О ТОМ, ЧТО ЕСЛИ KEY БУДЕТ NULL ТО ТОГДА ПОДСТАВИТЬ ЗНАЧЕНИЕ СПРАВА ОТ ОПЕРАТОРА, т.е "empty"

        // .child создает дополнительный путь в разделе базы данных Meetings, чтобы у каждого мероприятия был свой
        // уникальный ключ и мы случайно не перезаписывали мероприятия

        if (auth.uid != null) {
            meetingDatabase // записываем в базу данных
                .child(meeting.category ?: "Без категории") // создаем путь категорий
                .child(meeting.key ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ МЕРОПРИЯТИЯ
                .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего мероприятие
                .child("meetingData")
                .setValue(meeting) // записываем само значение. Передаем целый класс
        }
    }


    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ МЕРОПРИЯТИЙ С БАЗЫ ДАННЫХ --------

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

                val meetingArray = ArrayList<MeetingsAdsClass>()

                // запускаем цикл и пытаемся добраться до наших данных
                // snapshot - по сути это JSON файл, в котором нам нужно как в папках прописать путь до наших данных
                // ниже используем итератор и некст для того, чтобы войти в папку, название которой мы не знаем
                // так как на нашем пути куча уникальных ключей, которые мы не можем знать
                // где знаем точный путь (как в "meetingData"), там пишем .child()

                // добираемся

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. Тут будут названия категорий
                        .children.iterator().next() // добираемся до следующей папки внутри категорий - путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .children.iterator().next() // добираемся до следующей папки внутри УКМероприятия - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null) {meetingArray.add(meeting)}

                    //Log.d("MyLog", "Data: $item")
                }

                meetingsList.value = meetingArray

            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }
}
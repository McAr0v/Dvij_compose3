package kz.dvij.dvij_compose3.firebase

import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BugsDatabaseManager {

    val bugsDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Bugs") // Создаем ПАПКУ В БД для ошибок


    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ БАГА --------

    fun publishBug(filledBug: BugsAdsClass, callback: (result: Boolean)-> Unit){

        bugsDatabase // записываем в базу данных
            .child(filledBug.ticketNumber ?: "empty")
            .child("bugData")
            .setValue(filledBug).addOnCompleteListener {

                if (it.isSuccessful) {

                    callback (true)

                } else {
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ ----------

    fun deleteBug(ticketNumber: String, callback: (result: Boolean)-> Unit){

        bugsDatabase // записываем в базу данных
            .child(ticketNumber)
            .child("bugData")
            .removeValue()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    callback (true)

                } else {
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }

    fun readBugListFromDb(
        bugList: MutableState<List<BugsAdsClass>>,
        status: String
    ){

        bugsDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val bugArray = ArrayList<BugsAdsClass>()

                for (item in snapshot.children) {

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val bug =
                        item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                            .child("bugData") // следующая папка с информацией об акции
                            .getValue(BugsAdsClass::class.java) // забираем данные из БД в виде нашего класса акций

                    if (bug != null && status == "Все сообщения"){

                        bugArray.add(bug)

                    } else if (bug != null && bug.status == status){

                        bugArray.add(bug)

                    }

                }

                if (bugArray.isEmpty()){
                    bugList.value = listOf(BugsAdsClass()) // если в списке ничего нет, то добавляем акцию по умолчанию
                } else {
                    bugList.value = bugArray // если список не пустой, то возвращаем избранные акции с БД
                }

            }

            override fun onCancelled(error: DatabaseError) {}

            }
        )

    }

    fun readBugStatusFromDb(
        ticketNumber: String,
        callback: (result: String) -> Unit
    ){

        bugsDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val bugArray = ArrayList<BugsAdsClass>()

                for (item in snapshot.children) {

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val bug =
                        item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                            .child("bugData") // следующая папка с информацией об акции
                            .getValue(BugsAdsClass::class.java) // забираем данные из БД в виде нашего класса акций

                    if (bug != null && ticketNumber == bug.ticketNumber){

                        callback (bug.status!!)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        }
        )

    }



}
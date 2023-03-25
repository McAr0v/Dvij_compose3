package kz.dvij.dvij_compose3.firebase

import androidx.compose.runtime.MutableState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CallbackDatabaseManager {

    val callbackDatabase = FirebaseDatabase // обращаемся к БД
    .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
    .getReference("Callbacks") // Создаем ПАПКУ В БД для ошибок


    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ БАГА --------

    fun publishCallback(filledCallback: CallbackAdsClass, callback: (result: Boolean)-> Unit){

        callbackDatabase // записываем в базу данных
            .child(filledCallback.ticketNumber ?: "empty")
            .child("callbackData")
            .setValue(filledCallback).addOnCompleteListener {

                if (it.isSuccessful) {

                    callback (true)

                } else {
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ ----------

    fun deleteCallback(ticketNumber: String, callback: (result: Boolean)-> Unit){

        callbackDatabase // записываем в базу данных
            .child(ticketNumber)
            .child("callbackData")
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

    fun readCallbackListFromDb(
        callbackList: MutableState<List<CallbackAdsClass>>,
        status: String
    ){

        callbackDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val callbackArray = ArrayList<CallbackAdsClass>()

                for (item in snapshot.children) {

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val callbackItem =
                        item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                            .child("callbackData") // следующая папка с информацией об акции
                            .getValue(CallbackAdsClass::class.java) // забираем данные из БД в виде нашего класса акций

                    if (callbackItem != null && status == "Все сообщения"){

                        callbackArray.add(callbackItem)

                    } else if (callbackItem != null && callbackItem.status == status){

                        callbackArray.add(callbackItem)

                    }

                }

                if (callbackArray.isEmpty()){
                    callbackList.value = listOf(CallbackAdsClass()) // если в списке ничего нет, то добавляем акцию по умолчанию
                } else {
                    callbackList.value = callbackArray // если список не пустой, то возвращаем избранные акции с БД
                }

            }

            override fun onCancelled(error: DatabaseError) {}

        }
        )

    }

}
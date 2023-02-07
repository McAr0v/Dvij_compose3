package kz.dvij.dvij_compose3.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity

class StockDatabaseManager (val act: MainActivity) {

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    private val stockDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Stock") // Создаем ПАПКУ В БД для акций

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО Акцию

    val default = PlacesAdsClass (
        placeDescription = "def"
    )

    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ АКЦИЙ --------

    suspend fun publishStock(filledStock: StockAdsClass, callback: (result: Boolean)-> Unit){

        stockDatabase // записываем в базу данных
            .child(filledStock.keyStock ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ АКЦИИ
            .child("info") // помещаем данные в папку info
            .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего акцию
            .child("stockData") // помещаем в папку
            .setValue(filledStock).addOnCompleteListener {

                if (it.isSuccessful) {
                    // если мероприятие опубликовано, возвращаем колбак тру
                    callback (true)

                } else {
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }


}
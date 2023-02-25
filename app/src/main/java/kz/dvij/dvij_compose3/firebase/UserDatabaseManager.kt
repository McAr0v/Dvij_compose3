package kz.dvij.dvij_compose3.firebase

import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity

class UserDatabaseManager (val act: MainActivity) {

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    private val userDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Users") // Создаем ПАПКУ В БД для пользователей

    private val auth = Firebase.auth // переменная УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ

    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ ПОЛЬЗОВАТЕЛЯ --------

    fun publishUser(filledUser: UserInfoClass, callback: (result: Boolean)-> Unit){

        userDatabase // записываем в базу данных
            .child(auth.uid ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ ПОЛЬЗОВАТЕЛЯ
            .child("userData") // помещаем в папку
            .setValue(filledUser).addOnCompleteListener {

                if (it.isSuccessful) {
                    // если информация о пользователе опубликована, возвращаем колбак тру
                    callback (true)

                } else {
                    // если не опубликована, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О ПОЛЬЗОВАТЕЛЕ --------

    fun readOneUserFromDataBase(userInfo: MutableState<UserInfoClass>, key: String, callback: (result: Boolean) -> Unit){

        userDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    val user = item
                        .child("userData")
                        .getValue(UserInfoClass::class.java) // забираем данные из БД в виде нашего класса пользователя

                    // если пользователь не нал и ключ пользователя совпадает с ключем из БД, то...
                    if (user != null && user.userKey == key) {

                        // передаем в переменную пользователя
                        callback (true)
                        userInfo.value = user

                    } else {

                        callback (false)

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
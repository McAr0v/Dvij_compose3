package kz.dvij.dvij_compose3.firebase

import com.google.firebase.database.FirebaseDatabase

class DatabaseManager {
    private val database = FirebaseDatabase.getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app").reference

    fun publishAd(){
        database.setValue("Hola2")
    }
}
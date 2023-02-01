package kz.dvij.dvij_compose3.callandwhatsapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.constants.WHATSAPP_URL

class CallAndWhatsapp(val act: MainActivity) {

    // ---- ФУНКЦИЯ НАБОРА НОМЕРА ПРИ НАЖАТИИ НА КНОПКУ -------

    fun makeACall (context: Context, phoneNumber: String){

        // Проверяем, выданы ли права на доступ к телефону

        if (ContextCompat.checkSelfPermission(act, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){

            // если права даны, то запускаем набор номера

            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel: $phoneNumber")
            act.startActivity(intent)

        } else {
            // если не выданы права, запрашиваем права
            ActivityCompat.requestPermissions(act, arrayOf(android.Manifest.permission.CALL_PHONE), 777)
        }
    }

    // ------ ФУНКЦИЯ ПЕРЕХОДА НА ДИАЛОГ В ВАТСАП -------

    fun writeInWhatsapp (context: Context, phoneNumber: String){
        val url = WHATSAPP_URL + phoneNumber // генерируем ссылку на Whatsapp
        val intent = Intent(Intent.ACTION_VIEW) // Инициализируем интент
        intent.data = Uri.parse(url) // Парсим URL, который создали выше
        act.startActivity(intent) // запускаем активити на переход в Ватсапп
    }

    // ------ ФУНКЦИЯ ПЕРЕХОДА В ИНСТАГРАМ ИЛИ ТЕЛЕГРАМ -------

    fun goToInstagramOrTelegram (url: String){

        val intent = Intent(Intent.ACTION_VIEW) // Инициализируем интент
        intent.data = Uri.parse(url) // Парсим URL, который создали выше
        act.startActivity(intent) // запускаем активити на переход в Ватсапп
    }
}
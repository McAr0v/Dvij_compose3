package kz.dvij.dvij_compose3.callandwhatsapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.constants.INSTAGRAM_URL
import kz.dvij.dvij_compose3.constants.PHONE_URL
import kz.dvij.dvij_compose3.constants.TELEGRAM_URL
import kz.dvij.dvij_compose3.constants.WHATSAPP_URL

class CallAndWhatsapp(val act: MainActivity) {

    // ---- ФУНКЦИЯ НАБОРА НОМЕРА ПРИ НАЖАТИИ НА КНОПКУ -------

    fun makeACall (phoneNumber: String){

        // Проверяем, выданы ли права на доступ к телефону

        if (ContextCompat.checkSelfPermission(act, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){

            // если права даны, то запускаем набор номера

            val intent = Intent(Intent.ACTION_CALL)
            val url = PHONE_URL + "+7${phoneNumber}"
            intent.data = Uri.parse(url)
            act.startActivity(intent)

        } else {
            // если не выданы права, запрашиваем права
            ActivityCompat.requestPermissions(act, arrayOf(android.Manifest.permission.CALL_PHONE), 777)
        }
    }

    // ------ ФУНКЦИЯ ПЕРЕХОДА НА ДИАЛОГ В ВАТСАП -------

    fun writeInWhatsapp (phoneNumber: String){
        val url = WHATSAPP_URL + "+7${phoneNumber}" // генерируем ссылку на Whatsapp
        val intent = Intent(Intent.ACTION_VIEW) // Инициализируем интент
        intent.data = Uri.parse(url) // Парсим URL, который создали выше
        act.startActivity(intent) // запускаем активити на переход в Ватсапп
    }

    // ------ ФУНКЦИЯ ПЕРЕХОДА В ИНСТАГРАМ ИЛИ ТЕЛЕГРАМ -------

    fun goToInstagramOrTelegram (url: String, social: String){

        if (social == TELEGRAM_URL){

            val intent = Intent(Intent.ACTION_VIEW) // Инициализируем интент
            intent.data = Uri.parse("$TELEGRAM_URL$url") // Парсим URL, который создали выше
            act.startActivity(intent) // запускаем активити на переход в Ватсапп

        } else if (social == INSTAGRAM_URL){

            val intent = Intent(Intent.ACTION_VIEW) // Инициализируем интент
            intent.data = Uri.parse("$INSTAGRAM_URL$url") // Парсим URL, который создали выше
            act.startActivity(intent) // запускаем активити на переход в Ватсапп

        }

    }
}
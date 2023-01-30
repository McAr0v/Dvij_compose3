package kz.dvij.dvij_compose3.functions

import android.net.Uri
import kz.dvij.dvij_compose3.R

// ----- ФУНКЦИЯ ПРОВЕРКИ ОБЯЗАТЕЛЬНЫХ ДЛЯ ЗАПОЛНЕНИЯ ПОЛЕЙ -----------

fun checkDataOnCreateMeeting (image1: Uri?, headline: String, phone: String, dataResult: String, timeStartResult: String, description: String, category: String, city: String ): Int{

    // Результат в числах, так как возвращает id сообщения для вывода тоста

    var result: Int = 0

    if (image1 == null || headline == "" || phone == "+77" || dataResult == "" || timeStartResult == "" || description == "" || category == "Выберите категорию" || city == "Выберите город" ) {


            if (image1 == null) {
            result = R.string.cm_no_image
            }

            if (headline == "") {
                result = R.string.cm_no_headline
            }

            if (phone == "+77") {
                result = R.string.cm_no_phone
            }

            if (dataResult == "") {
                result = R.string.cm_no_date
            }

            if (timeStartResult == "") {
                result = R.string.cm_no_start_time
            }
            if (description == "") {
                result = R.string.cm_no_description
            }
            if (category == "Выберите категорию") {
                result = R.string.cm_no_category
            }

            if (city == "Выберите город") {
                result = R.string.cm_no_city
            }

        }
    return result
}
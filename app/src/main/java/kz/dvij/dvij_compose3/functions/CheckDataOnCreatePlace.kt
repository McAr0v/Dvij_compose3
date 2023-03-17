package kz.dvij.dvij_compose3.functions

import android.net.Uri
import kz.dvij.dvij_compose3.R

// ----- ФУНКЦИЯ ПРОВЕРКИ ОБЯЗАТЕЛЬНЫХ ДЛЯ ЗАПОЛНЕНИЯ ПОЛЕЙ -----------

fun checkDataOnCreatePlace (
    image1: Uri?,
    headline: String,
    phone: String,
    description: String,
    category: String,
    city: String,
    address: String,
    imageUriFromDb: String



): Int{

    // Результат в числах, так как возвращает id сообщения для вывода тоста

    var result: Int = 0

    if (image1 == null && imageUriFromDb == "") {

        result = R.string.cp_no_image

    }

    if (image1 == null || headline == "" || phone == "+77" || description == "" || category == "Выбери категорию" || city == "Выбери город" || address == "" ) {

        if (headline == "") {
            result = R.string.cp_no_place_name
        }

        if (phone == "+77") {
            result = R.string.cm_no_phone
        }

        if (description == "") {
            result = R.string.cm_no_description
        }
        if (category == "Выбери категорию") {
            result = R.string.cm_no_category
        }

        if (city == "Выбери город") {
            result = R.string.cm_no_city
        }

        if (address == "") {
            result = R.string.cp_no_address
        }

    }
    return result
}
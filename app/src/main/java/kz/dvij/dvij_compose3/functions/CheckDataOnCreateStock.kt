package kz.dvij.dvij_compose3.functions

import android.net.Uri
import kz.dvij.dvij_compose3.R

// ----- ФУНКЦИЯ ПРОВЕРКИ ОБЯЗАТЕЛЬНЫХ ДЛЯ ЗАПОЛНЕНИЯ ПОЛЕЙ -----------

fun checkDataOnCreateStock (
    image1: Uri?,
    headline: String,
    startDay: String,
    finishDay: String,
    description: String,
    category: String,
    city: String,
    placeKey: String?,
    inputHeadlinePlace: String,
    inputAddressPlace: String,
    imageUriFromDb: String
): Int{

    // Результат в числах, так как возвращает id сообщения для вывода тоста

    var result: Int = 0

    if(placeKey == "" || placeKey == "null" || placeKey == null){

        if (inputAddressPlace == "" || inputHeadlinePlace == ""){

            result = R.string.cm_choose_place

        }
    }

    if (image1 == null && imageUriFromDb == "") {

        result = R.string.cs_no_image

    }

    if (image1 == null && imageUriFromDb == "Empty") {

        result = R.string.cs_no_image

    }

    if (image1 == null || headline == "" || startDay == "" || finishDay == "" || description == "" || category == "Выбери категорию" || city == "Выбери город") {

        if (headline == "") {
            result = R.string.cs_no_headline
        }

        if (startDay == "") {
            result = R.string.cs_no_start_day
        }

        if (finishDay == "") {
            result = R.string.cs_no_finish_day
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

    }
    return result
}
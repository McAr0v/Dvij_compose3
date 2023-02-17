package kz.dvij.dvij_compose3.functions

import android.net.Uri
import kz.dvij.dvij_compose3.R

// ----- ФУНКЦИЯ ПРОВЕРКИ ОБЯЗАТЕЛЬНЫХ ДЛЯ ЗАПОЛНЕНИЯ ПОЛЕЙ -----------

fun checkDataOnCreateMeeting (
    image1: Uri?,
    headline: String,
    phone: String,
    dataResult: String,
    timeStartResult: String,
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

        result = R.string.cm_no_image

    }

    if (image1 == null || headline == "" || phone == "+77" || dataResult == "" || timeStartResult == "" || description == "" || category == "Выбери категорию" || city == "Выбери город" ) {




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
            if (category == "Выбери категорию") {
                result = R.string.cm_no_category
            }

            if (city == "Выбери город") {
                result = R.string.cm_no_city
            }

        }
    return result
}
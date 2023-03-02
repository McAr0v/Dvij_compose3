package kz.dvij.dvij_compose3.filters

import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.firebase.MeetingsAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesAdsClass
import kz.dvij.dvij_compose3.firebase.PlacesCardClass
import kz.dvij.dvij_compose3.firebase.StockAdsClass

class FilterFunctions(val act: MainActivity) {

    // ------ ФУНКЦИЯ СОЗДАНИЯ СТРОКИ ФИЛЬТРА МЕРОПРИЯТИЙ -----

    fun createMeetingFilter (city: String = "Выбери город", category: String = "Выбери категорию", date: String = "Выбери дату"): String{

        val stringBuilder = StringBuilder()
        val arrayTempFilter = listOf(city, category, date)

        for ((index, string) in arrayTempFilter.withIndex()){

            stringBuilder.append(string)
            if (index != arrayTempFilter.size - 1) stringBuilder.append("_")

        }

        return stringBuilder.toString()

    }


    // ------ ФУНКЦИЯ СОЗДАНИЯ СТРОКИ ФИЛЬТРА АКЦИЙ -----

    fun createStockFilter (city: String = "Выбери город", category: String = "Выбери категорию", startDate: String = "Выбери дату", finishDate: String = "Выбери дату"): String{

        val stringBuilder = StringBuilder()
        val arrayTempFilter = listOf(city, category, startDate, finishDate)

        for ((index, string) in arrayTempFilter.withIndex()){

            stringBuilder.append(string)
            if (index != arrayTempFilter.size - 1) stringBuilder.append("_")

        }

        return stringBuilder.toString()

    }

    // ------ ФУНКЦИЯ СОЗДАНИЯ СТРОКИ ФИЛЬТРА Заведения -----

    fun createPlaceFilter (city: String = "Выбери город", category: String = "Выбери категорию"): String{

        val stringBuilder = StringBuilder()
        val arrayTempFilter = listOf(city, category)

        for ((index, string) in arrayTempFilter.withIndex()){

            stringBuilder.append(string)
            if (index != arrayTempFilter.size - 1) stringBuilder.append("_")

        }

        return stringBuilder.toString()

    }

    // ----- Функция разделения фильтра на отдельные элементы -----

    fun splitFilter(filter: String): List<String> {

        return filter.split("_")
    }

    // ---- Функция разделения даты на отдельные элементы ----
    fun splitData(date: String): List<String> {

        return date.split(" ")
    }

    // Функция превращения даты из БД в нужный формат

    fun getSplitDataFromDb(date: String): String {
        val splitDate = date.split(" ") // разбиваем дату
        return getDataNumber(splitDate) // переконвертируем в нужный формат и возвращаем
    }

    // ---- Функция превращения ТЕКСТА МЕСЯЦА в ЧИСЛО

    fun monthToNumber (month: String): String{

        return when (month) {

            act.getString(R.string.january) -> "01"
            act.getString(R.string.february) -> "02"
            act.getString(R.string.march) -> "03"
            act.getString(R.string.april) -> "04"
            act.getString(R.string.may) -> "05"
            act.getString(R.string.june) -> "06"
            act.getString(R.string.july) -> "07"
            act.getString(R.string.august) -> "08"
            act.getString(R.string.september) -> "09"
            act.getString(R.string.october) -> "10"
            act.getString(R.string.november) -> "11"
            act.getString(R.string.december) -> "12"
            else -> month

        }
    }

    fun numberToNameOfMonth (month: Int): String {

        return when (month+1) {
            1 -> act.getString(R.string.january)
            2 -> act.getString(R.string.february)
            3 -> act.getString(R.string.march)
            4 -> act.getString(R.string.april)
            5 -> act.getString(R.string.may)
            6 -> act.getString(R.string.june)
            7 -> act.getString(R.string.july)
            8 -> act.getString(R.string.august)
            9 -> act.getString(R.string.september)
            10 -> act.getString(R.string.october)
            11 -> act.getString(R.string.november)
            else -> act.getString(R.string.december)
        }

    }

    // ---- Функция добавления 0 в начале числа

    private fun numberWithZero (number: String): String {

        return when (number){

            "1" -> "01"
            "2" -> "02"
            "3" -> "03"
            "4" -> "04"
            "5" -> "05"
            "6" -> "06"
            "7" -> "07"
            "8" -> "08"
            "9" -> "09"
            else -> number
        }

    }

    // ---- Функция получения числа даты в нужном формате для фильтра -----

    fun getDataNumber(list: List<String>): String {

        val year = list[2] // год берем как есть
        val day = numberWithZero(list[0]) // день превращаем в нужный формат с нулем в начале
        val month = monthToNumber(list[1]) // названия месяцев превращаем в цифры

        return year + month + day
    }

    // ---- Функция проверки - попадает ли наша дата МЕРОПРИЯТИЯ в диапазон фильтра, заданного пользователем ----

    fun checkMeetingDatePeriod (meetingDate: String, startFilterDay: String, finishFilterDay: String, callback: (result: Boolean) -> Unit) {

        if (
            startFilterDay.toInt() <= meetingDate.toInt()
            && meetingDate.toInt() <= finishFilterDay.toInt()
        ) {

            callback (true)

        }
    }

    // ---- Функция проверки - попадает ли наша дата АКЦИИ в диапазон фильтра, заданного пользователем ----

    fun checkStockDatePeriod (stockStartDate: String, stockFinishDate: String, startFilterDay: String, finishFilterDay: String, callback: (result: Boolean) -> Unit) {

        if (
            startFilterDay.toInt() > stockFinishDate.toInt()
            || stockStartDate.toInt() > finishFilterDay.toInt()
        ) {
            callback (false)
        } else {
            callback (true)
        }
    }

    // ---- ФУНКЦИЯ СОРТИРОВКИ СПИСКА МЕРОПРИЯТИЙ В ЗАВИСИМОСТИ ОТ ВЫБРАННОГО ТИПА -----

    fun sortedMeetingList (meetingsList: List<MeetingsAdsClass>, query: String): List<MeetingsAdsClass>{

        return when (query) {

            "Сначала новые" -> meetingsList.sortedBy { it.createdTime }.asReversed() // по дате создания
            "Сначала старые" -> meetingsList.sortedBy { it.createdTime } // по дате создания
            "Дата: По возрастанию" -> meetingsList.sortedBy { it.dateInNumber } // по дате проведения
            "Дата: По убыванию" -> meetingsList.sortedBy { it.dateInNumber }.asReversed() // по дате проведения
            else -> meetingsList.sortedBy { it.createdTime }.asReversed() // по умолчанию - сначала новые
        }

    }

    // ---- ФУНКЦИЯ СОРТИРОВКИ СПИСКА АКЦИЙ В ЗАВИСИМОСТИ ОТ ВЫБРАННОГО ТИПА -----

    fun sortedStockList (stockList: List<StockAdsClass>, query: String): List<StockAdsClass>{

        return when (query) {

            "Сначала новые" -> stockList.sortedBy { it.createTime }.asReversed() // по дате создания
            "Сначала старые" -> stockList.sortedBy { it.createTime } // по дате создания
            "По дате начала акции: По возрастанию" -> stockList.sortedBy { it.startDateNumber } // по дате проведения
            "По дате начала акции: По убыванию" -> stockList.sortedBy { it.startDateNumber }.asReversed() // по дате проведения
            "По дате завершения акции: По возрастанию" -> stockList.sortedBy { it.finishDateNumber } // по дате проведения
            "По дате завершения акции: По убыванию" -> stockList.sortedBy { it.finishDateNumber }.asReversed() // по дате проведения
            else -> stockList.sortedBy { it.createTime }.asReversed() // по умолчанию - сначала новые
        }

    }

    // ---- ФУНКЦИЯ СОРТИРОВКИ СПИСКА Заведений В ЗАВИСИМОСТИ ОТ ВЫБРАННОГО ТИПА -----

    fun sortedPlaceList (placesList: List<PlacesCardClass>, query: String): List<PlacesCardClass>{

        return when (query) {

            // вместо it.createTime вставить функцию считывания информации о количестве добавивших в избранное
            "Сначала новые" -> placesList.sortedBy { it.createPlaceTime }.asReversed() // по дате создания
            "Сначала старые" -> placesList.sortedBy { it.createPlaceTime } // по дате создания
            "По популярности: По возрастанию" -> placesList.sortedBy { it.favCounter } // по дате проведения
            "По популярности: По убыванию" -> placesList.sortedBy { it.favCounter }.asReversed() // по дате проведения
            "По количеству мероприятий: По возрастанию" -> placesList.sortedBy { it.meetingCounter } // по дате проведения
            "По количеству мероприятий: По убыванию" -> placesList.sortedBy { it.meetingCounter }.asReversed() // по дате проведения
            "По количеству акций: По возрастанию" -> placesList.sortedBy { it.stockCounter } // по дате проведения
            "По количеству акций: По убыванию" -> placesList.sortedBy { it.stockCounter }.asReversed() // по дате проведения
            else -> placesList.sortedBy { it.createPlaceTime }.asReversed() // по умолчанию - сначала новые
        }

    }

    // ----- Функция определения типа фильтра для корректной фильтрации МЕРОПРИЯТИЙ-------

    fun getTypeOfMeetingFilter(inputList: List<String>): String{

        var result = ""

        val city = inputList[0]
        val category = inputList[1]
        val date = inputList[2]

        if (city != "Выбери город" && category != "Выбери категорию" && date != "Выбери дату"){

            result = "cityCategoryDate"

        } else if (city != "Выбери город" && category != "Выбери категорию"){

            result = "cityCategory"

        } else if (city != "Выбери город" && date != "Выбери дату"){

            result = "cityDate"

        } else if (city != "Выбери город"){

            result = "city"

        } else if (category != "Выбери категорию" && date != "Выбери дату"){

            result = "categoryDate"

        } else if (category != "Выбери категорию"){

            result = "category"

        } else if (date != "Выбери дату"){

            result = "date"

        } else {

            result = "noFilter"

        }

        return result
    }

    // ----- Функция определения типа фильтра для корректной фильтрации МЕРОПРИЯТИЙ-------

    fun getTypeOfPlaceFilter(inputList: List<String>): String{

        var result = ""

        val city = inputList[0]
        val category = inputList[1]

        if (city != "Выбери город" && category != "Выбери категорию"){

            result = "cityCategory"

        } else if (city != "Выбери город"){

            result = "city"

        } else if (category != "Выбери категорию"){

            result = "category"

        } else {

            result = "noFilter"

        }

        return result
    }

    // ----- Функция определения типа фильтра для корректной фильтрации АКЦИЙ ------

    fun getTypeOfStockFilter(inputList: List<String>): String{

        var result = ""

        val city = inputList[0]
        val category = inputList[1]
        val startDate = inputList[2]
        val finishDate = inputList[3]

        if (city != "Выбери город" && category != "Выбери категорию" && startDate != "Выбери дату" && finishDate != "Выбери дату"){

            result = "cityCategoryStartFinish"

        } else if (city != "Выбери город" && category != "Выбери категорию" && startDate != "Выбери дату"){

            result = "cityCategoryStart"

        } else if (city != "Выбери город" && category != "Выбери категорию" && finishDate != "Выбери дату"){

            result = "cityCategoryFinish"

        } else if (city != "Выбери город" && startDate != "Выбери дату" && finishDate != "Выбери дату"){

            result = "cityStartFinish"

        } else if (city != "Выбери город" && category != "Выбери категорию"){

            result = "cityCategory"

        } else if (city != "Выбери город" && finishDate != "Выбери дату"){

            result = "cityFinish"

        } else if (city != "Выбери город" && startDate != "Выбери дату"){

            result = "cityStart"

        } else if (city != "Выбери город"){

            result = "city"

        } else if (category != "Выбери категорию" && startDate != "Выбери дату" && finishDate != "Выбери дату"){

            result = "categoryStartFinish"

        } else if (category != "Выбери категорию" && startDate != "Выбери дату"){

            result = "categoryStart"

        } else if (category != "Выбери категорию" && finishDate != "Выбери дату"){

            result = "categoryFinish"

        } else if (startDate != "Выбери дату" && finishDate != "Выбери дату"){

            result = "startFinish"

        } else if (category != "Выбери категорию"){

            result = "category"

        } else if (finishDate != "Выбери дату"){

            result = "finish"

        } else if (startDate != "Выбери дату"){

            result = "start"

        } else {

            result = "noFilter"

        }

        return result
    }


}
package kz.dvij.dvij_compose3.firebase

import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.filters.FilterFunctions
import kz.dvij.dvij_compose3.filters.FilterStockClass
import kz.dvij.dvij_compose3.navigation.STOCK_ROOT
import kz.dvij.dvij_compose3.pickers.convertMillisecondsToDate
import kz.dvij.dvij_compose3.pickers.getTodayInMilliseconds

class StockDatabaseManager (val act: MainActivity) {

    private val filterFunctions = FilterFunctions(act)

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    val stockDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Stock") // Создаем ПАПКУ В БД для акций

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО Акцию

    val default = StockAdsClass (
        description = "Default"
    )

    val defaultStockCard = StockCardClass (
        description = "Default"
    )

    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ АКЦИЙ --------

    fun publishStock(filledStock: StockAdsClass, callback: (result: Boolean)-> Unit){

        // ---- СОЗДАЕМ ФИЛЬТРЫ ДЛЯ ВЫГРУЗКИ В БД ------

        val filledFilter = FilterStockClass (

            cityCategoryStartFinish = filterFunctions.createStockFilter(filledStock.city!!, category = filledStock.category!!, startDate = filledStock.startDate!!, finishDate = filledStock.finishDate!!),
            cityCategoryStart = filterFunctions.createStockFilter(filledStock.city, category = filledStock.category, startDate = filledStock.startDate),
            cityCategoryFinish = filterFunctions.createStockFilter(filledStock.city, category = filledStock.category, finishDate = filledStock.finishDate),
            cityStartFinish = filterFunctions.createStockFilter(filledStock.city, startDate = filledStock.startDate, finishDate = filledStock.finishDate),
            cityCategory = filterFunctions.createStockFilter(filledStock.city, category = filledStock.category),
            cityFinish = filterFunctions.createStockFilter(filledStock.city, finishDate = filledStock.finishDate),
            cityStart = filterFunctions.createStockFilter(filledStock.city, startDate = filledStock.startDate),
            city = filterFunctions.createStockFilter(filledStock.city),
            categoryStartFinish = filterFunctions.createStockFilter(category = filledStock.category, startDate = filledStock.startDate, finishDate = filledStock.finishDate),
            categoryStart = filterFunctions.createStockFilter(category = filledStock.category, startDate = filledStock.startDate),
            categoryFinish = filterFunctions.createStockFilter(category = filledStock.category, finishDate = filledStock.finishDate),
            startFinish = filterFunctions.createStockFilter(startDate = filledStock.startDate, finishDate = filledStock.finishDate),
            category = filterFunctions.createStockFilter(category = filledStock.category),
            finish = filterFunctions.createStockFilter(finishDate = filledStock.finishDate),
            start = filterFunctions.createStockFilter(startDate = filledStock.startDate),
            noFilter = filterFunctions.createStockFilter()

        )

        stockDatabase // записываем в базу данных
            .child(filledStock.keyStock ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ АКЦИИ
            .child("info") // помещаем данные в папку info
            .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего акцию
            .child("stockData") // помещаем в папку
            .setValue(filledStock).addOnCompleteListener {

                if (it.isSuccessful) {

                    // если акция опубликована, запускаем публикацию фильтра
                    stockDatabase
                        .child(filledStock.keyStock ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ АКЦИИ
                        .child("filterInfo") // помещаем данные в папку filterInfo
                        .setValue(filledFilter).addOnCompleteListener { filterIsPublish ->

                            if (filterIsPublish.isSuccessful){

                                if (filledStock.keyPlace != null && filledStock.keyPlace != "null" && filledStock.keyPlace != ""){

                                    addStockToPlace(filledStock.keyStock!!, filledStock.keyPlace){ addedToPlace ->

                                        // Если добавилось, то тру
                                        if (addedToPlace) {

                                            callback (true)

                                        } else {
                                            // если нет то фолс
                                            callback (false)

                                        }

                                    }

                                } else {
                                    // если не заведение, а просто введен адрес вручную, то просто возвращаем результат как тру
                                    callback (true)
                                }

                            } else {
                                // если не опубликован фильтр, то возвращаем фалс
                                callback (false)
                            }
                        }

                } else {
                    // если не опубликована, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ АКЦИИ ----------

    fun deleteStock(key: String, imageUrl: String, callback: (result: Boolean)-> Unit){

        act.photoHelper.deleteStockImage(imageUrl = imageUrl){ deletedImage ->

            if (deletedImage){

                Log.d ("MyLog", "Картинка АКЦИИ была успешно автоматически удалена")

                // если ключ пользователя не будет нал, то выполнится функция удаления самого мероприятия

                act.mAuth.uid?.let {
                    stockDatabase // обращаемся к БД
                        .child(key) // заходим в папку с уникальным ключем акции
                        .removeValue() // удаляем значение
                }?.addOnCompleteListener {
                    // слушаем выполнение. Если успешно сделано, то...
                    if (it.isSuccessful){
                        // возвращаем колбак ТРУ
                        callback (true)
                    }
                }
            }
        }
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ ФИЛЬТРОВАННЫХ АКЦИЙ С БАЗЫ ДАННЫХ --------

    fun readFilteredStockDataFromDb(
        stockList: MutableState<List<StockCardClass>>,
        cityForFilter: MutableState<String>,
        stockCategoryForFilter: MutableState<String>,
        stockStartDateForFilter: MutableState<String>,
        stockFinishDateForFilter: MutableState<String>,
        stockSortingForFilter: MutableState<String>,
    ){

        // Определяем тип фильтра
        val typeFilter = filterFunctions.getTypeOfStockFilter(listOf(cityForFilter.value, stockCategoryForFilter.value, stockStartDateForFilter.value, stockFinishDateForFilter.value))

        // Создаем фильтр из пришедших выбранных пользователем данных
        var filter = filterFunctions.createStockFilter(city = cityForFilter.value, category = stockCategoryForFilter.value, startDate = stockStartDateForFilter.value, finishDate = stockFinishDateForFilter.value)

        // Время в миллисекундах СЕГОДНЯ
        val dateInMillis = getTodayInMilliseconds()

        // Конвертируем дату из миллисекунд в обычную дату 2 февраля 2023
        val today = convertMillisecondsToDate(dateInMillis.toString())

        // ДАТА - СЕГОДНЯ В НУЖНОМ ФОРМАТЕ 20230202
        val todayInRightFormat = filterFunctions.getSplitDataFromDb(today.toString())

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val stockArray = ArrayList<StockCardClass>() // создаем пустой список акций

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                        .child("info") // следующая папка с информацией об акции
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("stockData") // добираесся до следующей папки внутри УКПользователя - папка с данными об акции
                        .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акций

                    // Читаем фильтр из акции
                    val getFilter = item
                        .child("filterInfo").getValue(FilterStockClass::class.java)


                    if (stock != null && getFilter != null){

                        // Читаем счетчик добавивших в избранное
                        val getFavCounter = item
                            .child("AddedToFavorites").childrenCount


                        val finishFilledStock = StockCardClass(

                            image = stock.image,
                            headline = stock.headline,
                            description = stock.description,
                            category = stock.category,
                            keyStock = stock.keyStock,
                            keyPlace = stock.keyPlace,
                            keyCreator = stock.keyCreator,
                            city = stock.city,
                            startDate = stock.startDate,
                            finishDate = stock.finishDate,
                            inputHeadlinePlace = stock.inputHeadlinePlace,
                            inputAddressPlace = stock.inputAddressPlace,
                            createTime = stock.createTime,
                            startDateNumber = stock.startDateNumber,
                            finishDateNumber = stock.finishDateNumber,
                            counterInFav = getFavCounter.toString()

                        )

                        // читаем числа даты для сортировки ГодМесяцДень
                        val startDataNumber = finishFilledStock.startDateNumber!!
                        val finishDataNumber = finishFilledStock.finishDateNumber!!

                        // ---- ЕСЛИ ДАТЫ НАЧАЛА И КОНЦА ПЕРИОДА ЕСТЬ, ТО

                        if (stockStartDateForFilter.value != "Выбери дату" && stockFinishDateForFilter.value != "Выбери дату") {

                            // Разделение дат фильтра на составляющие
                            val startDayList = filterFunctions.splitData(stockStartDateForFilter.value)
                            val finishDayList = filterFunctions.splitData(stockFinishDateForFilter.value)

                            // Получаем дату начала и конца для фильтра в нужном формате
                            val startDayNumber = filterFunctions.getDataNumber(startDayList)
                            val finishDayNumber = filterFunctions.getDataNumber(finishDayList)

                            // ПРОВЕРЯЕМ - ПОПАДАЕТ ЛИ НАША АКЦИЯ В ДИАПАЗОН ДАТ ИЗ ФИЛЬТРА

                            filterFunctions.checkStockDatePeriod(
                                stockStartDate = startDataNumber, // дата начала акции из БД в правильном формате
                                stockFinishDate = finishDataNumber, // дата завершения акции из БД в правильном формате
                                startFilterDay = startDayNumber, // Начало периода в правильном формате
                                finishFilterDay = finishDayNumber // конец периода в правильном формате
                            ) { inPeriod ->

                                // Результат сравнения

                                filter = if (inPeriod) {

                                    // Создаем фильтр, который будет сравниваться с фильтром из БД.
                                    // Получается, что фильтр будет постоянно меняться и подстраиваться к каждому мероприятию, которое попадает
                                    // В период

                                    filterFunctions.createStockFilter(
                                        city = cityForFilter.value, // город, который выбрал для фильтра пользователь
                                        category = stockCategoryForFilter.value, // категория, которую выбрал пользователь для фильтра
                                        startDate = finishFilledStock.startDate!!, // дата из БД, чтобы фильтр совпал с фильтром из БД и акция подошла
                                        finishDate = finishFilledStock.finishDate!! // дата из БД, чтобы фильтр совпал с фильтром из БД и акция подошла
                                    )

                                } else {

                                    // если не попадает в диапазон, делаем фильтр как выбрал пользователь и акция не попала в список


                                    filterFunctions.createStockFilter(
                                        city = cityForFilter.value, // город, который выбрал для фильтра пользователь
                                        category = stockCategoryForFilter.value, // категория, которую выбрал пользователь для фильтра
                                        startDate = stockStartDateForFilter.value, // дата из БД, чтобы фильтр совпал с фильтром из БД и акция подошла
                                        finishDate = stockFinishDateForFilter.value // дата из БД, чтобы фильтр совпал с фильтром из БД и акция подошла
                                    )
                                }
                            }
                        }

                        // УКАЗЫВАЕМ, КАКИЕ ФИЛЬТРЫ НАДО БРАТЬ В ЗАВИСИМОСТИ ОТ ТИПА ВЫБРАННОГО ПОЛЬЗОВАТЕЛЕМ ФИЛЬТРА

                        when (typeFilter){

                            "cityCategoryStartFinish" -> {if (getFilter.cityCategoryStartFinish == filter) {

                                if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                    if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                        act.stockDatabaseManager.deleteStockWithPlaceNote(
                                            stockKey = finishFilledStock.keyStock,
                                            imageUrl = finishFilledStock.image,
                                            placeKey = finishFilledStock.keyPlace
                                        ) {

                                            if (it) {

                                                Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                            } else {

                                                Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                            }

                                        }

                                    }

                                } else {
                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "cityCategoryStart" -> {
                                if (getFilter.cityCategoryStart == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "cityCategoryFinish" -> {
                                if (getFilter.cityCategoryFinish == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "cityStartFinish" -> {
                                if (getFilter.cityStartFinish == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "cityCategory" -> {
                                if (getFilter.cityCategory == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "cityFinish" -> {
                                if (getFilter.cityFinish == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "cityStart" -> {
                                if (getFilter.cityStart == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "city" -> {
                                if (getFilter.city == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "categoryStartFinish" -> {
                                if (getFilter.categoryStartFinish == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "categoryStart" -> {
                                if (getFilter.categoryStart == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "categoryFinish" -> {
                                if (getFilter.categoryFinish == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "startFinish" -> {
                                if (getFilter.startFinish == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "category" -> {
                                if (getFilter.category == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "finish" -> {
                                if (getFilter.finish == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "start" -> {
                                if (getFilter.start == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                            "noFilter" -> {
                                if (getFilter.noFilter == filter) {
                                    if (finishDataNumber.toInt() < todayInRightFormat.toInt()) {

                                        if (finishFilledStock.keyStock != null && finishFilledStock.keyPlace != null && finishFilledStock.image != null) {

                                            act.stockDatabaseManager.deleteStockWithPlaceNote(
                                                stockKey = finishFilledStock.keyStock,
                                                imageUrl = finishFilledStock.image,
                                                placeKey = finishFilledStock.keyPlace
                                            ) {

                                                if (it) {

                                                    Log.d ("MyLog", "Удалилась и картинка и сама акция и запись у заведения")

                                                } else {

                                                    Log.d ("MyLog", "Почемуто акция не удалилась не удалилось")

                                                }

                                            }

                                        }
                                    } else {

                                        stockArray.add(finishFilledStock)
                                    }
                                }
                            }
                        }
                    }
                }

                if (stockArray.isEmpty()){
                    stockList.value = listOf(defaultStockCard) // если в список-черновик ничего не добавилось, то добавляем акцию по умолчанию
                } else {
                    val sortedList = filterFunctions.sortedStockList(stockArray, stockSortingForFilter.value)
                    stockList.value = sortedList // если добавились акции в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ---- ФУНКЦИЯ ДОБАВЛЕНИЯ АКЦИИ К МЕСТУ ------

    private fun addStockToPlace (stockKey: String, placeKey: String, callback: (result: Boolean) -> Unit){

        act.placesDatabaseManager.placeDatabase
            .child(placeKey) //папка с ключем заведения
            .child("AddedStocks") // папка, куда добавляются акции этого заведения
            .child(stockKey) // создаем подпапку со значением ключа
            .setValue(stockKey) // записываем ключ акции
            .addOnCompleteListener{ addedToPlace ->

                // Если добавилось, то тру
                if (addedToPlace.isSuccessful) {

                    callback (true)

                } else {
                    // если нет то фолс
                    callback (false)

                }

            }

    }

    // ---- ФУНКЦИЯ УДАЛЕНИЯ АКЦИИ У МЕСТА ------

    fun deleteStockFromPlace (stockKey: String, placeKey: String, callback: (result: Boolean) -> Unit){

        act.placesDatabaseManager.placeDatabase // обращаемся к БД
            .child(placeKey) // заходим в папку с уникальным ключем заведения
            .child("AddedStocks") // заходим в папку мероприятий этого заведения
            .child(stockKey) // заходим в папку с названием как наш ключ
            .removeValue() // удаляем значение
            .addOnCompleteListener{ deleted ->

                if (deleted.isSuccessful){

                    callback (true)

                } else {

                    callback (false)

                }
            }
    }

    fun deleteStockWithPlaceNote (stockKey: String, imageUrl: String, placeKey: String, callback: (result: Boolean)-> Unit) {

        // Сначала удаляем акцию и картинку
        deleteStock(stockKey, imageUrl){ deletedStock ->

            // Если удалилось
            if (deletedStock) {

                // если есть заведение
                if (placeKey != "") {

                    // Удаляем ключ из заведения
                    deleteStockFromPlace(stockKey, placeKey){ deleteFromPlace ->

                        // если ключ удалился, возвращаем тру
                        if (deleteFromPlace){

                            callback (true)

                        } else {
                            // Если нет - фалс
                            callback (false)
                        }
                    }

                } else {

                    // Если заведения нет, но акция удалилась, то просто возвращаем тру
                    callback(true)
                }

            } else {
                // Если акция не удалилось, возвращаем фалс
                callback (false)

            }
        }
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ АКЦИЙ С БАЗЫ ДАННЫХ --------

    fun readStockDataFromDb(stockList: MutableState<List<StockAdsClass>>){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val stockArray = ArrayList<StockAdsClass>() // создаем пустой список акций

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                        .child("info") // следующая папка с информацией об акции
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("stockData") // добираесся до следующей папки внутри УКПользователя - папка с данными об акции
                        .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акций

                    if (stock != null) {stockArray.add(stock)} // если акция не пустая, добавляем в список

                }

                if (stockArray.isEmpty()){
                    stockList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем акцию по умолчанию
                } else {
                    stockList.value = stockArray // если добавились акции в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ - АКЦИЯ В ИЗБРАННОМ УЖЕ ИЛИ НЕТ

    fun favIconStock(key: String, callback: (result: Boolean)-> Unit){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    if (auth.uid !=null) {

                        // считываем данные акции

                        val stock = item
                            .child("info")
                            .children.iterator().next()
                            .child("stockData")
                            .getValue(StockAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        val stockFav = item
                            .child("AddedToFavorites")
                            .child(auth.uid!!)
                            .getValue(String::class.java)

                        // проверка акции на Null

                        if (stock != null) {

                            // если ключ акции равен переданному ключу
                            if (stock.keyStock == key) {

                                // если в списке добавивших в избранное есть мой ключ, то вернуть колбак тру
                                if (stockFav == auth.uid){
                                    callback (true)
                                } else {
                                    // если в списке нет, то фалс
                                    callback (false)
                                }
                            }
                        }
                    }
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }

    // --- ФУНКЦИЯ ДОБАВЛЕНИЯ АКЦИИ В ИЗБРАННОЕ ---------

    fun addFavouriteStock(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        act.mAuth.uid?.let {
            stockDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем акции
                .child("AddedToFavorites") // заходим в папку добавивших в избранное
                .child(it) // заходим в папку с названием как наш ключ
                .setValue(act.mAuth.uid) // записываем наш ключ
        }?.addOnCompleteListener {
            // слушаем выполнение. Если успешно сделано, то...
            if (it.isSuccessful){
                // возвращаем колбак ТРУ
                callback (true)
            }
        }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ АКЦИИ ИЗ ИЗБРАННОГО ----------

    fun removeFavouriteStock(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        act.mAuth.uid?.let {
            stockDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем акции
                .child("AddedToFavorites") // заходим в папку добавивших в избранное
                .child(it) // заходим в папку с названием как наш ключ
                .removeValue() // удаляем значение
        }?.addOnCompleteListener {
            // слушаем выполнение. Если успешно сделано, то...
            if (it.isSuccessful){
                // возвращаем колбак ТРУ
                callback (true)
            }
        }
    }

    // --- ФУНКЦИЯ СЧЕТЧИКА ПРОСМОТРА АКЦИЙ ---------

    fun viewCounterStock(key: String, callback: (result: Boolean)-> Unit){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // считываем данные акций

                    val stock = item
                        .child("info")
                        .children.iterator().next()
                        .child("stockData")
                        .getValue(StockAdsClass::class.java)

                    // считываем список добавивших в избранное пользователей

                    var stockCount = item
                        .child("viewCounter")
                        .child("viewCounter")
                        .getValue(Int::class.java)

                    // проверка акции на Null

                    if (stock != null) {

                        // если ключ акции равен переданному ключу
                        if (stock.keyStock == key) {

                            // Если счетчик просмотров не нал
                            if (stockCount != null) {
                                stockCount ++ // добавляем к счетчику 1

                                // Перезаписываем новое значение счетчика

                                stockDatabase
                                    .child(key)
                                    .child("viewCounter")
                                    .child("viewCounter")
                                    .setValue(stockCount)

                                callback (true) // возвращаем колбак тру

                            } else {
                                // если счетчик еще не создан, то создаем и устанавливаем значение 1
                                stockDatabase
                                    .child(key)
                                    .child("viewCounter")
                                    .child("viewCounter")
                                    .setValue(1)

                                callback (true) // возвращаем колбак тру
                            }
                        }
                    }
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}

        }
        )
    }

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ МОИХ АКЦИЙ --------

    fun readStockMyDataFromDb(stockList: MutableState<List<StockCardClass>>){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val stockArray = ArrayList<StockCardClass>()

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    if (auth.uid !=null) {
                        val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                            .child("info") // следующая папка с информацией об акции
                            .child(auth.uid!!) // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                            .child("stockData") // добираемся до следующей папки внутри УКПользователя - папка с данными об акции
                            .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акции

                        if (stock != null) {

                            // Читаем счетчик добавивших в избранное
                            val getFavCounter = item
                                .child("AddedToFavorites").childrenCount


                            val finishFilledStock = StockCardClass(

                                image = stock.image,
                                headline = stock.headline,
                                description = stock.description,
                                category = stock.category,
                                keyStock = stock.keyStock,
                                keyPlace = stock.keyPlace,
                                keyCreator = stock.keyCreator,
                                city = stock.city,
                                startDate = stock.startDate,
                                finishDate = stock.finishDate,
                                inputHeadlinePlace = stock.inputHeadlinePlace,
                                inputAddressPlace = stock.inputAddressPlace,
                                createTime = stock.createTime,
                                startDateNumber = stock.startDateNumber,
                                finishDateNumber = stock.finishDateNumber,
                                counterInFav = getFavCounter.toString()

                            )


                            stockArray.add(finishFilledStock)
                        } //  если акция не нал, то добавляем в список-черновик
                    }
                }

                if (stockArray.isEmpty()){
                    stockList.value = listOf(defaultStockCard) // если в списке ничего нет, то добавляем акцию по умолчанию
                } else {
                    stockList.value = stockArray // если список не пустой, то возвращаем мои акции с БД
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ ИЗБРАННЫХ АКЦИЙ --------

    fun readStockFavDataFromDb(stockList: MutableState<List<StockCardClass>>){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val stockArray = ArrayList<StockCardClass>()

                for (item in snapshot.children){

                    // Считываем каждую акцию для сравнения

                    if (auth.uid !=null) {
                        val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА акции
                            .child("info") // папка с информацией об акции
                            .children.iterator().next() // папка уникального ключа пользователя. Пропускаем ее
                            .child("stockData") // добираесся до следующей папки внутри УКПользователя - папка с данными об акции
                            .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акции

                        // Считываем папку, в которую попадают ключи добавивших в избранное

                        val stockFav = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА акции
                            .child("AddedToFavorites") // Папка со списком добавивших в избранное
                            .child(auth.uid!!) // ищем папку с ключом пользователя
                            .getValue(String::class.java) // забираем данные из БД если они есть

                        // сравниваем ключи

                        if (stockFav == auth.uid) {
                            // если ключи совпали, проверяем акцию на нал
                            if (stock != null) {

                                // Читаем счетчик добавивших в избранное
                                val getFavCounter = item
                                    .child("AddedToFavorites").childrenCount


                                val finishFilledStock = StockCardClass(

                                    image = stock.image,
                                    headline = stock.headline,
                                    description = stock.description,
                                    category = stock.category,
                                    keyStock = stock.keyStock,
                                    keyPlace = stock.keyPlace,
                                    keyCreator = stock.keyCreator,
                                    city = stock.city,
                                    startDate = stock.startDate,
                                    finishDate = stock.finishDate,
                                    inputHeadlinePlace = stock.inputHeadlinePlace,
                                    inputAddressPlace = stock.inputAddressPlace,
                                    createTime = stock.createTime,
                                    startDateNumber = stock.startDateNumber,
                                    finishDateNumber = stock.finishDateNumber,
                                    counterInFav = getFavCounter.toString()

                                )

                                //  если акция не нал, то добавляем в список-черновик
                                stockArray.add(finishFilledStock)
                            }
                        }
                    }
                }

                if (stockArray.isEmpty()){
                    stockList.value = listOf(defaultStockCard) // если в списке ничего нет, то добавляем акцию по умолчанию
                } else {
                    stockList.value = stockArray // если список не пустой, то возвращаем избранные акции с БД
                }
            }
            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОЙ АКЦИИ --------

    fun readOneStockFromDataBase(stockInfo: MutableState<StockAdsClass>, key: String, callback: (result: List<Int>)-> Unit){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА акции
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("stockData") // добираесся до следующей папки внутри - папка с данными о акции
                        .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акции

                    // считываем данные для счетчика - количество добавивших в избранное
                    val stockFav = item.child("AddedToFavorites").childrenCount

                    // считываем данные для счетчика - количество просмотров акции
                    val stockViewCount = item
                        .child("viewCounter").child("viewCounter").getValue(Int::class.java)

                    // если мероприятие не нал и ключ акции совпадает с ключем из БД, то...
                    if (stock != null && stock.keyStock == key) {

                        // передаем в переменную нужную акцию

                        stockInfo.value = stock

                        // если счетчик просмотров не нал, то...
                        if (stockViewCount != null) {
                            // Возвращаем калбак в виде списка счетчиков
                            callback (listOf(stockFav.toInt(), stockViewCount.toInt()))
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОЙ АКЦИИ --------

    fun readOneStockFromDataBaseReturnClass(key: String, callback: (result: StockAdsClass)-> Unit){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА акции
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("stockData") // добираесся до следующей папки внутри - папка с данными о акции
                        .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акции


                    // если мероприятие не нал и ключ акции совпадает с ключем из БД, то...
                    if (stock != null && stock.keyStock == key) {

                        callback (stock)

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ АКЦИЙ С БАЗЫ ДАННЫХ --------

    fun readStockInPlaceFromDb(stockList: MutableState<List<StockCardClass>>, placeKey: String){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val stockArray = ArrayList<StockCardClass>() // создаем пустой список акций

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                        .child("info") // следующая папка с информацией об акции
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("stockData") // добираесся до следующей папки внутри УКПользователя - папка с данными об акции
                        .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акций

                    if (stock != null && stock.keyPlace == placeKey) {

                        // Читаем счетчик добавивших в избранное
                        val getFavCounter = item
                            .child("AddedToFavorites").childrenCount


                        val finishFilledStock = StockCardClass(

                            image = stock.image,
                            headline = stock.headline,
                            description = stock.description,
                            category = stock.category,
                            keyStock = stock.keyStock,
                            keyPlace = stock.keyPlace,
                            keyCreator = stock.keyCreator,
                            city = stock.city,
                            startDate = stock.startDate,
                            finishDate = stock.finishDate,
                            inputHeadlinePlace = stock.inputHeadlinePlace,
                            inputAddressPlace = stock.inputAddressPlace,
                            createTime = stock.createTime,
                            startDateNumber = stock.startDateNumber,
                            finishDateNumber = stock.finishDateNumber,
                            counterInFav = getFavCounter.toString()

                        )

                        stockArray.add(finishFilledStock)

                    } // если акция не пустая, добавляем в список

                }

                if (stockArray.isEmpty()){
                    stockList.value = listOf(defaultStockCard) // если в список-черновик ничего не добавилось, то добавляем акцию по умолчанию
                } else {
                    stockList.value = stockArray // если добавились акции в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ АКЦИЙ С БАЗЫ ДАННЫХ --------

    fun readStockCounterInPlaceFromDb(placeKey: String, callback: (stockCounter: Int)-> Unit){

        stockDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val stockArray = ArrayList<StockAdsClass>() // создаем пустой список акций

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val stock = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА АКЦИИ
                        .child("info") // следующая папка с информацией об акции
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("stockData") // добираесся до следующей папки внутри УКПользователя - папка с данными об акции
                        .getValue(StockAdsClass::class.java) // забираем данные из БД в виде нашего класса акций

                    if (stock != null && stock.keyPlace == placeKey) {stockArray.add(stock)} // если акция не пустая, добавляем в список

                }

                if (stockArray.isEmpty()){
                    callback (0) // выдаем колбак, что список пустой
                } else {
                    callback (stockArray.size) // выдаем размер списка акций
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }


}
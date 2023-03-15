package kz.dvij.dvij_compose3.firebase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.filters.FilterMeetingClass
import kz.dvij.dvij_compose3.filters.FilterPlacesClass
import java.time.MonthDay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PlacesDatabaseManager (val act: MainActivity) {

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    val placeDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Places") // Создаем ПАПКУ В БД для Заведений

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    val default = PlacesAdsClass (
        placeDescription = "Default"
    )

    val defaultForCard = PlacesCardClass (
        placeDescription = "Default"
    )

    private val meetingDatabaseManager = MeetingDatabaseManager(act)

    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ ЗАВЕДЕНИЯ --------

    fun publishPlace(filledPLace: PlacesAdsClass, callback: (result: Boolean)-> Unit){

        val filledFilter = FilterPlacesClass(
            cityCategory = act.filterFunctions.createPlaceFilter(city = filledPLace.city!!, category = filledPLace.category!!),
            city = act.filterFunctions.createPlaceFilter(city = filledPLace.city),
            category = act.filterFunctions.createPlaceFilter(category = filledPLace.category),
            noFilter = act.filterFunctions.createPlaceFilter()
        )

        placeDatabase // записываем в базу данных
            .child(filledPLace.placeKey ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ ЗАВЕДЕНИЯ
            .child("info") // помещаем данные в папку info
            .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего заведение
            .child("placeData") // помещаем в папку
            .setValue(filledPLace).addOnCompleteListener {

                if (it.isSuccessful) {

                    // если заведение опубликовано, запускаем публикацию фильтра
                    placeDatabase
                        .child(filledPLace.placeKey ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ заведения
                        .child("filterInfo") // помещаем данные в папку filterInfo
                        .setValue(filledFilter).addOnCompleteListener{

                            // если заведение и фильтр опубликованы, возвращаем колбак тру
                            callback (true)

                        }
                } else {
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ Заведения----------

    fun deletePlace(key: String, imageUrl: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        act.photoHelper.deletePlaceImage(imageUrl = imageUrl){

            if (it) {

                placeDatabase.child(key).removeValue().addOnCompleteListener{deleteP ->

                    if (deleteP.isSuccessful) {

                        callback (true)

                    } else {

                        callback (false)

                    }
                }

            } else {

                callback (false)

            }
        }



        act.mAuth.uid?.let {
            placeDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем заведения
                .removeValue() // удаляем значение
        }?.addOnCompleteListener {
            // слушаем выполнение. Если успешно сделано, то...
            if (it.isSuccessful){
                // возвращаем колбак ТРУ
                callback (true)
            }
        }
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ ЗАВЕДЕНИЙ С БАЗЫ ДАННЫХ --------

    fun readPlaceDataFromDb(placeList: MutableState<List<PlacesAdsClass>>){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val placeArray = ArrayList<PlacesAdsClass>() // создаем пустой список заведений

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА ЗАВЕДЕНИЯ
                        .child("info") // следующая папка с информацией о заведении
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("placeData") // добираесся до следующей папки внутри УКПользователя - папка с данными о заведении
                        .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведений

                    if (place != null) {placeArray.add(place)} // если заведение не пустое, добавляем в список

                }

                if (placeArray.isEmpty()){
                    placeList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем заведение по умолчанию
                } else {
                    placeList.value = placeArray // если добавились заведения в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ ЗАВЕДЕНИЙ С БАЗЫ ДАННЫХ --------

    @RequiresApi(Build.VERSION_CODES.O)
    fun readPlaceSortedDataFromDb(
        placeList: MutableState<List<PlacesCardClass>>,
        cityForFilter: MutableState<String>,
        placeCategoryForFilter: MutableState<String>,
        placeIsOpenForFilter: MutableState<Boolean>,
        placeSortingForFilter: MutableState<String>
    ){

        // Определяем тип фильтра
        val typeFilter = act.filterFunctions.getTypeOfPlaceFilter(listOf(cityForFilter.value, placeCategoryForFilter.value))

        // Создаем фильтр из пришедших выбранных пользователем данных
        var filter = act.filterFunctions.createPlaceFilter(city = cityForFilter.value, category = placeCategoryForFilter.value)

        val getNowTime = ZonedDateTime.now(ZoneId.of("Asia/Almaty"))
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE, HH:mm"))

        val splitDate = getNowTime.split(", ")

        val nowDay = splitDate[1]
        val nowTime = splitDate[2]



        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val placeArray = ArrayList<PlacesCardClass>() // создаем пустой список заведений

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА ЗАВЕДЕНИЯ
                        .child("info") // следующая папка с информацией о заведении
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("placeData") // добираесся до следующей папки внутри УКПользователя - папка с данными о заведении
                        .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведений

                    // Читаем фильтр из заведения
                    val getFilter = item
                        .child("filterInfo").getValue(FilterPlacesClass::class.java)



                    // считываем данные для счетчика - количество добавивших в избранное
                    val placeFav = item.child("AddedToFavorites").childrenCount

                    // считываем данные для счетчика - количество мероприятий, ссылающихся на это заведение
                    val placeMeetingsCounter = item.child("AddedMeetings").childrenCount

                    // считываем данные для счетчика - количество акций, ссылающихся на это заведение
                    val placeStockCounter = item.child("AddedStocks").childrenCount

                    // считываем данные для счетчика - количество просмотров объявления
                    val placeViewCount = item
                        .child("viewCounter").child("viewCounter").getValue(Int::class.java)



                    if (place != null && getFilter != null){

                        val placeTimeOnToday = act.placesDatabaseManager.returnWrightTimeOnCurrentDayInStandartClass(nowDay, place)

                        val nowIsOpen = act.placesDatabaseManager.nowIsOpenPlace(nowTime, placeTimeOnToday[0], placeTimeOnToday[1])

                        // Считываем количество мероприятий у этого заведения

                        val filledFinishPlace = PlacesCardClass(
                            logo = place.logo,
                            placeName = place.placeName,
                            placeDescription = place.placeDescription,
                            phone = place.phone,
                            whatsapp = place.whatsapp,
                            telegram = place.telegram,
                            instagram = place.instagram,
                            category = place.category,
                            city = place.city,
                            address = place.address,
                            placeKey = place.placeKey,
                            owner = place.owner,
                            meetingCounter = placeMeetingsCounter.toString(),
                            stockCounter = placeStockCounter.toString(),
                            favCounter = placeFav.toString(),
                            viewCounter = placeViewCount.toString(),
                            mondayOpenTime = place.mondayOpenTime,
                            mondayCloseTime = place.mondayCloseTime,
                            tuesdayOpenTime = place.tuesdayOpenTime,
                            tuesdayCloseTime = place.tuesdayCloseTime,
                            wednesdayOpenTime = place.wednesdayOpenTime,
                            wednesdayCloseTime = place.wednesdayCloseTime,
                            thursdayOpenTime = place.thursdayOpenTime,
                            thursdayCloseTime = place.thursdayCloseTime,
                            fridayOpenTime = place.fridayOpenTime,
                            fridayCloseTime = place.fridayCloseTime,
                            saturdayOpenTime = place.saturdayOpenTime,
                            saturdayCloseTime = place.saturdayCloseTime,
                            sundayOpenTime = place.sundayOpenTime,
                            sundayCloseTime = place.sundayCloseTime

                        )

                        when (typeFilter) {

                            "cityCategory" -> {

                                // Если полученный фильтр равен фильтру выбранному пользователем

                                if (getFilter.cityCategory == filter) {

                                    // если нажата кнопка "Сейчас открыто"
                                    if (placeIsOpenForFilter.value){

                                        // проверяем, открыто ли сейчас заведение
                                        if (nowIsOpen) {

                                            // Если открыто, добавляем в список
                                            placeArray.add(filledFinishPlace)

                                        }

                                    } else {

                                        // Если не выбрана опция только открытые, то просто добавляем в список,
                                        placeArray.add(filledFinishPlace)

                                    }
                                }
                            }
                            "city" -> {

                                // Если полученный фильтр равен фильтру выбранному пользователем

                                if (getFilter.city == filter) {

                                    // если нажата кнопка "Сейчас открыто"
                                    if (placeIsOpenForFilter.value){

                                        // проверяем, открыто ли сейчас заведение
                                        if (nowIsOpen) {

                                            // Если открыто, добавляем в список
                                            placeArray.add(filledFinishPlace)

                                        }

                                    } else {

                                        // Если не выбрана опция только открытые, то просто добавляем в список,
                                        placeArray.add(filledFinishPlace)

                                    }
                                }

                            }
                            "category" -> {

                                // Если полученный фильтр равен фильтру выбранному пользователем

                                if (getFilter.category == filter) {

                                    // если нажата кнопка "Сейчас открыто"
                                    if (placeIsOpenForFilter.value){

                                        // проверяем, открыто ли сейчас заведение
                                        if (nowIsOpen) {

                                            // Если открыто, добавляем в список
                                            placeArray.add(filledFinishPlace)

                                        }

                                    } else {

                                        // Если не выбрана опция только открытые, то просто добавляем в список,
                                        placeArray.add(filledFinishPlace)

                                    }
                                }

                            }
                            "noFilter" -> {

                                // Если полученный фильтр равен фильтру выбранному пользователем

                                if (getFilter.noFilter == filter) {

                                    // если нажата кнопка "Сейчас открыто"
                                    if (placeIsOpenForFilter.value){

                                        // проверяем, открыто ли сейчас заведение
                                        if (nowIsOpen) {

                                            // Если открыто, добавляем в список
                                            placeArray.add(filledFinishPlace)

                                        }

                                    } else {

                                        // Если не выбрана опция только открытые, то просто добавляем в список,
                                        placeArray.add(filledFinishPlace)

                                    }
                                }
                            }
                        }
                    }
                }

                if (placeArray.isEmpty()){
                    placeList.value = listOf(defaultForCard) // если в список-черновик ничего не добавилось, то добавляем заведение по умолчанию
                } else {
                    val sortedList = act.filterFunctions.sortedPlaceList(placesList = placeArray, placeSortingForFilter.value)
                    placeList.value = sortedList // если добавились заведения в список, то этот новый список и передаем

                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // --- ФУНКЦИЯ ДОБАВЛЕНИЯ ЗАВЕДЕНИЯ В ИЗБРАННОЕ ---------

    fun addFavouritePlace(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        act.mAuth.uid?.let {
            placeDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем заведения
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

    // --- ФУНКЦИЯ УДАЛЕНИЯ ЗАВЕДЕНИЯ ИЗ ИЗБРАННОГО ----------

    fun removeFavouritePlace(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        act.mAuth.uid?.let {
            placeDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем заведения
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

    fun returnWrightTimeOnCurrentDay (day: String, placeInfo: PlacesCardClass): List<String>{

        return when (day) {

            "понедельник", "Monday" -> listOf(placeInfo.mondayOpenTime!!, placeInfo.mondayCloseTime!!)
            "вторник", "Tuesday"  -> listOf(placeInfo.tuesdayOpenTime!!, placeInfo.tuesdayCloseTime!!)
            "среда", "Wednesday" -> listOf(placeInfo.wednesdayOpenTime!!, placeInfo.wednesdayCloseTime!!)
            "четверг", "Thursday" -> listOf(placeInfo.thursdayOpenTime!!, placeInfo.thursdayCloseTime!!)
            "пятница", "Friday" -> listOf(placeInfo.fridayOpenTime!!, placeInfo.fridayCloseTime!!)
            "суббота", "Saturday" -> listOf(placeInfo.saturdayOpenTime!!, placeInfo.saturdayCloseTime!!)
            "воскресенье", "Sunday" -> listOf(placeInfo.sundayOpenTime!!, placeInfo.sundayCloseTime!!)
            else -> listOf("00:00", "00:00")

        }

    }

    fun returnWrightTimeOnCurrentDayInStandartClass (day: String, placeInfo: PlacesAdsClass): List<String>{

        return when (day) {

            "понедельник" -> listOf(placeInfo.mondayOpenTime!!, placeInfo.mondayCloseTime!!)
            "вторник" -> listOf(placeInfo.tuesdayOpenTime!!, placeInfo.tuesdayCloseTime!!)
            "среда" -> listOf(placeInfo.wednesdayOpenTime!!, placeInfo.wednesdayCloseTime!!)
            "четверг" -> listOf(placeInfo.thursdayOpenTime!!, placeInfo.thursdayCloseTime!!)
            "пятница" -> listOf(placeInfo.fridayOpenTime!!, placeInfo.fridayCloseTime!!)
            "суббота" -> listOf(placeInfo.saturdayOpenTime!!, placeInfo.saturdayCloseTime!!)
            else -> listOf(placeInfo.sundayOpenTime!!, placeInfo.sundayCloseTime!!)

        }

    }

    fun nowIsOpenPlace (nowTime: String, startTime: String, finishTime: String):Boolean{

        var result = false

        val nowInNumber = getTimeNumber(nowTime).toInt()

        val startInNumber = getTimeNumber(startTime).toInt()
        val finishInNUmber = getTimeNumber(finishTime).toInt()

        result = if (startInNumber>finishInNUmber){

            // если заведение работает за полночь, то финишное время будет меньше начального
            val currentFinishTime = finishInNUmber + 2400

            nowInNumber in (startInNumber + 1) until currentFinishTime // startInNumber<nowInNumber && nowInNumber<currentFinishTime

        } else {

            nowInNumber in (startInNumber + 1) until finishInNUmber // startInNumber < nowInNumber && nowInNumber < finishInNUmber

        }

        return result
    }

    private fun getTimeNumber (date: String): String {

        val split = date.split(":")

        return "${split[0]}${split[1]}"

    }

    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ - ЗАВЕДЕНИЕ В ИЗБРАННОМ УЖЕ ИЛИ НЕТ

    fun favIconPlace(key: String, callback: (result: Boolean)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    if (auth.uid !=null) {

                        // считываем данные заведения

                        val place = item
                            .child("info")
                            .children.iterator().next()
                            .child("placeData")
                            .getValue(PlacesAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        val placeFav = item
                            .child("AddedToFavorites")
                            .child(auth.uid!!)
                            .getValue(String::class.java)

                        // проверка заведения на Null

                        if (place != null) {

                            // если ключ заведения равен переданному ключу
                            if (place.placeKey == key) {

                                // если в списке добавивших в избранное есть мой ключ, то вернуть колбак тру
                                if (placeFav == auth.uid){
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

    // --- ФУНКЦИЯ СЧЕТЧИКА ПРОСМОТРА ЗАВЕДЕНИЯ ---------

    fun viewCounterPlace(key: String, callback: (result: Boolean)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // считываем данные заведения

                    val place = item
                        .child("info")
                        .children.iterator().next()
                        .child("placeData")
                        .getValue(PlacesAdsClass::class.java)

                    // считываем список добавивших в избранное пользователей

                    var placeCount = item
                        .child("viewCounter")
                        .child("viewCounter")
                        .getValue(Int::class.java)

                    // проверка заведения на Null

                    if (place != null) {

                        // если ключ заведения равен переданному ключу
                        if (place.placeKey == key) {

                            // Если счетчик просмотров не нал
                            if (placeCount != null) {
                                placeCount ++ // добавляем к счетчику 1

                                // Перезаписываем новое значение счетчика

                                placeDatabase
                                    .child(key)
                                    .child("viewCounter")
                                    .child("viewCounter")
                                    .setValue(placeCount)

                                callback (true) // возвращаем колбак тру

                            } else {
                                // если счетчик еще не создан, то создаем и устанавливаем значение 1
                                placeDatabase
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

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ МОИХ ЗАВЕДЕНИЙ --------

    fun readPlaceMyDataFromDb(placesList: MutableState<List<PlacesAdsClass>>){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val placeArray = ArrayList<PlacesAdsClass>()

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    if (auth.uid !=null) {
                        val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА ЗАВЕДЕНИЯ
                            .child("info") // следующая папка с информацией о заведении
                            .child(auth.uid!!) // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                            .child("placeData") // добираемся до следующей папки внутри УКПользователя - папка с данными о заведении
                            .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведения

                        if (place != null) {placeArray.add(place)} //  если заведение не нал, то добавляем в список-черновик
                    }
                }

                if (placeArray.isEmpty()){
                    placesList.value = listOf(default) // если в списке ничего нет, то добавляем заведение по умолчанию
                } else {
                    placesList.value = placeArray // если список не пустой, то возвращаем мои заведения с БД
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    fun readFavCounter(key: String, callback: (result: String)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную stock, в которую в конце поместим наш ДАТАКЛАСС с акцией с БД

                    val place = item // это как бы первый слой иерархии в папке Stock. путь УНИКАЛЬНОГО КЛЮЧА акции
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("placeData") // добираесся до следующей папки внутри - папка с данными о акции
                        .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса акции

                    // считываем данные для счетчика - количество добавивших в избранное
                    val placeFav = item.child("AddedToFavorites").childrenCount

                    // если мероприятие не нал и ключ акции совпадает с ключем из БД, то...
                    if (place != null && place.placeKey == key) {

                        callback (placeFav.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ ИЗБРАННЫХ Заведений --------

    fun readPlacesFavDataFromDb(placesList: MutableState<List<PlacesAdsClass>>){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val placeArray = ArrayList<PlacesAdsClass>()

                for (item in snapshot.children){

                    // Считываем каждое заведение для сравнения

                    if (auth.uid !=null) {
                        val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА заведения
                            .child("info") // папка с информацией о заведении
                            .children.iterator().next() // папка уникального ключа пользователя. Пропускаем ее
                            .child("placeData") // добираесся до следующей папки внутри УКПользователя - папка с данными о заведении
                            .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведения

                        // Считываем папку, в которую попадают ключи добавивших в избранное

                        val placeFav = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА заведения
                            .child("AddedToFavorites") // Папка со списком добавивших в избранное
                            .child(auth.uid!!) // ищем папку с ключом пользователя
                            .getValue(String::class.java) // забираем данные из БД если они есть

                        // сравниваем ключи

                        if (placeFav == auth.uid) {
                            // если ключи совпали, проверяем заведение на нал
                            if (place != null) {

                                //  если заведение не нал, то добавляем в список-черновик
                                placeArray.add(place)
                            }
                        }
                    }
                }

                if (placeArray.isEmpty()){
                    placesList.value = listOf(default) // если в списке ничего нет, то добавляем заведение по умолчанию
                } else {
                    placesList.value = placeArray // если список не пустой, то возвращаем избранные заведения с БД
                }
            }
            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ ЗАВЕДЕНИИ ВОЗВРАЩАЮЩАЯ СПИСОК СЧЕТЧИКОВ --------

    fun readOnePlaceFromDataBase(placeInfo: MutableState<PlacesAdsClass>, key: String, callback: (result: List<Int>)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА заведения
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("placeData") // добираесся до следующей папки внутри - папка с данными о заведении
                        .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведеиня

                    // считываем данные для счетчика - количество добавивших в избранное
                    val placeFav = item.child("AddedToFavorites").childrenCount

                    // считываем данные для счетчика - количество просмотров объявления
                    val placeViewCount = item
                        .child("viewCounter").child("viewCounter").getValue(Int::class.java)

                    // если мероприятие не нал и ключ завдения совпадает с ключем из БД, то...
                    if (place != null && place.placeKey == key) {

                        // передаем в переменную нужное заведение

                        placeInfo.value = place

                        // если счетчик просмотров заведение не нал, то...
                        if (placeViewCount != null) {
                            // Возвращаем калбак в виде списка счетчиков
                            callback (listOf(placeFav.toInt(), placeViewCount.toInt()))
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ ЗАВЕДЕНИИ ВОЗВРАЩАЮЩАЯ ДАТА КЛАСС --------

    fun readOnePlaceFromDataBaseReturnDataClass(key: String, callback: (result: PlacesAdsClass)-> Unit){

        placeDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную place, в которую в конце поместим наш ДАТАКЛАСС с заведением с БД

                    val place = item // это как бы первый слой иерархии в папке Places. путь УНИКАЛЬНОГО КЛЮЧА заведения
                        .child("info") // Папка инфо
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("placeData") // добираесся до следующей папки внутри - папка с данными о заведении
                        .getValue(PlacesAdsClass::class.java) // забираем данные из БД в виде нашего класса заведеиня

                    // если мероприятие не нал и ключ завдения совпадает с ключем из БД, то...
                    if (place != null && place.placeKey == key) {

                        callback (place)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }



}
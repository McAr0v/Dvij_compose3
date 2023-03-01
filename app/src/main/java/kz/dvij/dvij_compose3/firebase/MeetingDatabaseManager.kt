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
import kz.dvij.dvij_compose3.filters.FilterMeetingClass
import kz.dvij.dvij_compose3.photohelper.PhotoHelper
import kz.dvij.dvij_compose3.pickers.convertMillisecondsToDate
import kz.dvij.dvij_compose3.pickers.getTodayInMilliseconds

class MeetingDatabaseManager (private val activity: MainActivity) {

    // --- ИНИЦИАЛИЗИРУЕМ БАЗУ ДАННЫХ -------

    val meetingDatabase = FirebaseDatabase // обращаемся к БД
        .getInstance("https://dvij-compose3-1cf6a-default-rtdb.europe-west1.firebasedatabase.app") // указываем ссылку на БД (без нее не работает)
        .getReference("Meetings") // Создаем ПАПКУ В БД для мероприятий

    private val auth = Firebase.auth // инициализируем для УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, ПУБЛИКУЮЩЕГО ОБЪЯВЛЕНИЕ

    val default = MeetingsAdsClass (
        description = "Default"
    )

    private val filterFunctions = FilterFunctions(activity)

    private val photoHelper = PhotoHelper(activity)


    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ МЕРОПРИЯТИИ ВОЗВРАЩАЮЩАЯ СПИСОК СЧЕТЧИКИ МЕРОПРИЯТИЯ --------

    fun readOneMeetingFromDataBase(
        meetingInfo: MutableState<MeetingsAdsClass>, // сюда записываться информация о мероприятии
        key: String, // ключ мероприятия
        callback: (result: List<Int>)-> Unit // калбак
    ){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    val meeting = item //путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // Папка инфо
                        .children.iterator().next() // путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    // считываем данные для счетчика - количество добавивших в избранное
                    val meetingFav = item.child("AddedToFavorites").childrenCount

                    // считываем данные для счетчика - количество просмотров объявления
                    val meetingCount = item
                        .child("viewCounter").child("viewCounter").getValue(Int::class.java)

                    // если мероприятие не нал и ключ мероприятия совпадает с ключем из БД, то...
                    if (meeting != null && meeting.key == key) {

                        // передаем в переменную нужное мероприятие

                        meetingInfo.value = meeting

                        // если счетчик просмотров мероприятия не нал, то...
                        if (meetingCount != null) {
                            // Возвращаем калбак в виде списка счетчиков
                            callback (listOf(meetingFav.toInt(), meetingCount.toInt()))
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ---- ФУНКЦИЯ СЧИТЫВАНИЯ ДАННЫХ О КОНКРЕТНОМ МЕРОПРИЯТИИ, ВОЗВРАЩАЮЩАЯ ДАТА КЛАСС --------

    fun readOneMeetingFromDBReturnClass(key: String, callback: (result: MeetingsAdsClass)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // Папка инфо
                        .children.iterator().next() // путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    // если мероприятие не нал и ключ мероприятия совпадает с ключем из БД, то...
                    if (meeting != null && meeting.key == key) {

                        // передаем в переменную нужное мероприятие

                        callback (meeting)

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    // ------ВЕРОЯТНО УЖЕ НЕ НУЖНА. ЕСЛИ ЗА МЕСЯЦ ТАК И НЕ ИСПОЛЬЗУЮ, УДАЛИТЬ. 25.02.23 написано это. ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ МЕРОПРИЯТИЙ С БАЗЫ ДАННЫХ --------

    fun readMeetingDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>){

        // Обращаемся к базе данных и вешаем слушатель addListenerForSingleValueEvent.
        // У этого слушателя функция такая - он один раз просматривает БД при запуске и все, ждет, когда мы его снова запустим
        // Есть другие типы слушателей, которые работают в режиме реального времени, т.е постоянно обращаются к БД
        // Это приводит к нагрузке на сервер и соответственно будем платить за большое количество обращений к БД

        // У самого объекта слушателя ValueEventListener есть 2 стандартные функции - onDataChange и onCancelled
        // их нужно обязательно добавить и заполнить нужным кодом

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>() // создаем пустой список мероприятий

                // запускаем цикл и пытаемся добраться до наших данных
                // snapshot - по сути это JSON файл, в котором нам нужно как в папках прописать путь до наших данных
                // ниже используем итератор и некст для того, чтобы войти в папку, название которой мы не знаем
                // так как на нашем пути куча уникальных ключей, которые мы не можем знать
                // где знаем точный путь (как в "meetingData"), там пишем .child()

                // добираемся

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // это как бы первый слой иерархии в папке Meetings. путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // следующая папка с информацией о мероприятии
                        .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // добираесся до следующей папки внутри УКПользователя - папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null) {meetingArray.add(meeting)} // если мероприятие не пустое, добавляем в список

                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray // если добавились мероприятия в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ ВСЕХ МЕРОПРИЯТИЙ С БАЗЫ ДАННЫХ С ФИЛЬТРОМ --------

    fun readFilteredMeetingDataFromDb(
        meetingsList: MutableState<List<MeetingsAdsClass>>,
        cityForFilter: MutableState<String>,
        meetingCategoryForFilter: MutableState<String>,
        meetingStartDateForFilter: MutableState<String>,
        meetingFinishDateForFilter: MutableState<String>,
        meetingSortingForFilter: MutableState<String>,
    ){

        // Определяем тип фильтра
        val typeFilter = filterFunctions.getTypeOfMeetingFilter(listOf(cityForFilter.value, meetingCategoryForFilter.value, meetingStartDateForFilter.value))

        // Создаем фильтр из пришедших выбранных пользователем данных
        var filter = filterFunctions.createMeetingFilter(city = cityForFilter.value, category = meetingCategoryForFilter.value, date = meetingStartDateForFilter.value)


        // Время в миллисекундах СЕГОДНЯ
        val dateInMillis = getTodayInMilliseconds()

        // Конвертируем дату из миллисекунд в обычную дату 2 февраля 2023
        val today = convertMillisecondsToDate(dateInMillis.toString())

        // ДАТА - СЕГОДНЯ В НУЖНОМ ФОРМАТЕ 20230202
        val todayInRightFormat = filterFunctions.getSplitDataFromDb(today.toString())

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>() // создаем пустой список мероприятий

                for (item in snapshot.children) {

                    // читаем мероприятие
                    val meeting =
                        item // путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info") // следующая папка с информацией о мероприятии
                            .children.iterator().next() // добираемся до следующей папки - путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                            .child("meetingData") // папка с данными о мероприятии
                            .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    // Читаем фильтр из мероприятия
                    val getFilter = item
                        .child("filterInfo").getValue(FilterMeetingClass::class.java)


                    if (meeting != null && getFilter != null) {

                        // читаем число даты для сортировки ГодМесяцДень
                        val meetingDataNumber = meeting.dateInNumber!!.toInt()

                        // ---- ЕСЛИ ДАТЫ НАЧАЛА И КОНЦА ПЕРИОДА ЕСТЬ, ТО

                        if (meetingStartDateForFilter.value != "Выбери дату" && meetingFinishDateForFilter.value != "Выбери дату") {

                            // Разделение дат фильтра на составляющие
                            val startDayList = filterFunctions.splitData(meetingStartDateForFilter.value)
                            val finishDayList = filterFunctions.splitData(meetingFinishDateForFilter.value)

                            // Получаем дату начала и конца для фильтра в нужном формате
                            val startDayNumber = filterFunctions.getDataNumber(startDayList)
                            val finishDayNumber = filterFunctions.getDataNumber(finishDayList)

                            // ПРОВЕРЯЕМ - ПОПАДАЕТ ЛИ НАШЕ МЕРОПРИЯТИЕ В ДИАПАЗОН ДАТ ИЗ ФИЛЬТРА

                            filterFunctions.checkMeetingDatePeriod(
                                meetingDate = meeting.dateInNumber, // дата мероприятия из БД в правильном формате
                                startFilterDay = startDayNumber, // Начало периода в правильном формате
                                finishFilterDay = finishDayNumber // конец периода в правильном формате
                            ) { inPeriod ->

                                // Результат сравнения

                                filter = if (inPeriod) {

                                    // Создаем фильтр, который будет сравниваться с фильтром из БД.
                                    // Получается, что фильтр будет постоянно меняться и подстраиваться к каждому мероприятию, которое попадает
                                    // В период

                                    filterFunctions.createMeetingFilter(
                                        city = cityForFilter.value, // город, который выбрал для фильтра пользователь
                                        category = meetingCategoryForFilter.value, // категория, которую выбрал пользователь для фильтра
                                        meeting.data!! // дата из БД, чтобы фильтр совпал с фильтром из БД и это мероприятие подошло
                                    )

                                } else {

                                    // если не попадает в диапазон, делаем фильтр как выбрал пользователь и мероприятие не попало в список
                                    filterFunctions.createMeetingFilter(
                                        city = cityForFilter.value, // город, который выбрал для фильтра пользователь
                                        category = meetingCategoryForFilter.value, // категория, которую выбрал пользователь для фильтра
                                        date = meetingStartDateForFilter.value // минимальная дата
                                    )

                                }
                            }
                        }

                        // УКАЗЫВАЕМ, КАКИЕ ФИЛЬТРЫ НАДО БРАТЬ В ЗАВИСИМОСТИ ОТ ТИПА ВЫБРАННОГО ПОЛЬЗОВАТЕЛЕМ ФИЛЬТРА

                        when (typeFilter) {

                            "cityCategoryDate" -> {

                                // Берем нужную строку из полученных фильтров и сравниваем с выбранным
                                // Ниже по аналогии

                                if (getFilter.cityCategoryDate == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }


                                }

                            }
                            "cityCategory" -> {
                                if (getFilter.cityCategory == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }
                                }
                            }
                            "cityDate" -> {
                                if (getFilter.cityDate == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }
                                }
                            }
                            "city" -> {
                                if (getFilter.city == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }
                                }
                            }
                            "categoryDate" -> {
                                if (getFilter.categoryDate == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }
                                }
                            }
                            "category" -> {
                                if (getFilter.category == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }
                                }
                            }
                            "date" -> {
                                if (getFilter.date == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }
                                }
                            }
                            "noFilter" -> {
                                if (getFilter.noFilter == filter) {

                                    // ---- АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ МЕРОПРИЯТИЯ ----

                                    // Если число из мероприятия меньше чем число СЕГОДНЯ
                                    if (meetingDataNumber < todayInRightFormat.toInt()) {

                                        // ---- УДАЛЯЕМ ЭТО МЕРОПРИЯТИЕ ВМЕСТЕ С КАРТИНКОЙ ---
                                        deleteMeeting(meeting.key!!, meeting.image1!!, meeting.placeKey!!){
                                            if(it){
                                                Log.d ("MyLog", "Мероприятие было успешно автоматически удалено вместе с картинкой")
                                            }
                                        }
                                    } else {

                                        meetingArray.add(meeting)

                                    }
                                }
                            }
                        }
                    }
                }

                if (meetingArray.isEmpty()) {
                    meetingsList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем мероприятие по умолчанию
                } else {

                    // ---- Сортируем список в зависимости от выбранной настройки ----

                    val sortedList = filterFunctions.sortedMeetingList(
                        meetingArray, // Передаем сырой список мероприятий
                        meetingSortingForFilter.value // настройка для сортировки
                    )
                    meetingsList.value = sortedList // возвращаем отсортированный список
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }


    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ МОИХ МЕРОПРИЯТИЙ --------

    fun readMeetingMyDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>()

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    if (auth.uid !=null) {
                        val meeting = item // путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info") // следующая папка с информацией о мероприятии
                            .child(auth.uid!!) // путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                            .child("meetingData") // папка с данными о мероприятии
                            .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                        if (meeting != null) {meetingArray.add(meeting)} //  если мероприятие не нал, то добавляем в список-черновик
                    }
                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в списке ничего нет, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray.asReversed() // если список не пустой, то возвращаем мои мероприятия с БД
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------- ФУНКЦИЯ СЧИТЫВАНИЯ ИЗБРАННЫХ МЕРОПРИЯТИЙ --------

    fun readMeetingFavDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            // функция при изменении данных в БД
            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>()

                for (item in snapshot.children){

                    // Считываем каждое мероприятие для сравнения

                    if (auth.uid !=null) {
                        val meeting = item // путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("info") // папка с информацией о мероприятии
                            .children.iterator().next() // папка уникального ключа пользователя. Пропускаем ее
                            .child("meetingData") // папка с данными о мероприятии
                            .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                        // Считываем папку, в которую попадают ключи добавивших в избранное

                        val meetingFav = item // путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                            .child("AddedToFavorites") // Папка со списком добавивших в избранное
                            .child(auth.uid!!) // ищем папку с ключом пользователя
                            .getValue(String::class.java) // забираем данные из БД если они есть

                        // сравниваем ключи мероприятия

                        if (meetingFav == auth.uid) {
                            // если ключи совпали, проверяем мероприятие на нал
                            if (meeting != null) {

                                //  если мероприятие не нал, то добавляем в список-черновик
                                meetingArray.add(meeting)
                            }
                        }
                    }
                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в списке ничего нет, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray.asReversed() // если список не пустой, то возвращаем избранные мероприятия с БД
                }
            }
            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }


    // ---- ФУНКЦИЯ ДОБАВЛЕНИЯ МЕРОПРИЯТИЯ К МЕСТУ ------

    private fun addMeetingToPlace (meetingKey: String, placeKey: String, callback: (result: Boolean) -> Unit){

        activity.placesDatabaseManager.placeDatabase
            .child(placeKey) //папка с ключем заведения
            .child("AddedMeetings") // папка, куда добавляются мероприятия этого заведения
            .child(meetingKey) // создаем подпапку со значением ключа
            .setValue(meetingKey) // записываем ключ мероприятия
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

    fun deleteMeetingFromPlace (meetingKey: String, placeKey: String, callback: (result: Boolean) -> Unit){

        activity.placesDatabaseManager.placeDatabase // обращаемся к БД
            .child(placeKey) // заходим в папку с уникальным ключем заведения
            .child("AddedMeetings") // заходим в папку мероприятий этого заведения
            .child(meetingKey) // заходим в папку с названием как наш ключ
            .removeValue() // удаляем значение
            .addOnCompleteListener{ deleted ->

                if (deleted.isSuccessful){

                    callback (true)

                } else {

                    callback (false)

                }
            }
    }




    // ------ ФУНКЦИЯ ПУБЛИКАЦИИ МЕРОПРИЯТИЯ --------

    fun publishMeeting(filledMeeting: MeetingsAdsClass, callback: (result: Boolean)-> Unit){

        // ---- СОЗДАЕМ ФИЛЬТРЫ ДЛЯ ВЫГРУЗКИ В БД ------

        val filledFilter = FilterMeetingClass(
            cityCategoryDate = filterFunctions.createMeetingFilter(city = filledMeeting.city ?: "Empty", category = filledMeeting.category ?: "Empty", date = filledMeeting.data ?: "Empty"),
            cityCategory = filterFunctions.createMeetingFilter(city = filledMeeting.city ?: "Empty", category = filledMeeting.category ?: "Empty"),
            cityDate = filterFunctions.createMeetingFilter(city = filledMeeting.city ?: "Empty", date = filledMeeting.data ?: "Empty"),
            city = filterFunctions.createMeetingFilter(city = filledMeeting.city ?: "Empty"),
            categoryDate = filterFunctions.createMeetingFilter(category = filledMeeting.category ?: "Empty", date = filledMeeting.data ?: "Empty"),
            category = filterFunctions.createMeetingFilter(category = filledMeeting.category ?: "Empty"),
            date = filterFunctions.createMeetingFilter(date = filledMeeting.data ?: "Empty"),
            noFilter = filterFunctions.createMeetingFilter()
        )

        meetingDatabase // записываем в базу данных
            .child(filledMeeting.key ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ МЕРОПРИЯТИЯ
            .child("info") // помещаем данные в папку info
            .child(auth.uid!!) // создаем для безопасности путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ, публикующего мероприятие
            .child("meetingData") // помещаем в папку
            .setValue(filledMeeting).addOnCompleteListener {

                if (it.isSuccessful) {

                    // если мероприятие опубликовано, запускаем публикацию фильтра
                    meetingDatabase
                        .child(filledMeeting.key ?: "empty") // создаем путь с УНИКАЛЬНЫМ КЛЮЧОМ МЕРОПРИЯТИЯ
                        .child("filterInfo") // помещаем данные в папку filterInfo
                        .setValue(filledFilter).addOnCompleteListener { filterIsPublish ->

                            if (filterIsPublish.isSuccessful){

                                // Если фильтер опубликовался и в нашем мероприятии есть заведение, то
                                if (filledMeeting.placeKey != null && filledMeeting.placeKey != "null" && filledMeeting.placeKey != ""){

                                    // НУЖНА ФУНКЦИЯ ПРОВЕРКА И УДАЛЕНИЕ КЛЮЧА ИЗ ЗАВЕДЕНИЯ
                                    // ВДРУГ Я ИЗМЕНИЛ ЗАВЕДЕНИЕ, ИЛИ ИЗМЕНИЛ НА ВВОД АДРЕСА ВРУЧНУЮ
                                    // ИЛИ НАОБОРОТ, БЫЛО ВРУЧНУЮ А ТЕПЕРЬ ЗАВЕДЕНИЕ

                                    // Добавляем в папку заведения ключ нашего мероприятия

                                    addMeetingToPlace(filledMeeting.key!!, filledMeeting.placeKey){ addedToPlace ->

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
                    // если не опубликовано, то возвращаем фалс
                    callback (false)
                }
            }
    }

    // --- ФУНКЦИЯ ДОБАВЛЕНИЯ МЕРОПРИЯТИЯ В ИЗБРАННОЕ ---------

    fun addFavouriteMeeting(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        activity.mAuth.uid?.let {
            meetingDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем мероприятия
                .child("AddedToFavorites") // заходим в папку добавших в избранное
                .child(it) // заходим в папку с названием как наш ключ
                .setValue(activity.mAuth.uid) // записываем наш ключ
        }?.addOnCompleteListener {
            // слушаем выполнение. Если успешно сделано, то...
            if (it.isSuccessful){
                // возвращаем колбак ТРУ
                callback (true)
            }
        }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ МЕРОПРИЯТИЯ----------

    fun deleteMeeting(meetingKey: String, placeKey: String = "", imageUrl: String, callback: (result: Boolean)-> Unit){

        // ---- СНАЧАЛА УДАЛЯЕМ КАРТИНКУ ------

        photoHelper.deleteMeetingImage(imageUrl){ resultDeletingImage ->

            // ЕСЛИ УДАЛЕНА ->

            if (resultDeletingImage) {

                Log.d ("MyLog", "Картинка мероприятия была успешно автоматически удалена")

                meetingDatabase // обращаемся к БД
                    .child(meetingKey) // заходим в папку с уникальным ключем мероприятия
                    .removeValue() // удаляем значение
                    .addOnCompleteListener{

                        // слушаем выполнение. Если успешно сделано, то...
                        if (it.isSuccessful){

                            if (placeKey != "") {

                                deleteMeetingFromPlace(meetingKey, placeKey){ deleted ->

                                    if (deleted) {

                                        // возвращаем колбак ТРУ
                                        callback (true)

                                    } else {

                                        // возвращаем колбак FALSE
                                        callback (false)
                                        Log.d ("MyLog", "Не удалился ключ с заведения")

                                    }

                                }

                            } else {

                                // возвращаем колбак ТРУ
                                callback (true)

                            }


                        }
                    }
            }
        }
    }

    // --- ФУНКЦИЯ УДАЛЕНИЯ МЕРОПРИЯТИЯ ИЗ ИЗБРАННОГО ----------

    fun removeFavouriteMeeting(key: String, callback: (result: Boolean)-> Unit){

        // если ключ пользователя не будет нал, то выполнится функция

        activity.mAuth.uid?.let {
            meetingDatabase // обращаемся к БД
                .child(key) // заходим в папку с уникальным ключем мероприятия
                .child("AddedToFavorites") // заходим в папку добавших в избранное
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

    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ - МЕРОПРИЯТИЕ В ИЗБРАННОМ УЖЕ ИЛИ НЕТ

    fun favIconMeeting(key: String, callback: (result: Boolean)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                    if (auth.uid !=null) {

                        // считываем данные мероприятия

                        val meeting = item
                            .child("info")
                            .children.iterator().next()
                            .child("meetingData")
                            .getValue(MeetingsAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        val meetingFav = item
                            .child("AddedToFavorites")
                            .child(auth.uid!!)
                            .getValue(String::class.java)

                        // проверка мероприятия на Null

                        if (meeting != null) {

                            // если ключ мероприятия равен переданному ключу
                            if (meeting.key == key) {

                                // если в списке добавивших в избранное есть мой ключ, то вернуть колбак тру
                                if (meetingFav == auth.uid){
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

    // --- ФУНКЦИЯ СЧЕТЧИКА ПРОСМОТРА МЕРОПРИЯТИЯ ---------

    fun viewCounterMeeting(key: String, callback: (result: Boolean)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (item in snapshot.children){

                        // считываем данные мероприятия

                        val meeting = item
                            .child("info")
                            .children.iterator().next()
                            .child("meetingData")
                            .getValue(MeetingsAdsClass::class.java)

                        // считываем список добавивших в избранное пользователей

                        var meetingCount = item
                            .child("viewCounter")
                            .child("viewCounter")
                            .getValue(Int::class.java)

                        // проверка мероприятия на Null

                        if (meeting != null) {

                            // если ключ мероприятия равен переданному ключу
                            if (meeting.key == key) {

                                // Если счетчик просмотров не нал
                                if (meetingCount != null) {
                                    meetingCount ++ // добавляем к счетчику 1

                                    // Перезаписываем новое значение счетчика

                                    meetingDatabase
                                        .child(key)
                                        .child("viewCounter")
                                        .child("viewCounter")
                                        .setValue(meetingCount)

                                    callback (true) // возвращаем колбак тру

                                } else {
                                    // если счетчик еще не создан, то создаем и устанавливаем значение 1
                                    meetingDatabase
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

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ МЕРОПРИЯТИЙ КОНКРЕТНОГО ЗАВЕДЕНИЯ С БАЗЫ ДАННЫХ --------

    fun readMeetingInPlaceDataFromDb(meetingsList: MutableState<List<MeetingsAdsClass>>, placeKey: String){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>() // создаем пустой список мероприятий

                for (item in snapshot.children){

                    val meeting = item // путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // следующая папка с информацией о мероприятии
                        .children.iterator().next() // путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null && meeting.placeKey == placeKey) {meetingArray.add(meeting)} // если мероприятие не пустое и ключ заведения совпадает с ключем заведения в мероприятии, добавляем в список

                }

                if (meetingArray.isEmpty()){
                    meetingsList.value = listOf(default) // если в список-черновик ничего не добавилось, то добавляем мероприятие по умолчанию
                } else {
                    meetingsList.value = meetingArray // если добавились мероприятия в список, то этот новый список и передаем
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    // ------ ФУНКЦИЯ СЧИТЫВАНИЯ КОЛИЧЕСТВА МЕРОПРИЯТИЙ КОНКРЕТНОГО ЗАВЕДЕНИЯ С БАЗЫ ДАННЫХ --------

    fun readMeetingCounterInPlaceDataFromDb(placeKey: String, callback: (meetingsCounter: Int)-> Unit){

        meetingDatabase.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val meetingArray = ArrayList<MeetingsAdsClass>() // создаем пустой список мероприятий

                for (item in snapshot.children){

                    // создаем переменную meeting, в которую в конце поместим наш ДАТАКЛАСС с объявлением с БД

                    val meeting = item // путь УНИКАЛЬНОГО КЛЮЧА МЕРОПРИЯТИЯ
                        .child("info") // следующая папка с информацией о мероприятии
                        .children.iterator().next() // путь УНИКАЛЬНОГО КЛЮЧА ПОЛЬЗОВАТЕЛЯ
                        .child("meetingData") // папка с данными о мероприятии
                        .getValue(MeetingsAdsClass::class.java) // забираем данные из БД в виде нашего класса МЕРОПРИЯТИЯ

                    if (meeting != null && meeting.placeKey == placeKey) {meetingArray.add(meeting)} // если мероприятие не пустое, добавляем в список

                }

                if (meetingArray.isEmpty()){
                    callback (0) // выдаем колбак, что список пустой
                } else {
                    callback (meetingArray.size) // выдаем размер списка мероприятий
                }
            }

            // в функцию onCancelled пока ничего не добавляем
            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }



}
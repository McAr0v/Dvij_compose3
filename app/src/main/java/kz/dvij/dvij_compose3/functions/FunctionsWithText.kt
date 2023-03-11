package kz.dvij.dvij_compose3.functions

fun returnMeetingWord (number: Int): String{

    return when (number) {

        1, 21, 31, 41, 51, 61 -> "Мероприятие"
        2, 3, 4, 22, 23, 24, 32, 33, 34, 42, 43, 44, 52, 53, 54 -> "Мероприятия"
        else -> "Мероприятий"

    }
}

fun returnPlaceWord (number: Int): String{

    return when (number) {

        1, 21, 31, 41, 51, 61 -> "Заведение"
        2, 3, 4, 22, 23, 24, 32, 33, 34, 42, 43, 44, 52, 53, 54 -> "Заведения"
        else -> "Заведений"

    }
}

fun returnStockWord (number: Int): String{

    return when (number) {

        1, 21, 31, 41, 51, 61 -> "Акция"
        2, 3, 4, 22, 23, 24, 32, 33, 34, 42, 43, 44, 52, 53, 54 -> "Акции"
        else -> "Акций"

    }
}
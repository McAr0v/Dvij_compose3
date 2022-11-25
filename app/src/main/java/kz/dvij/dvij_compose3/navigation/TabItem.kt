package kz.dvij.dvij_compose3.navigation

import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R

sealed class TabItem (
    @StringRes val title: Int,
    val route: String
        ) {
    object MyMeetings: TabItem (R.string.tab_my, MEETINGS_MY_ROOT)
    object TapeMeetings: TabItem (R.string.tab_tape, MEETINGS_TAPE_ROOT)
    object FavMeetings: TabItem (R.string.tab_fav, MEETINGS_FAV_ROOT)

    object MyPlaces: TabItem (R.string.tab_my, PLACES_MY_ROOT)
    object TapePlaces: TabItem (R.string.tab_tape, PLACES_TAPE_ROOT)
    object FavPlaces: TabItem (R.string.tab_fav, PLACES_FAV_ROOT)

    object MyStock: TabItem (R.string.tab_my, STOCK_MY_ROOT)
    object TapeStock: TabItem (R.string.tab_tape, STOCK_TAPE_ROOT)
    object FavStock: TabItem (R.string.tab_fav, STOCK_FAV_ROOT)
}

const val MEETINGS_MY_ROOT = "meetingsMyScreen"
const val MEETINGS_TAPE_ROOT = "meetingsTapeScreen"
const val MEETINGS_FAV_ROOT = "meetingsFavScreen"
const val PLACES_MY_ROOT = "placesMyScreen"
const val PLACES_TAPE_ROOT = "placesTapeScreen"
const val PLACES_FAV_ROOT = "placesFavScreen"
const val STOCK_MY_ROOT = "stockMyScreen"
const val STOCK_TAPE_ROOT = "stockTapeScreen"
const val STOCK_FAV_ROOT = "stockFavScreen"

// https://www.geeksforgeeks.org/tab-layout-in-android-using-jetpack-compose/

// https://swiftbook.ru/post/tutorials/how-to-create-tabs-with-jetpack-compose/
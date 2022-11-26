package kz.dvij.dvij_compose3.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.screens.MeetingsFavScreen
import kz.dvij.dvij_compose3.screens.MeetingsMyScreen
import kz.dvij.dvij_compose3.screens.MeetingsTapeScreen

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem (
    @DrawableRes val icon: Int ,
    @StringRes val title: Int,
    val screen: ComposableFun
        ) {
    object MyMeetings: TabItem (R.drawable.ic_fire, R.string.tab_my, { MeetingsMyScreen()})
    object TapeMeetings: TabItem (R.drawable.ic_fire, R.string.tab_tape, { MeetingsTapeScreen()})
    object FavMeetings: TabItem (R.drawable.ic_fire, R.string.tab_fav, { MeetingsFavScreen()})

    /*object MyPlaces: TabItem (R.string.tab_my, {})
    object TapePlaces: TabItem (R.string.tab_tape, {})
    object FavPlaces: TabItem (R.string.tab_fav, {})

    object MyStock: TabItem (R.string.tab_my, {})
    object TapeStock: TabItem (R.string.tab_tape, {})
    object FavStock: TabItem (R.string.tab_fav, {})*/
}

/* sealed class TabName (
    @StringRes val title: Int
        ) {
    object MyTab: TabName (R.string.tab_my)
    object TapeTab: TabName (R.string.tab_tape)
    object FavTab: TabName (R.string.tab_fav)
}*/

const val MEETINGS_MY_ROOT = "meetingsMyScreen"
// const val MEETINGS_TAPE_ROOT = "meetingsTapeScreen"
const val MEETINGS_TAPE_ROOT = "meetingsScreenTabLayuot"
const val MEETINGS_FAV_ROOT = "meetingsFavScreen"
const val PLACES_MY_ROOT = "placesMyScreen"
const val PLACES_TAPE_ROOT = "placesTapeScreen"
const val PLACES_FAV_ROOT = "placesFavScreen"
const val STOCK_MY_ROOT = "stockMyScreen"
const val STOCK_TAPE_ROOT = "stockTapeScreen"
const val STOCK_FAV_ROOT = "stockFavScreen"

// https://www.geeksforgeeks.org/tab-layout-in-android-using-jetpack-compose/

// https://swiftbook.ru/post/tutorials/how-to-create-tabs-with-jetpack-compose/
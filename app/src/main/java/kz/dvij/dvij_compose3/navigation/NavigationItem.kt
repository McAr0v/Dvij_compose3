package kz.dvij.dvij_compose3.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R

sealed class NavigationItem(

    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val navRoute: String

) {
    // создаем объекты меню.

    // ВАЖНО! Надо navRoute посмотреть как он делал и изменить. navRoute - это путь куда

    object Profile: NavigationItem(R.string.profile, R.drawable.ic_person, PROFILE_ROOT)
    object Meetings: NavigationItem(R.string.meetings, R.drawable.ic_meetings, MEETINGS_ROOT)
    object Places: NavigationItem(R.string.places, R.drawable.ic_baseline_places, PLACES_ROOT)
    object Stock: NavigationItem(R.string.stock, R.drawable.ic_stock, STOCK_ROOT)

}

const val MEETINGS_ROOT = "meetingsScreen"
const val PLACES_ROOT = "placesScreen"
const val STOCK_ROOT = "stockScreen"
const val PROFILE_ROOT = "profileScreen"

package kz.dvij.dvij_compose3

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class NavigationItem(

    @StringRes title: Int,
    @DrawableRes icon: Int,
    navRoute: String

) {
    // создаем объекты меню.

    // ВАЖНО! Надо navRoute посмотреть как он делал и изменить. navRoute - это путь куда

    object Profile: NavigationItem(R.string.profile, R.drawable.ic_person, "profile")
    object Meetings: NavigationItem(R.string.meetings, R.drawable.ic_meetings, "meetings")
    object Places: NavigationItem(R.string.places, R.drawable.ic_baseline_places, "places")
    object Stock: NavigationItem(R.string.stock, R.drawable.ic_stock, "stock")

}
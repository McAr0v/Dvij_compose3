package kz.dvij.dvij_compose3.navigation
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R

// РАБОТА С ЭЛЕМЕНТАМИ МЕНЮ

// Элементы НИЖНЕГО МЕНЮ BottomNavigationMenu

sealed class NavigationItem(

    // создаем запечатанный класс (sealed class), в котором прописываем
    // что должен содержать элемент нижнего меню

    @StringRes val title: Int, // заголовок. Чтобы обращаться к String-ресурсам, надо написать @StringRes
    @DrawableRes val icon: Int, // Иконка. Чтобы обращаться к Drawable-папке, нужно прописать @DrawableRes
    val navRoute: String // Навигационный путь. Ниже в константах прописаны пути экранов. Если их нужно изменить, менять в константе.

    // Обрати внимание, что возле title, icon, navRoute - везде стоит val.
    // Это сделано для того, чтобы к ним можно было обращаться при работе непосредственно с меню

) {

    // создаем объекты меню согласно тем элементам, которые мы указали выше.

    object Profile: NavigationItem(R.string.profile, R.drawable.ic_person, PROFILE_ROOT)
    object Meetings: NavigationItem(R.string.meetings, R.drawable.ic_meetings_2, MEETINGS_ROOT)
    object Places: NavigationItem(R.string.places, R.drawable.ic_baseline_places, PLACES_ROOT)
    object Stock: NavigationItem(R.string.stock, R.drawable.ic_fire, STOCK_ROOT)

}

// Константы путей navRoute

const val MEETINGS_ROOT = "meetingsScreen"
const val PLACES_ROOT = "placesScreen"
const val STOCK_ROOT = "stockScreen"
const val PROFILE_ROOT = "profileScreen"

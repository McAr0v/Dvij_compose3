package kz.dvij.dvij_compose3.navigation


import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R


// элементы TabMenu

sealed class TabName (

    // Прописываем, что должны содержать элементы меню табов

    @StringRes val title: Int
        )
    {
        // создаем сами табы

    object MyTab: TabName (R.string.tab_my)
    object TapeTab: TabName (R.string.tab_tape)
    object FavTab: TabName (R.string.tab_fav)

}


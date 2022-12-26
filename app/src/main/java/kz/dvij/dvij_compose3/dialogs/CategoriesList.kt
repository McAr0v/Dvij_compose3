package kz.dvij.dvij_compose3.dialogs

import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R

sealed class CategoriesList (
    @StringRes val categoryName: Int,
    val code: String
        ) {

    // ------- САМ СПИСОК КАТЕГОРИЙ ----------

    object HobieCat: CategoriesList (R.string.cat_hobie, "Hobie")
    object DefaultCat: CategoriesList (R.string.cat_default, "Def_cat")
    object ConcertsCat: CategoriesList (R.string.cat_concerts, "Concerts")
}
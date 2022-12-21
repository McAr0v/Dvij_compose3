package kz.dvij.dvij_compose3.dialogs

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R

sealed class CitiesList(
    @SuppressLint("SupportAnnotationUsage")
    @StringRes val cityName: Int,
    val code: String
) {
    object Astana: CitiesList (R.string.astana, "Astana")
    object UKa: CitiesList (R.string.ust_kamenogorsk, "UstKamenogorsk")
    object Ridder: CitiesList (R.string.ridder, "Ridder")
    object Altay: CitiesList (R.string.altay, "Altay")
    object Almaty: CitiesList (R.string.almaty, "Almaty")
}



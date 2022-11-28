package kz.dvij.dvij_compose3.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R

sealed class SideNavigationItems (
    @StringRes val title: Int, // заголовок. Чтобы обращаться к String-ресурсам, надо написать @StringRes
    @DrawableRes val icon: Int, // Иконка. Чтобы обращаться к Drawable-папке, нужно прописать @DrawableRes
    val navRoute: String
) {
    object About: SideNavigationItems (
        title = R.string.side_about,
        icon = R.drawable.ic_info,
        navRoute = ""
            )

    object PrivatePolicy: SideNavigationItems (
        title = R.string.side_private_policy,
        icon = R.drawable.ic_security,
        navRoute = ""
            )
    object Ads: SideNavigationItems (
        title = R.string.side_ad,
        icon = R.drawable.ic_ads,
        navRoute = ""
            )
    object Bugs: SideNavigationItems (
        title = R.string.side_report_bug,
        icon = R.drawable.ic_bug,
        navRoute = ""
            )
}
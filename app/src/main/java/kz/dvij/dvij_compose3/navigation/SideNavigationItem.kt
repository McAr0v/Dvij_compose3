package kz.dvij.dvij_compose3.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.accounthelper.SIGN_IN

sealed class SideNavigationItems (
    @StringRes val title: Int, // заголовок. Чтобы обращаться к String-ресурсам, надо написать @StringRes
    @StringRes val contentDescription: Int, // Описание для слабовидящих. Чтобы обращаться к String-ресурсам, надо написать @StringRes
    @DrawableRes val icon: Int, // Иконка. Чтобы обращаться к Drawable-папке, нужно прописать @DrawableRes
    val navRoute: String // путь для NavController
) {
    object About: SideNavigationItems (
        title = R.string.side_about,
        icon = R.drawable.ic_info,
        navRoute = ABOUT_ROOT,
        contentDescription = R.string.cd_about
            )

    object PrivatePolicy: SideNavigationItems (
        title = R.string.side_private_policy,
        icon = R.drawable.ic_security,
        navRoute = POLICY_ROOT,
        contentDescription = R.string.cd_private_policy
            )
    object Ads: SideNavigationItems (
        title = R.string.side_ad,
        icon = R.drawable.ic_ads,
        navRoute = ADS_ROOT,
        contentDescription = R.string.cd_ads
            )
    object Bugs: SideNavigationItems (
        title = R.string.side_report_bug,
        icon = R.drawable.ic_bug,
        navRoute = BUGS_ROOT,
        contentDescription = R.string.cd_bugs
            )
}

const val ABOUT_ROOT = "About"
const val POLICY_ROOT = "Private_Policy"
const val ADS_ROOT = "Ads"
const val BUGS_ROOT = "Bugs"
const val REG_ROOT = "RegRoot"
const val LOG_IN_ROOT = "LoginRoot"
const val THANK_YOU_PAGE_ROOT = "thankyou"
const val FORGOT_PASSWORD_ROOT = "ForgotPassword"
const val RESET_PASSWORD_SUCCESS = "resetPasswordSuccess"

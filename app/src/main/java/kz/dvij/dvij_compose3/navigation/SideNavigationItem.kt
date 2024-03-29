package kz.dvij.dvij_compose3.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kz.dvij.dvij_compose3.R

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

    object BugsList: SideNavigationItems (
        title = R.string.side_bug_list,
        icon = R.drawable.ic_bug,
        navRoute = BUGS_LIST_ROOT,
        contentDescription = R.string.cd_bugs_list
    )

    object CallbackScreen: SideNavigationItems (
        title = R.string.callback_headline,
        icon = R.drawable.ic_callback,
        navRoute = CALLBACK_ROOT,
        contentDescription = R.string.cd_callback_screen
    )

    object CallbackListScreen: SideNavigationItems (
        title = R.string.callback_list_headline,
        icon = R.drawable.ic_callback,
        navRoute = CALLBACK_LIST_ROOT,
        contentDescription = R.string.cd_callback_list_screen
    )

    object CityListScreen: SideNavigationItems (
        title = R.string.city_list,
        icon = R.drawable.ic_baseline_places,
        navRoute = CITIES_LIST_ROOT,
        contentDescription = R.string.cd_city_list_screen
    )

}

const val ABOUT_ROOT = "About"
const val CALLBACK_ROOT = "Callback"
const val CALLBACK_LIST_ROOT = "Callback_LIST"
const val POLICY_ROOT = "Private_Policy"
const val ADS_ROOT = "Ads"
const val BUGS_ROOT = "Bugs"
const val BUGS_LIST_ROOT = "Bugs_List_Root"
const val CITIES_LIST_ROOT = "Cities_List_Root"
const val REG_ROOT = "RegRoot"
const val LOG_IN_ROOT = "LoginRoot"
const val THANK_YOU_PAGE_ROOT = "thankYou"
const val FORGOT_PASSWORD_ROOT = "ForgotPassword"
const val RESET_PASSWORD_SUCCESS = "resetPasswordSuccess"
const val CREATE_MEETINGS_SCREEN = "createMeetingsScreen"
const val EDIT_MEETINGS_SCREEN = "editMeetingsScreen"
const val MEETING_VIEW = "MeetingView"
const val CREATE_PLACES_SCREEN = "createPlacesScreen"
const val EDIT_PLACES_SCREEN = "editPlacesScreen"
const val PLACE_VIEW = "PlaceView"
const val CREATE_STOCK_SCREEN = "createStockScreen"
const val EDIT_STOCK_SCREEN = "editStockScreen"
const val STOCK_VIEW = "StockView"
const val CREATE_USER_INFO_SCREEN = "CreateUserInfoScreen"

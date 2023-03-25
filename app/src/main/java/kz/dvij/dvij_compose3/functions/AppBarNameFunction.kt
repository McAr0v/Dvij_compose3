package kz.dvij.dvij_compose3.functions

import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.navigation.*

fun appBarNameFunction (currentRoute: String?): Int {

    return when (currentRoute) {
        null -> R.string.meetings
        MEETINGS_ROOT -> R.string.meetings
        PLACES_ROOT -> R.string.places
        STOCK_ROOT -> R.string.stock
        PROFILE_ROOT -> R.string.profile
        ABOUT_ROOT -> R.string.side_about
        POLICY_ROOT -> R.string.side_private_policy
        ADS_ROOT -> R.string.side_ad
        BUGS_ROOT -> R.string.side_report_bug
        CREATE_MEETINGS_SCREEN -> R.string.create_meeting
        EDIT_MEETINGS_SCREEN -> R.string.edit_meeting
        CREATE_PLACES_SCREEN -> R.string.create_place
        CREATE_STOCK_SCREEN -> R.string.create_stock
        MEETING_VIEW -> R.string.meetings
        PLACE_VIEW -> R.string.places
        STOCK_VIEW -> R.string.stock
        BUGS_LIST_ROOT -> R.string.bugs_list_name
        CALLBACK_ROOT -> R.string.callback_headline
        CALLBACK_LIST_ROOT -> R.string.callback_list_headline
        else -> R.string.app_name

    }

}
package kz.dvij.dvij_compose3.functions

import kz.dvij.dvij_compose3.R

fun checkDataOnCreateCallback (
    phone: String,
    subject: String,
    text: String

): Int{

    return if (phone == ""){

        R.string.callback_no_phone

    } else if (subject == ""){

        R.string.callback_no_subject

    } else if (text == ""){

        R.string.callback_no_text

    } else {

        0

    }
}
package kz.dvij.dvij_compose3.functions

import kz.dvij.dvij_compose3.R

fun checkDataOnCreateBugText (
    email: String,
    subject: String,
    text: String

): Int{

    return if (email == ""){

        R.string.bug_no_email

    } else if (subject == ""){

        R.string.bug_no_subject

    } else if (text == ""){

        R.string.bug_no_text

    } else {

        0

    }
}
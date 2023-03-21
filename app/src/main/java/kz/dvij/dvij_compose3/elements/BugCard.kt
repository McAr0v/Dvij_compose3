package kz.dvij.dvij_compose3.elements

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.FOR_CARDS
import kz.dvij.dvij_compose3.constants.PRIMARY
import kz.dvij.dvij_compose3.firebase.BugsAdsClass
import kz.dvij.dvij_compose3.ui.theme.Grey_ForCards
import kz.dvij.dvij_compose3.ui.theme.Grey_Text
import kz.dvij.dvij_compose3.ui.theme.Typography
import kz.dvij.dvij_compose3.ui.theme.WhiteDvij

class BugCard() {

    @SuppressLint("NotConstructor")
    @Composable
    fun BugCard (
        bugItem: BugsAdsClass,
        navController: NavController
    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(
                    color = Grey_ForCards,
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable { }
                .padding(20.dp)

        ) {

            if (bugItem.ticketNumber != null && bugItem.ticketNumber != ""){

                Text(
                    text = bugItem.ticketNumber,
                    color = Grey_Text,
                    style = Typography.labelMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

            }

            if (bugItem.subject != null && bugItem.subject != ""){

                Text(
                    text = bugItem.subject,
                    color = WhiteDvij,
                    style = Typography.titleMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

            }

            if (bugItem.publishDate != null && bugItem.publishDate != ""){

                Text(
                    text = bugItem.publishDate,
                    color = Grey_Text,
                    style = Typography.labelMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

            }

            if (bugItem.status != null && bugItem.status != ""){

                Bubble(
                    buttonText = bugItem.status,
                    typeButton = when (bugItem.status){

                        "new" -> ATTENTION
                        "in_work" -> PRIMARY
                        "done" -> DARK
                        else -> "Not"

                    }
                ) {

                }

                Spacer(modifier = Modifier.height(10.dp))

            }

            if (bugItem.text != null && bugItem.text != ""){

                Text(
                    text = bugItem.text,
                    color = Grey_Text,
                    style = Typography.labelMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

            }




        }


    }

}
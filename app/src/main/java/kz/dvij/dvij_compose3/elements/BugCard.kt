package kz.dvij.dvij_compose3.elements

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
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
        bugInfoFromAct: MutableState<BugsAdsClass>,
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
                .clickable {
                    bugInfoFromAct.value = bugItem
                    // navController.navigate()
                }
                .padding(20.dp)

        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {

                    if (bugItem.ticketNumber != null && bugItem.ticketNumber != ""){

                        Text(
                            text = bugItem.ticketNumber,
                            color = Grey_Text,
                            style = Typography.labelMedium
                        )

                    }

                    if (
                        bugItem.publishDate != null
                        && bugItem.publishDate != ""
                        && bugItem.publishTime != null
                        && bugItem.publishTime != ""
                    ){

                        Text(
                            text = "${bugItem.publishDate} ${bugItem.publishTime}",
                            color = Grey_Text,
                            style = Typography.labelMedium
                        )

                    }
                }

                if (bugItem.status != null && bugItem.status != ""){

                    Bubble(
                        buttonText = when (bugItem.status){

                            "new" -> "Новое сообщение"
                            "in_work" -> "В работе"
                            "done" -> "Выполнено"
                            else -> "Отложено"

                        },
                        typeButton = when (bugItem.status){

                            "new" -> ATTENTION
                            "in_work" -> PRIMARY
                            "done" -> DARK
                            else -> "Not"

                        }
                    ) {}
                }

            }



            if (bugItem.subject != null && bugItem.subject != ""){

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = bugItem.subject,
                    color = WhiteDvij,
                    style = Typography.titleMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

            }

            if (bugItem.text != null && bugItem.text != ""){

                Text(
                    text = bugItem.text,
                    color = WhiteDvij,
                    style = Typography.bodySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

            }
        }
    }
}
package kz.dvij.dvij_compose3.elements

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.PRIMARY
import kz.dvij.dvij_compose3.firebase.BugsAdsClass
import kz.dvij.dvij_compose3.firebase.BugsDatabaseManager
import kz.dvij.dvij_compose3.firebase.CallbackAdsClass
import kz.dvij.dvij_compose3.firebase.CallbackDatabaseManager
import kz.dvij.dvij_compose3.navigation.BUGS_LIST_ROOT
import kz.dvij.dvij_compose3.navigation.CALLBACK_LIST_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

class CallbackCard() {

    @SuppressLint("NotConstructor")
    @Composable
    fun CallbackCard (
        callbackItem: CallbackAdsClass,
        navController: NavController,
        act: MainActivity,
        openLoadingState: MutableState<Boolean>
    ){

        val chosenStatus = remember{ mutableStateOf("") }
        val openChangeStatus = remember { mutableStateOf(false) } // диалог ЗАВЕДЕНИЙ

        val callbackDatabase = CallbackDatabaseManager()
        val bugCard = BugCard()

        val bugsDatabase = BugsDatabaseManager()

        val openConfirmChoose = remember { mutableStateOf(false) } // диалог действительно хотите удалить?

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(
                    color = Grey_ForCards,
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable {

                }
                .padding(20.dp)

        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                if (callbackItem.senderPhone != null && callbackItem.senderPhone != "" && callbackItem.senderPhone != "+7" && callbackItem.senderPhone != "+77"){

                    Text(
                        text = "+7${callbackItem.senderPhone}",
                        color = YellowDvij,
                        style = Typography.bodySmall,
                        modifier = Modifier.clickable { act.callAndWhatsapp.makeACall(callbackItem.senderPhone) }
                    )

                }



                if (chosenStatus.value == ""){

                    if (callbackItem.status != null && callbackItem.status != "null" && callbackItem.status != "") {

                        Bubble(
                            buttonText = callbackItem.status,
                            typeButton = when (callbackItem.status){

                                "Новые сообщения" -> ATTENTION
                                "В работе" -> PRIMARY
                                "Получена договоренность" -> DARK
                                else -> "Not"

                            }
                        ) {

                            openChangeStatus.value = true

                        }
                    }

                } else {

                    Bubble(
                        buttonText = chosenStatus.value,
                        typeButton = when (chosenStatus.value){

                            "Новые сообщения" -> ATTENTION
                            "В работе" -> PRIMARY
                            "Получена договоренность" -> DARK
                            else -> "Not"

                        }
                    ) {

                        openChangeStatus.value = true

                    }

                }


            }





            if (callbackItem.subject != null && callbackItem.subject != ""){

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = callbackItem.subject,
                    color = WhiteDvij,
                    style = Typography.titleMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

            }

            if (callbackItem.text != null && callbackItem.text != ""){

                Text(
                    text = callbackItem.text,
                    color = WhiteDvij,
                    style = Typography.bodySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                if (callbackItem.ticketNumber != null && callbackItem.ticketNumber != ""){

                    Text(
                        text = callbackItem.ticketNumber,
                        color = Grey_Text,
                        style = Typography.labelMedium
                    )

                }

                if (
                    callbackItem.publishDate != null
                    && callbackItem.publishDate != ""
                    && callbackItem.publishTime != null
                    && callbackItem.publishTime != ""
                ){

                    Text(
                        text = "${callbackItem.publishDate} ${callbackItem.publishTime}",
                        color = Grey_Text,
                        style = Typography.labelMedium
                    )

                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                if (chosenStatus.value != callbackItem.status && chosenStatus.value != "") {

                    Text(
                        text = "Сохранить",
                        color = YellowDvij,
                        style = Typography.bodySmall,
                        modifier = Modifier.clickable {

                            openLoadingState.value = true

                            val filledCallback = CallbackAdsClass (

                                senderPhone = callbackItem.senderPhone,
                                subject = callbackItem.subject,
                                text = callbackItem.text,
                                ticketNumber = callbackItem.ticketNumber,
                                publishTime = callbackItem.publishTime,
                                publishDate = callbackItem.publishDate,
                                status = chosenStatus.value
                            )

                            callbackDatabase.publishCallback(filledCallback = filledCallback) {

                                if (it) {

                                    navController.navigate(CALLBACK_LIST_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                                    // показываем ТОСТ

                                    Toast.makeText(
                                        act,
                                        "Сообщение успешно отправлено!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {

                                    Toast.makeText(
                                        act,
                                        "Что-то пошло не так( Попробуй позже",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                            }

                        }
                    )

                }

                Spacer(modifier = Modifier
                    .width(20.dp)
                    .weight(1f))

                Text(
                    text = "Удалить",
                    color = AttentionRed,
                    style = Typography.bodySmall,
                    modifier = Modifier.clickable { openConfirmChoose.value = true }
                )

            }

            if (openChangeStatus.value){

                bugCard.StatusDialog(chosenStatus = chosenStatus, list = listOf("Новые сообщения", "В работе", "Получена договоренность", "Отложенные", "Не интересно")) {

                    openChangeStatus.value = false

                }

            }

            if (openConfirmChoose.value) {

                ConfirmDialog(onDismiss = { openConfirmChoose.value = false }){

                    openLoadingState.value = true

                    callbackDatabase.deleteCallback(ticketNumber = callbackItem.ticketNumber!!) {

                        if (it) {

                            navController.navigate(CALLBACK_LIST_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                            // показываем ТОСТ

                            Toast.makeText(
                                act,
                                "Сообщение успешно удалено!",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                act,
                                "Что-то пошло не так( Попробуй позже",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }
                }
            }
        }
    }

}
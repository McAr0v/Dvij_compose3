package kz.dvij.dvij_compose3.elements

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.ATTENTION
import kz.dvij.dvij_compose3.constants.DARK
import kz.dvij.dvij_compose3.constants.PRIMARY
import kz.dvij.dvij_compose3.firebase.BugsAdsClass
import kz.dvij.dvij_compose3.firebase.BugsDatabaseManager
import kz.dvij.dvij_compose3.navigation.BUGS_LIST_ROOT
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

class BugCard() {

    @SuppressLint("NotConstructor")
    @Composable
    fun BugCard (
        //bugInfoFromAct: MutableState<BugsAdsClass>,
        bugItem: BugsAdsClass,
        navController: NavController,
        act: MainActivity
    ){

        val status = remember{ mutableStateOf(bugItem.status) }
        val bugInfo = remember{ mutableStateOf(BugsAdsClass()) }
        val chosenStatus = remember{ mutableStateOf("") }
        val openChangeStatus = remember { mutableStateOf(false) } // диалог ЗАВЕДЕНИЙ
        val bugsDatabase = BugsDatabaseManager()
        val openConfirmChoose = remember {mutableStateOf(false)} // диалог действительно хотите удалить?

        /*if (bugItem.ticketNumber != null) {

            bugsDatabase.readBugStatusFromDb(ticketNumber = bugItem.ticketNumber){

                status.value = it

            }

        }*/


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(
                    color = Grey_ForCards,
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable {
                    //bugInfoFromAct.value = bugItem
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

                if (chosenStatus.value == ""){

                    if (bugItem.status != null && bugItem.status != "null" && bugItem.status != "") {

                        Bubble(
                            buttonText = bugItem.status,
                            typeButton = when (bugItem.status){

                                "Новые сообщения" -> ATTENTION
                                "В работе" -> PRIMARY
                                "Выполненные" -> DARK
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
                            "Выполненные" -> DARK
                            else -> "Not"

                        }
                    ) {

                        openChangeStatus.value = true

                    }

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

            if (bugItem.senderEmail != null && bugItem.senderEmail != ""){

                Text(
                    text = bugItem.senderEmail,
                    color = Grey_Text,
                    style = Typography.labelMedium
                )

            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                if (chosenStatus.value != bugItem.status && chosenStatus.value != "") {

                    Text(
                        text = "Сохранить",
                        color = YellowDvij,
                        style = Typography.bodySmall,
                        modifier = Modifier.clickable {

                            val filledBug = BugsAdsClass (

                                senderEmail = bugItem.senderEmail,
                                subject = bugItem.subject,
                                text = bugItem.text,
                                ticketNumber = bugItem.ticketNumber,
                                publishTime = bugItem.publishTime,
                                publishDate = bugItem.publishDate,
                                status = chosenStatus.value
                                    )

                            bugsDatabase.publishBug(filledBug = filledBug){

                                if (it) {

                                    navController.navigate(BUGS_LIST_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

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

                Spacer(modifier = Modifier.width(20.dp).weight(1f))

                Text(
                    text = "Удалить",
                    color = AttentionRed,
                    style = Typography.bodySmall,
                    modifier = Modifier.clickable { openConfirmChoose.value = true }
                )

            }

            if (openChangeStatus.value){

                StatusDialog(chosenStatus = chosenStatus, list = listOf("Новые сообщения", "В работе", "Выполненные",  "Отложенные")) {

                    openChangeStatus.value = false

                }

            }

            if (openConfirmChoose.value) {

                ConfirmDialog(onDismiss = { openConfirmChoose.value = false }){

                    bugsDatabase.deleteBug(ticketNumber = bugItem.ticketNumber!!){

                        if (it) {

                            navController.navigate(BUGS_LIST_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

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

    @Composable
    fun StatusDialog(
        chosenStatus: MutableState<String>,
        list: List<String>,
        onDismiss: () -> Unit
    ) {

        // ------ САМ ДИАЛОГ ---------

        Dialog(
            onDismissRequest = { onDismiss() } // действие на нажатие за пределами диалога
        ) {

            // -------- СОДЕРЖИМОЕ ДИАЛОГА ---------

            Column(
                modifier = Modifier
                    .border(
                        2.dp, // толщина границы
                        color = YellowDvij, // цвет границы
                        shape = RoundedCornerShape(15.dp) // скругление углов
                    )
                    .background(
                        Grey_Background, // цвет фона
                        shape = RoundedCornerShape(15.dp) // скругление углов
                    )
                    .padding(20.dp) // отступы
                    .fillMaxWidth() // занять всю ширину

            ) {


                // ------- ЗАГЛОВОК и КНОПКА ЗАКРЫТЬ -----------

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание элементов по центру
                    horizontalArrangement = Arrangement.End // выравнивание по горизонтали
                ) {

                    // --------- ЗАГОЛОВОК ----------

                    androidx.compose.material3.Text(
                        text = "Выбери статус", // текст заголовка
                        style = Typography.titleMedium, // стиль заголовка
                        color = WhiteDvij, // цвет заголовка
                        modifier = Modifier.weight(1f)
                    ) // занять всю оставшуюся ширину

                    Spacer(modifier = Modifier.height(20.dp)) // разделитель

                    // ------------- ИКОНКА ЗАКРЫТЬ ----------------

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close), // сама иконка
                        contentDescription = stringResource(id = R.string.close_page), // описание для слабовидяших
                        tint = WhiteDvij, // цвет иконки
                        modifier = Modifier.clickable { onDismiss() } // действие на нажатие
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                // ---------- СПИСОК Сортировки -------------

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth() // занять ширину
                        .background(
                            Grey_OnBackground, // цвет фона
                            shape = RoundedCornerShape(15.dp) // скругление углов
                        )
                        .padding(20.dp), // отступ
                    verticalArrangement = Arrangement.spacedBy(20.dp) // расстояние между элементами

                ) {

                    items(list) { item ->

                        // ------------ строка с названием сортировок -------------

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // действие на нажатие на элемент
                                chosenStatus.value =
                                    item // выбранная сортировка теперь та, которую выбрали, а не по умолчанию
                                onDismiss() // закрыть диалог
                            }
                        ) {

                            androidx.compose.material3.Text(
                                text = item, // само название сортировки
                                color = WhiteDvij, // цвет текста
                                style = Typography.bodySmall // стиль текста
                            )

                        }
                    }
                }
            }
        }
    }

}
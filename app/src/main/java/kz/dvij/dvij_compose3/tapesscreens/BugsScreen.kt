package kz.dvij.dvij_compose3.tapesscreens

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.PRIMARY
import kz.dvij.dvij_compose3.constants.SECONDARY
import kz.dvij.dvij_compose3.elements.*
import kz.dvij.dvij_compose3.firebase.BugsAdsClass
import kz.dvij.dvij_compose3.firebase.BugsDatabaseManager
import kz.dvij.dvij_compose3.firebase.StockCardClass
import kz.dvij.dvij_compose3.firebase.UserInfoClass
import kz.dvij.dvij_compose3.functions.checkDataOnCreateBugText
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.ui.theme.Grey_Background
import kz.dvij.dvij_compose3.ui.theme.Primary70
import kz.dvij.dvij_compose3.ui.theme.Typography
import kz.dvij.dvij_compose3.ui.theme.WhiteDvij
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BugsScreen(
    act: MainActivity,
    filledUserInfo: UserInfoClass = UserInfoClass(), // данные пользователя с БД
    navController: NavController
){


    val bugsDatabase = BugsDatabaseManager()

    val openLoading = remember { mutableStateOf(false) } // диалог ИДЕТ ЗАГРУЗКА

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Grey_Background)
            .padding(horizontal = 20.dp, vertical = 30.dp),
        
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
        
    ) {
        
        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.bug_say), color = WhiteDvij, style = Typography.titleLarge)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.bug_text), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.bug_text1), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.bug_text2), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.bug_text3), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(30.dp))

        val email = if (filledUserInfo.email != null && filledUserInfo.email != ""){

            fieldEmailComponent(act = act, inputEmail = filledUserInfo.email)

        } else {

            fieldEmailComponent(act = act, inputEmail = filledUserInfo.email)

        }

        Spacer(modifier = Modifier.height(20.dp))

        val subject = fieldInstagramComponent(act = act, icon = kz.dvij.dvij_compose3.R.drawable.ic_email, placeHolder = "Напиши тему письма")

        Spacer(modifier = Modifier.height(20.dp))

        val text = fieldDescriptionComponent(placeHolder = "Опиши свое предложение или причину ошибки")

        Spacer(modifier = Modifier.height(20.dp))

        ButtonCustom(buttonText = "Отправить") {

            val check = checkDataOnCreateBugText(email = email, subject = subject, text = text)

            if (check != 0) {

                Toast.makeText(act, act.resources.getString(check), Toast.LENGTH_SHORT).show()

            } else {

                openLoading.value = true // открываем диалог загрузки

                GlobalScope.launch(Dispatchers.Main) {

                    val getNowTime = ZonedDateTime.now(ZoneId.of("Asia/Almaty"))
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE, HH:mm"))

                    val splitDate = getNowTime.split(", ")

                    val nowTime = splitDate[2]
                    val nowDate = splitDate[0]

                    val filledBug = BugsAdsClass(
                        senderEmail = email,
                        subject = subject,
                        text = text,
                        ticketNumber = bugsDatabase.bugsDatabase.push().key,
                        publishDate = nowDate,
                        publishTime = nowTime,
                        status = "Новые сообщения"
                    )

                    bugsDatabase.publishBug(filledBug){

                        if (it) {

                            navController.navigate(MEETINGS_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

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
            }

        }

        if (openLoading.value) {
            LoadingScreen(act.resources.getString(R.string.ss_loading))
        }
        
    }
}

@Composable
fun BugsListScreen (
    navController: NavController,
    bugInfoFromAct: MutableState<BugsAdsClass>,
    act: MainActivity
) {

    val filterDialog = FilterDialog(act)

    val bugCard = BugCard()

    val bugsDatabase = BugsDatabaseManager()

    val bugList = remember {
        mutableStateOf(listOf<BugsAdsClass>())
    }

    val bugFilter = remember {
        mutableStateOf("Все сообщения")
    }

    val openFilterDialog = remember { mutableStateOf(false) }

    val sortingList = listOf("Все сообщения", "Новые сообщения", "В работе", "Выполненные", "Отложенные")


    if (openFilterDialog.value){

        filterDialog.SortingDialog(sorting = bugFilter, list = sortingList) {

            openFilterDialog.value = false

        }

    }

    bugsDatabase.readBugListFromDb(
        bugList = bugList,
        status = bugFilter.value
            )

    Column(
        modifier = Modifier
            .background(Grey_Background)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        ButtonCustom(
            buttonText = bugFilter.value,
            typeButton = if (bugFilter.value == "Все сообщения") SECONDARY else PRIMARY
        ) {
            openFilterDialog.value = true
        }
        
        Spacer(modifier = Modifier.height(10.dp))

        if (bugList.value.isNotEmpty() && bugList.value != listOf(BugsAdsClass()) ){

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey_Background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){

                if (bugList.value.isNotEmpty() && bugList.value != listOf(BugsAdsClass()) ){

                    items(bugList.value){ item ->

                        bugCard.BugCard(
                            bugItem = item,
                            navController = navController,
                            //bugInfoFromAct = bugInfoFromAct,
                            act = act
                        )

                    }

                }

            }

        } else if (bugList.value == listOf(BugsAdsClass())){

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.empty_meeting),
                    style = Typography.bodySmall,
                    color = WhiteDvij
                )
            }


        } else {

            LoadingScreen(messageText = "Идет загрузка")

        }

    }

}
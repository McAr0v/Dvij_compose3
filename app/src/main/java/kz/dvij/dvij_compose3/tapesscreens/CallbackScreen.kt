package kz.dvij.dvij_compose3.tapesscreens

import android.os.Build
import android.telecom.Call
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kz.dvij.dvij_compose3.firebase.*
import kz.dvij.dvij_compose3.functions.checkDataOnCreateCallback
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.ui.theme.Grey_Background
import kz.dvij.dvij_compose3.ui.theme.Typography
import kz.dvij.dvij_compose3.ui.theme.WhiteDvij
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CallbackScreen(
    act: MainActivity,
    filledUserInfo: UserInfoClass = UserInfoClass(), // данные пользователя с БД
    navController: NavController
){

    var phoneNumberFromDb by rememberSaveable {mutableStateOf(filledUserInfo.phoneNumber)}

    // ПУСТОЙ ТЕЛЕФОН // Переменная, если нет телефона ни в пользователе, ни в мероприятии. ОБЫЧНО ТАК В РЕЖИМЕ СОЗДАНИЯ
    var phoneNumber by rememberSaveable { mutableStateOf("7") }

    val callbackCard = CallbackCard()

    val bugsDatabase = BugsDatabaseManager()

    val callbackDatabase = CallbackDatabaseManager()

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

        Text(text = stringResource(id = R.string.callback_headline), color = WhiteDvij, style = Typography.titleLarge)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = R.string.callback_dear_friend), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = R.string.callback_text1), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = R.string.callback_text2), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = R.string.callback_text3), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(30.dp))

        val phone = if (phoneNumberFromDb != null && phoneNumberFromDb != "" && phoneNumberFromDb != "+7" && phoneNumberFromDb != "+77") {

            fieldPhoneComponent(phone = phoneNumberFromDb!!, onPhoneChanged = { phoneNumberFromDb = it })

        } else {

            fieldPhoneComponent(phone = phoneNumber, onPhoneChanged = { phoneNumber = it })

        }

        Spacer(modifier = Modifier.height(20.dp))

        val subject = fieldInstagramComponent(act = act, icon = R.drawable.ic_email, placeHolder = "Напиши тему предложения")

        Spacer(modifier = Modifier.height(20.dp))

        val text = fieldDescriptionComponent(placeHolder = "Напиши свое предложение здесь")

        Spacer(modifier = Modifier.height(20.dp))

        ButtonCustom(buttonText = "Отправить") {

            val check = checkDataOnCreateCallback(phone = phone, subject = subject, text = text)

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

                    val filledCallback = CallbackAdsClass(
                        senderPhone = phone,
                        subject = subject,
                        text = text,
                        ticketNumber = bugsDatabase.bugsDatabase.push().key,
                        publishDate = nowDate,
                        publishTime = nowTime,
                        status = "Новые сообщения"
                    )


                    callbackDatabase.publishCallback(filledCallback = filledCallback) {

                        if (it) {

                            navController.navigate(MEETINGS_ROOT) {popUpTo(0)} // переходим на страницу мероприятий

                            // показываем ТОСТ

                            Toast.makeText(
                                act,
                                "Сообщение успешно отправлено! С тобой обязательно свяжутся!",
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

    if (openLoading.value) {
        LoadingScreen(act.resources.getString(R.string.ss_loading))
    }

}

@Composable
fun CallbackListScreen (
    navController: NavController,
    act: MainActivity
) {

    val filterDialog = FilterDialog(act)

    val callbackCard = CallbackCard()

    val bugCard = BugCard()

    val callbackDatabase = CallbackDatabaseManager()

    val callbackList = remember {
        mutableStateOf(listOf<CallbackAdsClass>())
    }

    val callbackFilter = remember {
        mutableStateOf("Все сообщения")
    }

    val openFilterDialog = remember { mutableStateOf(false) }

    val sortingList = listOf("Все сообщения", "Новые сообщения", "В работе", "Получена договоренность", "Отложенные", "Не интересно")

    val openLoading = remember { mutableStateOf(false) } // диалог ИДЕТ ЗАГРУЗКА


    if (openFilterDialog.value){

        filterDialog.SortingDialog(sorting = callbackFilter, list = sortingList) {

            openFilterDialog.value = false

        }

    }

    callbackDatabase.readCallbackListFromDb(
        callbackList = callbackList,
        status = callbackFilter.value
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
            buttonText = callbackFilter.value,
            typeButton = if (callbackFilter.value == "Все сообщения") SECONDARY else PRIMARY
        ) {
            openFilterDialog.value = true
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (callbackList.value.isNotEmpty() && callbackList.value != listOf(CallbackAdsClass()) ){

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey_Background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){

                if (callbackList.value.isNotEmpty() && callbackList.value != listOf(CallbackAdsClass()) ){

                    items(callbackList.value){ item ->

                        callbackCard.CallbackCard(callbackItem = item, navController = navController, act = act, openLoadingState = openLoading)

                    }

                }

            }

        } else if (callbackList.value == listOf(CallbackAdsClass())){

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

    if (openLoading.value) {
        LoadingScreen(act.resources.getString(R.string.ss_loading))
    }

}
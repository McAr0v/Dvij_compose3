package kz.dvij.dvij_compose3.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.constants.INSTAGRAM_URL
import kz.dvij.dvij_compose3.constants.TELEGRAM_URL
import kz.dvij.dvij_compose3.firebase.UserInfoClass
import kz.dvij.dvij_compose3.navigation.PLACE_VIEW
import kz.dvij.dvij_compose3.ui.theme.*

class OwnerCard (val act: MainActivity) {

    @Composable
    fun OwnerCardView (userKey: String){
        
        val userInfo = remember {
            mutableStateOf(UserInfoClass())
        }
        
        act.userDatabaseManager.readOneUserFromDataBase(userInfo = userInfo, key = userKey){
            
            if (it){
                
                Log.d ("MyLog", "Данные пользователя успешно получены")
                
            }
            
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            // ----- ЛОГОТИП ЗАВЕДЕНИЯ ---------

            if (userInfo.value.avatar != null && userInfo.value.avatar != ""){

                AsyncImage(
                    model = userInfo.value.avatar,
                    contentDescription = "",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {

                    // ----- ИМЯ И ФАМИЛИЯ --------

                    if (userInfo.value.name != null && userInfo.value.name != "null" && userInfo.value.name != "") {

                        if (userInfo.value.surname != null && userInfo.value.surname != "null" && userInfo.value.surname != "") {

                            Text(
                                text = "${userInfo.value.name!!} ${userInfo.value.surname!!}",
                                style = Typography.bodyMedium,
                                color = WhiteDvij
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                        } else {

                            Text(
                                text = userInfo.value.name!!,
                                style = Typography.bodyMedium,
                                color = WhiteDvij
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                        }


                    }

                    // --------- АДРЕС -------------

                    androidx.compose.material3.Text(
                        text = "Создатель мероприятия",
                        style = Typography.labelMedium,
                        color = Grey_Text
                    )

                    Spacer(modifier = Modifier.height(10.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {


                        // ----- КНОПКА ПОЗВОНИТЬ --------

                        if (userInfo.value.phoneNumber != null && userInfo.value.phoneNumber != "7" && userInfo.value.phoneNumber != "+77" && userInfo.value.phoneNumber != "" && userInfo.value.phoneNumber != "null") {

                            Icon(
                                painter = painterResource(id = R.drawable.ic_phone),
                                contentDescription = "",
                                tint = YellowDvij,
                                modifier = Modifier.clickable {
                                    act.callAndWhatsapp.makeACall(userInfo.value.phoneNumber!!)
                                }.size(30.dp)
                            )

                            Spacer(
                                modifier = Modifier
                                    .width(20.dp)
                            )

                        }

                        // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                        if (userInfo.value.whatsapp != null && userInfo.value.whatsapp != "7" && userInfo.value.whatsapp != "+77" && userInfo.value.whatsapp != "" && userInfo.value.whatsapp != "null") {

                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = stringResource(id = R.string.social_whatsapp),
                                tint = YellowDvij,
                                modifier = Modifier.clickable {act.callAndWhatsapp.writeInWhatsapp(userInfo.value.whatsapp!!)}.size(30.dp)
                            )

                            Spacer(
                                modifier = Modifier
                                    .width(20.dp)
                            )

                        }

                        // ---- КНОПКА НАПИСАТЬ В ИНСТАГРАМ -----------

                        if (userInfo.value.instagram != null && userInfo.value.instagram != "" && userInfo.value.instagram != "null") {

                            Icon(
                                painter = painterResource(id = R.drawable.instagram),
                                contentDescription = stringResource(id = R.string.social_instagram),
                                tint = YellowDvij,
                                modifier = Modifier.clickable {act.callAndWhatsapp.goToInstagramOrTelegram(userInfo.value.instagram!!, INSTAGRAM_URL)}.size(30.dp)
                            )

                            Spacer(
                                modifier = Modifier
                                    .width(20.dp)
                            )

                        }

                        // ---- КНОПКА НАПИСАТЬ В ТЕЛЕГРАМ -----------

                        if (userInfo.value.telegram != null && userInfo.value.telegram != "" && userInfo.value.telegram != "null") {

                            Icon(
                                painter = painterResource(id = R.drawable.telegram),
                                contentDescription = stringResource(id = R.string.social_telegram),
                                tint = YellowDvij,
                                modifier = Modifier
                                    .clickable {act.callAndWhatsapp.goToInstagramOrTelegram(userInfo.value.telegram!!, TELEGRAM_URL)}
                                    .size(30.dp)
                            )

                            Spacer(
                                modifier = Modifier
                                    .width(20.dp)
                            )

                        }
                    }
                }
            }
        }
    }
}
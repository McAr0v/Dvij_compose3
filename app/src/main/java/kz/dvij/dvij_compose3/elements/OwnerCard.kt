package kz.dvij.dvij_compose3.elements

import android.util.Log
import androidx.compose.foundation.background
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
                .fillMaxWidth()
                .background(Grey100, shape = RoundedCornerShape(10.dp)).padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            
            AsyncImage(
                model = userInfo.value.avatar, 
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp).clip(CircleShape),
            )

            Spacer(
                modifier = Modifier
                    .width(20.dp)
            )
            
            Column(modifier = Modifier.fillMaxWidth()) {
                // ----- РАЗДЕЛ СВЯЗАТЬСЯ С ОРГАНИЗАТОРОМ -----

                // -------- ЗАГОЛОВОК МЕРОПРИЯТИЯ ----------

                if (userInfo.value.name != null && userInfo.value.name != "null" && userInfo.value.name != "") {

                    if (userInfo.value.surname != null && userInfo.value.surname != "null" && userInfo.value.surname != "") {

                        Text(
                            text = "${userInfo.value.name!!} ${userInfo.value.surname!!}",
                            style = Typography.titleLarge,
                            color = Grey10
                        )

                    } else {

                        Text(
                            text = userInfo.value.name!!,
                            style = Typography.titleLarge,
                            color = Grey10
                        )

                    }


                }

                // ------- ГОРОД ------------

                if (userInfo.value.city != null && userInfo.value.city != "null" && userInfo.value.city != "") {

                    Text(
                        text = userInfo.value.city!!,
                        style = Typography.bodyMedium,
                        color = Grey40
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )

                Row(modifier = Modifier.fillMaxSize()) {

                    // ----- КНОПКА ПОЗВОНИТЬ --------

                    if (userInfo.value.phoneNumber != null && userInfo.value.phoneNumber != "7" && userInfo.value.phoneNumber != "+77" && userInfo.value.phoneNumber != "" && userInfo.value.phoneNumber != "null") {

                        IconButton(
                            onClick = { act.callAndWhatsapp.makeACall(userInfo.value.phoneNumber!!) }, // функция набора номера
                            modifier = Modifier.background(
                                Grey90,
                                shape = RoundedCornerShape(50)
                            )
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.ic_phone),
                                contentDescription = "",
                                tint = Grey10
                            )

                        }

                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )

                    }

                    // ---- КНОПКА НАПИСАТЬ В ВАТСАП -----------

                    if (userInfo.value.whatsapp != null && userInfo.value.whatsapp != "7" && userInfo.value.whatsapp != "+77" && userInfo.value.whatsapp != "" && userInfo.value.whatsapp != "null") {

                        IconButton(
                            onClick = { act.callAndWhatsapp.writeInWhatsapp(userInfo.value.whatsapp!!) }, // Функция перехода в ватсапп
                            modifier = Modifier.background(
                                Grey90,
                                shape = RoundedCornerShape(50)
                            )
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = stringResource(id = R.string.social_whatsapp),
                                tint = Grey10
                            )

                        }

                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )

                    }

                    // ---- КНОПКА НАПИСАТЬ В ИНСТАГРАМ -----------

                    if (userInfo.value.instagram != null && userInfo.value.instagram != "" && userInfo.value.instagram != "null") {

                        IconButton(
                            onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(userInfo.value.instagram!!, INSTAGRAM_URL) }, // Функция перейти на инстаграм
                            modifier = Modifier.background(
                                Grey90,
                                shape = RoundedCornerShape(50)
                            )
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.instagram),
                                contentDescription = stringResource(id = R.string.social_instagram),
                                tint = Grey10
                            )

                        }

                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )

                    }

                    // ---- КНОПКА НАПИСАТЬ В ТЕЛЕГРАМ -----------

                    if (userInfo.value.telegram != null && userInfo.value.telegram != "" && userInfo.value.telegram != "null") {

                        IconButton(
                            onClick = { act.callAndWhatsapp.goToInstagramOrTelegram(userInfo.value.telegram!!, TELEGRAM_URL) }, // Функция написать в телеграм
                            modifier = Modifier.background(
                                Grey90,
                                shape = RoundedCornerShape(50)
                            )
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.telegram),
                                contentDescription = stringResource(id = R.string.social_telegram),
                                tint = Grey10
                            )

                        }

                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )

                    }
                }
            }
            
        }


    }


}
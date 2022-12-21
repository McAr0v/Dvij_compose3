package kz.dvij.dvij_compose3.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun CityChooseDialog (onDismiss: ()-> Unit){

    val citiesList = mutableListOf<CitiesList>(CitiesList.Ridder, CitiesList.Astana, CitiesList.UKa, CitiesList.Altay, CitiesList.Almaty)
    val contextForToast = LocalContext.current.applicationContext

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {

        Column(
            modifier = Modifier
                .border(2.dp, color = Grey80, shape = RoundedCornerShape(20.dp))
                .background(Grey95, shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {
                Text(
                    text = "Выберите город:",
                    style = Typography.titleMedium,
                    color = Grey10,
                modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(20.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close_page),
                    tint = Grey00,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().background(Grey100, shape = RoundedCornerShape(10.dp)).padding(20.dp)

            ){
                items (citiesList) { city->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(modifier = Modifier.clickable {

                    },
                        text = stringResource(id = city.cityName),
                        color = Grey40,
                        style = Typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))



                }
            }



            


        }

    }

}



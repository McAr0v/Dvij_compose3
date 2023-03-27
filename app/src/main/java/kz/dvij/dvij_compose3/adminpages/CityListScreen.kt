package kz.dvij.dvij_compose3.adminpages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.firebase.StockCardClass
import kz.dvij.dvij_compose3.ui.theme.Grey_Background
import kz.dvij.dvij_compose3.ui.theme.Grey_OnBackground
import kz.dvij.dvij_compose3.ui.theme.Typography
import kz.dvij.dvij_compose3.ui.theme.WhiteDvij

class CityListScreen(act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun CityListScreen(act: MainActivity){

        val citiesList = remember {
            mutableStateOf(listOf<CitiesList>())
        }// инициализируем список

        act.chooseCityNavigation.readCityDataFromDb(citiesList = citiesList)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey_Background)
                .padding(horizontal = 20.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){

            if (citiesList.value != listOf<CitiesList>() ) {

                items(citiesList.value) { list ->

                    Row (
                        modifier = Modifier
                            .fillMaxSize().padding(vertical = 5.dp).background(Grey_OnBackground).padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ){

                        Text(
                            text = list.cityName!!,
                            color = WhiteDvij,
                            style = Typography.bodySmall,
                            modifier = Modifier.weight(0.3f)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = list.code!!,
                            color = WhiteDvij,
                            style = Typography.bodySmall,
                            modifier = Modifier.weight(0.3f)
                        )

                    }


                }

            }


        }

    }

}
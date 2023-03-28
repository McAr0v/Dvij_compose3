package kz.dvij.dvij_compose3.adminpages

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.dialogs.CitiesList
import kz.dvij.dvij_compose3.dialogs.CityAddDialog
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.elements.LoadingScreen
import kz.dvij.dvij_compose3.firebase.StockCardClass
import kz.dvij.dvij_compose3.navigation.CITIES_LIST_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

class CityListScreen(act: MainActivity) {

    @SuppressLint("NotConstructor")
    @Composable
    fun CityListScreen(
        act: MainActivity,
        navController: NavController
    ){

        val citiesList = remember {
            mutableStateOf(listOf<CitiesList>())
        }// инициализируем список

        val cityDialog = CityAddDialog()

        act.chooseCityNavigation.readCityDataFromDb(citiesList = citiesList)

        val openAddDialog = remember { mutableStateOf(false) } // диалог создания города
        val openLoading = remember { mutableStateOf(false) } // диалог создания города




        if (openAddDialog.value) {

            cityDialog.AddCityDialog(
                onDismiss = { openAddDialog.value = false },
                navController = navController,
                act = act,
                openLoadingState = openLoading
            )

        }

        if (openLoading.value) {

            LoadingScreen(messageText = "Идет загрузка")

        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey_Background)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){

            item {

                Spacer(modifier = Modifier.height(20.dp))

                ButtonCustom(buttonText = "Добавить город") {

                    openAddDialog.value = true

                }

                Spacer(modifier = Modifier.height(20.dp))

            }

            if (citiesList.value != listOf<CitiesList>() ) {

                items(citiesList.value) { list ->

                    val deleteSwitch = remember { mutableStateOf(false) }

                    if (!deleteSwitch.value){

                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 5.dp)
                                .background(Grey_ForCards, shape = RoundedCornerShape(15.dp))
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ){

                            Text(
                                text = list.cityName!!,
                                color = WhiteDvij,
                                style = Typography.bodySmall,
                                modifier = Modifier.weight(0.55f)
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = list.code!!,
                                color = WhiteDvij,
                                style = Typography.bodySmall,
                                modifier = Modifier.weight(0.25f)
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = "Удалить",
                                color = AttentionRed,
                                style = Typography.bodySmall,
                                modifier = Modifier.weight(0.2f).clickable {

                                    deleteSwitch.value = true

                                }
                            )

                        }

                    } else {

                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 5.dp)
                                .background(Grey_OnBackground, shape = RoundedCornerShape(15.dp))
                                .border(width = 2.dp, color = YellowDvij, shape = RoundedCornerShape(15.dp))
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ){

                            Text(
                                text = "Удалить ${list.cityName!!}?",
                                color = WhiteDvij,
                                style = Typography.bodySmall,
                                modifier = Modifier.weight(0.7f)
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = "Да",
                                color = YellowDvij,
                                style = Typography.bodySmall,
                                modifier = Modifier.weight(0.15f).clickable {

                                    if (list.code != null) {

                                        openLoading.value = true

                                        act.chooseCityNavigation.deleteCityFromDb(list.code){

                                            if (it) {

                                                deleteSwitch.value = false
                                                navController.navigate(CITIES_LIST_ROOT)
                                                Toast.makeText(act, "Город успешно удален", Toast.LENGTH_SHORT).show()

                                            } else {

                                                openLoading.value = false
                                                deleteSwitch.value = false
                                                Toast.makeText(act, "Произошла ошибка. Город не удален", Toast.LENGTH_SHORT).show()

                                            }

                                        }


                                    }
                                }
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = "Нет",
                                color = AttentionRed,
                                style = Typography.bodySmall,
                                modifier = Modifier.weight(0.15f).clickable {

                                    deleteSwitch.value = false

                                }
                            )

                        }
                    }
                }
            }
        }
    }
}
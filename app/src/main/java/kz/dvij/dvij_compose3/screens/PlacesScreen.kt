package kz.dvij.dvij_compose3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.navigation.BodySideNavigation
import kz.dvij.dvij_compose3.navigation.HeaderSideNavigation
import kz.dvij.dvij_compose3.navigation.SideNavigationItems
import kz.dvij.dvij_compose3.navigation.TopBar
import kz.dvij.dvij_compose3.ui.theme.Primary10

// функция превью экрана

@Preview
@Composable
fun PlacesScreen () {

    Column() {
        // создаем колонку, чтобы элементы отображались друг за другом
        TopBar(stringResource(id = R.string.places)) // вызываем верхнее меню, передаем название страницы
        // ОТ НАЗВАНИЯ СТРАНИЦЫ ЗАВИСИТ ЗАГОЛОВОК МЕНЮ, ВЫЗОВ НУЖНОГО СОДЕРЖАНИЯ СТРАНИЦЫ И ПОКАЗ ЗНАЧКА ФИЛЬТРА!


    }
}


// экран заведений

@Composable
fun PlacesTapeScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "PlacesScreen TAPE")

    }
}


@Composable
fun PlacesFavScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "PlacesScreen FAV")

    }
}

@Composable
fun PlacesMyScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "PlacesScreen MY")

    }
}
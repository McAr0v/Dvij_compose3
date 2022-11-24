package kz.dvij.dvij_compose3

import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kz.dvij.dvij_compose3.ui.theme.Grey00
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.PrimaryColor


@Composable

 fun BottomNavigationMenu (
    //смотри видео индуса примерно с 5 минуты

    navController: NavController
) {
    val navItems = listOf(NavigationItem.Profile, NavigationItem.Meetings, NavigationItem.Places, NavigationItem.Stock )
    
    BottomNavigation (
        backgroundColor = Grey100
            ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navItems.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.navRoute,
                onClick = { navController.navigate(item.navRoute) },
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = "Иконка")},
                label = { Text(text = stringResource(id = item.title))},
                selectedContentColor = PrimaryColor,
                unselectedContentColor = Grey00
            )

        }
    }
}
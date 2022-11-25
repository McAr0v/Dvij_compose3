package kz.dvij.dvij_compose3.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.PrimaryColor


@Composable

 fun BottomNavigationMenu (

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
                label = { Text(text = stringResource(id = item.title), fontSize = 11.sp)},
                icon = {
                    Icon(painter = painterResource(id = item.icon),
                        tint = if (item.navRoute == currentRoute){PrimaryColor} else {Grey10},
                        contentDescription = "${item.title}")},
                selectedContentColor = PrimaryColor,
                unselectedContentColor = Grey10
            )

        }
    }
}
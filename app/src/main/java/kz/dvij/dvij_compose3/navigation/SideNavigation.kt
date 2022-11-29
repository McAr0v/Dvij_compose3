package kz.dvij.dvij_compose3.navigation

import android.view.MenuItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.ui.theme.Typography


// https://semicolonspace.com/jetpack-compose-navigation-drawer/
// https://www.youtube.com/watch?v=JLICaBEiJS0

@Composable
fun HeaderSideNavigation(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Header", fontSize = 60.sp)
    }
}

@Composable
fun BodySideNavigation(
    items: List<SideNavigationItems>,
    navController: NavController,
    scaffoldState: ScaffoldState
) {

    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState() // записываем в navBackStackEntry текущее состояние navController
    val currentRoute = navBackStackEntry?.destination?.route

    LazyColumn {
        items(items) { item ->
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(item.navRoute)
                        coroutineScope.launch{
                            scaffoldState.drawerState.close()
                        }
                    }
                    .padding(16.dp)
                    ){
                Icon(
                    painter = painterResource(id = item.icon), 
                    contentDescription = stringResource(id = item.contentDescription)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = item.title),
                    style = Typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


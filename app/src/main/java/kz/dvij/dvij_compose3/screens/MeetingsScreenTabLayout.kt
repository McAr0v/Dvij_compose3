package kz.dvij.dvij_compose3.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.navigation.TabItem
import kz.dvij.dvij_compose3.navigation.TabLayoutMeetings
import kz.dvij.dvij_compose3.ui.theme.*

class MeetingsScreenTabLayout: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {



        }
    }
}
    @OptIn(ExperimentalPagerApi::class)
    @Composable

    fun MainScreen() {
        val tabs = listOf(TabItem.TapeMeetings, TabItem.MyMeetings, TabItem.FavMeetings)

        val pagerState = rememberPagerState(pageCount = tabs.size)

        Scaffold(
        topBar = { TopBar() }
    ) { paddingValues ->
        Column (modifier = Modifier.padding(paddingValues)){
            Tabs(tabs = tabs, pagerState = pagerState)
            TabsContent(tabs = tabs, pagerState = pagerState)
        }

    }

    }


    @Composable
    fun TopBar() {
        TopAppBar(
            backgroundColor = Grey90,
            title = { Text(text = stringResource(id = R.string.app_name)) },
            contentColor = Grey00
        )
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {
        val scope = rememberCoroutineScope()
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Grey100,
            contentColor = Grey00,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }) {
            tabs.forEachIndexed { index, tab ->
                LeadingIconTab(
                    icon = {
                        Icon(
                            painter = painterResource(id = tab.icon),
                            contentDescription = ""
                        )
                    },
                    text = { Text(text = stringResource(id = tab.title)) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsContent (tabs: List<TabItem>, pagerState: PagerState){
            HorizontalPager(state = pagerState) { it ->
                tabs[it].screen()

     }
}



@Composable
fun MeetingsTapeScreen (){

    Column (
        modifier = Modifier
            .background(Grey95)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        MeetingCard("Развлечение", "Куда-то мы пойдем сегодня", "21:00", "4 октября")
        MeetingCard("Хобби", "Выступление великолепной группы Korn", "11:00", "5 ноября")
    }
}

@Composable
fun MeetingsMyScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MainScreen()
        Text(text = "MeetingsMyScreen")

    }
}

@Composable
fun MeetingsFavScreen (){
    Column (
        modifier = Modifier
            .background(Primary10)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MainScreen()
        Text(text = "MeetingsFavScreen")

    }
}
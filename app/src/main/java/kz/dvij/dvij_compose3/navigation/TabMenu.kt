package kz.dvij.dvij_compose3.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.Grey00
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.Typography

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MeetingsTabMenu(){
    val tabItem = listOf(TabItem.MyMeetings, TabItem.TapeMeetings, TabItem.FavMeetings)

    val pagerStateMeetings = rememberPagerState(pageCount = tabItem.size)

    /*Scaffold (
        topBar = {}
            ){
        PaddingValues->
        Column() {

        }
        //add code later
    }*/

    @Composable
    fun TopBar(){
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.meetings), style = Typography.labelLarge, color = Grey00)},
            Modifier.background(Grey100)
        )
    }

   /* @Composable
    fun TabsItems (tabs: List<TabItem>, pagerState: PagerState){
        val scope = rememberCoroutineScope()

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Grey100,
            contentColor = Grey00,
            indicator = {tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions = tabPositions)
                )
            }
        ) {tabs.forEachIndexed( index, tab ->
            Tab(
                icon = { Icon(painter = , contentDescription = )}
            )

        }
    }*/

}
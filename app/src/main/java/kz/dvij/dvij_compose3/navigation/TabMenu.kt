package kz.dvij.dvij_compose3.navigation

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.screens.MeetingsFavScreen
import kz.dvij.dvij_compose3.screens.MeetingsMyScreen
import kz.dvij.dvij_compose3.screens.MeetingsTapeScreen
import kz.dvij.dvij_compose3.ui.theme.Grey10
import kz.dvij.dvij_compose3.ui.theme.Grey100
import kz.dvij.dvij_compose3.ui.theme.Typography



@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable

fun TabLayoutMeetings(){
/*
    val tabItem = listOf(TabItem.TapeMeetings, TabItem.FavMeetings, TabItem.MyMeetings )
    val pagerState = rememberPagerState(pageCount = tabItem.size)
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.clip(RoundedCornerShape(5.dp))) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { pos ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, pos)
                        )
            },
            contentColor = Grey10,
            backgroundColor = Grey100
        ) {
            tabItem.forEachIndexed{index, tabItems ->
                Tab(selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                              },
                    text = { Text(text = stringResource(id = tabItems.title), style = Typography.labelMedium)}
                )
            }
        }

        HorizontalPager(
            state = pagerState,
        ) { page ->




        }

    }

*/
}
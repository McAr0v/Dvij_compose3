package kz.dvij.dvij_compose3.tapesscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.ui.theme.Grey_Background
import kz.dvij.dvij_compose3.ui.theme.Primary10
import kz.dvij.dvij_compose3.ui.theme.Typography
import kz.dvij.dvij_compose3.ui.theme.WhiteDvij

@Composable
fun AboutScreen(){
    Column (
        modifier = Modifier
            .background(Grey_Background)
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.side_about),
            color = WhiteDvij,
            style = Typography.titleLarge
        )

    }
}
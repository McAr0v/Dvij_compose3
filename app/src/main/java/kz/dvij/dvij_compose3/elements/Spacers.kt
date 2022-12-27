package kz.dvij.dvij_compose3.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kz.dvij.dvij_compose3.ui.theme.Grey40
import kz.dvij.dvij_compose3.ui.theme.Grey60
import kz.dvij.dvij_compose3.ui.theme.Grey80
import kz.dvij.dvij_compose3.ui.theme.Typography

@Composable
fun SpacerTextWithLine(headline: String){

    Column(modifier = Modifier.fillMaxWidth()) {

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = headline,
                style = Typography.labelMedium,
                color = Grey40
            )
            
            Spacer(modifier = Modifier.width(20.dp))

            Divider(modifier = Modifier
                .fillMaxWidth()
                .weight(1f), color = Grey80)

        }

        Spacer(modifier = Modifier.height(10.dp))

    }

}
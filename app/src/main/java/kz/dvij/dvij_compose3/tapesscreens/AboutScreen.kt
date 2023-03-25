package kz.dvij.dvij_compose3.tapesscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.elements.ButtonCustom
import kz.dvij.dvij_compose3.navigation.CALLBACK_ROOT
import kz.dvij.dvij_compose3.ui.theme.*

@Composable
fun AboutScreen(navController: NavController){
    Column (
        modifier = Modifier
            .background(Grey_Background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        Icon(
            painter = painterResource(id = R.drawable.dvij_logo),
            contentDescription = stringResource(id = R.string.cd_logo),
            modifier = Modifier.size(150.dp),
            tint = YellowDvij
        )

        //Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(id = R.string.side_about),
            color = WhiteDvij,
            style = Typography.titleLarge
        )


        // ---------------- ВЕСЬ ДВИЖ ГОРОДА ПЕРЕД ГЛАЗАМИ ----------

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.this_is_dvij_headline), color = WhiteDvij, style = Typography.bodyLarge)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.this_is_dvij_text), color = WhiteDvij, style = Typography.bodySmall)


        // ------------- НАЙДИ СВОИХ ЕДИНОМЫШЛЕННИКОВ -------------

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.find_friends_headline), color = WhiteDvij, style = Typography.bodyLarge)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.find_friends_text_1), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.find_friends_text_2), color = WhiteDvij, style = Typography.bodySmall)


        // ------------- МЕСТА ----------------

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.places_headline), color = WhiteDvij, style = Typography.bodyLarge)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.places_text_1), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.places_text_2), color = WhiteDvij, style = Typography.bodySmall)


        // ------------- АКЦИИ ------------

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.stocks_headline), color = WhiteDvij, style = Typography.bodyLarge)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.stocks_text_1), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.stocks_text_2), color = WhiteDvij, style = Typography.bodySmall)


        // -------------

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.callback_headline), color = WhiteDvij, style = Typography.bodyLarge)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.callback_text_1), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.callback_text_2), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(id = kz.dvij.dvij_compose3.R.string.callback_text_3), color = WhiteDvij, style = Typography.bodySmall)

        Spacer(modifier = Modifier.height(30.dp))

        ButtonCustom(buttonText = "Связаться с администратором") {

            navController.navigate(CALLBACK_ROOT)

        }

    }
}
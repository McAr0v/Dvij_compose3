package kz.dvij.dvij_compose3.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kz.dvij.dvij_compose3.R


// https://www.youtube.com/watch?v=fEFuF1dnWNk

@Composable
fun LoginScreen(
    navController: NavController, viewModel: LoginScreenViewModel = viewModel() // по идее тут должен быть LoginScreenViewModel
){

    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            viewModel.signWithGoogleCredential(credential)
        } catch (e:ApiException) {
            Log.w("MyLog", "GoogleSign in failed", e)
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        
    }

}
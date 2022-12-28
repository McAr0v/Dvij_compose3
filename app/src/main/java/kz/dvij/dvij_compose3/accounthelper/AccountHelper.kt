package kz.dvij.dvij_compose3.accounthelper

import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.screens.AccountScreens

class AccountHelper (act: MainActivity) {

    // Передаем в класс Account Helper Main Activity, чтобы мы имели доступ к переменныс с MainActivity в этом классе


    private val act = act // инициализируем Main Activity
    private lateinit var signInClient: GoogleSignInClient

    fun errorInSignInAndUp (error: Exception) {

        // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ ОШИБКИ И ВЫВОДА СООБЩЕНИЯ ПРИ РЕГИСТРАЦИИ И ВХОДЕ --------

        Log.d("MyLog", "Exception: $error") // определяем класс ошибки (ниже это FirebaseAuthUserCollisionException)

        // При возникновении ошибки, нужно смотреть LogCat


        // ---- ЕСЛИ ОШИБКА КЛАССА FirebaseAuthUserCollisionException

        if (error is FirebaseAuthUserCollisionException) {
            val exception = error as FirebaseAuthUserCollisionException // Указываем ошибку как ошибку нужного класса
            Log.d("MyLog", "Exception: ${exception.errorCode}") // Выводим в ЛОГ уже конкретный код ошибки - ERROR_EMAIL_ALREADY_IN_USE

            // создаем в константах AccountConst константу для обозначения ошибки

            // Прописываем условие, если наш код ошибки равняется коду ошибки из констант - то выводим ТОСТ

            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                Toast.makeText(
                    act,
                    R.string.exception_user_exist,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // ----------- ДАЛЕЕ ВСЕ СДЕЛАНО ПО АНАЛОГИИ, КОММЕНТИРОВАТЬ НЕ БУДУ --------------


        // ---- ЕСЛИ ОШИБКА КЛАССА FirebaseAuthInvalidUserException

        if (error is FirebaseAuthInvalidUserException) {

            val exception = error as FirebaseAuthInvalidUserException

            // Log.d("MyLog", "Exception: ${exception.errorCode}") // уже конкретно уточняем код ошибки

            if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(
                    act,
                    R.string.exception_user_not_exist,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // ---- ЕСЛИ ОШИБКА КЛАССА FirebaseAuthInvalidCredentialsException

        if (error is FirebaseAuthInvalidCredentialsException) {

            val exception = error as FirebaseAuthInvalidCredentialsException
            // Log.d("MyLog", "Exception: ${exception.errorCode}") // уже конкретно уточняем код ошибки

            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act,
                    R.string.exception_wrong_format_email,
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(
                    act,
                    R.string.exception_wrong_password,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // ---- ЕСЛИ ОШИБКА КЛАССА FirebaseAuthWeakPasswordException

        if (error is FirebaseAuthWeakPasswordException) {

            val exception = error as FirebaseAuthWeakPasswordException
            // Log.d("MyLog", "Exception: ${exception.errorCode}") // уже конкретно уточняем код ошибки

            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(
                    act,
                    R.string.exception_password_need_more_letters,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // Функция отправки письма с подтверждением Email. НЕ УДАЛЯТЬ ФУНКЦИЮ

    fun sendEmailVerification(user: FirebaseUser){
        // функция отправки письма с подтверждением Email при регистрации
        // данные зарегистрированного user находвтся в mAuth

        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, R.string.send_email_verification_success, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(act, R.string.send_email_verification_error, Toast.LENGTH_SHORT).show()
            }
        }
    }


    // выход из аккаунта гугл. НЕ УДАЛЯТЬ ФУНКЦИЮ
    fun signOutGoogle(){
        getSignInClient().signOut()
    }


    // GOOGLE вход ------------------------------------

    // 1 действие - нужно получить гуглсингинклиент

    fun getSignInClient (): GoogleSignInClient {

        // функция получает доступ приложения к аккаунту гугл с телефона

        // то, что мы возвращаем, GoogleSignInClient - этот класс позволяет нам отправить интент (специальное сообщение системе) -
        // так как аккаунт лежит не в приложении, а на смартфоне, нам надо отправить специальный запрос в систему и ждать результат -
        // система должна вернуть данные об аккаунте

        // gso = google sign in options

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(act, gso)

    }

    // 2 действие создаем функцию, которая будет запускаться когда при нажатии на кнопку гугла

    fun signInWithGoogle(){
        signInClient = getSignInClient() // sign in Client - переменная, которая инициализируется не сразу. Мы ее создали как lateinit  и она вверху mainActivity

        val intent = signInClient.signInIntent

        try {
            act.googleSignInResultLauncher?.launch(intent)
        } catch (e: Exception) {

            Log.d("MyLog", "ApiError: ${e.message}")

        }



        //act.startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE)

    }



    fun signInFirebaseWithGoogle (token: String) {

        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.signInWithCredential(credential).addOnCompleteListener{ task ->

            if (task.isSuccessful) {
                //act.recreate()
                Toast.makeText(act, R.string.sign_in_google_success, Toast.LENGTH_SHORT).show()
            } else {Toast.makeText(act, "Произошла ошибка в signInFirebaseWithGoogle", Toast.LENGTH_SHORT).show()}

        }
    }


}


package kz.dvij.dvij_compose3.accounthelper

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R

class AccountHelper (val act: MainActivity) {

    private lateinit var signInClient: GoogleSignInClient


    // ----- ФУНКЦИЯ ВХОДА В АККАУНТ ЧЕРЕЗ EMAIL PASSWORD

    fun signInWithEmailAndPassword(email: String, password: String, callback: (result: Boolean)-> Unit){

        // запускаем функцию от Google

        act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                // если вход выполнен успешно, возвращаем колбак ТРУ
                callback (true)


            } else {

                // если вход не выполнен запускам функцию отслеживания ошибки и вывода ТОСТА
                task.exception?.let {
                    errorInSignInAndUp(it)
                }
            }
        }
    }

    // ------ ФУНКЦИЯ РЕГИСТРАЦИИ ЧЕРЕЗ EMAIL и PASSWORD -------------

    fun registerWIthEmailAndPassword(email: String, password: String, callback: (result: FirebaseUser)-> Unit) {

        // запускаем функцию createUserWithEMailAndPassword и вешаем слушатель, который говорит что действие закончено

        act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                //  если регистрация прошла успешно

                if (task.isSuccessful) {
                        // возвращаем колбак с данными пользователя
                        callback (task.result.user!!)

                } else {

                    // если регистрация не выполнилась
                    // отправляем в функцию отслеживания ошибки и вывода нужной информации

                    task.exception?.let {
                        errorInSignInAndUp(it)
                    }
                }
            }
    }


    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ ОШИБКИ И ВЫВОДА СООБЩЕНИЯ ПРИ РЕГИСТРАЦИИ И ВХОДЕ --------

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


    // --- Функция отправки письма с подтверждением Email. НЕ УДАЛЯТЬ ФУНКЦИЮ ------

    fun sendEmailVerification(user: FirebaseUser, callback: (result: Boolean)-> Unit){
        // функция отправки письма с подтверждением Email при регистрации
        // данные зарегистрированного user находвтся в mAuth

        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback (true) // если успешно, возвращаем колбак ТРУ
                Toast.makeText(act, R.string.send_email_verification_success, Toast.LENGTH_SHORT).show()
            } else {
                callback (false) // если не успешно, возвращаем колбак фалс
                Toast.makeText(act, R.string.send_email_verification_error, Toast.LENGTH_SHORT).show()
            }
        }
    }


    // -------- ВЫХОД ИЗ АККАУНТА ГУГЛ. НЕ УДАЛЯТЬ ФУНКЦИЮ ------------

    fun signOutGoogle(){
        getSignInClient().signOut()
    }


    // --------- ФУНКЦИЯ ПОЛУЧЕНИЯ ДОСТУПА К АККАУНТУ ГУГЛ НА ТЕЛЕФОНЕ ------------

    private fun getSignInClient (): GoogleSignInClient {

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



    // ------- ФУНКЦИЯ, ЗАПУСКАЮШАЯСЯ ПРИ НАЖАТИИ НА КНОПКУ GOOGLE

    fun signInWithGoogle(){
        signInClient = getSignInClient() // sign in Client - переменная, которая инициализируется не сразу. Мы ее создали как late init  и она вверху mainActivity

        val intent = signInClient.signInIntent

        try {
            act.googleSignInResultLauncher?.launch(intent)
        } catch (e: Exception) {
            Log.d("MyLog", "ApiError: ${e.message}")
        }
    }


    // ------- ФУНКЦИЯ НЕПОСРЕДСТВЕННО ВХОДА/РЕГИСТРАЦИИ В FIREBASE ЧЕРЕЗ GOOGLE -----------

    fun signInFirebaseWithGoogle (token: String) {

        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                act.recreate()
                Toast.makeText(act, R.string.sign_in_google_success, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(
                    act,
                    "Произошла ошибка в signInFirebaseWithGoogle",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}


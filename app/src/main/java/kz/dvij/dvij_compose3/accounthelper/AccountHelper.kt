package kz.dvij.dvij_compose3.accounthelper

import android.widget.Toast
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseUser
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.navigation.PROFILE_ROOT
import kz.dvij.dvij_compose3.screens.MeetingsScreen

class AccountHelper (act: MainActivity) {

    // Передаем в класс Account Helper Main Activity, чтобы мы имели доступ к переменныс с MainActivity в этом классе


    private val act = act // инициализируем Main Activity

    fun registrWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {

            // запускаем существующую функцию от FireBase создание пользователя с имейл и паролем.
            // addOnCompleteListener слушает - успешно ли прошла регистрация

            act.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    //  если регистрация прошла успешно
                    if (task.isSuccessful) {

                        // если пользователь успешно зарегистрировался, mAuth будет содержать всю информацию о пользователе user

                        sendEmailVerification(task.result.user!!) // отправляем письмо с подтверждением Email. task.result.user можно взять act.mAuth.currentUser


                    } else { // если регистрация не выполнилась
                        Toast.makeText(
                            act,
                            act.resources.getString(R.string.registr_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
        }
    }

    private fun sendEmailVerification(
        user: FirebaseUser
    ) {
        // функция отправки письма с подтверждением Email при регистрации
        // данные зарегистрированного user находвтся в mAuth

        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Toast.makeText(act, "На вашу почту был отправлен Email", Toast.LENGTH_SHORT).show()


            } else {
                Toast.makeText(act, "Не удалось отправить письмо", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
package kz.dvij.dvij_compose3.screens

import android.annotation.SuppressLint
import android.icu.text.Transliterator.Position
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.R
import kz.dvij.dvij_compose3.accounthelper.AccountHelper
import kz.dvij.dvij_compose3.accounthelper.FirebaseAuthConstants
import kz.dvij.dvij_compose3.accounthelper.REGISTRATION
import kz.dvij.dvij_compose3.elements.fieldEmailComponent
import kz.dvij.dvij_compose3.elements.fieldPasswordComponent
import kz.dvij.dvij_compose3.navigation.*
import kz.dvij.dvij_compose3.ui.theme.*

class AccountScreens(act: MainActivity) {

    // https://storyset.com/illustration/forgot-password/bro -- картинки

    private val act = act // Инициализируем MainActivity
    private val accountHelper = AccountHelper(act) // инициализируем класс AccountHelper


    // ------ СТРАНИЦА "СПАСИБО ЗА РЕГИСТРАЦИЮ" --------------

    @Composable
    fun ThankYouPage (navController: NavController){

        Column(

        ) {

            // помещаем все содержимое в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого

            // создаем строку, чтобы задать положение кнопки "Закрыть"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey95),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {

                // ------- сама кнопка Закрыть -----------

                IconButton(
                    onClick = {
                        navController.navigate(MEETINGS_ROOT) // действие если нажать на кнопку
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = Grey00
                    )
                }
            }


            // ------------ САМ КОНТЕНТ СТРАНИЦЫ -----------------

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey95)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )

            {

                Spacer(modifier = Modifier.height(20.dp)) // разделитель


                // ---------------- ИЛЛЮСТРАЦИЯ -----------------


                Image(
                    painter = painterResource(id = R.drawable.send_email_illustration),
                    contentDescription = stringResource(id = R.string.cd_illustration)
                )



                Spacer(modifier = Modifier.height(40.dp)) // разделитель



                // -------------  ЗАГОЛОВОК ПРОВЕРЬ ПОЧТУ И АКТИВИРУЙ АККАУНТ ---------------

                Text(
                    text = stringResource(id = R.string.verify_email_title), //"Проверь свой Email и активируй аккаунт"
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00, // цвет заголовка
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(20.dp)) // разделитель между заголовком и полями для ввода


                // ------ ДОПОЛНИТЕЛЬНОЕ ОПИСАНИЕ ПОД ЗАГОЛОВКОМ ДЛЯ ПОДТВЕРЖДЕНИЯ ПОЧТЫ ---------------

                Text(
                    text = stringResource(id = R.string.verify_email_text),
                    style = Typography.bodyMedium, // стиль текста
                    color = Grey40, // цвет текста
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(40.dp)) // разделитель между полями


                // ------ КНОПКА ВЕРНУТЬСЯ НА ГЛАВНУЮ ----------------

                Button(

                    onClick = { // действия при нажатии
                        navController.navigate(MEETINGS_ROOT)
                    },

                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor, // цвет кнопки
                        contentColor = Grey100 // цвет контента на кнопке
                    )
                )
                {

                    // СОДЕРЖИМОЕ КНОПКИ

                    Icon(
                        painter = painterResource(id = R.drawable.ic_home), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой


                    // ТЕКСТ КНОПКИ ВЕРНУТЬСЯ НА ГЛАВНУЮ СТРАНИЦУ

                    Text(
                        text = stringResource(id = R.string.go_to_home),
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }


    // -------- СТРАНИЦА "ЗАБЫЛИ ПАРОЛЬ?" --------------

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun ForgotPasswordPage (navController: NavController) {

        Column(

        ) {

            // помещаем все содержимое в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого

            // создаем строку, чтобы задать положение кнопки "Закрыть"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey95),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {

                // сама кнопка Закрыть
                IconButton(
                    onClick = {
                        navController.navigate(LOG_IN_ROOT) // действие если нажать на кнопку

                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = Grey00
                    )
                }
            }

            // ---- САМ КОНТЕНТ СТРАНИЦЫ ---------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey95)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var email = remember { mutableStateOf("") }

            // создаем переменные, в которые будет записываться цвет. Они нужны, чтобы поля
            // при фокусе на них окрашивались в нужные цвета

            var focusColorEmail = remember { mutableStateOf(Grey40) }

            var isErrorEmail =
                remember { mutableStateOf(false) } // состояние формы - ошибка или нет
            var errorEmailMassage = remember { mutableStateOf("") } // сообщение об ошибке


            val focusManager =
                LocalFocusManager.current // инициализируем фокус на форме. Нужно, чтобы потом снимать фокус с формы



                Spacer(modifier = Modifier.height(40.dp)) // разделитель




            // -------------- ИЛЛЮСТРАЦИЯ ------------------


                Image(
                    //modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.forgot_password_illustration),
                    contentDescription = stringResource(id = R.string.cd_illustration)
                )



            Spacer(modifier = Modifier.height(40.dp)) // разделитель



                // -------------  ЗАГОЛОВОК ВОССТАНОВИТЬ ПАРОЛЬ  ---------------

                Text(  // заголовок зависит от switch
                    text = stringResource(id = R.string.help_change_password),
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00 // цвет заголовка
                )




            Spacer(modifier = Modifier.height(20.dp)) // разделитель между заголовком и полями для ввода




            // -------------  ДОПОЛНИТЕЛЬНЫЙ ТЕКСТ ПОД ЗАГОЛОВКОМ ---------------

                Text(  // заголовок зависит от switch
                    text = stringResource(id = R.string.forgot_password_text),
                    style = Typography.bodyMedium, // стиль заголовка
                    color = Grey40, // цвет заголовка
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp)) // разделитель



            // ТЕКСТОВОЕ ПОЛЕ EMAIL

            email.value = fieldEmailComponent(act = act)

            Spacer(modifier = Modifier.height(20.dp)) // разделитель между полями



            // ---------- КНОПКА ВОССТАНОВИТЬ ПАРОЛЬ -------------------

                Button(

                    onClick = { // действия при нажатии
                        act.mAuth.sendPasswordResetEmail(email.value).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate(RESET_PASSWORD_SUCCESS)
                            } else {
                                task.exception?.let { accountHelper.errorInSignInAndUp(it) }
                            }
                        }
                    },

                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor, // цвет кнопки
                        contentColor = Grey100 // цвет контента на кнопке
                    )
                )
                {

                    // СОДЕРЖИМОЕ КНОПКИ

                    Icon(
                        painter = painterResource(id = R.drawable.ic_key), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                    Text(
                        text = stringResource(id = R.string.help_change_password),
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }


    // ------- СТРАНИЦА "МЫ ОТПРАВИЛИ ССЫЛКУ НА ВОССТАНОВЛЕНИЕ ПАРОЛЯ"

    @Composable
    fun ResetPasswordSuccess (navController: NavController){

        Column(

        ) {

            // помещаем все содержимое в колонку, чтобы кнопка "Закрыть" была отдельно от остального содержимого

            // создаем строку, чтобы задать положение кнопки "Закрыть"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Grey95),
                verticalAlignment = Alignment.CenterVertically, // вертикальное выравнивание кнопки
                horizontalArrangement = Arrangement.End // выравнивание кнопки по правому краю
            ) {

                // сама кнопка Закрыть
                IconButton(
                    onClick = {
                        navController.navigate(MEETINGS_ROOT) // действие если нажать на кнопку
                    }
                ) {
                    // содержимое кнопки (в теории слева можно добавить надпись текстом "Закрыть")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close_page),
                        tint = Grey00
                    )
                }
            }

            // ---------- САМ КОНТЕНТ СТРАНИЦЫ ---------------

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Grey95)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )

            {

                Spacer(modifier = Modifier.height(20.dp)) // разделитель


                // ----- ИЛЛЮСТРАЦИЯ ---------

                Image(
                    //modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.send_email_illustration),
                    contentDescription = stringResource(id = R.string.cd_illustration)
                )


                Spacer(modifier = Modifier.height(40.dp)) // разделитель



                // -------------  ЗАГОЛОВОК "Проверь почту и сбрось пароль" ---------------

                Text(  // заголовок
                    text = stringResource(id = R.string.reset_password_title),
                    style = Typography.titleLarge, // стиль заголовка
                    color = Grey00, // цвет заголовка
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(20.dp)) // разделитель между заголовком и полями для ввода


                // ---------- ВСПОМОГАТЕЛЬНЫЙ ТЕКСТ ПОД ИНСТРУКЦИИ ------------

                Text(
                    text = stringResource(id = R.string.reset_password_text),
                    style = Typography.bodyMedium, // стиль текста
                    color = Grey40, // цвет текста
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp)) // разделитель между полями



                // -------- КНОПКА "ПЕРЕЙТИ НА ГЛАВНУЮ СТРАНИЦУ" --------

                Button(

                    onClick = { // действия при нажатии
                        navController.navigate(MEETINGS_ROOT)
                    },

                    // Остальные настройки кнопки

                    modifier = Modifier
                        .fillMaxWidth() // кнопка на всю ширину
                        .height(50.dp),// высота - 50
                    shape = RoundedCornerShape(50), // скругление углов
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PrimaryColor, // цвет кнопки
                        contentColor = Grey100 // цвет контента на кнопке
                    )
                )
                {

                    // СОДЕРЖИМОЕ КНОПКИ

                    Icon(
                        painter = painterResource(id = R.drawable.ic_home), // иконка
                        contentDescription = stringResource(id = R.string.cd_icon), // описание для слабовидящих
                        tint = Grey100 // цвет иконки
                    )

                    Spacer(modifier = Modifier.width(10.dp)) // разделитель между текстом и иконкой

                    Text(
                        text = stringResource(id = R.string.go_to_home),
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }
}




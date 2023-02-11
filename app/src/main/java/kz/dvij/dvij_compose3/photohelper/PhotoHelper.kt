package kz.dvij.dvij_compose3.photohelper

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.navigation.CREATE_USER_INFO_SCREEN
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.PLACES_ROOT
import kz.dvij.dvij_compose3.navigation.STOCK_ROOT
import java.io.ByteArrayOutputStream

class PhotoHelper (val act: MainActivity) {

    // ----- STORAGE МЕРОПРИЯТИЙ -----------

    private val storageMeetings = Firebase
        .storage("gs://dvij-compose3-1cf6a.appspot.com") // Указываем путь на наш Storage
        .getReference("Meetings") // инициализируем папку, в которую будет сохраняться картинка мероприятия

    // делаем дополнительные подпапки для более удобного поиска изображений

    private val imageRefMeetings = storageMeetings
        .child(act.mAuth.uid ?: "empty") // в папке "Meetings" будет еще папка - для каждого пользователя своя
        .child("image_${System.currentTimeMillis()}") // название изображения

    // ----- STORAGE ЗАВЕДЕНИЙ -----------

    private val storagePlaces = Firebase
        .storage("gs://dvij-compose3-1cf6a.appspot.com")
        .getReference("Places") // инициализируем папку, в которую будет сохраняться картинка мест

    // делаем дополнительные подпапки для более удобного поиска изображений

    private val imageRefPlaces = storagePlaces
        .child(act.mAuth.uid ?: "empty") // в папке "Places" будет еще папка - для каждого пользователя своя
        .child("image_${System.currentTimeMillis()}") // название изображения

    // ----- STORAGE АКЦИЙ -----------

    private val storageStock = Firebase
        .storage("gs://dvij-compose3-1cf6a.appspot.com")
        .getReference("Stock") // инициализируем папку, в которую будет сохраняться картинка акций

    // делаем дополнительные подпапки для более удобного поиска изображений

    private val imageRefStock = storageStock
        .child(act.mAuth.uid ?: "empty") // в папке "Stock" будет еще папка - для каждого пользователя своя
        .child("image_${System.currentTimeMillis()}") // название изображения

    // ----- STORAGE ПОЛЬЗОВАТЕЛЕЙ -----------

    private val storageUser = Firebase
        .storage("gs://dvij-compose3-1cf6a.appspot.com")
        .getReference("Stock") // инициализируем папку, в которую будет сохраняться картинка акций

    // делаем дополнительные подпапки для более удобного поиска изображений

    private val imageRefUser = storageUser
        .child(act.mAuth.uid ?: "empty") // в папке "Stock" будет еще папка - для каждого пользователя своя
        .child("image_${System.currentTimeMillis()}") // название изображения



    // ------ ФУНКЦИЯ СЖАТИЯ ИЗОБРАЖЕНИЯ -------

    fun compressImage(context: ComponentActivity, uri: Uri): Uri?{

        // создаем битмап

            val bitmap = if (Build.VERSION.SDK_INT < 28){

                //  если версия СДК меньше 28, то запускаем одну версию галереи

                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {

                // если версия сдк больше 28, то запускаем другую версию галереи

                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }

            // Создаем потом байтов

            val bytes = ByteArrayOutputStream()

            val correctImageSize = getWriteSizeImage(bitmap) // получаем размеры, до которых надо уменьшить картинку

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, correctImageSize[0], correctImageSize[1], false) // изменение размера картинки

            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes) // сжимаем изображение до нужного качества

            // помещаем сжатую картинку в хранилище на телефоне

            val path: String = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                resizedBitmap,
                "image_${System.currentTimeMillis()}",
                null
            )

            // возвращаем путь сжатой картинки
            return Uri.parse(path)

    }

    // ---- ФУНКЦИЯ ОПРЕДЕЛЕНИЯ ПРАВИЛЬНОГО РАЗМЕРА ИЗОБРАЖЕНИЯ -----------

    private fun getWriteSizeImage(bitmap: Bitmap): List<Int>{

        var listOfSize = arrayListOf<Int>(bitmap.width, bitmap.height) // создаем список с входящими размерами картинки

        val width = bitmap.width // ширина
        val height = bitmap.height // высота

        val ratio = (width / height).toFloat() // ratio - коэффициент. Определяем положение, вертикальное или горизонтальное

        val scale: Float = width.toFloat() / height.toFloat() // коэфициент, на сколько нужно уменьшить сторону чтобы соблюсти пропорции

        // если коэффициент больше или равен 1 (картинка квадратная или горизонтальная)

        if (ratio >= 1) {

            // если ширина меньше 1000, оставляем размеры картинки как есть
            if (width <= 1000) {
                listOfSize[0] = width
                listOfSize[1] = height

            } else {
                // если ширина больше 1000, то пересчитываем высоту на нужное значение
                val resizeHeight = 1000/scale

                listOfSize[0] = 1000 // ширина 1000, как нам надо
                listOfSize[1] = resizeHeight.toInt() // высота пересчитана

            }
        } else {

            // Если картинка вертикальная

            if (height <= 1000) { // Если высота меньше или равна 1000

                // оставляем размеры без изменений
                listOfSize[0] = width
                listOfSize[1] = height

            } else {
                // если высота больше 1000, то пересчитываем ширину на нужное значение
                val resizeWidth = 1000*scale

                listOfSize[0] = resizeWidth.toInt() // пересчитанная ширина
                listOfSize[1] = 1000 // высота 1000, как нам надо
            }

        }
        return listOfSize // возвращаем список с измененными размерами, какие нам нужны

    }

    // ------ ФУНКЦИЯ ЗАГРУЗКИ ФОТО В FIRESTORE -----------

    suspend fun uploadPhoto(uri: Uri, name: String, mimeType: String?, typePost: String, callback: (url: String)-> Unit){

        // Берем метаданные файла

        val metadata = mimeType?.let {
            StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()
        }

        // ------ Если загружаем ФОТО МЕРОПРИЯТИЙ ---------

        if (typePost == MEETINGS_ROOT){

            if (metadata != null){
                imageRefMeetings.putFile(uri, metadata).await() // с метаданными
            } else {
                imageRefMeetings.putFile(uri).await() // без метаданных
            }

            callback(imageRefMeetings.downloadUrl.await().toString()) // дожидаемся URL картинки и в качестве колбэка возвращаем

        } else if (typePost == PLACES_ROOT){

            // ----- ЕСЛИ ЗАГРУЖАЕМ ФОТО МЕСТ --------

            // То же самое что и в фото мероприятий, только в другую папку

            if (metadata != null){
                imageRefPlaces.putFile(uri, metadata).await()
            } else {
                imageRefPlaces.putFile(uri).await()
            }

            callback(imageRefPlaces.downloadUrl.await().toString())

        } else if (typePost == STOCK_ROOT){

            // ----- ЕСЛИ ЗАГРУЖАЕМ ФОТО АКЦИЙ --------

            // То же самое что и в фото мероприятий, только в другую папку

            if (metadata != null){
                imageRefStock.putFile(uri, metadata).await()
            } else {
                imageRefStock.putFile(uri).await()
            }

            callback(imageRefStock.downloadUrl.await().toString())

        } else if (typePost == CREATE_USER_INFO_SCREEN){

            // ----- ЕСЛИ ЗАГРУЖАЕМ ФОТО ПОЛЬЗОВАТЕЛЕЙ --------

            // То же самое что и в фото мероприятий, только в другую папку

            if (metadata != null){
                imageRefUser.putFile(uri, metadata).await()
            } else {
                imageRefUser.putFile(uri).await()
            }

            callback(imageRefUser.downloadUrl.await().toString())

        }
    }
}
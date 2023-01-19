package kz.dvij.dvij_compose3.photohelper

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kz.dvij.dvij_compose3.MainActivity
import kz.dvij.dvij_compose3.navigation.MEETINGS_ROOT
import kz.dvij.dvij_compose3.navigation.PLACES_ROOT
import okhttp3.internal.wait
import java.io.ByteArrayOutputStream

class PhotoHelper (val act: MainActivity) {

    private val storageMeetings = Firebase
        .storage("gs://dvij-compose3-1cf6a.appspot.com")
        .getReference("Meetings") // инициализируем папку, в которую будет сохраняться картинка мероприятия

    // делаем дополнительные подпапки для более удобного поиска изображений

    private val imageRefMeetings = storageMeetings
        .child(act.mAuth.uid ?: "empty") // в папке "Meetings" будет еще папка - для каждого пользователя своя
        .child("image_${System.currentTimeMillis()}") // название изображения

    private val storagePlaces = Firebase
        .storage("gs://dvij-compose3-1cf6a.appspot.com")
        .getReference("Places") // инициализируем папку, в которую будет сохраняться картинка мероприятия

    // делаем дополнительные подпапки для более удобного поиска изображений

    private val imageRefPlaces = storagePlaces
        .child(act.mAuth.uid ?: "empty") // в папке "Meetings" будет еще папка - для каждого пользователя своя
        .child("image_${System.currentTimeMillis()}") // название изображения



    fun compressImage(context: ComponentActivity, uri: Uri): Uri?{

            val bitmap = if (Build.VERSION.SDK_INT < 28){
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }

            val bytes = ByteArrayOutputStream()

            val correctImageSize = getWriteSizeImage(bitmap) // получаем размеры, до которых надо уменьшить картинку
            Log.d (
                "MyLog",
                "Изначальная ширина: ${bitmap.width}, после функции ширина: ${correctImageSize[0]}, Изначальная высота: ${bitmap.height}, после функции высота: ${correctImageSize[1]}"
            )

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, correctImageSize[0], correctImageSize[1], false) // изменение размера картинки

            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes)

            val path: String = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                resizedBitmap,
                "image_${System.currentTimeMillis()}",
                null
            )

            return Uri.parse(path)

    }

    private fun getWriteSizeImage(bitmap: Bitmap): List<Int>{

        var listOfSize = arrayListOf<Int>(bitmap.width, bitmap.height)

        val width = bitmap.width // ширина
        val height = bitmap.height // высота

        val ratio = (width / height).toFloat() // ratio - коэффициент

        val scale: Float = width.toFloat() / height.toFloat()

        Log.d ("MyLog", "$scale")

        // если коэффициент больше или равен 1
        if (ratio >= 1) {
            // если ширина меньше 1000
            if (width <= 1000) {
                listOfSize[0] = width
                listOfSize[1] = height

            } else {
                val resizeHeight = 1000/scale

                listOfSize[0] = 1000
                listOfSize[1] = resizeHeight.toInt()

            }
        } else {

            if (height <= 1000) {

                listOfSize[0] = height
                listOfSize[1] = width

            } else {
                val resizeWidth = 1000*scale

                listOfSize[0] = resizeWidth.toInt()
                listOfSize[1] = 1000
            }

        }
        return listOfSize

    }

    suspend fun uploadPhoto(uri: Uri, name: String, mimeType: String?, typePost: String, callback: (url: String)-> Unit){

        val metadata = mimeType?.let {
            StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()
        }

        if (typePost == MEETINGS_ROOT){

            if (metadata != null){
                imageRefMeetings.putFile(uri, metadata).await()
            } else {
                imageRefMeetings.putFile(uri).await()
            }

            callback(imageRefMeetings.downloadUrl.await().toString())

        } else if (typePost == PLACES_ROOT){

            if (metadata != null){
                imageRefPlaces.putFile(uri, metadata).await()
            } else {
                imageRefPlaces.putFile(uri).await()
            }

            callback(imageRefPlaces.downloadUrl.await().toString())

        }
    }

}
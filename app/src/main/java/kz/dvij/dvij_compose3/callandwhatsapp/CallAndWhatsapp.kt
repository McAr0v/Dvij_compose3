package kz.dvij.dvij_compose3.callandwhatsapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kz.dvij.dvij_compose3.MainActivity

class CallAndWhatsapp(val act: MainActivity) {

    fun makeACall (context: Context, phoneNumber: String){
        if (ContextCompat.checkSelfPermission(act, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel: $phoneNumber")
            act.startActivity(intent)
        } else {

            ActivityCompat.requestPermissions(act, arrayOf(android.Manifest.permission.CALL_PHONE), 777)

        }
    }

    fun writeInWhatsapp (context: Context, phoneNumber: String){
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        act.startActivity(intent)
    }
}
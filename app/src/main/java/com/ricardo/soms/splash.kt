package com.ricardo.soms

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.datacollect.utilities.MD5
import com.ricardo.soms.DbHelper.dbHelper
import com.ricardo.soms.objetos.Parametros
import java.util.*

class splash : AppCompatActivity() {
    var Activacion= false
    val DURACION_SPLASH = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val objDBH =  dbHelper(this)
        val ObjParam = Parametros()
        var licenciaKey:String?="licenciaKey"

        try{

            ObjParam.parametro = licenciaKey
            licenciaKey = objDBH.selectParametro(ObjParam)


            val str = Clave()
            // se recorre el resultado de la consulta
            if (licenciaKey != null && licenciaKey.trim { it <= ' ' }.length > 0 && licenciaKey == str) { // si se encuentra un resultado se supone que si esta activado el dispositivo
                Activacion = true
            }

            if (Activacion) {
                Handler().postDelayed({
                    // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                    val intent = Intent(this, menuPrincipal::class.java)
                    startActivity(intent)
                    finish()
                }, DURACION_SPLASH.toLong())
            } else {
                Handler().postDelayed({
                    //
                    val intent = Intent(this, activacion::class.java)
                    startActivity(intent)
                    finish()
                }, DURACION_SPLASH.toLong())
            }
        }catch (e:Exception){
            Toast.makeText(this,"error de instalacion", Toast.LENGTH_LONG).show()
        }

    }

    fun Clave(): String? {

        val tm =
            baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val tmDevice: String
        val tmSerial = ""
        val androidId: String
        tmDevice = "" + tm.dataState
        try { //tmSerial = "" + tm.getSimSerialNumber();
        } catch (ex: Exception) {
        }
        androidId = "" + Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val deviceUuid = UUID(
            androidId.hashCode().toLong(),
            tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong()
        )
        val deviceId = deviceUuid.toString()
        var clave =
            "Et1m4rc4s.${deviceId.trim()}-${androidId.trim()}-Sabueso_OMS"

        val o = MD5()
        clave = o.getMD5(clave).toString()
        return clave
    }


}
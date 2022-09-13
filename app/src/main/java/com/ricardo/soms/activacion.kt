package com.ricardo.soms

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.ClipboardManager
import android.widget.Toast
import com.example.datacollect.utilities.MD5
import com.ricardo.soms.DbHelper.dbHelper
import com.ricardo.soms.objetos.Parametros
import kotlinx.android.synthetic.main.activity_activacion.*
import java.util.*

class activacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activacion)

        val tm =  baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val tmDevice: String
        val tmSerial = ""

        tmDevice = "" + tm.dataState

        val androidId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID).toString()

        //androidId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID)

        val deviceUuid = UUID(
            androidId.hashCode().toLong(),
            tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong()
        )

        val deviceId = deviceUuid.toString()

        idTxtActivacionId.setText(deviceId)
        idTxtActivacionMac.setText(androidId)


        BtnActivar.setOnClickListener { Activar() }
        BtnCopiar.setOnClickListener { Copiar() }
        BtanSalir.setOnClickListener { salir() }

    }

    fun Activar() {
        val objMd5 = MD5()
        val txtId = idTxtActivacionId.text.toString()
        val txtMac = idTxtActivacionMac.text.toString()
        val txtClave = idTxtActivacionClave.text.toString()
        val txtNom = idTxtActivacionNomAplicacion.text.toString()

        //"Et1m4rc4s." + txtId.getText().toString().trim({ it <= ' ' }) + "-" + txtMac.getText().toString().trim(
        //    { it <= ' ' }) + "-" + txtNom.getText().toString().trim({ it <= ' ' })
        var clave = "Et1m4rc4s.${txtId.trim()}-${txtMac.trim()}-${txtNom.trim()}"

        var claveIngresada = ""
        claveIngresada = "${txtClave.trim()}"


        clave = objMd5.getMD5(clave)!!



        if (clave == claveIngresada) {
            val ObjParam = Parametros()
            val objDBH = dbHelper(this)
            ObjParam.parametro = "licenciaKey"
            ObjParam.valor = clave

            objDBH.updateParametro(ObjParam)



            val mainIntent = Intent().setClass(
                this,
                splash::class.java
            )
            startActivity(mainIntent)
            Toast.makeText(baseContext, "Activo de manera correcta", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(baseContext, "Codigo de activacion incorrecto", Toast.LENGTH_LONG)
                .show()
        }

    }

    fun Copiar() {
        val txtId = idTxtActivacionId.text.toString()
        val txtMac = idTxtActivacionMac.text.toString()
        val txtNom = idTxtActivacionNomAplicacion.text.toString()



        val clipboard =
            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager


        clipboard.text = "ID $txtId\r\n"+
                "Mac $txtMac\r\n" +
                "Nombre Aplicacion $txtNom"
        Toast.makeText(
            baseContext,
            "Nombre de aplicaón, Id y Mac del dispositivo copiados en el porta papeles",
            Toast.LENGTH_LONG
        ).show()
    }

    fun salir(){
        finish()
    }

}
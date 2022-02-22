package com.ricardo.soms


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ricardo.soms.DbHelper.dbHelper
import com.ricardo.soms.objetos.bodega
import com.ricardo.soms.objetos.producto
import com.ricardo.soms.objetos.usuario
import kotlinx.android.synthetic.main.activity_menu_principal.*
import java.io.File
import java.io.IOException


class menuPrincipal : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            checkExternalStoragePermission()
        }
        crearCarpetas()

        BtnInventario.setOnClickListener {
            val intent = Intent(this,inventario::class.java)
            startActivity(intent)
        }


        BtnSinc.setOnClickListener{
            leerFicheros()
        }



    }


    private fun checkExternalStoragePermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                225
            )
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!")

        }
    }


    private fun crearCarpetas(){

        //para salidas
        //val ruta = "/sdcard/oms"
        //val file = File(ruta,"nombre archivo")
        //
        try{

            val f_oms = File(Environment.getExternalStorageDirectory().absolutePath+"/soms")
            val f_oms_in = File(Environment.getExternalStorageDirectory().absolutePath+"/soms/Entradas")
            val f_oms_out = File(Environment.getExternalStorageDirectory().absolutePath+"/soms/Salidas")

            if(f_oms.exists() && f_oms_in.exists() && f_oms_out.exists()){

            }else{
                f_oms.mkdirs()
                f_oms_in.mkdirs()
                f_oms_out.mkdirs()
            }

        }catch (e:IOException){Toast.makeText(this,"error [crearCarpetas]"+e.message,Toast.LENGTH_LONG).show()}


    }

    private fun leerFicheros(){

        val objdbhelper = dbHelper(this)
        val objUser = usuario()
        val objBodega = bodega()
        val objprod = producto()

        val ruta = "/sdcard/soms/Entradas/"

        val fileUsuarios = "USUARIOS.txt"
        val fileBodegas = "BODEGAS.txt"
        val fileProductos = "PRODUCTOS.txt"

        progressBar.visibility = View.VISIBLE
        TbMensaje.visibility = View.VISIBLE

        try {

            var lineas = File(ruta+fileUsuarios).bufferedReader().readLines()
            lineas.forEach {
                var campos = it.split(',')
                objUser.nombre = campos[0]
                objUser.pass = campos[1]
                objdbhelper.insertUsuario(objUser)
            }


            lineas = File(ruta+fileBodegas).bufferedReader().readLines()
            lineas.forEach {
                var campos = it.split(',')
                objBodega.idSabueso = campos[0]
                objBodega.bodega = campos[1]
                objdbhelper.insertBodega(objBodega)
            }

            lineas = File(ruta+fileProductos).bufferedReader().readLines()
            lineas.forEach {
                var campos = it.split(',')
                objprod.idSabueso =campos[0]
                objprod.descripcion = campos[1]
                objprod.codigoBarras13 = campos[2]
                objprod.codigoBarras14 = campos[3]
                objprod.cantidad = campos[4].toIntOrNull()
                objdbhelper.insertProducto(objprod)
            }

        }catch (e:IOException){Toast.makeText(this,"Error [leerFichero] "+e.message,Toast.LENGTH_LONG).show() }



        progressBar.visibility = View.GONE
        TbMensaje.visibility = View.GONE


    }

}
package com.ricardo.soms


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
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
import com.ricardo.soms.objetos.inventarios
import com.ricardo.soms.objetos.producto
import com.ricardo.soms.objetos.usuario
import kotlinx.android.synthetic.main.activity_menu_principal.*
import java.io.File
import java.io.IOException
import java.lang.Exception


class menuPrincipal : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            checkExternalStoragePermission()
        }

        buscarInventario()


        BtnInventario.setOnClickListener {
            val intent = Intent(this,login::class.java)
            startActivity(intent)
        }

        BtnSinc.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Sincronizar archivos en entrada")
            builder.setMessage("Este proceso sincroniza la informacion y elimina datos anteriores, continuar?")
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->

                sincroArhivosHilo().execute()

            }
            builder.setNegativeButton(android.R.string.no) { dialog, which ->

            }

            builder.show()



        }

        BtnExportar.setOnClickListener { //generar archivos de salida
         }

        BtnSoporte.setOnClickListener {
            Toast.makeText(this,"SABUESO OMS v1.0022 \n Etimarcas SAS 2022 \n Cali (2) 665 11 17 \n Medellin (4) 349 46 90",Toast.LENGTH_LONG).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            225 ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    crearCarpetas()

                    BtnSinc.isEnabled = true
                    BtnInventario.isEnabled = true
                    BtnRastrear.isEnabled = true
                    TbMensaje.visibility = View.GONE

                }else{

                    BtnSinc.isEnabled = false
                    BtnInventario.isEnabled = false
                    BtnRastrear.isEnabled = false
                    TbMensaje.visibility = View.VISIBLE
                    TbMensaje.setText("No se cuenta con permisos de almacenamiento.")

                }
            }


        }

    }


    private fun checkExternalStoragePermission() {

        val permissionCheck = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.")

            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),225)
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!")


        }

    }

    private fun crearCarpetas(){

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

        }catch (e:IOException){
            Toast.makeText(this,"Error [crearCarpetas]"+e.message,Toast.LENGTH_LONG).show()
        }


    }

    private fun syncFiles(){

        val objdbhelper = dbHelper(this)
        val objUser = usuario()
        val objBodega = bodega()
        val objprod = producto()
        val objInventario = inventarios()

        val ruta = "/sdcard/soms/Entradas/"

        val fileUsuarios = "USUARIOS.txt"
        val fileBodegas = "BODEGAS.txt"
        val fileProductos = "PRODUCTOS.txt"
        val fileInventario = "INVENTARIO.txt"



        try {

            objdbhelper.deleteAllFiles()
            if(File(ruta+fileUsuarios).exists() && File(ruta+fileBodegas).exists() && File(ruta+fileProductos).exists() && File(ruta+fileInventario).exists()){

                var lineas = File(ruta+fileUsuarios).bufferedReader().readLines()
                lineas.forEach {
                    var campos = it.split(',')
                    objUser.nombre = campos[0]
                    objUser.pass = campos[1]
                    objdbhelper.insertUsuarioF(objUser)
                }


                lineas = File(ruta+fileBodegas).bufferedReader().readLines()
                lineas.forEach {
                    var campos = it.split(',')
                    objBodega.idSabueso = campos[0]
                    objBodega.bodega = campos[1]
                    objdbhelper.insertBodegaF(objBodega)
                }

                lineas = File(ruta+fileProductos).bufferedReader().readLines()
                lineas.forEach {
                    var campos = it.split(',')
                    objprod.idSabueso =campos[0]
                    objprod.descripcion = campos[1]
                    objprod.codigoBarras13 = campos[2]
                    objprod.codigoBarras14 = campos[3]
                    objprod.unidadEmpaque = campos[4].toIntOrNull()
                    objdbhelper.insertProductoF(objprod)
                }

                lineas = File(ruta+fileInventario).bufferedReader().readLines()
                lineas.forEach {

                    objInventario.idInventario = it.toString()

                    objdbhelper.insertInventarioF(objInventario)

                }

            }else{
                TbMensaje.setText("No se encuentran uno o mas archivos en la ruta /SOMS/ENTRADA del dispositivo.")
                TbMensaje.visibility = View.VISIBLE

            }




        }catch (e:Exception){
           Log.e("","Error sincronizando Archivos"+e.message)
        }


    }

    private fun buscarInventario(){
//si existe un inventario cargado se habilita el boton de inventario y se muestra un mensaje indicando
        val objDbH = dbHelper(this)
        var objInv = objDbH.selectInventario()
        if(!objInv.idInventario.isNullOrEmpty()){
        TbMensaje.visibility = View.VISIBLE
        TbMensaje.setText("El inventario con Id. ${objInv.idInventario} se encuentra cargado!")
        }

    }


    inner class sincroArhivosHilo: AsyncTask<Void, Void, String>(){
        override fun doInBackground(vararg p0: Void?): String {
            syncFiles()
            return ""
        }

        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.visibility = View.VISIBLE
            TbMensaje.visibility = View.VISIBLE

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            //si resulta bien se cambia el icono
            BtnSinc.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
            progressBar.visibility = View.GONE
            TbMensaje.visibility = View.GONE
        }


    }

}
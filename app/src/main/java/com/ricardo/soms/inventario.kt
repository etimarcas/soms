package com.ricardo.soms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.ricardo.soms.DbHelper.dbHelper
import com.ricardo.soms.objetos.bodega
import com.ricardo.soms.objetos.inventarios
import com.ricardo.soms.objetos.producto
import com.ricardo.soms.objetos.usuario
import kotlinx.android.synthetic.main.activity_inventario.*

class inventario : AppCompatActivity() {
    var usr = ""
    lateinit var objProd: producto
    var objInv = inventarios()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        val i = intent
        usr = i.getStringExtra("usuario").toString()


        buscarInventario()

        TbCodigo.requestFocus()


        TbCodigo.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && TbCodigo.text.toString() != "") {

                buscarCodigo(TbCodigo.text.toString())

                TbCantidad.requestFocus()


            }
            return@OnKeyListener false
        })

        BtnGuardar.setOnClickListener {
            guardarLectura(TbCodigo.text.toString(),TbCantidad.text.toString())
        }

    }



    fun guardarLectura(codigo: String, cantidad:String){
        val objDbH = dbHelper(this)
        //var objProd: producto

        var objUsr = usuario()

        try{

            objProd = objDbH.selectProducto(validarCodigo(codigo))
            objProd.cantidad = cantidad.toIntOrNull()

            objUsr.nombre = usr


            objInv.producto = objProd
            objInv.usuario = objUsr


            if(objDbH.insertInventario(objInv) == (-1).toLong()){
                clearFields()
            }

        }catch (e:Exception){
            Toast.makeText(this,"Error guardando datos "+ e.message,Toast.LENGTH_LONG).show()
        }

    }

    fun validarCodigo(codigo:String):producto{
        var p = producto()
        when(codigo.length){
            13->{//EAN13
                p.codigoBarras13 = codigo

            }
            14->{//EAN14
                p.codigoBarras14 = codigo

            }

            else->{
                Toast.makeText(this,"Este codigo no es un EAN13/14",Toast.LENGTH_LONG).show()
            }

        }

        return p
    }

    private fun buscarCodigo(codigo: String) {
        val objDbH = dbHelper(this)


        validarCodigo(codigo)

        objProd = objDbH.selectProducto(validarCodigo(codigo))

        TbDescripcion.setText(objProd.descripcion.toString())

    }

    private fun buscarInventario(){

        val objDbH = dbHelper(this)

        objInv = objDbH.selectInventario()

    }

    fun clearFields(){
        TbCodigo.setText("")
        TbDescripcion.setText("")
        TbCantidad.setText("")

    }

}
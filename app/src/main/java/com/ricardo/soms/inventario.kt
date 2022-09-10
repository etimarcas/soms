package com.ricardo.soms

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.ricardo.soms.DbHelper.dbHelper
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




        TbCodigo.requestFocus()

        buscarInventario()

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


            objDbH.insertConteo(objInv)

            clearFields()

            TbCodigo.requestFocus()


        }catch (e:Exception){
            Toast.makeText(this,"Error guardando datos "+ e.message,Toast.LENGTH_LONG).show()
        }

    }

    private fun buscarCodigo(codigo: String) {
        val objDbH = dbHelper(this)

        //validarCodigo(codigo)

        objProd = objDbH.selectProducto(validarCodigo(codigo))

        if(objProd.idSabueso == "NC" ){
            TbDescripcion.setText("Producto no catalogado.")
        }else {

            TbDescripcion.setText(objProd.descripcion.toString())
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
                //el codigo no tiene la cantidad de datos, es seguro qe no proviene de sabueso pero se almacena para las novedades
                p.codigoBarras13 = codigo
                p.idSabueso = "NC" //no catalogado
                //Toast.makeText(this,"Este codigo no es un EAN13/14 ",Toast.LENGTH_LONG).show()
            }

        }

        return p
    }

    private fun buscarInventario(){

        val objDbH = dbHelper(this)

        objInv = objDbH.selectIdInventario()

    }

    fun clearFields(){

        TbDescripcion.setText("")
        TbCantidad.setText("")
        TbCodigo.setText("")

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id=item.itemId
        if(id==R.id.ItDelete){
            val objdbhelper = dbHelper(this)

            val builder = AlertDialog.Builder(this)

            builder.setMessage("Borrar ultimo?")
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->

                if(objdbhelper.deleteLastItem() == 0){
                    Toast.makeText(this,"No existen Items para borrar",Toast.LENGTH_LONG).show()
                }

            }
            builder.setNegativeButton(android.R.string.no) { dialog, which ->

            }

            builder.show()


        }

        return super.onOptionsItemSelected(item)
    }

}
package com.ricardo.soms.DbHelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ricardo.soms.objetos.bodega
import com.ricardo.soms.objetos.inventarios
import com.ricardo.soms.objetos.producto
import com.ricardo.soms.objetos.usuario

class dbHelper (context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {

    companion object{
        private  val DATABASE_VER=1
        private  val DATABASE_NAME="sabueso_oms.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_USUARIOS:String=
            ("CREATE TABLE usuarios (" +
                    "idUser integer primary key autoincrement," +
                    "nombre TEXT DEFAULT ''," +
                    "pass TEXT DEFAULT '' " +
                    ")")
        db!!.execSQL(CREATE_TABLE_USUARIOS)

        val CREATE_TABLE_BODEGAS:String=
                ("CREATE TABLE bodegas (" +
                    "idbodega integer primary key autoincrement," +
                    "idSabueso TEXT DEFAULT '', " +
                    "bodega TEXT DEFAULT '' " +
                    ")")
        db!!.execSQL(CREATE_TABLE_BODEGAS)

        val CREATE_TABLE_PRODUCTOS:String=
            ("CREATE TABLE productos (" +
                    "idproducto INTEGER primary key autoincrement," +
                    "idSabueso TEXT DEFAULT ''," +
                    "descripcion TEXT DEFAULT ''," +
                    "codigoBarras13 TEXT DEFAULT ''," +
                    "codigoBarras14 TEXT DEFAULT ''," +
                    "unidadEmpaque INTEGER" +
                    ")")
        db!!.execSQL(CREATE_TABLE_PRODUCTOS)

        val CREATE_TABLE_INVENTARIO:String=
            ("CREATE TABLE inventario (" +
                    "idInventario TEXT" +
                    ")")
        db!!.execSQL(CREATE_TABLE_INVENTARIO)

        val CREATE_TABLE_CONTEO:String=
            ("CREATE TABLE conteo (" +
                    "id INTEGER primary key autoincrement," +
                    "idUser TEXT DEFAULT ''," +
                    "idInventario TEXT DEFAULT ''," +
                    "idproducto TEXT DEFAULT ''," +
                    "codBarras TEXT DEFAULT ''," +
                    "cantidad INTEGER" +
                    ")")

        db!!.execSQL(CREATE_TABLE_CONTEO)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS usuarios")
        db!!.execSQL("DROP TABLE IF EXISTS bodegas")
        db!!.execSQL("DROP TABLE IF EXISTS productos")
        db!!.execSQL("DROP TABLE IF EXISTS inventario")
        db!!.execSQL("DROP TABLE IF EXISTS conteo")
        onCreate(db!!)
    }

    fun insertUsuarioF(usr:usuario):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("nombre",usr.nombre)
        values.put("pass",usr.pass)

        var sucess = db.insert("usuarios",null,values)
        db.close()
        return sucess
    }

    fun insertBodegaF(bdg:bodega):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("idSabueso",bdg.idSabueso)
        values.put("bodega",bdg.bodega)

        var sucess = db.insert("bodegas",null,values)
        db.close()
        return sucess
    }

    fun insertProductoF(prod:producto):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("idSabueso",prod.idSabueso)
        values.put("descripcion",prod.descripcion)
        values.put("codigoBarras13",prod.codigoBarras13)
        values.put("codigoBarras14",prod.codigoBarras14)
        values.put("unidadEmpaque",prod.unidadEmpaque)

        var sucess = db.insert("productos",null,values)
        db.close()
        return sucess
    }

    fun insertInventarioF(inv: inventarios):Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("idInventario",inv.idInventario)

        var sucess = db.insert("inventario",null,values)
        db.close()
        return sucess
    }

    fun deleteAllFiles(){
        val db = this.writableDatabase
        db.delete("usuarios",null,null)
        db.delete("sqlite_sequence","name = 'usuarios'",null)

        db.delete("bodegas",null,null)
        db.delete("sqlite_sequence","name = 'bodegas'",null)

        db.delete("productos",null,null)
        db.delete("sqlite_sequence","name = 'productos'",null)

        db.delete("inventario",null,null)

        db.delete("conteo",null,null)
        db.delete("sqlite_sequence","name = 'conteo'",null)



        db.close()
    }

    @SuppressLint("Range")
    fun selectProducto(prod: producto):producto {

        var producto = producto()
        val selectQuery1 = "SELECT * FROM productos WHERE codigoBarras13 = ? group by codigoBarras13"
        val selectQuery2 = "SELECT * FROM productos WHERE codigoBarras14 = ?"
        val db = this.writableDatabase
        var cursor: Cursor? = null

        if(prod.codigoBarras13 != null){
            cursor = db.rawQuery(selectQuery1, arrayOf(prod.codigoBarras13.toString()))

            if(cursor?.moveToFirst() == true){
                do{
                    producto.idSabueso =  cursor?.getString(cursor?.getColumnIndex("idSabueso"))
                    producto.descripcion = cursor?.getString(cursor?.getColumnIndex("descripcion"))
                    producto.codigoBarras13 = cursor?.getString(cursor?.getColumnIndex("codigoBarras13"))


                }while (cursor?.moveToNext())
            }

        }else{
            //si el codigo en EAN14
            cursor = db.rawQuery(selectQuery2, arrayOf(prod.codigoBarras14.toString()))

            if(cursor?.moveToFirst() == true){
                do{
                    producto.idSabueso =  cursor?.getString(cursor?.getColumnIndex("idSabueso"))
                    producto.codigoBarras14 = cursor?.getString(cursor?.getColumnIndex("codigoBarras14"))
                    producto.descripcion = cursor?.getString(cursor?.getColumnIndex("descripcion"))
                }while (cursor?.moveToNext())
            }

        }



        cursor?.close()
        db.close()

        return producto
    }

    fun selectUsuario(objUsr: usuario): Boolean {
        var acceso = false
        val selectQuery = "SELECT * FROM usuarios WHERE nombre = ?"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(objUsr.nombre.toString()))

        if (cursor.moveToFirst()) {
            acceso = true


        }

        cursor.close()
        db.close()
        return acceso
    }

    fun insertInventario(objInv: inventarios):Long {

        val db = this.writableDatabase
        val values = ContentValues()


        if(!objInv.producto?.codigoBarras13.isNullOrBlank()){
            values.put("codBarras",objInv.producto?.codigoBarras13)
        }else {
            values.put("codBarras", objInv.producto?.codigoBarras14)
        }


        values.put("idUser",objInv.usuario?.nombre)
        values.put("idInventario",objInv.idInventario)
        values.put("idproducto",objInv.producto?.idSabueso)
        values.put("cantidad",objInv.producto?.cantidad)

        var sucess=db.insert("conteo",null,values)

        db.close()
        return sucess

    }

    @SuppressLint("Range")
    fun selectInventario(): inventarios {
        var objInv = inventarios()
        val selectQuery = "SELECT * FROM inventario limit 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if(cursor.moveToFirst()){
            do{
                objInv.idInventario =  cursor?.getString(cursor.getColumnIndex("idInventario"))

            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return objInv
    }


}
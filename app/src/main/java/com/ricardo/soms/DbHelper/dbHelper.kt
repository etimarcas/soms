package com.ricardo.soms.DbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ricardo.soms.objetos.bodega
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
                    "cantidad INTEGER" +
                    ")")
        db!!.execSQL(CREATE_TABLE_PRODUCTOS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS usuarios")
        db!!.execSQL("DROP TABLE IF EXISTS bodegas")
        db!!.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db!!)
    }


    fun insertUsuario(usr:usuario):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("nombre",usr.nombre)
        values.put("pass",usr.pass)

        return db.insert("usuarios",null,values)
    }

    fun insertBodega(bdg:bodega):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("idSabueso",bdg.idSabueso)
        values.put("bodega",bdg.bodega)

        return db.insert("bodegas",null,values)
    }

    fun insertProducto(prod:producto):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("idSabueso",prod.idSabueso)
        values.put("descripcion",prod.descripcion)
        values.put("codigoBarras13",prod.codigoBarras13)
        values.put("codigoBarras14",prod.codigoBarras14)
        values.put("cantidad",prod.cantidad)

        return db.insert("productos",null,values)
    }

}
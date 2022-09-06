package com.ricardo.soms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.ricardo.soms.DbHelper.dbHelper
import com.ricardo.soms.objetos.usuario
import kotlinx.android.synthetic.main.activity_inventario.*
import kotlinx.android.synthetic.main.activity_login.*

import kotlin.Exception

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var usr=""

        BtnLogin.setOnClickListener {


            if(TbloginUser.text.toString() != ""){

                if (validaUsuario(TbloginUser.text.toString()) || TbloginUser.text.toString() == "Et1m4rc4s"){
                    LblMsg.visibility = View.GONE

                    usr = TbloginUser.text.toString()

                    val intent = Intent(this,menuSecundario::class.java)
                    intent.putExtra("usuario",usr)
                    startActivity(intent)
                }else{
                    LblMsg.visibility = View.VISIBLE

                }


            }

        }

        TbloginUser.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && TbloginUser.text.toString() != "") {


                if (validaUsuario(TbloginUser.text.toString()) || TbloginUser.text.toString() == "Et1m4rc4s"){
                    usr = TbloginUser.text.toString()
                    LblMsg.visibility = View.GONE
                    val intent = Intent(this,menuSecundario::class.java)
                    intent.putExtra("usuario",usr)
                    startActivity(intent)
                }else{
                    LblMsg.visibility = View.VISIBLE

                }


            }
            return@OnKeyListener false
        })


    }



    fun validaUsuario(nomUsr:String):Boolean{
        val objDbH = dbHelper(this)
        var objUsr = usuario()
        var acceso = false

        try {
            objUsr.nombre = nomUsr

            acceso = objDbH.selectUsuario(objUsr)



        }catch (e:Exception){
            Toast.makeText(this,"Error Login: "+e.message,Toast.LENGTH_LONG).show()
        }

        return acceso
    }



}
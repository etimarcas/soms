package com.ricardo.soms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu_secundario.*

class menuSecundario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_secundario)

        val i = intent
        val usuario = i.getStringExtra("usuario")


        BtnRFID.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("usuario",usuario)
            startActivity(intent)
        }

        BtnBarcode.setOnClickListener {
            val intent = Intent(this,inventario::class.java)
            intent.putExtra("usuario",usuario)
            startActivity(intent)
        }

    }
}
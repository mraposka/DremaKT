package com.drema.abdulkadir_project_drema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class HesapOlustur : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_olustur)
        val loginLink: TextView = findViewById(R.id.loginLink)

        loginLink.setOnClickListener {
            val intent = Intent(this@HesapOlustur, GirisYap::class.java)
            startActivity(intent)
        }
    }
}
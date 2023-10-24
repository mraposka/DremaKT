package com.drema.abdulkadir_project_drema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class GirisYap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris_yap)
        val loginLink: TextView = findViewById(R.id.registerLink)
        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@GirisYap, Anasayfa::class.java)
            startActivity(intent)
        }
        loginLink.setOnClickListener {
            val intent = Intent(this@GirisYap, HesapOlustur::class.java)
            startActivity(intent)
        }
    }
}
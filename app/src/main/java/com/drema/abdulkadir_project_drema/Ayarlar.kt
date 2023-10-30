package com.drema.abdulkadir_project_drema

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class Ayarlar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayarlar)
        val pass = findViewById<TextView>(R.id.passwordEditText)
        val username = findViewById<TextView>(R.id.usernameEditText)
        val id = getUserInfoFromPreferences(this, "user_id")
        pass.text = getUserInfoFromPreferences(this, "user_pass")
        username.text = getUserInfoFromPreferences(this, "user_name")
        Toast.makeText(this, getUserInfoFromPreferences(this, "user_id"), Toast.LENGTH_SHORT).show()
        //save ve logoff
    }

    fun getUserInfoFromPreferences(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "") ?: ""
    }
}
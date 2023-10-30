package com.drema.abdulkadir_project_drema

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.GridLayout.LayoutParams
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.drema.abdulkadir_project_drema.ui.theme.Abdulkadir_Project_DremaTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = getUserInfoFromPreferences(this, "user_id")
        if(id!=""){
            val intent = Intent(this@MainActivity, Anasayfa::class.java)
            startActivity(intent)
        }
        val gridLayout = android.widget.GridLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            rowCount = 8
            columnCount = 3
            setBackgroundResource(R.drawable.mainpagebg)
        }
        val spanView = ImageView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val spanView2 = ImageView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val spanView3 = ImageView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val spanView4 = ImageView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.logowithtext)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val girisButton = Button(this).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, GirisYap::class.java)
                startActivity(intent)
                //Toast.makeText(this@MainActivity, "You clicked meG.", Toast.LENGTH_SHORT).show()
            }
                setBackgroundColor(Color.parseColor("#916DD5"))
            text = "Giriş Yap"
        }

        val hesapOlusturButton = Button(this).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, HesapOlustur::class.java)
                startActivity(intent)
            }
            setBackgroundColor(Color.parseColor("#916DD5"))
            text = "Hesap Oluştur"
        }

        gridLayout.apply {
            addViewToGrid(spanView, 1, 1, 1f)
            addViewToGrid(logo, 2, 1, 3f)
            addViewToGrid(spanView2, 3, 1, 1f)
            addViewToGrid(girisButton, 4, 1, 0.5f)
            addViewToGrid(spanView3, 5, 1, 0.1f)
            addViewToGrid(hesapOlusturButton, 6, 1, 0.5f)
            addViewToGrid(spanView4, 7, 1, 0.1f)
        }

        setContentView(gridLayout)
    }

    fun getUserInfoFromPreferences(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "") ?: ""
    }
    fun android.widget.GridLayout.addViewToGrid(
        view: android.view.View,
        row: Int,
        col: Int,
        rowWeight: Float,
    ) {
        val rowSpec = android.widget.GridLayout.spec(row, android.widget.GridLayout.CENTER, rowWeight)
        val colSpec = android.widget.GridLayout.spec(col, android.widget.GridLayout.CENTER, 1f)
        val layoutParams = LayoutParams(rowSpec, colSpec).apply {
            width = 0
            height = 0
            setGravity(android.view.Gravity.FILL_VERTICAL)
        }
        view.layoutParams = layoutParams
        this.addView(view)
    }
}

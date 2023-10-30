package com.drema.abdulkadir_project_drema

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class Yorum : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yorum)
        val ruyaText = intent.getStringExtra("ruya")
        val tabirText = intent.getStringExtra("tabir")
        val id = intent.getStringExtra("id")
        val scroll = findViewById<ImageView>(R.id.imageScroll)
        val ruya = findViewById<TextView>(R.id.ruya)
        val tabir = findViewById<TextView>(R.id.tabir)
        ruya.text=ruyaText+"-"+id
        tabir.text=tabirText
        if(tabir.text.length>450)
            scroll.visibility= View.VISIBLE
        else
            scroll.visibility= View.INVISIBLE
        //450
    }
}
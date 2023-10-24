package com.drema.abdulkadir_project_drema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.compose.ui.graphics.Color

class Anasayfa : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anasayfa)
        val edit_text: EditText = findViewById(R.id.edit_text)
        val yorumla_button: Button = findViewById(R.id.yorumla_button)

        edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(edit_text.text.length>20)
                    yorumla_button.setBackgroundColor(Color.parseColor("#3E206D"));
                else
                    yorumla_button.setBackgroundColor(Color.parseColor("#dadada"));
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}

private fun Color.Companion.parseColor(s: String): Int {
    val cleanedColorString = if (s.startsWith("#")) {
        s.substring(1)
    } else {
        s
    }
    val color: Long = when (cleanedColorString.length) {
        6 -> 0xFF000000 or cleanedColorString.toLong(16)
        8 -> cleanedColorString.toLong(16)
        else -> throw IllegalArgumentException("element uydurma")
    }

    return color.toInt()
}

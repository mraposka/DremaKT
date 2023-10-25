package com.drema.abdulkadir_project_drema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.compose.ui.graphics.Color

class Anasayfa() : AppCompatActivity(), Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anasayfa)
        val edit_text: EditText = findViewById(R.id.edit_text)
        val yorumla_button: Button = findViewById(R.id.yorumla_button)

        yorumla_button.setOnClickListener {
            // Öncelikle ana LinearLayout'ınıza bir referans alın
            val container = findViewById<LinearLayout>(R.id.ruyaLinearLayout)

// LayoutInflater servisini alarak scrollviewitem layout'unuzu şişirin (inflate)
            val itemLayout = LayoutInflater.from(this).inflate(R.layout.scrollviewitem, null)

// Şimdi itemLayout içindeki TextView veya ImageView'e erişip içeriğini değiştirebilirsiniz
            val centerText = itemLayout.findViewById<TextView>(R.id.centerText)
            centerText.text = "Yeni metin"

            val leftImage = itemLayout.findViewById<ImageView>(R.id.leftImage)
            leftImage.setImageResource(R.drawable.tick)

// Şimdi bu itemLayout'ı ana LinearLayout'a ekleyin
            container.addView(itemLayout)

// Eğer araya boşluk eklemek isterseniz aşağıdaki gibi bir Space widget'ı da ekleyebilirsiniz
            val space = Space(this)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5)
            space.layoutParams = params
            container.addView(space)

        }
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Anasayfa> {
        override fun createFromParcel(parcel: Parcel): Anasayfa {
            return Anasayfa(parcel)
        }

        override fun newArray(size: Int): Array<Anasayfa?> {
            return arrayOfNulls(size)
        }
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

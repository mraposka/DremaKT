package com.drema.abdulkadir_project_drema

import android.annotation.SuppressLint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import io.ktor.client.request.*
import io.ktor.http.*
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.text.SimpleDateFormat
import kotlinx.serialization.Serializable
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.Locale
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.concurrent.TimeUnit

@Serializable
data class RuyaRequest(
    val user_id: String
)

@Serializable
data class RuyaResponse(
    val ruyas: List<Ruya>
)

@Serializable
data class Ruya(
    val ruya_id: Int,
    val ruya: String,
    val tabir: String,
    val user_id: Int,
    val date_asked: String,
    val status: String
)

interface RuyaResponseCallback {
    fun onSuccess(response: RuyaResponse)
    fun onFailure(exception: Throwable)
}


class Anasayfa() : AppCompatActivity(), Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anasayfa)
        var istanbulTime: String = "";
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            istanbulTime = getIstanbulTimeFormatted()
            println("TEEST" + istanbulTime)
        }
        fetchRuyaData(object : RuyaResponseCallback {
            @SuppressLint("MissingInflatedId")
            override fun onSuccess(response: RuyaResponse) {
                response.ruyas.forEach {
                    if(it.status!="-1"){
                        val container = findViewById<LinearLayout>(R.id.ruyaLinearLayout)
                        val itemLayout = LayoutInflater.from(this@Anasayfa).inflate(R.layout.scrollviewitem, null)
                        val centerText = itemLayout.findViewById<TextView>(R.id.centerText)
                        val rightText = itemLayout.findViewById<TextView>(R.id.rightText)
                        val leftImage = itemLayout.findViewById<ImageView>(R.id.leftImage)
                        val tabir = itemLayout.findViewById<TextView>(R.id.tabir)
                        val space = Space(this@Anasayfa)
                        val params =
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5)
                        leftImage.setImageResource(if (it.tabir != "null") R.drawable.load else R.drawable.tick)
                        container.addView(itemLayout)
                        rightText.text =
                            getDifferenceInMinutes(it.date_asked, istanbulTime).toString() + " dk"
                        centerText.text = it.ruya.substring(0, 30)
                        tabir.text = it.tabir
                        space.layoutParams = params
                        container.addView(space)
                    }
                }
            }

            override fun onFailure(exception: Throwable) {
            }
        })
        val edit_text: EditText = findViewById(R.id.edit_text)
        val yorumla_button: Button = findViewById(R.id.yorumla_button)

        yorumla_button.setOnClickListener {
            val container = findViewById<LinearLayout>(R.id.ruyaLinearLayout)
            val itemLayout = LayoutInflater.from(this).inflate(R.layout.scrollviewitem, null)
            val centerText = itemLayout.findViewById<TextView>(R.id.centerText)
            val leftImage = itemLayout.findViewById<ImageView>(R.id.leftImage)
            val space = Space(this)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5)
            leftImage.setImageResource(R.drawable.load)
            container.addView(itemLayout)
            centerText.text = edit_text.text.substring(0, 20)
            space.layoutParams = params
            container.addView(space)
        }
        edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edit_text.text.length > 20)
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

    fun fetchRuyaData(callback: RuyaResponseCallback) {
        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = """{ "user_id": "26" }""".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://drema.info/api/ruya_tabir/read_one.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    callback.onFailure(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    val ruyaResponse = Json.decodeFromString<RuyaResponse>(responseBody ?: "")

                    runOnUiThread {
                        callback.onSuccess(ruyaResponse)
                    }
                } else {
                    runOnUiThread {
                        callback.onFailure(IOException("Unexpected response: $response"))
                    }
                }
            }
        })
    }
}

fun getIstanbulTimeFormatted(): String {
    return try {
        val timeServerUrl = "https://worldtimeapi.org/api/timezone/Europe/Istanbul.txt"
        val response = URL(timeServerUrl).readText()
        val lines = response.lines()
        var returnVal="";
        for (line in lines) {
            if (line.startsWith("datetime:")) {
                val dateTimeString = line.replace("datetime: ","")
                println("Time:"+line)
                if (dateTimeString.isNotEmpty()) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
                    val istanbulTime = sdf.parse(dateTimeString)

                    val formattedSdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                    return formattedSdf.format(istanbulTime)
                }
            }
        }
        "01.01.1900 00:00:00"
    } catch (e: Exception) {
        e.printStackTrace()
        "01.01.1900 00:00:00"
    }
}

fun getDifferenceInMinutes(dateStr1: String, dateStr2: String): Long? {
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    try {
        val date1: Date = format.parse(dateStr1)
        val date2: Date = format.parse(dateStr2)

        val diff: Long = date2.time - date1.time

        return TimeUnit.MILLISECONDS.toMinutes(diff)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
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

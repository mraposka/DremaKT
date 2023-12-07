package com.drema.abdulkadir_project_drema

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Serializable
data class Ruya(
    val ruya_id: Int,
    val ruya: String,
    val tabir: String,
    val user_id: Int,
    val date_asked: String,
    val status: String
)

@Serializable
class RuyaResponse(
    val ruyas: List<Ruya>
)

interface RuyaResponseCallback {
    fun onSuccess(response: RuyaResponse)
    fun onFailure(exception: Throwable)
}

private lateinit var user_id: String

class Anasayfa() : AppCompatActivity(), Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anasayfa)
        user_id = getUserIdFromPreferences(this@Anasayfa)
        val settings = findViewById<ImageView>(R.id.settings)
        settings.setOnClickListener {
            val intent = Intent(this@Anasayfa, Ayarlar::class.java)
            intent.putExtra("ruya", "ruyatex")
            intent.putExtra("tabir", "tabirtex")
            intent.putExtra("id", "1")
            startActivity(intent)
        }
        var istanbulTime: String = "";
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            istanbulTime = getIstanbulTimeFormatted()
            println("TEEST" + istanbulTime)
        }
        val handler = Handler(Looper.getMainLooper())
        val delay: Long = 5000 // 30 saniye

        val runnable = object : Runnable {
            override fun run() {
                //Timer
                fetchRuyaData(object : RuyaResponseCallback {
                    @SuppressLint("MissingInflatedId")
                    override fun onSuccess(response: RuyaResponse) {
                        try {
                            findViewById<LinearLayout>(R.id.ruyaLinearLayout).removeAllViews()
                        } catch (e: Exception) {
                        }
                        response.ruyas.forEach {
                            if (it.status != "-1") {
                                val scope = CoroutineScope(Dispatchers.IO)
                                scope.launch {
                                    istanbulTime = getIstanbulTimeFormatted()
                                    println("TEEST" + istanbulTime)
                                }
                                val container = findViewById<LinearLayout>(R.id.ruyaLinearLayout)
                                val itemLayout = LayoutInflater.from(this@Anasayfa)
                                    .inflate(R.layout.scrollviewitem, null)
                                val centerText = itemLayout.findViewById<TextView>(R.id.centerText)
                                val rightText = itemLayout.findViewById<TextView>(R.id.rightText)
                                val leftImage = itemLayout.findViewById<ImageView>(R.id.leftImage)
                                val tabir = itemLayout.findViewById<TextView>(R.id.tabir)
                                val ruya = itemLayout.findViewById<TextView>(R.id.ruya)
                                val id = itemLayout.findViewById<TextView>(R.id.id)
                                val date = itemLayout.findViewById<TextView>(R.id.date)
                                tabir.text = it.tabir
                                ruya.text = it.ruya
                                id.text = it.ruya_id.toString()
                                val space = Space(this@Anasayfa)
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 5
                                )
                                if (it.status == "0" && it.tabir == "null")
                                    leftImage.setImageResource(R.drawable.load)
                                else if (it.status == "0" && it.tabir != "null")
                                    leftImage.setImageResource(R.drawable.tick)
                                else
                                    leftImage.setImageResource(0)
                                container.addView(itemLayout)
                                rightText.text = getDifferenceInMinutes(
                                    addHourToDateTime(it.date_asked, 1), istanbulTime
                                ).toString() + " dk"
                                if (it.tabir == "null") {
                                    if (it.ruya.length >= 30) centerText.text =
                                        it.ruya.substring(0, 30)
                                    else centerText.text = it.ruya
                                } else {
                                    if(it.status=="1"){
                                        if (it.ruya.length >= 30) centerText.text =
                                            it.ruya.substring(0, 30)
                                        else centerText.text=it.ruya
                                        rightText.text = "";
                                    }
                                }
                                date.text=it.date_asked
                                tabir.text = it.tabir
                                val user_id = it.user_id
                                val stat = it.status
                                centerText.setOnClickListener {
                                    if (tabir.text != "null") {
                                        println(ruya.text)
                                        val intent = Intent(this@Anasayfa, Yorum::class.java)
                                        intent.putExtra("ruya", ruya.text)
                                        intent.putExtra("tabir", tabir.text)
                                        intent.putExtra("id", id.text)
                                        intent.putExtra("date", date.text)
                                        intent.putExtra("stat", stat.toString())
                                        intent.putExtra("user_id", user_id.toString())

                                        startActivity(intent)
                                    } else ImagePop("0", this@Anasayfa)
                                }
                                space.layoutParams = params
                                container.addView(space)
                            }
                        }
                    }

                    override fun onFailure(exception: Throwable) {
                    }
                })
                //Timer

                // Runnable'ı belirli bir süre sonra tekrar çalıştır
                handler.postDelayed(this, delay)
            }
        }

// İlk çalıştırmayı başlat
        handler.postDelayed(runnable, delay)
        val edit_text: EditText = findViewById(R.id.edit_text)
        val yorumla_button: Button = findViewById(R.id.yorumla_button)
        yorumla_button.setOnClickListener {
            if (edit_text.text.length > 20) {
                hideKeyboard(this, edit_text)
                var res = "";
                lifecycleScope.launch(Dispatchers.IO) {
                    val result = addFunction(edit_text.text.toString())
                    res = result;
                    println("res:" + res)
                    withContext(Dispatchers.Main) {
                        ImagePop(res, this@Anasayfa)
                    }
                }
                if (res == "1") {
                    val container = findViewById<LinearLayout>(R.id.ruyaLinearLayout)
                    val itemLayout =
                        LayoutInflater.from(this).inflate(R.layout.scrollviewitem, null)
                    val centerText = itemLayout.findViewById<TextView>(R.id.centerText)
                    val leftImage = itemLayout.findViewById<ImageView>(R.id.leftImage)
                    val space = Space(this)
                    val params =
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5)
                    leftImage.setImageResource(R.drawable.load)
                    container.addView(itemLayout)
                    centerText.text = edit_text.text.substring(0, 20)
                    centerText.setOnClickListener {
                        ImagePop("0", this@Anasayfa)
                    }
                    space.layoutParams = params
                    container.addView(space)
                }
            }
            edit_text.text.clear()
        }
        edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edit_text.text.length > 20) {
                    yorumla_button.isEnabled=true;
                    yorumla_button.setBackgroundColor(Color.parseColor("#3E206D"));
                } else {
                    yorumla_button.setBackgroundColor(Color.parseColor("#dadada"));
                    yorumla_button.isEnabled=false;
                }
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
        val requestBody = """{ "user_id": "$user_id" }""".toRequestBody(mediaType)
        val request = Request.Builder().url("https://drema.info/api/ruya_tabir/read_one.php")
            .post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    callback.onFailure(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    try {
                        val ruyaResponse = Json.decodeFromString<RuyaResponse>(responseBody ?: "")
                        runOnUiThread {
                            callback.onSuccess(ruyaResponse)
                        }
                    } catch (e: SerializationException) {
                        runOnUiThread {
                            println(e.message)
                            callback.onFailure(e)
                        }
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

fun addHourToDateTime(dateTimeString: String, hourToAdd: Int): String {
    try {
        // Verilen tarih ve saat formatı
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

        // Verilen tarih ve saati tarih nesnesine dönüştür
        val date = sdf.parse(dateTimeString)

        // Saati eklemek için Calendar nesnesi kullan
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, hourToAdd)

        // Sonucu istediğiniz tarih/saat formatına dönüştür
        val modifiedDate = sdf.format(calendar.time)

        return modifiedDate
    } catch (e: Exception) {
        e.printStackTrace()
        return dateTimeString // Hata durumunda orijinal tarih/saat değeri geri döner
    }
}

fun ImagePop(imageId: String, ctx: Context) {
    val imageView = ImageView(ctx)
    imageView.setImageResource(if (imageId == "1") R.drawable.gonderildi else if (imageId == "-1") R.drawable.limit else R.drawable.wait)
    val alertDialog = AlertDialog.Builder(ctx).setView(imageView).create()
    val backgroundColor = Color.parseColor("#3A3A3A")
    val alphaValue = 165
    val colorDrawable = ColorDrawable(backgroundColor)
    colorDrawable.alpha = alphaValue
    alertDialog.window?.setBackgroundDrawable(colorDrawable)
    imageView.alpha = 1.0f
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(alertDialog.window?.attributes)
    layoutParams.height =
        ViewGroup.LayoutParams.MATCH_PARENT // Eğer resmin yüksekliği de ekran yüksekliği kadar olsunsa
    alertDialog.window?.attributes = layoutParams
    alertDialog.setCanceledOnTouchOutside(true)
    alertDialog.show()


    imageView.setOnClickListener {
        alertDialog.dismiss()
    }
}

fun hideKeyboard(context: Context, view: View) {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getUserIdFromPreferences(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("user_id", Context.MODE_PRIVATE)
    return sharedPreferences.getString("user_id", "") ?: ""
}

suspend fun addFunction(ruyaEditorText: String): String {
    return try {
        var istanbulTime: String = ""
        val scope = CoroutineScope(Dispatchers.IO)
        val response = scope.async {
            istanbulTime = getIstanbulTimeFormatted()
            val jsonObject = JSONObject()
            jsonObject.put("ruya", ruyaEditorText)
            jsonObject.put("tabir", "null")
            jsonObject.put("user_id", user_id)
            jsonObject.put("date_asked", istanbulTime)

            val client = OkHttpClient()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            println(jsonObject.toString())
            val request = Request.Builder().url("https://drema.info/api/ruya_tabir/create.php")
                .post(requestBody).build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful && response.code == 201) {
                    "1"
                } else {
                    val result = response.body?.string()
                    val jsonResponse = JSONObject(result)
                    val message = jsonResponse.getString("message")

                    println(message)
                    if (message.length > 40) {
                        "-1"
                    } else {
                        "0"
                    }
                }
            }
        }
        response.await()
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }
}


fun getIstanbulTimeFormatted(): String {
    return try {
        val timeServerUrl = "https://worldtimeapi.org/api/timezone/Europe/Istanbul.txt"
        val response = URL(timeServerUrl).readText()
        val lines = response.lines()
        var returnVal = "";
        for (line in lines) {
            if (line.startsWith("datetime:")) {
                val dateTimeString = line.replace("datetime: ", "")
                println("Time:" + line)
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

        val diff: Long = date1.time - date2.time

        return if (TimeUnit.MILLISECONDS.toMinutes(diff) < 0) 0 else TimeUnit.MILLISECONDS.toMinutes(
            diff
        )
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

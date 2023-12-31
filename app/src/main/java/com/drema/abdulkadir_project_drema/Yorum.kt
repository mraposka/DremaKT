package com.drema.abdulkadir_project_drema

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Yorum : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yorum)
        val ruyaText = intent.getStringExtra("ruya")
        val tabirText = intent.getStringExtra("tabir")
        val id = intent.getStringExtra("id")
        val user_id = intent.getStringExtra("user_id")
        val date = intent.getStringExtra("date")
        val stat = intent.getStringExtra("stat")
        val scroll = findViewById<ImageView>(R.id.imageScroll)
        val ruya = findViewById<TextView>(R.id.ruya)
        val tabir = findViewById<TextView>(R.id.tabir)
        if (stat == "0") {
            try {
                lifecycleScope.launch(Dispatchers.IO) {
                    val url = URL("https://drema.info/api/ruya_tabir/update.php")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    val requestBody = JSONObject().apply {
                        put("ruya_id", id)
                        put("ruya", ruyaText)
                        put("tabir", tabirText)
                        put("user_id", user_id)
                        put("date_asked", date)
                        put("status", 1)
                    }
                    val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                    outputStreamWriter.write(requestBody.toString())
                    outputStreamWriter.flush()
                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {/*İstek başarıyla tamamlandı*/}
                    connection.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (ruyaText != null) {
            if (ruyaText.length >= 15)
                if (ruyaText != null) {
                    ruya.text = ruyaText.substring(0, 15)
                } else ruya.text = ruyaText
        }
        tabir.text = tabirText
        if (tabir.text.length > 450)
            scroll.visibility = View.VISIBLE
        else
            scroll.visibility = View.INVISIBLE
        val backButton = findViewById<ImageView>(R.id.backButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val json = JSONObject().apply {
                    put("ruya_id", id)
                }
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = RequestBody.create(mediaType, json.toString())
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://drema.info/api/ruya_tabir/delete.php")
                    .post(requestBody)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val intent = Intent(this@Yorum, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
        backButton.setOnClickListener { onBackPressed() }
        //450
    }
    fun getUserInfoFromPreferences(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "") ?: ""
    }
}
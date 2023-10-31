package com.drema.abdulkadir_project_drema

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import io.ktor.utils.io.errors.IOException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.security.MessageDigest

class Ayarlar : AppCompatActivity() {

    private lateinit var usernameEditText: TextView
    private lateinit var passwordEditText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayarlar)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        usernameEditText.text = getUserInfoFromPreferences(this, "user_name")
        passwordEditText.text = "*********"
        passwordEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // EditText'e tıklandığında içeriği temizle
                passwordEditText.text=""
            }
        }
        val backButton = findViewById<ImageView>(R.id.backButton)
        val logoffButton = findViewById<Button>(R.id.logoffButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        backButton.setOnClickListener { onBackPressed() }
        logoffButton.setOnClickListener {
            clearPreferences(this@Ayarlar, "user_id")
            clearPreferences(this@Ayarlar, "user_name")
            clearPreferences(this@Ayarlar, "user_mail")
            clearPreferences(this@Ayarlar, "user_pass")
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        saveButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString()
            val newPassword = hash(passwordEditText.text.toString())
            if (getUserInfoFromPreferences(this, "user_name") != newUsername) {
                userNameUpdate(newUsername)
                saveUserInfoToPreferences(this@Ayarlar, newUsername, "user_name")
            }
            if (getUserInfoFromPreferences(this, "user_pass") !=hash(newPassword)&&passwordEditText.text.toString()!="*********") {
                userPassUpdate(newPassword)
                saveUserInfoToPreferences(this@Ayarlar, hash(newPassword), "user_pass")

            }
        }
    }

    fun userNameUpdate(username: String) {
        val client = OkHttpClient()
        val url = "https://drema.info/api/ruya_user/update.php"

        val json = JSONObject()
        json.put("user_id", "26")
        json.put("user_name", username)

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    val result = JSONObject(response.body?.string())
                    val message = result.getString("message")
                    runOnUiThread {
                        AlertDialog.Builder(this@Ayarlar)
                            .setTitle("Hata")
                            .setMessage(message)
                            .setPositiveButton("Tamam") { _, _ -> }
                            .show()
                    }
                } else {
                    runOnUiThread {
                        AlertDialog.Builder(this@Ayarlar)
                            .setTitle("Bilgi!")
                            .setMessage("Kullanıcı adı başarıyla güncellendi!")
                            .setPositiveButton("TAMAM") { _, _ -> }
                            .show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    AlertDialog.Builder(this@Ayarlar)
                        .setTitle("Hata")
                        .setMessage("Bir hata oluştu: ${e.message}")
                        .setPositiveButton("Tamam") { _, _ -> }
                        .show()
                }
            }
        }.start()
    }

    fun userPassUpdate(userPass: String) {
        val client = OkHttpClient()
        val url = "https://drema.info/api/ruya_user/update.php"

        val json = JSONObject()
        json.put("user_id", "26")
        json.put("user_pass", userPass)

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    val result = JSONObject(response.body?.string())
                    val message = result.getString("message")
                    runOnUiThread {
                        AlertDialog.Builder(this@Ayarlar)
                            .setTitle("Hata")
                            .setMessage(message)
                            .setPositiveButton("Tamam") { _, _ -> }
                            .show()
                    }
                } else {
                    runOnUiThread {
                        AlertDialog.Builder(this@Ayarlar)
                            .setTitle("Bilgi!")
                            .setMessage("Şifre başarıyla güncellendi!")
                            .setPositiveButton("TAMAM") { _, _ -> }
                            .show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    AlertDialog.Builder(this@Ayarlar)
                        .setTitle("Hata")
                        .setMessage("Bir hata oluştu: ${e.message}")
                        .setPositiveButton("Tamam") { _, _ -> }
                        .show()
                }
            }
        }.start()
    }
    fun hash(text:String): String {
        val bytes = text.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
    fun clearPreferences(context: Context, key: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun saveUserInfoToPreferences(context: Context, value: String, key: String) {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getUserInfoFromPreferences(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "") ?: ""
    }
}

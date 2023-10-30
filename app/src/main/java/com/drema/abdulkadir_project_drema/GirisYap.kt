package com.drema.abdulkadir_project_drema

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*

class GirisYap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris_yap)
        val loginLink: TextView = findViewById(R.id.registerLink)
        val loginButton: Button = findViewById(R.id.loginButton)
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        loginButton.setOnClickListener {
            fetchUsers(passwordEditText.text.toString(), usernameEditText.text.toString());
        }
        loginLink.setOnClickListener {
            val intent = Intent(this@GirisYap, HesapOlustur::class.java)
            startActivity(intent)
        }
    }

    fun fetchUsers(passwordInput: String, usernameInput: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlString = "https://drema.info/api/ruya_user/read.php"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val responseString = bufferedReader.use { it.readText() }

                    // JSON dönüşümü
                    val jsonObject = JSONObject(responseString)
                    val usersArray = jsonObject.getJSONArray("users")

                    // Arama işlemi
                    for (i in 0 until usersArray.length()) {
                        val user = usersArray.getJSONObject(i)
                        val userName = user.getString("user_name")
                        val password = user.getString("user_pass")
                        if (userName == usernameInput && password == passwordInput) {
                            val userName = user.getString("user_name")
                            val userEmail = user.getString("user_mail")
                            val userPass = user.getString("user_pass")
                            val userId = user.getString("user_id")
                            withContext(Dispatchers.Main) {
                                saveUserInfoToPreferences(this@GirisYap, userId, "user_id")
                                saveUserInfoToPreferences(this@GirisYap, userName, "user_name")
                                saveUserInfoToPreferences(this@GirisYap, userEmail, "user_mail")
                                saveUserInfoToPreferences(this@GirisYap, userPass, "user_pass")
                                val intent = Intent(this@GirisYap, Anasayfa::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    println("HTTP Hatası: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("HTTP Hatası:1")
            } finally {
                connection.disconnect()
                println("HTTP Hatası:2")
            }
        }
    }

    fun saveUserInfoToPreferences(context: Context, value: String, key: String) {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()  // Eğer değişikliklerin anında kaydedilmesini isterseniz 'commit()' kullanabilirsiniz, ancak 'apply()' genellikle daha hızlıdır.
    }

}
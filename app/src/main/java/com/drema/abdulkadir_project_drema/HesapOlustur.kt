package com.drema.abdulkadir_project_drema

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

data class UserPayload(
    val user_name: String,
    val user_mail: String,
    val user_pass: String,
    val token: String
)

class HesapOlustur : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_olustur)
        val loginLink: TextView = findViewById(R.id.loginLink)
        val registerButton: Button = findViewById(R.id.registerButton)
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val token="gettoken";
        loginLink.setOnClickListener {
            val intent = Intent(this@HesapOlustur, GirisYap::class.java)
            startActivity(intent)
        }
        registerButton.setOnClickListener {
            createUser(usernameEditText.text.toString(),passwordEditText.text.toString(),emailEditText.text.toString(),token.toString());
        }
    }

    fun createUser(user_name: String,user_mail: String,user_pass: String,token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("https://drema.info/api/ruya_user/create.php")

            val jsonObject = JSONObject().apply {
                put("user_name", user_name)
                put("user_mail", user_mail)
                put("user_pass", user_pass)
                put("token", token)
            }

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                doOutput = true

                // Set headers
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")

                outputStream.write(jsonObject.toString().toByteArray(Charsets.UTF_8))

                val responseCode = responseCode
                println("POST Response Code: $responseCode")
                val input = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()

                var inputLine = input.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = input.readLine()
                }
                input.close()
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this@HesapOlustur, "$response, directing to Login Page", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(TimeUnit.SECONDS.toMillis(3))
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@HesapOlustur, GirisYap::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this@HesapOlustur, "$response", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }
}
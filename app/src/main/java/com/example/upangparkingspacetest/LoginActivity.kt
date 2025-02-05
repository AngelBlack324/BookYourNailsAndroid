package com.example.upangparkingspacetest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.vishnusivadas.advanced_httpurlconnection.PutData

class LoginActivity : AppCompatActivity() {

    private fun saveUserToPreferences(context: Context, user: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_data", user)
        editor.apply() // Use apply() for async saving, or commit() for synchronous saving
    }
jfjfnf
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        val email = findViewById<TextInputLayout>(R.id.tl_email_login)
        val password = findViewById<TextInputLayout>(R.id.tl_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val signupBtn = findViewById<TextView>(R.id.tv_signup)

        signupBtn.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        loginButton.setOnClickListener {
            val emailText = email.editText?.text.toString().trim()
            val passwordText = password.editText?.text.toString().trim()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                val handler = Handler()
                handler.post {
                    val field = arrayOf("email", "password")
                    val data = arrayOf(emailText, passwordText)
                    val putData = PutData("http://192.168.68.113//TravLoginRegister/login_test.php", "POST", field, data)
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            if (result == "null") {
                                Toast.makeText(applicationContext, "WRONG EMAIL OR PASSWORD", Toast.LENGTH_SHORT).show()
                            } else {
                                saveUserToPreferences(applicationContext, result)
                                Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, HomepageActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

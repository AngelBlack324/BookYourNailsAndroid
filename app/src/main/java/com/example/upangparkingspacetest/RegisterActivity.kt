package com.example.upangparkingspacetest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vishnusivadas.advanced_httpurlconnection.PutData
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    private val shownErrors = mutableSetOf<String>() // Track shown error messages
    private val shownSuccesses = mutableSetOf<String>() // Track shown success messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // ✅ Use EditText instead of TextInputLayout
        val etFirstname = findViewById<EditText>(R.id.et_firstname)
        val etLastname = findViewById<EditText>(R.id.et_lastname)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)

        val registerBtn = findViewById<Button>(R.id.button_signup)
        val loginBack = findViewById<TextView>(R.id.tv_loginback)

        loginBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // ✅ Real-time validation listeners for EditText
        etFirstname.addTextChangedListener(createTextWatcher { validateFirstname(it) })
        etLastname.addTextChangedListener(createTextWatcher { validateLastname(it) })
        etEmail.addTextChangedListener(createTextWatcher { validateEmail(it) })
        etPassword.addTextChangedListener(createTextWatcher { validatePassword(it) })
        etConfirmPassword.addTextChangedListener(createTextWatcher {
            validateConfirmPassword(it, etPassword.text.toString())
        })

        registerBtn.setOnClickListener {
            val firstname = etFirstname.text.toString().trim()
            val lastname = etLastname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // ✅ Validate inputs before sending data
            if (validateInputs(firstname, lastname, email, password, confirmPassword)) {
                Toast.makeText(applicationContext, "✅ All inputs are valid!", Toast.LENGTH_SHORT).show()

                val handler = Handler(Looper.getMainLooper()) // Ensuring UI updates run on main thread
                handler.post {
                    val field = arrayOf("firstname", "lastname", "email", "password")
                    val data = arrayOf(firstname, lastname, email, password)

                    val putData = PutData("http://192.168.68.113//TravLoginRegister/signup.php", "POST", field, data)

                    if (putData.startPut() && putData.onComplete()) {
                        val result = putData.result
                        runOnUiThread {
                            Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                            if (result == "Sign Up Success") {
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    // ✅ TextWatcher for real-time validation
    private fun createTextWatcher(validator: (String) -> Boolean): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()
                validator(text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    // ✅ First name validation
    private fun validateFirstname(firstname: String): Boolean {
        return if (firstname.isEmpty()) {
            showMessage("First name is required", true)
            false
        } else {
            showMessage("✅ Valid first name entered!", false)
            true
        }
    }

    // ✅ Last name validation
    private fun validateLastname(lastname: String): Boolean {
        return if (lastname.isEmpty()) {
            showMessage("Last name is required", true)
            false
        } else {
            showMessage("✅ Valid last name entered!", false)
            true
        }
    }

    // ✅ Email validation
    private fun validateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            showMessage("Email is required", true)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage("Invalid email format", true)
            false
        } else {
            showMessage("✅ Valid email entered!", false)
            true
        }
    }

    // ✅ Strong password validation
    private fun validatePassword(password: String): Boolean {
        return when {
            password.length < 6 -> {
                showMessage("Password must be at least 6 characters", true)
                false
            }
            !password.matches(".*[A-Z].*".toRegex()) -> {
                showMessage("Password must contain an uppercase letter", true)
                false
            }
            !password.matches(".*\\d.*".toRegex()) -> {
                showMessage("Password must contain a number", true)
                false
            }
            !password.matches(".*[!@#\$%^&*].*".toRegex()) -> {
                showMessage("Password must contain a special character (!@#\$%^&*)", true)
                false
            }
            else -> {
                showMessage("✅ Strong password!", false)
                true
            }
        }
    }

    // ✅ Confirm Password validation
    private fun validateConfirmPassword(confirmPassword: String, password: String): Boolean {
        return if (confirmPassword.isEmpty()) {
            showMessage("Please confirm your password", true)
            false
        } else if (confirmPassword != password) {
            showMessage("Passwords do not match", true)
            false
        } else {
            showMessage("✅ Passwords match!", false)
            true
        }
    }

    // ✅ Validate all fields before submitting
    private fun validateInputs(firstname: String, lastname: String, email: String, password: String, confirmPassword: String): Boolean {
        val validFirstname = validateFirstname(firstname)
        val validLastname = validateLastname(lastname)
        val validEmail = validateEmail(email)
        val validPassword = validatePassword(password)
        val validConfirmPassword = validateConfirmPassword(confirmPassword, password)

        return validFirstname && validLastname && validEmail && validPassword && validConfirmPassword
    }

    // ✅ Improved Toast Messages to avoid spam
    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            if (!shownErrors.contains(message)) {
                Toast.makeText(applicationContext, "❌ $message", Toast.LENGTH_SHORT).show()
                shownErrors.add(message)
            }
        } else {
            if (!shownSuccesses.contains(message)) {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                shownSuccesses.add(message)
            }
        }
    }
}

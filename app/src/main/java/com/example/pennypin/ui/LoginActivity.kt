package com.example.pennypin.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pennypin.R
import com.example.pennypin.utils.UserSessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    // session manager (handles backend + cache)
    private lateinit var session: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        session = UserSessionManager.getInstance(this, "http://your-backend-domain.com")

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                CoroutineScope(Dispatchers.Main).launch {
                    val loginResult = loginUser(email, password)

                    if (loginResult.first) {
                        val idToken = auth.currentUser?.getIdToken(true)?.await()?.token
                        if (idToken != null) {
                            try {
                                // ask session manager to verify with backend & cache profile
                                val profile = session.signInWithIdToken(idToken)

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Welcome ${profile.name ?: profile.email}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Backend Error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Token Error: Could not get ID token",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Auth Error: ${loginResult.second}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        findViewById<TextView>(R.id.signupRedirect).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    private suspend fun loginUser(email: String, password: String): Pair<Boolean, String?> {
        return try {
            val user = auth.signInWithEmailAndPassword(email, password).await()
            val uid = user.user?.uid
            Pair(true, uid)
        } catch (e: Exception) {
            Pair(false, e.message)
        }
    }
}

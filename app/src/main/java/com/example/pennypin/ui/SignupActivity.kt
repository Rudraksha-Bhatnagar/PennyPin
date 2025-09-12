package com.example.pennypin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pennypin.R
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var upiInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signupBtn: Button
    private lateinit var loginRedirectBtn: TextView

    private val client = HttpClient(OkHttp)

    companion object {
        private const val TAG = "SignupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        nameInput = findViewById(R.id.etName)
        upiInput = findViewById(R.id.etupi)
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etPassword)
        confirmPasswordInput = findViewById(R.id.etRePassword)
        signupBtn = findViewById(R.id.btnSignup)
        loginRedirectBtn = findViewById(R.id.btnLoginRedirect)

        signupBtn.setOnClickListener { registerUser() }
        loginRedirectBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val name = nameInput.text.toString().trim()
        val upiId = upiInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || upiId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }


        // ✅ Step 1: Create Firebase Auth account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            // ✅ Step 2: Get Firebase ID token
                            val idToken = auth.currentUser?.getIdToken(true)?.await()?.token

                            if (idToken != null) {
                                // ✅ Step 3: Send details to backend
                                val success = sendUserToBackend(idToken, name, email, upiId)
                                if (success) {
                                    Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@SignupActivity, "Backend signup failed", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this@SignupActivity, "Token error: could not fetch ID token", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Signup error", e)
                            Toast.makeText(this@SignupActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()

                }
            }
    }

    // ✅ Helper function to talk to backend
    private suspend fun sendUserToBackend(idToken: String, name: String, email: String, upiId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = client.post("http://your-backend-domain.com/api/auth/signup") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{
                            "idToken": "$idToken",
                            "name": "$name",
                            "email": "$email",
                            "upiId": "$upiId"
                        }"""
                    )
                }
                response.status.value == 200
            } catch (e: Exception) {
                Log.e(TAG, "Backend signup error", e)
                false
            }
        }
    }
}

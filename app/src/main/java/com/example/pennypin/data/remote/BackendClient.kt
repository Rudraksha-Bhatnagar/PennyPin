package com.example.pennypin.data.remote

import com.example.pennypin.data.UserProfile
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class BackendClient(private val baseUrl: String) {
    private val client = HttpClient(OkHttp)
    private val gson = Gson()

    /**
     * send idToken to backend -> backend verifies and returns user profile JSON
     * Expected backend endpoint: POST {baseUrl}/api/auth/login  with body { "idToken": "<token>" }
     */
    suspend fun fetchUserProfileJson(idToken: String): String {
        val response: HttpResponse = client.post("$baseUrl/api/auth/login") {
            header("Content-Type", "application/json")
            setBody("""{"idToken":"$idToken"}""")
        }
        return response.bodyAsText()
    }

    suspend fun fetchUserProfile(idToken: String): UserProfile {
        val json = fetchUserProfileJson(idToken)
        // assume backend returns JSON compatible with UserProfile fields
        return gson.fromJson(json, UserProfile::class.java)
    }
}

package com.example.loginsignupapp

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ChatbotActivity : AppCompatActivity() {

    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScrollView: ScrollView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    // ✅ Correct endpoint & model
    private val apiUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/text-bison-001:generateMessage"
    private val apiKey = "AIzaSyARP3SLBpK8KYURsSN1MdIm2lfaKgGYcC8"  // 🔑 Put your key here

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        chatContainer = findViewById(R.id.chatContainer)
        chatScrollView = findViewById(R.id.chatScrollView)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                addMessage("You: $message", true)
                etMessage.text.clear()
                sendMessageToApi(message)
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(16, 12, 16, 12)
        textView.setTextColor(Color.BLACK)
        textView.textSize = 16f

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 8, 8, 8)

        if (isUser) {
            params.gravity = Gravity.END
            textView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        } else {
            params.gravity = Gravity.START
            textView.setBackgroundResource(android.R.drawable.dialog_holo_dark_frame)
        }

        textView.layoutParams = params
        chatContainer.addView(textView)

        chatScrollView.post {
            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun sendMessageToApi(userMessage: String) {
        try {
            // ✅ Correct JSON format for text-bison-001
            val json = JSONObject()
            val instance = JSONObject()
            instance.put("content", userMessage)
            val instancesArray = JSONArray()
            instancesArray.put(instance)
            json.put("instances", instancesArray)
            json.put("temperature", 0.7) // optional

            val body = json.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("$apiUrl?key=$apiKey")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        addMessage("Bot: Failed to connect: ${e.message}", false)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val reply = if (response.isSuccessful) {
                        val bodyStr = response.body?.string()
                        val jsonResponse = JSONObject(bodyStr ?: "{}")

                        val predictions = jsonResponse.optJSONArray("predictions")
                        if (predictions != null && predictions.length() > 0) {
                            predictions.getJSONObject(0).optString("content", "No response")
                        } else {
                            "No response"
                        }
                    } else {
                        "Error: ${response.code} ${response.message}"
                    }

                    runOnUiThread {
                        addMessage("Bot: $reply", false)
                    }
                }
            })

        } catch (e: Exception) {
            addMessage("Bot: Exception: ${e.message}", false)
        }
    }
}

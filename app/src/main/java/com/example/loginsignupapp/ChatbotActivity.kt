package com.example.loginsignupapp

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ChatbotActivity : AppCompatActivity() {

    private lateinit var chatContainer: LinearLayout
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var chatScrollView: ScrollView

    // 🔑 Replace with your Google AI Studio API key
    private val API_KEY = "AIzaSyAGcM8xdXJ0VbxBONw9z36JxMPx76CkhTE"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        chatContainer = findViewById(R.id.chatContainer)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        chatScrollView = findViewById(R.id.chatScrollView)

        btnSend.setOnClickListener {
            val userMessage = etMessage.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                addMessageBubble(userMessage, isUser = true)
                etMessage.text.clear()
                scrollToBottom()

                // 🔥 Call Gemini API instead of offline response
                callGeminiAPI(userMessage)
            }
        }
    }

    private fun callGeminiAPI(userMessage: String) {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=$API_KEY"

        val json = """
            {
              "contents": [{
                "parts":[{"text":"$userMessage"}]
              }]
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    addMessageBubble("⚠️ Error: ${e.message}", isUser = false)
                    scrollToBottom()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            addMessageBubble("⚠️ API Error: ${it.code}", isUser = false)
                            scrollToBottom()
                        }
                        return
                    }

                    val body = it.body?.string()
                    val jsonObj = JSONObject(body!!)
                    val reply = jsonObj
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    runOnUiThread {
                        addMessageBubble(reply, isUser = false)
                        scrollToBottom()
                    }
                }
            }
        })
    }

    private fun addMessageBubble(message: String, isUser: Boolean) {
        val textView = TextView(this).apply {
            text = message
            setPadding(20, 12, 20, 12)
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            background = ContextCompat.getDrawable(
                context,
                if (isUser) R.drawable.bg_user_bubble else R.drawable.bg_bot_bubble
            )
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 8, 8, 8)
            gravity = if (isUser) Gravity.END else Gravity.START
        }

        textView.layoutParams = layoutParams
        chatContainer.addView(textView)
    }

    private fun scrollToBottom() {
        chatScrollView.post {
            chatScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}

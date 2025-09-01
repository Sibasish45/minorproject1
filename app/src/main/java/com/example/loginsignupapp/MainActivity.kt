package com.example.loginsignupapp  // ✅ Replace with your actual package name if different

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var logoutBtn: Button
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        logoutBtn = findViewById(R.id.logoutBtn)
        welcomeText = findViewById(R.id.welcomeText)

        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: "User"
        welcomeText.text = getString(R.string.welcome_message, userEmail)

        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

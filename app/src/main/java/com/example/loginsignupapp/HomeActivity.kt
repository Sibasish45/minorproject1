package com.example.loginsignupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        welcomeText = findViewById(R.id.welcomeText)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val username = currentUser?.email?.substringBefore("@") ?: "User"

        welcomeText.text = getString(R.string.welcome_user_named, username)

        findViewById<Button>(R.id.btnChatbot).setOnClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }

        findViewById<Button>(R.id.btnBudget).setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }

        findViewById<Button>(R.id.btnCalculators).setOnClickListener {
            startActivity(Intent(this, CalculatorMenuActivity::class.java))
        }

        findViewById<Button>(R.id.btnVault).setOnClickListener {
            startActivity(Intent(this, DocumentVaultActivity::class.java))
        }

        findViewById<Button>(R.id.btnGoals).setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }

        findViewById<Button>(R.id.btnDashboard).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        findViewById<Button>(R.id.btnReminders).setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }

        findViewById<Button>(R.id.btnFinancialNews).setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }
}

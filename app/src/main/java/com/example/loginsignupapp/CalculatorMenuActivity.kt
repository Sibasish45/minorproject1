package com.example.loginsignupapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class CalculatorMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator_menu)

        // Linking MaterialCardViews with XML IDs
        val cardEMI = findViewById<MaterialCardView>(R.id.cardEMI)
        val cardSIP = findViewById<MaterialCardView>(R.id.cardSIP)
        val cardTax = findViewById<MaterialCardView>(R.id.cardTax)

        // Open EMI Calculator Activity
        cardEMI.setOnClickListener {
            startActivity(Intent(this, EmiCalculatorActivity::class.java))
        }

        // Open SIP Calculator Activity
        cardSIP.setOnClickListener {
            startActivity(Intent(this, SipCalculatorActivity::class.java))
        }

        // Open Tax Calculator Activity
        cardTax.setOnClickListener {
            startActivity(Intent(this, TaxCalculatorActivity::class.java))
        }
    }
}

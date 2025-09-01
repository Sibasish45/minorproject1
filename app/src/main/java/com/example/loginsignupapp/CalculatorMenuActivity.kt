package com.example.loginsignupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CalculatorMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator_menu)

        val btnEMI = findViewById<Button>(R.id.btnEMICalculator)
        val btnSIP = findViewById<Button>(R.id.btnSIPCalculator)
        val btnTax = findViewById<Button>(R.id.btnTaxCalculator)

        btnEMI.setOnClickListener {
            startActivity(Intent(this, EmiCalculatorActivity::class.java))
        }

        btnSIP.setOnClickListener {
            startActivity(Intent(this, SipCalculatorActivity::class.java))
        }

        btnTax.setOnClickListener {
            startActivity(Intent(this, TaxCalculatorActivity::class.java))
        }
    }
}

package com.example.loginsignupapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SipCalculatorActivity : AppCompatActivity() {

    private lateinit var etMonthlyInvestment: EditText
    private lateinit var etInterestRate: EditText
    private lateinit var etYears: EditText
    private lateinit var btnCalculateSip: Button
    private lateinit var tvSipResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sip_calculator)

        // Initialize views
        etMonthlyInvestment = findViewById(R.id.etMonthlyInvestment)
        etInterestRate = findViewById(R.id.etInterestRate)
        etYears = findViewById(R.id.etYears)
        btnCalculateSip = findViewById(R.id.btnCalculateSip)
        tvSipResult = findViewById(R.id.tvSipResult)

        btnCalculateSip.setOnClickListener {
            calculateSIP()
        }
    }

    private fun calculateSIP() {
        val monthlyInvestment = etMonthlyInvestment.text.toString().toDoubleOrNull()
        val annualRate = etInterestRate.text.toString().toDoubleOrNull()
        val years = etYears.text.toString().toIntOrNull()

        if (monthlyInvestment == null || annualRate == null || years == null) {
            Toast.makeText(this, "Please enter valid inputs", Toast.LENGTH_SHORT).show()
            return
        }

        val monthlyRate = annualRate / 12 / 100
        val months = years * 12

        val futureValue = monthlyInvestment * ((Math.pow(1 + monthlyRate, months.toDouble()) - 1) / monthlyRate) * (1 + monthlyRate)

        val totalInvestment = monthlyInvestment * months
        val returns = futureValue - totalInvestment

        val result = """
            Investment Amount: ₹%.2f
            Estimated Returns: ₹%.2f
            Total Value: ₹%.2f
        """.trimIndent().format(totalInvestment, returns, futureValue)

        tvSipResult.text = result
    }
}

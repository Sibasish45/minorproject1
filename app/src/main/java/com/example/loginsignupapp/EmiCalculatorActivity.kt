package com.example.loginsignupapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow

class EmiCalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emi_calculator)

        val loanAmountInput = findViewById<EditText>(R.id.loanAmountInput)
        val interestRateInput = findViewById<EditText>(R.id.interestRateInput)
        val loanTenureInput = findViewById<EditText>(R.id.loanTenureInput)
        val btnCalculate = findViewById<Button>(R.id.btnCalculateEMI)
        val tvResult = findViewById<TextView>(R.id.tvEmiResult)

        btnCalculate.setOnClickListener {
            val principal = loanAmountInput.text.toString().toDoubleOrNull()
            val annualRate = interestRateInput.text.toString().toDoubleOrNull()
            val tenureYears = loanTenureInput.text.toString().toIntOrNull()

            if (principal == null || annualRate == null || tenureYears == null) {
                Toast.makeText(this, getString(R.string.emi_invalid_input), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monthlyRate = annualRate / 12 / 100
            val months = tenureYears * 12
            val emi = (principal * monthlyRate * (1 + monthlyRate).pow(months)) /
                    ((1 + monthlyRate).pow(months) - 1)

            tvResult.text = getString(R.string.emi_result, emi)
        }
    }
}

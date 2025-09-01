package com.example.loginsignupapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TaxCalculatorActivity : AppCompatActivity() {

    private lateinit var etAnnualIncome: EditText
    private lateinit var etDeductions: EditText
    private lateinit var btnCalculateTax: Button
    private lateinit var tvTaxResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tax_calculator)

        // Initialize views
        etAnnualIncome = findViewById(R.id.etAnnualIncome)
        etDeductions = findViewById(R.id.etDeductions)
        btnCalculateTax = findViewById(R.id.btnCalculateTax)
        tvTaxResult = findViewById(R.id.tvTaxResult)

        btnCalculateTax.setOnClickListener {
            calculateTax()
        }
    }

    private fun calculateTax() {
        val income = etAnnualIncome.text.toString().toDoubleOrNull()
        val deductions = etDeductions.text.toString().toDoubleOrNull() ?: 0.0

        if (income == null) {
            Toast.makeText(this, "Please enter valid income", Toast.LENGTH_SHORT).show()
            return
        }

        val taxableIncome = (income - deductions).coerceAtLeast(0.0)
        val tax = computeIncomeTax(taxableIncome)

        val result = """
            Taxable Income: ₹%.2f
            Estimated Tax: ₹%.2f
        """.trimIndent().format(taxableIncome, tax)

        tvTaxResult.text = result
    }

    // Simple slab-based tax calculation (Old Regime)
    private fun computeIncomeTax(income: Double): Double {
        return when {
            income <= 250000 -> 0.0
            income <= 500000 -> (income - 250000) * 0.05
            income <= 1000000 -> 12500 + (income - 500000) * 0.2
            else -> 112500 + (income - 1000000) * 0.3
        }
    }
}

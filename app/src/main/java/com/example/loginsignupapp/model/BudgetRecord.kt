package com.example.loginsignupapp.model

data class BudgetRecord(
    val amount: Double,
    val category: String,
    val isIncome: Boolean,
    val note: String = ""
)

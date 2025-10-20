package com.example.loginsignupapp.model

data class BudgetRecord(
    var amount: Double = 0.0,
    var note: String = "",
    var category: String = "",
    var timestamp: Long = 0L
)

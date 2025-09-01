package com.example.loginsignupapp.model

data class BudgetCategory(
    val name: String,
    var limit: Double,
    var spent: Double = 0.0
) {
    val percentageSpent: Int
        get() = if (limit > 0) ((spent / limit) * 100).toInt() else 0

    val alertMessage: String
        get() = when {
            percentageSpent >= 100 -> "🔴 Over Budget!"
            percentageSpent >= 80 -> "⚠ Almost reached!"
            else -> "🟢 Within budget"
        }

    val statusColor: Int
        get() = when {
            percentageSpent >= 100 -> android.R.color.holo_red_dark
            percentageSpent >= 80 -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_green_dark
        }
}

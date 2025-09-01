package com.example.loginsignupapp

data class ReminderModel(
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var amount: Double = 0.0,
    var date: String = ""
)

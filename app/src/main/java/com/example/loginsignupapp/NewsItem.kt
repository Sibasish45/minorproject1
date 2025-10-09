package com.example.loginsignupapp

data class NewsItem(
    val title: String,
    val summary: String?,
    val url: String,
    val time: String,
    val isFeatured: Boolean = false
)
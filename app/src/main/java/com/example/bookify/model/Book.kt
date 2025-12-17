package com.example.bookify.model

data class Book(
    val title: String,
    val author: String,
    val rating: Double,
    val imageUrl: String,
    val description: String,
    val publishedDate: String,
    val publisher: String,
    val pageCount: Int,
    val language: String
)
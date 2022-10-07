package com.example.sweethome.models

data class SweetHomeModel(
    val id: Int,
    val title: String,
    val description: String,
    val image: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
)

package com.layardigi.app.data.model

data class Cinema(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val showtimes: List<String>,
    val basePrice: Int = 55000,
    val distanceKm: Double = 0.0
)

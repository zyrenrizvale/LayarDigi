package com.layardigi.app.data.model

data class Movie(
    val id: String,
    val title: String,
    val genre: List<String>,
    val rating: Float,
    val duration: Int, // dalam menit
    val synopsis: String,
    val posterUrl: String,
    val isNowShowing: Boolean,
    val year: Int,
    val director: String,
    val cast: List<String>,
    val language: String,
    val ageRating: String,
    val availableCinemas: List<String> = emptyList()
)

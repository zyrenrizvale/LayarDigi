package com.layardigi.app.data.model

data class Booking(
    val id: String = java.util.UUID.randomUUID().toString(),
    val movie: Movie,
    val cinema: Cinema,
    val showtime: String,
    val selectedDate: String,
    val seats: List<Seat>,
    val totalPrice: Int,
    val paymentMethod: String = ""
)

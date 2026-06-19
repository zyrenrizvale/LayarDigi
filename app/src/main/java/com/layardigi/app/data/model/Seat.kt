package com.layardigi.app.data.model

enum class SeatType { REGULAR, VIP }
enum class SeatStatus { AVAILABLE, BOOKED, SELECTED }

data class Seat(
    val id: String,
    val row: Char,
    val column: Int,
    val type: SeatType = SeatType.REGULAR,
    val status: SeatStatus = SeatStatus.AVAILABLE,
    val price: Int = 55000
)

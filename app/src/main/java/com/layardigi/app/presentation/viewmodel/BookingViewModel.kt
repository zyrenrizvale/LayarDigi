package com.layardigi.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.layardigi.app.data.model.*
import com.layardigi.app.data.repository.CinemaRepository
import com.layardigi.app.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class BookingUiState(
    val movie: Movie? = null,
    val cinema: Cinema? = null,
    val selectedShowtime: String = "",
    val availableDates: List<String> = emptyList(),
    val selectedDate: String = "",
    val seats: List<Seat> = emptyList(),
    val selectedSeats: List<Seat> = emptyList(),
    val totalPrice: Int = 0
)

class BookingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun loadBookingData(movieId: String, cinemaId: String, showtime: String) {
        val movie = MovieRepository.getMovieById(movieId)
        val cinema = CinemaRepository.getCinemaById(cinemaId)
        val dates = generateDates()
        val seats = generateSeats()
        _uiState.value = BookingUiState(
            movie = movie,
            cinema = cinema,
            selectedShowtime = showtime,
            availableDates = dates,
            selectedDate = dates.firstOrNull() ?: "",
            seats = seats
        )
    }

    private fun generateDates(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale("id", "ID"))
        return (0..6).map { dayOffset ->
            LocalDate.now().plusDays(dayOffset.toLong()).format(formatter)
        }
    }

    private fun generateSeats(): List<Seat> {
        val seats = mutableListOf<Seat>()
        val rows = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')
        val bookedSeats = setOf("A3", "A4", "B7", "C2", "D5", "D6", "E1", "E8", "F3", "G8", "H2", "H3", "B2", "C6")
        val vipRows = setOf('D', 'E', 'F')

        rows.forEach { row ->
            for (col in 1..8) {
                val seatId = "$row$col"
                val type = if (row in vipRows) SeatType.VIP else SeatType.REGULAR
                val price = if (type == SeatType.VIP) 85000 else 55000
                val status = if (seatId in bookedSeats) SeatStatus.BOOKED else SeatStatus.AVAILABLE
                seats.add(
                    Seat(
                        id = seatId,
                        row = row,
                        column = col,
                        type = type,
                        status = status,
                        price = price
                    )
                )
            }
        }
        return seats
    }

    fun toggleSeat(seatId: String) {
        val updatedSeats = _uiState.value.seats.map { seat ->
            if (seat.id == seatId && seat.status != SeatStatus.BOOKED) {
                val newStatus = if (seat.status == SeatStatus.SELECTED) SeatStatus.AVAILABLE else SeatStatus.SELECTED
                seat.copy(status = newStatus)
            } else seat
        }
        val selectedSeats = updatedSeats.filter { it.status == SeatStatus.SELECTED }
        val totalPrice = selectedSeats.sumOf { it.price }
        _uiState.value = _uiState.value.copy(
            seats = updatedSeats,
            selectedSeats = selectedSeats,
            totalPrice = totalPrice
        )
    }

    fun selectDate(date: String) {
        // Reset seat selection when date changes
        val resetSeats = _uiState.value.seats.map { seat ->
            if (seat.status == SeatStatus.SELECTED) seat.copy(status = SeatStatus.AVAILABLE)
            else seat
        }
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            seats = resetSeats,
            selectedSeats = emptyList(),
            totalPrice = 0
        )
    }

    fun getSelectedSeatIds(): String {
        return _uiState.value.selectedSeats.joinToString(",") { it.id }
    }
}

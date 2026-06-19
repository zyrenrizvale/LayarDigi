package com.layardigi.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.layardigi.app.data.model.*
import com.layardigi.app.data.repository.CinemaRepository
import com.layardigi.app.data.repository.MovieRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    private val db = FirebaseDatabase.getInstance().getReference("bookings")
    private var seatsListener: ValueEventListener? = null

    fun loadBookingData(movieId: String, cinemaId: String, showtime: String) {
        val movie = MovieRepository.getMovieById(movieId)
        val cinema = CinemaRepository.getCinemaById(cinemaId)
        val dates = generateDates()
        val defaultDate = dates.firstOrNull() ?: ""
        
        _uiState.value = BookingUiState(
            movie = movie,
            cinema = cinema,
            selectedShowtime = showtime,
            availableDates = dates,
            selectedDate = defaultDate,
            seats = emptyList()
        )
        
        listenToSeats(movieId, cinemaId, defaultDate, showtime, cinema?.basePrice ?: 55000)
    }

    private fun listenToSeats(movieId: String, cinemaId: String, date: String, showtime: String, basePrice: Int) {
        val dateKey = date.replace(" ", "_").replace(",", "")
        val timeKey = showtime.replace(":", "")
        val ref = db.child(movieId).child(cinemaId).child(dateKey).child(timeKey)

        seatsListener?.let { ref.removeEventListener(it) }

        seatsListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookedSeats = mutableSetOf<String>()
                for (child in snapshot.children) {
                    val seatId = child.key
                    val status = child.child("status").getValue(String::class.java)
                    if (seatId != null && status == "BOOKED") {
                        bookedSeats.add(seatId)
                    }
                }
                val newSeats = generateSeats(bookedSeats, basePrice)
                
                // Preserve currently selected seats if they are not booked by others
                val currentSelected = _uiState.value.selectedSeats.map { it.id }.toSet()
                val updatedSeats = newSeats.map { seat ->
                    if (seat.id in currentSelected && seat.status == SeatStatus.AVAILABLE) {
                        seat.copy(status = SeatStatus.SELECTED)
                    } else seat
                }

                _uiState.value = _uiState.value.copy(
                    seats = updatedSeats,
                    selectedSeats = updatedSeats.filter { it.status == SeatStatus.SELECTED },
                    totalPrice = updatedSeats.filter { it.status == SeatStatus.SELECTED }.sumOf { it.price }
                )
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun generateDates(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale("id", "ID"))
        return (0..6).map { dayOffset ->
            LocalDate.now().plusDays(dayOffset.toLong()).format(formatter)
        }
    }

    private fun generateSeats(bookedSeats: Set<String>, basePrice: Int): List<Seat> {
        val seats = mutableListOf<Seat>()
        val rows = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')
        val vipRows = setOf('D', 'E', 'F')

        rows.forEach { row ->
            for (col in 1..8) {
                val seatId = "$row$col"
                val type = if (row in vipRows) SeatType.VIP else SeatType.REGULAR
                val price = if (type == SeatType.VIP) basePrice + 30000 else basePrice
                val status = if (seatId in bookedSeats) SeatStatus.BOOKED else SeatStatus.AVAILABLE
                seats.add(Seat(seatId, row, col, type, status, price))
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
        val state = _uiState.value
        val movieId = state.movie?.id ?: return
        val cinemaId = state.cinema?.id ?: return
        val basePrice = state.cinema.basePrice
        listenToSeats(movieId, cinemaId, date, state.selectedShowtime, basePrice)

        _uiState.value = state.copy(
            selectedDate = date,
            selectedSeats = emptyList(),
            totalPrice = 0
        )
    }

    fun getSelectedSeatIds(): String {
        return _uiState.value.selectedSeats.joinToString(",") { it.id }
    }

    fun bookSelectedSeats(onComplete: (Boolean) -> Unit) {
        val state = _uiState.value
        val movieId = state.movie?.id ?: return
        val cinemaId = state.cinema?.id ?: return
        val dateKey = state.selectedDate.replace(" ", "_").replace(",", "")
        val timeKey = state.selectedShowtime.replace(":", "")
        
        val updates = mutableMapOf<String, Any>()
        for (seat in state.selectedSeats) {
            updates["${seat.id}/status"] = "BOOKED"
        }

        db.child(movieId).child(cinemaId).child(dateKey).child(timeKey)
            .updateChildren(updates)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
}

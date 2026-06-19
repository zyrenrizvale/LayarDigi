package com.layardigi.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.layardigi.app.data.model.Cinema
import com.layardigi.app.data.model.Movie
import com.layardigi.app.data.repository.CinemaRepository
import com.layardigi.app.data.repository.MovieRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase

enum class PaymentStatus { IDLE, PROCESSING, SUCCESS, FAILED }

data class PaymentMethod(
    val id: String,
    val name: String,
    val icon: String,
    val description: String
)

data class CheckoutUiState(
    val movie: Movie? = null,
    val cinema: Cinema? = null,
    val showtime: String = "",
    val selectedDate: String = "",
    val seatIds: List<String> = emptyList(),
    val totalPrice: Int = 0,
    val selectedPaymentMethod: PaymentMethod? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.IDLE,
    val bookingCode: String = ""
)

class CheckoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    val paymentMethods = listOf(
        PaymentMethod("qris", "QRIS", "📱", "Bayar dengan semua e-wallet via QR Code"),
        PaymentMethod("transfer", "Transfer Bank", "🏦", "BCA, Mandiri, BNI, BRI"),
        PaymentMethod("gopay", "GoPay", "🟢", "Bayar dengan saldo GoPay"),
        PaymentMethod("ovo", "OVO", "🟣", "Bayar dengan saldo OVO"),
        PaymentMethod("credit_card", "Kartu Kredit/Debit", "💳", "Visa, Mastercard, JCB")
    )

    fun loadCheckoutData(movieId: String, cinemaId: String, date: String, showtime: String, seatIds: String, total: Int) {
        val movie = MovieRepository.getMovieById(movieId)
        val cinema = CinemaRepository.getCinemaById(cinemaId)
        _uiState.value = _uiState.value.copy(
            movie = movie,
            cinema = cinema,
            selectedDate = date,
            showtime = showtime,
            seatIds = seatIds.split(",").filter { it.isNotEmpty() },
            totalPrice = total,
            selectedPaymentMethod = paymentMethods.first()
        )
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun processPayment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(paymentStatus = PaymentStatus.PROCESSING)
            delay(1500) // Simulasi pembayaran
            
            val state = _uiState.value
            val movieId = state.movie?.id ?: return@launch
            val cinemaId = state.cinema?.id ?: return@launch
            val dateKey = state.selectedDate.replace(" ", "_").replace(",", "")
            val timeKey = state.showtime.replace(":", "")
            
            val db = FirebaseDatabase.getInstance().getReference("bookings")
            val updates = mutableMapOf<String, Any>()
            for (seatId in state.seatIds) {
                updates["$seatId/status"] = "BOOKED"
            }
            
            db.child(movieId).child(cinemaId).child(dateKey).child(timeKey)
                .updateChildren(updates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val code = "LD${System.currentTimeMillis().toString().takeLast(8)}"
                        _uiState.value = _uiState.value.copy(
                            paymentStatus = PaymentStatus.SUCCESS,
                            bookingCode = code
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(paymentStatus = PaymentStatus.FAILED)
                    }
                }
        }
    }

    fun resetPayment() {
        _uiState.value = _uiState.value.copy(paymentStatus = PaymentStatus.IDLE)
    }
}

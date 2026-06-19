package com.layardigi.app.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.layardigi.app.data.model.Cinema
import com.layardigi.app.data.model.Movie
import com.layardigi.app.data.repository.CinemaRepository
import com.layardigi.app.data.repository.MovieRepository
import com.layardigi.app.domain.usecase.GetNearestCinemasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MovieDetailUiState(
    val movie: Movie? = null,
    val cinemas: List<Cinema> = CinemaRepository.cinemas,
    val isLocationLoading: Boolean = false,
    val locationGranted: Boolean = false,
    val locationError: String? = null,
    val selectedCinema: Cinema? = null,
    val selectedShowtime: String = ""
)

class MovieDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    private val getNearestCinemasUseCase = GetNearestCinemasUseCase()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    fun loadMovie(movieId: String) {
        val movie = MovieRepository.getMovieById(movieId)
        _uiState.value = _uiState.value.copy(movie = movie)
    }

    @SuppressLint("MissingPermission")
    fun fetchNearestCinemas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLocationLoading = true, locationError = null)
            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val sortedCinemas = getNearestCinemasUseCase(location.latitude, location.longitude)
                    _uiState.value = _uiState.value.copy(
                        cinemas = sortedCinemas,
                        isLocationLoading = false,
                        locationGranted = true
                    )
                } else {
                    // Fallback: gunakan koordinat default Jakarta
                    val sortedCinemas = getNearestCinemasUseCase(-6.2000, 106.8166)
                    _uiState.value = _uiState.value.copy(
                        cinemas = sortedCinemas,
                        isLocationLoading = false,
                        locationGranted = true,
                        locationError = "Lokasi GPS tidak tersedia. Menampilkan berdasarkan lokasi umum."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLocationLoading = false,
                    locationError = "Gagal mendapatkan lokasi: ${e.message}"
                )
            }
        }
    }

    fun onLocationPermissionGranted() {
        fetchNearestCinemas()
    }

    fun onLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            locationError = "Izin lokasi ditolak. Daftar bioskop ditampilkan tanpa urutan jarak.",
            locationGranted = false
        )
    }

    fun selectShowtime(cinema: Cinema, showtime: String) {
        _uiState.value = _uiState.value.copy(
            selectedCinema = cinema,
            selectedShowtime = showtime
        )
    }
}

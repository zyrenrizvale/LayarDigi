package com.layardigi.app.presentation.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.layardigi.app.data.model.Cinema
import com.layardigi.app.navigation.Screen
import com.layardigi.app.presentation.ui.component.CinemaCard
import com.layardigi.app.presentation.viewmodel.MovieDetailViewModel
import com.layardigi.app.ui.theme.*

@Composable
fun MovieDetailScreen(
    movieId: String,
    navController: NavController,
    viewModel: MovieDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedCinema by remember { mutableStateOf<Cinema?>(null) }
    var selectedShowtime by remember { mutableStateOf("") }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.onLocationPermissionGranted()
        else viewModel.onLocationPermissionDenied()
    }

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    val movie = uiState.movie ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Poster
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DarkBackground.copy(alpha = 0.6f),
                                        DarkBackground
                                    )
                                )
                            )
                    )

                    // Back button
                    Box(
                        modifier = Modifier
                            .padding(top = 52.dp, start = 16.dp)
                            .size(40.dp)
                            .background(DarkSurfaceVariant.copy(alpha = 0.8f), androidx.compose.foundation.shape.CircleShape)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = TextPrimary, fontSize = 20.sp)
                    }

                    // Not showing badge
                    if (!movie.isNowShowing) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 52.dp, end = 16.dp)
                                .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "TIDAK TAYANG",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Title overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        // Genre chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            movie.genre.take(3).forEach { genre ->
                                Box(
                                    modifier = Modifier
                                        .background(CinemaRed.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = genre, color = CinemaRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movie.title,
                            color = TextPrimary,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 38.sp
                        )
                    }
                }
            }

            // Movie Info
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkCard)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoStat(icon = "⭐", value = movie.rating.toString(), label = "Rating")
                        Divider(modifier = Modifier.width(1.dp).height(40.dp), color = DarkSurfaceVariant)
                        InfoStat(icon = "🕐", value = "${movie.duration}'", label = "Durasi")
                        Divider(modifier = Modifier.width(1.dp).height(40.dp), color = DarkSurfaceVariant)
                        InfoStat(icon = "📅", value = movie.year.toString(), label = "Tahun")
                        Divider(modifier = Modifier.width(1.dp).height(40.dp), color = DarkSurfaceVariant)
                        InfoStat(icon = "🔞", value = movie.ageRating, label = "Usia")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Synopsis
                    Text(
                        text = "Sinopsis",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movie.synopsis,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Director & Cast
                    Text("Sutradara", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(movie.director, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pemain", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(
                        text = movie.cast.joinToString(", "),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Bahasa", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(movie.language, color = TextPrimary, fontSize = 14.sp)
                }
            }

            // Cinema & Booking section — only for now showing films
            if (movie.isNowShowing) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Divider(color = DarkSurfaceVariant, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pilih Bioskop",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Location button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (uiState.locationGranted) SuccessGreen.copy(alpha = 0.15f)
                                        else CinemaRed.copy(alpha = 0.15f)
                                    )
                                    .clickable {
                                        if (!uiState.locationGranted) {
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (uiState.isLocationLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = CinemaRed,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = if (uiState.locationGranted) "✅" else "📍",
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (uiState.locationGranted) "Terdekat" else "Aktifkan Lokasi",
                                        color = if (uiState.locationGranted) SuccessGreen else CinemaRed,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        uiState.locationError?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = WarningAmber,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                items(uiState.cinemas) { cinema ->
                    CinemaCard(
                        cinema = cinema,
                        showDistance = uiState.locationGranted,
                        onShowtimeSelected = { c, time ->
                            selectedCinema = c
                            selectedShowtime = time
                        },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp)
                    )
                }
            } else {
                // "Tidak Tayang" notice
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkCard)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎭", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Film Ini Sudah Tidak Tayang",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Film ${movie.title} (${movie.year}) sudah tidak tersedia di bioskop LayarDigi. Nikmati film-film yang sedang tayang di halaman utama.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Booking Bar — only for now showing films with showtime selected
        if (movie.isNowShowing) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, DarkBackground)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                val canBook = selectedCinema != null && selectedShowtime.isNotEmpty()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (canBook)
                                Brush.linearGradient(listOf(CinemaRed, CinemaRedDark))
                            else
                                Brush.linearGradient(listOf(DarkCard, DarkCard))
                        )
                        .clickable(enabled = canBook) {
                            selectedCinema?.let { cinema ->
                                navController.navigate(
                                    Screen.Booking.createRoute(movie.id, cinema.id, selectedShowtime)
                                )
                            }
                        }
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (canBook)
                            "🎟  Pesan Tiket — ${selectedCinema?.name?.take(20)}... | $selectedShowtime"
                        else
                            "Pilih bioskop & jadwal tayang",
                        color = if (canBook) Color.White else TextDisabled,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun InfoStat(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}

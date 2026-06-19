package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.layardigi.app.data.model.SeatStatus
import com.layardigi.app.data.model.SeatType
import com.layardigi.app.navigation.Screen
import com.layardigi.app.presentation.ui.component.SeatItem
import com.layardigi.app.presentation.viewmodel.BookingViewModel
import com.layardigi.app.ui.theme.*

@Composable
fun BookingScreen(
    movieId: String,
    cinemaId: String,
    showtime: String,
    navController: NavController,
    viewModel: BookingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId, cinemaId, showtime) {
        viewModel.loadBookingData(movieId, cinemaId, showtime)
    }

    val movie = uiState.movie
    val cinema = uiState.cinema

    // Group seats by row
    val seatsByRow = uiState.seats.groupBy { it.row }.toSortedMap()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 52.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(DarkCard, androidx.compose.foundation.shape.CircleShape)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", color = TextPrimary, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pilih Kursi",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${movie?.title} • $showtime",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Cinema info
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏟", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = cinema?.name ?: "",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Studio 5 • ${uiState.selectedShowtime} • ${uiState.selectedDate}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Date Selector
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(
                        text = "Pilih Tanggal",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.availableDates) { date ->
                            val isSelected = uiState.selectedDate == date
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) CinemaRed else DarkCard,
                                animationSpec = tween(200),
                                label = "date_color"
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .clickable { viewModel.selectDate(date) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Screen indicator
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, CinemaRed.copy(0.6f), Color.Transparent)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "LAYAR",
                        color = TextDisabled,
                        fontSize = 10.sp,
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Seat Grid
            seatsByRow.forEach { (row, rowSeats) ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Row label
                        Text(
                            text = row.toString(),
                            color = TextDisabled,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(20.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Left seats (1-4)
                        rowSeats.filter { it.column <= 4 }.forEach { seat ->
                            SeatItem(
                                seat = seat,
                                onSeatClick = { viewModel.toggleSeat(it) }
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }

                        // Aisle
                        Spacer(modifier = Modifier.width(20.dp))

                        // Right seats (5-8)
                        rowSeats.filter { it.column > 4 }.forEach { seat ->
                            SeatItem(
                                seat = seat,
                                onSeatClick = { viewModel.toggleSeat(it) }
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    }
                }
            }

            // Legend
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = SeatAvailableColor, label = "Tersedia")
                    Spacer(modifier = Modifier.width(20.dp))
                    LegendItem(color = SeatSelectedColor, label = "Dipilih")
                    Spacer(modifier = Modifier.width(20.dp))
                    LegendItem(color = SeatBookedColor, label = "Terisi")
                    Spacer(modifier = Modifier.width(20.dp))
                    LegendItem(color = SeatVIPColor.copy(alpha = 0.4f), label = "VIP")
                }
            }
        }

        // Bottom Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(colors = listOf(Color.Transparent, DarkBackground))
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (uiState.selectedSeats.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Kursi: ${uiState.selectedSeats.joinToString(", ") { it.id }}",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${uiState.selectedSeats.size} tiket",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = formatPrice(uiState.totalPrice),
                            color = CinemaGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            val canProceed = uiState.selectedSeats.isNotEmpty()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (canProceed)
                            Brush.linearGradient(listOf(CinemaRed, CinemaRedDark))
                        else
                            Brush.linearGradient(listOf(DarkCard, DarkCard))
                    )
                    .clickable(enabled = canProceed) {
                        navController.navigate(
                            Screen.Checkout.createRoute(
                                movieId = movieId,
                                cinemaId = cinemaId,
                                showtime = showtime,
                                seats = viewModel.getSelectedSeatIds(),
                                total = uiState.totalPrice
                            )
                        )
                    }
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (canProceed) "Lanjut ke Pembayaran →" else "Pilih minimal 1 kursi",
                    color = if (canProceed) Color.White else TextDisabled,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}

fun formatPrice(price: Int): String {
    return "Rp ${String.format("%,d", price).replace(",", ".")}"
}

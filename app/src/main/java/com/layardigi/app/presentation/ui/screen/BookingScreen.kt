package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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

    val seatsByRow = uiState.seats.groupBy { it.row }.toSortedMap()

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {

            // Back button (floating)
            item {
                Spacer(modifier = Modifier.height(52.dp))
            }

            // Cinema info bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp)).background(DarkCard).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Theaters, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(uiState.cinema?.name ?: "", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.EventSeat, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Studio 5  •  ${uiState.selectedShowtime}  •  ${uiState.selectedDate}",
                                color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Date Selector
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih Tanggal", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.availableDates) { date ->
                            val isSelected = uiState.selectedDate == date
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) CinemaRed else DarkCard,
                                animationSpec = tween(200), label = "date_color"
                            )
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(bgColor)
                                    .clickable { viewModel.selectDate(date) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(date, color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            // Screen indicator
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.fillMaxWidth(0.8f).height(4.dp).clip(RoundedCornerShape(2.dp))
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, CinemaRed.copy(0.5f), Color.Transparent))))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("LAYAR", color = TextDisabled, fontSize = 10.sp, letterSpacing = 3.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Seat Grid
            seatsByRow.forEach { (row, rowSeats) ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(row.toString(), color = TextDisabled, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.width(8.dp))
                        rowSeats.filter { it.column <= 4 }.forEach { seat ->
                            SeatItem(seat = seat, onSeatClick = { viewModel.toggleSeat(it) })
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        rowSeats.filter { it.column > 4 }.forEach { seat ->
                            SeatItem(seat = seat, onSeatClick = { viewModel.toggleSeat(it) })
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    }
                }
            }

            // Legend
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
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

        // Bottom bar
        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, DarkBackground)))
            .padding(horizontal = 20.dp, vertical = 16.dp)) {

            if (uiState.selectedSeats.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(DarkCard).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.EventSeat, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(uiState.selectedSeats.joinToString(", ") { it.id },
                                color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("${uiState.selectedSeats.size} tiket dipilih", color = TextSecondary, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total", color = TextSecondary, fontSize = 11.sp)
                        Text(formatPrice(uiState.totalPrice), color = CinemaGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            val canProceed = uiState.selectedSeats.isNotEmpty()
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (canProceed) Brush.linearGradient(listOf(CinemaRed, CinemaRedDark))
                        else Brush.linearGradient(listOf(DarkCard, DarkCard)))
                    .clickable(enabled = canProceed) {
                        navController.navigate(Screen.Checkout.createRoute(
                            movieId = movieId, cinemaId = cinemaId, date = uiState.selectedDate,
                            showtime = showtime, seats = viewModel.getSelectedSeatIds(), total = uiState.totalPrice
                        ))
                    }
                    .padding(18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canProceed) {
                    Icon(Icons.Rounded.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lanjut ke Pembayaran", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(Icons.Rounded.Chair, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pilih minimal 1 kursi", color = TextDisabled, fontSize = 15.sp)
                }
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

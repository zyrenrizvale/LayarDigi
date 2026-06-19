package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.layardigi.app.navigation.Screen
import com.layardigi.app.presentation.ui.component.PaymentMethodCard
import com.layardigi.app.presentation.viewmodel.CheckoutViewModel
import com.layardigi.app.presentation.viewmodel.PaymentStatus
import com.layardigi.app.ui.theme.*

@Composable
fun CheckoutScreen(
    movieId: String,
    cinemaId: String,
    showtime: String,
    seatIds: String,
    total: Int,
    navController: NavController,
    viewModel: CheckoutViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId, cinemaId, showtime, seatIds, total) {
        viewModel.loadCheckoutData(movieId, cinemaId, showtime, seatIds, total)
    }

    // Navigate to success when payment completes
    LaunchedEffect(uiState.paymentStatus) {
        if (uiState.paymentStatus == PaymentStatus.SUCCESS) {
            navController.navigate(Screen.TicketSuccess.createRoute(uiState.bookingCode)) {
                popUpTo(Screen.Home.route)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
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
                    Text(
                        text = "Konfirmasi Pembayaran",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Order Summary Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkCard)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Ringkasan Pesanan",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OrderRow(label = "🎬 Film", value = uiState.movie?.title ?: "")
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(label = "🏟 Bioskop", value = uiState.cinema?.name ?: "")
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(label = "⏰ Jadwal", value = showtime)
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(
                        label = "💺 Kursi",
                        value = seatIds.split(",").filter { it.isNotEmpty() }.joinToString(", ")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(
                        label = "🎟 Tiket",
                        value = "${seatIds.split(",").filter { it.isNotEmpty() }.size} tiket"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = DarkSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Pembayaran",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formatPrice(total),
                            color = CinemaGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // Payment Methods
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = "Metode Pembayaran",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pilih cara pembayaran yang diinginkan",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            items(viewModel.paymentMethods) { method ->
                PaymentMethodCard(
                    method = method,
                    isSelected = uiState.selectedPaymentMethod?.id == method.id,
                    onSelect = { viewModel.selectPaymentMethod(method) },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 10.dp)
                )
            }

            // Security note
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🔒", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Transaksi aman & dienkripsi",
                        color = TextDisabled,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Bottom Pay Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(colors = listOf(Color.Transparent, DarkBackground))
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            val isProcessing = uiState.paymentStatus == PaymentStatus.PROCESSING

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                if (isProcessing) DarkCard else CinemaRed,
                                if (isProcessing) DarkCard else CinemaRedDark
                            )
                        )
                    )
                    .clickable(enabled = !isProcessing) { viewModel.processPayment() }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = CinemaRed,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Memproses Pembayaran...",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Text(
                        text = "💳  Bayar Sekarang — ${formatPrice(total)}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OrderRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End
        )
    }
}

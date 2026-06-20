package com.layardigi.app.presentation.ui.screen

import android.app.NotificationManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.layardigi.app.LayarDigiApp
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.navigation.Screen
import com.layardigi.app.presentation.ui.component.PaymentMethodCard
import com.layardigi.app.presentation.viewmodel.CheckoutViewModel
import com.layardigi.app.presentation.viewmodel.PaymentStatus
import com.layardigi.app.ui.theme.*

@Composable
fun CheckoutScreen(
    movieId: String,
    cinemaId: String,
    date: String,
    showtime: String,
    seatIds: String,
    total: Int,
    navController: NavController,
    viewModel: CheckoutViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val username by AuthRepository.currentUsername.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(movieId, cinemaId, date, showtime, seatIds, total) {
        viewModel.loadCheckoutData(movieId, cinemaId, date, showtime, seatIds, total)
    }
    LaunchedEffect(uiState.paymentStatus) {
        if (uiState.paymentStatus == PaymentStatus.SUCCESS) {
            // Save ticket to Firebase
            val user = username ?: "guest"
            val ticketRef = FirebaseDatabase.getInstance().getReference("tickets").child(user).push()
            ticketRef.setValue(mapOf(
                "bookingCode" to uiState.bookingCode,
                "movieTitle" to (uiState.movie?.title ?: ""),
                "cinemaName" to (uiState.cinema?.name ?: ""),
                "date" to date,
                "showtime" to showtime,
                "seats" to seatIds,
                "total" to total,
                "posterUrl" to (uiState.movie?.posterUrl ?: ""),
                "timestamp" to System.currentTimeMillis()
            ))

            // Local notification
            val notif = NotificationCompat.Builder(context, LayarDigiApp.NOTIF_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Pemesanan Berhasil! 🎬")
                .setContentText("${uiState.movie?.title} • ${uiState.bookingCode}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(System.currentTimeMillis().toInt(), notif)

            navController.navigate(Screen.TicketSuccess.createRoute(uiState.bookingCode)) {
                popUpTo(Screen.Home.route)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {

            // Top spacing
            item { Spacer(modifier = Modifier.height(52.dp)) }

            // Order Summary
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp)).background(DarkCard).padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Receipt, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ringkasan Pesanan", color = TextSecondary, fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OrderRow(Icons.Rounded.Movie, "Film", uiState.movie?.title ?: "")
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(Icons.Rounded.Theaters, "Bioskop", uiState.cinema?.name ?: "")
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(Icons.Rounded.Schedule, "Jadwal", showtime)
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(Icons.Rounded.EventSeat, "Kursi",
                        seatIds.split(",").filter { it.isNotEmpty() }.joinToString(", "))
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderRow(Icons.Rounded.ConfirmationNumber, "Tiket",
                        "${seatIds.split(",").filter { it.isNotEmpty() }.size} tiket")

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total Pembayaran", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(formatPrice(total), color = CinemaGold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // Payment Methods header
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text("Metode Pembayaran", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Pilih cara pembayaran yang diinginkan", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            items(viewModel.paymentMethods) { method ->
                PaymentMethodCard(
                    method = method,
                    isSelected = uiState.selectedPaymentMethod?.id == method.id,
                    onSelect = { viewModel.selectPaymentMethod(method) },
                    modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp)
                )
            }

            // Security note
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Transaksi aman & dienkripsi", color = TextDisabled, fontSize = 12.sp)
                }
            }
        }

        // Pay Button
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, DarkBackground)))
            .padding(horizontal = 20.dp, vertical = 20.dp)) {

            val isProcessing = uiState.paymentStatus == PaymentStatus.PROCESSING

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isProcessing) Brush.linearGradient(listOf(DarkCard, DarkCard))
                        else Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)))
                    .clickable(enabled = !isProcessing) { viewModel.processPayment() }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CinemaRed, strokeWidth = 2.5.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Memproses Pembayaran...", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(Icons.Rounded.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Bayar Sekarang  —  ${formatPrice(total)}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OrderRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(modifier = Modifier.weight(0.4f), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = TextSecondary, fontSize = 13.sp)
        }
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
    }
}

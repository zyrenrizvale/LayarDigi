package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import androidx.navigation.NavController
import com.layardigi.app.navigation.Screen
import com.layardigi.app.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TicketSuccessScreen(bookingCode: String, navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }

    val checkmarkScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "checkmark"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, delayMillis = 400), label = "content_alpha"
    )
    val contentOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 40f,
        animationSpec = tween(600, delayMillis = 400, easing = EaseOut), label = "content_offset"
    )

    val currentTime = remember {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale("id", "ID")))
    }

    LaunchedEffect(true) { startAnimation = true }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.size(500.dp).offset(y = (-100).dp)
            .background(Brush.radialGradient(listOf(SuccessGreen.copy(0.12f), Color.Transparent))))

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated check icon
            Box(
                modifier = Modifier.size(110.dp).scale(checkmarkScale)
                    .background(SuccessGreen.copy(0.15f), CircleShape)
                    .border(2.dp, SuccessGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = "Sukses", tint = SuccessGreen, modifier = Modifier.size(60.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Pembayaran Berhasil!", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.alpha(contentAlpha), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Tiketmu sudah siap! Tunjukkan kode di bawah ke petugas.",
                color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 20.sp,
                modifier = Modifier.alpha(contentAlpha))

            Spacer(modifier = Modifier.height(28.dp))

            // E-Ticket Card
            Column(
                modifier = Modifier.fillMaxWidth()
                    .offset(y = contentOffset.dp)
                    .alpha(contentAlpha)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkCard)
            ) {
                // Header
                Box(modifier = Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)))
                    .padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Theaters, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("LayarDigi", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                            Text("E-TIKET DIGITAL", color = Color.White.copy(0.7f), fontSize = 10.sp, letterSpacing = 3.sp)
                        }
                    }
                }

                // Perforated edge
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).offset(x = (-12).dp).background(DarkBackground, CircleShape))
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(
                        Brush.horizontalGradient(List(12) { if (it % 2 == 0) DarkSurfaceVariant else Color.Transparent })))
                    Box(modifier = Modifier.size(24.dp).offset(x = 12.dp).background(DarkBackground, CircleShape))
                }

                // Booking code
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant).padding(16.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.QrCode, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("KODE BOOKING", color = TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(bookingCode, color = CinemaGold, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    TicketInfoRow(Icons.Rounded.Schedule, "Waktu Pembelian", currentTime)
                    Spacer(modifier = Modifier.height(8.dp))
                    TicketInfoRow(Icons.Rounded.CheckCircle, "Status", "LUNAS")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Back to Home
            Row(
                modifier = Modifier.fillMaxWidth().alpha(contentAlpha).clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)))
                    .clickable { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } }
                    .padding(18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kembali ke Beranda", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.alpha(contentAlpha), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocalMovies, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Selamat menikmati filmnya!", color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun TicketInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = TextSecondary, fontSize = 13.sp)
        }
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

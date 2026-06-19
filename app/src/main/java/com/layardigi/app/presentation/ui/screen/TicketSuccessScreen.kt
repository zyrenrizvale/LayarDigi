package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
fun TicketSuccessScreen(
    bookingCode: String,
    navController: NavController
) {
    var startAnimation by remember { mutableStateOf(false) }

    val checkmarkScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkmark_scale"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, delayMillis = 400),
        label = "card_alpha"
    )

    val cardTranslation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 50f,
        animationSpec = tween(600, delayMillis = 400, easing = EaseOut),
        label = "card_translation"
    )

    val currentTime = remember {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale("id", "ID")))
    }

    LaunchedEffect(true) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(500.dp)
                .offset(y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SuccessGreen.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success checkmark animation
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(checkmarkScale)
                    .background(
                        Brush.radialGradient(listOf(SuccessGreen.copy(0.2f), SuccessGreen.copy(0.05f))),
                        androidx.compose.foundation.shape.CircleShape
                    )
                    .border(3.dp, SuccessGreen, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✓", color = SuccessGreen, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pembayaran Berhasil!",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.alpha(cardAlpha),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tiketmu sudah siap! Tunjukkan kode di bawah ke petugas bioskop.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.alpha(cardAlpha)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // E-Ticket Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = cardTranslation.dp)
                    .alpha(cardAlpha)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkCard)
            ) {
                // Ticket header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(CinemaRed, CinemaRedDark))
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎬", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LayarDigi",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "E-TIKET DIGITAL",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            letterSpacing = 3.sp
                        )
                    }
                }

                // Dotted separator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = (-12).dp)
                            .background(DarkBackground, androidx.compose.foundation.shape.CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = List(10) { if (it % 2 == 0) DarkSurfaceVariant else Color.Transparent }
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = 12.dp)
                            .background(DarkBackground, androidx.compose.foundation.shape.CircleShape)
                    )
                }

                // Ticket details
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Booking Code
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "KODE BOOKING",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = bookingCode,
                                color = CinemaGold,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 3.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    TicketRow(label = "Waktu Pembelian", value = currentTime)
                    Spacer(modifier = Modifier.height(8.dp))
                    TicketRow(label = "Status", value = "✅ LUNAS")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Back to Home button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardAlpha)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)))
                    .clickable {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏠  Kembali ke Beranda",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Selamat menikmati filmnya! 🍿",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.alpha(cardAlpha),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TicketRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

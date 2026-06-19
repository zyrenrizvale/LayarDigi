package com.layardigi.app.presentation.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.layardigi.app.data.model.Cinema
import com.layardigi.app.ui.theme.*
import java.util.Locale

@Composable
fun CinemaCard(
    cinema: Cinema,
    onShowtimeSelected: (Cinema, String) -> Unit,
    showDistance: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedShowtime by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cinema.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cinema.address,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            if (showDistance && cinema.distanceKm > 0) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            Brush.linearGradient(listOf(CinemaRed.copy(alpha = 0.2f), CinemaRedDark.copy(alpha = 0.1f))),
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, CinemaRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📍",
                            fontSize = 14.sp
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f km", cinema.distanceKm),
                            color = CinemaRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Showtimes
        Text(
            text = "Jadwal Tayang",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cinema.showtimes) { time ->
                val isSelected = selectedShowtime == time
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) CinemaRed else DarkSurfaceVariant,
                    animationSpec = tween(200),
                    label = "showtime_color"
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .border(
                            1.dp,
                            if (isSelected) CinemaRed else DarkCardElevated,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedShowtime = time
                            onShowtimeSelected(cinema, time)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = time,
                        color = if (isSelected) Color.White else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

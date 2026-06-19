package com.layardigi.app.presentation.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.layardigi.app.data.model.Seat
import com.layardigi.app.data.model.SeatStatus
import com.layardigi.app.data.model.SeatType
import com.layardigi.app.ui.theme.*

@Composable
fun SeatItem(
    seat: Seat,
    onSeatClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            seat.status == SeatStatus.BOOKED -> SeatBookedColor
            seat.status == SeatStatus.SELECTED && seat.type == SeatType.VIP -> SeatVIPSelectedColor
            seat.status == SeatStatus.SELECTED -> SeatSelectedColor
            seat.type == SeatType.VIP -> SeatVIPColor.copy(alpha = 0.25f)
            else -> SeatAvailableColor
        },
        animationSpec = tween(200),
        label = "seat_color"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            seat.status == SeatStatus.BOOKED -> SeatBookedColor
            seat.status == SeatStatus.SELECTED && seat.type == SeatType.VIP -> CinemaGoldDark
            seat.status == SeatStatus.SELECTED -> CinemaRedDark
            seat.type == SeatType.VIP -> CinemaGold.copy(alpha = 0.6f)
            else -> DarkSurfaceVariant
        },
        animationSpec = tween(200),
        label = "seat_border"
    )

    val scale by animateFloatAsState(
        targetValue = if (seat.status == SeatStatus.SELECTED) 1.05f else 1f,
        animationSpec = tween(150),
        label = "seat_scale"
    )

    Box(
        modifier = modifier
            .size(36.dp)
            .scale(scale)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(enabled = seat.status != SeatStatus.BOOKED) {
                onSeatClick(seat.id)
            },
        contentAlignment = Alignment.Center
    ) {
        if (seat.status != SeatStatus.BOOKED) {
            Text(
                text = "${seat.row}${seat.column}",
                color = when {
                    seat.status == SeatStatus.SELECTED -> Color.White
                    seat.type == SeatType.VIP -> CinemaGold
                    else -> TextSecondary
                },
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        } else {
            Text(text = "✕", color = TextDisabled, fontSize = 10.sp)
        }
    }
}

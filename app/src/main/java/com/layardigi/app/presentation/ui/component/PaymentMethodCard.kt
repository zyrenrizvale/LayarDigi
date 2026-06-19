package com.layardigi.app.presentation.ui.component

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.layardigi.app.presentation.viewmodel.PaymentMethod
import com.layardigi.app.ui.theme.*

@Composable
fun PaymentMethodCard(
    method: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) CinemaRed.copy(alpha = 0.12f) else DarkCard,
        animationSpec = tween(200),
        label = "payment_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) CinemaRed else DarkCardElevated,
        animationSpec = tween(200),
        label = "payment_border"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (isSelected) CinemaRed.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = method.icon, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = method.name,
                color = if (isSelected) TextPrimary else TextPrimary,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
            Text(
                text = method.description,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Radio indicator
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(
                    2.dp,
                    if (isSelected) CinemaRed else TextDisabled,
                    androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(CinemaRed, androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
    }
}

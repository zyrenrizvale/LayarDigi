package com.layardigi.app.presentation.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RatingBar(
    rating: Float,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    val normalizedRating = rating / 2f // Convert 10-scale to 5-star
    Row(modifier = modifier) {
        for (i in 1..maxStars) {
            val starChar = when {
                i <= normalizedRating -> "★"
                i - 0.5f <= normalizedRating -> "⭐"
                else -> "☆"
            }
            Text(
                text = starChar,
                fontSize = (starSize.value).sp,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

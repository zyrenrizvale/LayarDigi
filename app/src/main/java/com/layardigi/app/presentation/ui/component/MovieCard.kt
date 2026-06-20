package com.layardigi.app.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.layardigi.app.data.model.Movie
import com.layardigi.app.ui.theme.*

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.width(150.dp).height(240.dp)
            .clip(RoundedCornerShape(16.dp)).clickable { onClick() }
    ) {
        AsyncImage(model = movie.posterUrl, contentDescription = movie.title,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent,
                DarkBackground.copy(0.7f), DarkBackground.copy(0.96f)))
        ))

        // Badge
        val isNowShowing = movie.status == "NOW_SHOWING"
        val isComingSoon = movie.status == "COMING_SOON"
        Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            .background(if (isNowShowing) CinemaRed else if (isComingSoon) CinemaRed.copy(0.2f) else DarkSurfaceVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)) {
            Text(if (isNowShowing) "TAYANG" else if (isComingSoon) "SEGERA" else "SELESAI",
                color = if (isComingSoon) CinemaRed else TextPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }

        // Title + rating
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
            Text(movie.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = CinemaGold, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(movie.rating.toString(), color = CinemaGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun MovieCardWide(movie: Movie, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(DarkCard).clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(70.dp).height(100.dp).clip(RoundedCornerShape(10.dp))) {
            AsyncImage(model = movie.posterUrl, contentDescription = movie.title,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            if (movie.status != "NOW_SHOWING") {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (movie.status != "NOW_SHOWING") {
                val isComing = movie.status == "COMING_SOON"
                Box(modifier = Modifier.background(if (isComing) CinemaRed.copy(0.15f) else DarkSurfaceVariant, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(if (isComing) "AKAN TAYANG" else "TIDAK TAYANG", color = if (isComing) CinemaRed else TextSecondary, fontSize = 9.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(movie.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(movie.genre.take(2).joinToString(" • "), color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = CinemaGold, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("${movie.rating}", color = CinemaGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(10.dp))
                Text("${movie.duration} min", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(movie.ageRating, color = TextSecondary, fontSize = 12.sp)
            }
        }

        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
    }
}

package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.layardigi.app.data.model.Movie
import com.layardigi.app.data.model.Cinema
import com.layardigi.app.data.repository.MovieRepository
import com.layardigi.app.data.repository.CinemaRepository
import com.layardigi.app.ui.theme.*

@Composable
fun AdminDashboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val movies by MovieRepository.moviesFlow.collectAsState()
    val cinemas by CinemaRepository.cinemasFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(42.dp)
                    .background(DarkCard, CircleShape)
            ) {
                Icon(Icons.Rounded.ArrowBackIos, "Kembali", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Dashboard Admin", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("Kelola daftar film", color = TextSecondary, fontSize = 13.sp)
            }
            IconButton(
                onClick = { navController.navigate("movie_form") },
                modifier = Modifier
                    .size(42.dp)
                    .background(CinemaRed, CircleShape)
            ) {
                Icon(Icons.Rounded.Add, "Tambah Film", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkBackground,
            contentColor = CinemaRed,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CinemaRed
                )
            }
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                text = { Text("Film", color = if (selectedTab == 0) CinemaRed else TextSecondary) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                text = { Text("Cabang", color = if (selectedTab == 1) CinemaRed else TextSecondary) })
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedTab == 0) {
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                items(movies) { movie ->
                    AdminMovieItem(
                        movie = movie,
                        onToggleStatus = { MovieRepository.toggleNowShowing(movie.id) },
                        onEdit = { navController.navigate("movie_form?movieId=${movie.id}") }
                    )
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                item {
                    Button(
                        onClick = { navController.navigate("cinema_form") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Cabang", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                items(cinemas) { cinema ->
                    AdminCinemaItem(
                        cinema = cinema,
                        onEdit = { navController.navigate("cinema_form?cinemaId=${cinema.id}") },
                        onDelete = { CinemaRepository.deleteCinema(cinema.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMovieItem(
    movie: Movie,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(85.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = movie.isNowShowing,
                    onCheckedChange = { onToggleStatus() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CinemaRed,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.scale(0.8f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (movie.isNowShowing) "Tayang" else "Selesai",
                    color = if (movie.isNowShowing) CinemaRed else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        IconButton(
            onClick = onEdit,
            modifier = Modifier
                .size(36.dp)
                .background(DarkSurfaceVariant, CircleShape)
        ) {
            Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = TextPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun AdminCinemaItem(
    cinema: Cinema,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cinema.name,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${cinema.city} • Rp ${cinema.basePrice}",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
        Row {
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .size(36.dp)
                    .background(DarkSurfaceVariant, CircleShape)
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .background(DarkSurfaceVariant, CircleShape)
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = CinemaRed, modifier = Modifier.size(16.dp))
            }
        }
    }
}

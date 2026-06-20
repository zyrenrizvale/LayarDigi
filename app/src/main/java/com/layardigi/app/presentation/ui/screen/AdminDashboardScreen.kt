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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(40.dp)
                    .background(DarkSurfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIos,
                    contentDescription = "Kembali",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp).padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Dashboard Admin",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tab for Films/Cinemas
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkBackground,
            contentColor = CinemaRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CinemaRed
                )
            }
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                text = { Text("Film", color = if (selectedTab == 0) CinemaRed else TextSecondary) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                text = { Text("Cabang", color = if (selectedTab == 1) CinemaRed else TextSecondary) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                text = { Text("Pengumuman", color = if (selectedTab == 2) CinemaRed else TextSecondary) })
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedTab == 0) {
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                item {
                    Button(
                        onClick = { navController.navigate("movie_form") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Film", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                items(movies) { movie ->
                    AdminMovieItem(
                        movie = movie,
                        onToggleStatus = { MovieRepository.toggleNowShowing(movie.id) },
                        onEdit = { navController.navigate("movie_form?movieId=${movie.id}") },
                        onDelete = { MovieRepository.deleteMovie(movie.id) }
                    )
                }
            }
        } else if (selectedTab == 1) {
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
        } else {
            AdminAnnouncementTab()
        }
    }
}

@Composable
fun AdminMovieItem(
    movie: Movie,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                val isNow = movie.status == "NOW_SHOWING"
                val isComing = movie.status == "COMING_SOON"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isNow) CinemaRed.copy(0.15f) else if (isComing) CinemaRed.copy(0.2f) else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isNow) CinemaRed else if (isComing) CinemaRed else TextDisabled
                    ),
                    modifier = Modifier.clickable { onToggleStatus() }
                ) {
                    Text(
                        text = if (isNow) "Sedang Tayang" else if (isComing) "Akan Tayang" else "Telah Tayang",
                        color = if (isNow) CinemaRed else if (isComing) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• Klik untuk ubah",
                    color = TextDisabled,
                    fontSize = 11.sp
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnnouncementTab() {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp).imePadding()
    ) {
        Text("Buat Pengumuman Baru", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Pengumuman akan terlihat oleh semua pengguna", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Judul Pengumuman", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = CinemaRed, unfocusedBorderColor = DarkSurfaceVariant, containerColor = DarkCard
            ),
            shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = body, onValueChange = { body = it },
            label = { Text("Isi Pengumuman", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = CinemaRed, unfocusedBorderColor = DarkSurfaceVariant, containerColor = DarkCard
            ),
            shape = RoundedCornerShape(12.dp), singleLine = false
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && body.isNotBlank()) {
                    isPosting = true
                    val ref = com.google.firebase.database.FirebaseDatabase.getInstance()
                        .getReference("announcements").push()
                    ref.setValue(mapOf(
                        "title" to title, "body" to body,
                        "author" to "Admin LayarDigi",
                        "timestamp" to System.currentTimeMillis()
                    )).addOnCompleteListener {
                        isPosting = false; title = ""; body = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
            shape = RoundedCornerShape(12.dp),
            enabled = !isPosting && title.isNotBlank() && body.isNotBlank()
        ) {
            if (isPosting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            else Text("Kirim Pengumuman", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

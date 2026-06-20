package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.database.*
import com.layardigi.app.data.model.Movie
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.data.repository.MovieRepository
import com.layardigi.app.data.repository.Role
import com.layardigi.app.navigation.Screen
import com.layardigi.app.ui.theme.*

@Composable
fun FavoritesScreen(navController: NavController) {
    val username by AuthRepository.currentUsername.collectAsState()
    val role by AuthRepository.currentUserRole.collectAsState()
    val allMovies by MovieRepository.moviesFlow.collectAsState()

    var favoriteIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(username) {
        if (username == null || role == Role.GUEST) return@LaunchedEffect
        val ref = FirebaseDatabase.getInstance().getReference("favorites").child(username!!)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = mutableSetOf<String>()
                for (child in snapshot.children) {
                    ids.add(child.key ?: "")
                }
                favoriteIds = ids
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    val favoriteMovies = allMovies.filter { it.id in favoriteIds }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBackground).statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Film Favorit", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 20.dp))
        Text("Film yang kamu simpan", color = TextSecondary, fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(16.dp))

        if (role == Role.GUEST) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Silakan login untuk melihat favorit", color = TextSecondary, fontSize = 15.sp)
                }
            }
        } else if (favoriteMovies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Belum ada film favorit", color = TextSecondary, fontSize = 15.sp)
                    Text("Tekan ikon hati di detail film untuk menyimpan", color = TextDisabled, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                items(favoriteMovies) { movie ->
                    FavoriteMovieCard(
                        movie = movie,
                        onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) },
                        onRemove = {
                            username?.let { user ->
                                FirebaseDatabase.getInstance().getReference("favorites")
                                    .child(user).child(movie.id).removeValue()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteMovieCard(movie: Movie, onClick: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp)).background(DarkCard)
            .clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = movie.posterUrl, contentDescription = movie.title,
            modifier = Modifier.size(60.dp, 85.dp).clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(movie.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(movie.genre.take(2).joinToString(" • "), color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = CinemaGold, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("${movie.rating}", color = CinemaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Rounded.Favorite, contentDescription = "Hapus dari favorit", tint = CinemaRed)
        }
    }
}

package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.layardigi.app.data.repository.MovieRepository
import com.layardigi.app.navigation.Screen
import com.layardigi.app.presentation.ui.component.MovieCardWide
import com.layardigi.app.ui.theme.*

@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.length >= 2)
            MovieRepository.allMovies.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.genre.any { g -> g.contains(query, ignoreCase = true) } ||
                it.director.contains(query, ignoreCase = true)
            }
        else emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = "Cari Film",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Temukan film favoritmu",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = TextPrimary, fontSize = 15.sp),
                    cursorBrush = SolidColor(CinemaRed),
                    singleLine = true,
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text("Judul, genre, sutradara...", color = TextDisabled, fontSize = 15.sp)
                        inner()
                    }
                )
                if (query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Hapus",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp).clickable { query = "" }
                    )
                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
            if (query.length >= 2 && results.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Rounded.SearchOff,
                                contentDescription = null,
                                tint = TextDisabled,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Tidak ditemukan", color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("Coba kata kunci lain", color = TextDisabled, fontSize = 13.sp)
                        }
                    }
                }
            } else if (query.length < 2) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.MovieFilter, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Ketik minimal 2 huruf", color = TextSecondary, fontSize = 15.sp, textAlign = TextAlign.Center)
                    }
                }
            }
            items(results) { movie ->
                MovieCardWide(
                    movie = movie,
                    onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}

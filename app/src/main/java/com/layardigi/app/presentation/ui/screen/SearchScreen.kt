package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    val allMovies by MovieRepository.moviesFlow.collectAsState()
    
    val filters = listOf("Semua", "Sedang Tayang", "Akan Tayang", "Telah Tayang")
    
    val results = remember(query, allMovies, selectedFilter) {
        var filtered = allMovies
        
        // Apply Category Filter
        filtered = when (selectedFilter) {
            "Sedang Tayang" -> filtered.filter { it.status == "NOW_SHOWING" }
            "Akan Tayang" -> filtered.filter { it.status == "COMING_SOON" }
            "Telah Tayang" -> filtered.filter { it.status == "ARCHIVED" } // Placeholder status for older movies
            else -> filtered
        }
        
        // Apply Text Search Filter
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.genre.any { g -> g.contains(query, ignoreCase = true) } ||
                it.director.contains(query, ignoreCase = true)
            }
        }
        
        filtered
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DarkCard)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null,
                tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(color = TextPrimary, fontSize = 15.sp),
                cursorBrush = SolidColor(CinemaRed),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) Text("Cari judul, genre, sutradara...", color = TextDisabled, fontSize = 15.sp)
                    inner()
                }
            )
            if (query.isNotEmpty()) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = "Hapus",
                    tint = TextSecondary, modifier = Modifier.size(20.dp).clickable { query = "" })
            }
        }
        
        // Filter Chips
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) CinemaRed else DarkSurfaceVariant)
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.SearchOff, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tidak ditemukan", color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("Coba kata kunci atau filter lain", color = TextDisabled, fontSize = 13.sp)
                }
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                Text("${results.size} film ditemukan", color = TextSecondary, fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp))
                results.forEach { movie ->
                    MovieCardWide(
                        movie = movie,
                        onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.layardigi.app.data.model.Movie
import com.layardigi.app.navigation.Screen
import com.layardigi.app.presentation.ui.component.MovieCard
import com.layardigi.app.presentation.ui.component.MovieCardWide
import com.layardigi.app.presentation.viewmodel.HomeViewModel
import com.layardigi.app.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Top Bar
            item {
                HomeTopBar(
                    searchQuery = uiState.searchQuery,
                    onSearchChange = viewModel::onSearchQueryChange,
                    onClearSearch = viewModel::clearSearch
                )
            }

            // Search Results
            if (uiState.isSearching) {
                item {
                    SearchResultsSection(
                        movies = uiState.filteredMovies,
                        onMovieClick = { movie ->
                            navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                        }
                    )
                }
            } else {
                // Hero Banner
                item {
                    HeroBanner(
                        movies = uiState.nowShowingMovies,
                        onMovieClick = { movie ->
                            navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                        }
                    )
                }

                // Now Showing Section
                item {
                    SectionHeader(
                        title = "Sedang Tayang",
                        subtitle = "${uiState.nowShowingMovies.size} Film"
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.nowShowingMovies) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                }

                // Film Lama Section
                item {
                    SectionHeader(
                        title = "Film Klasik",
                        subtitle = "Sudah tidak tayang"
                    )
                }
                items(uiState.classicMovies) { movie ->
                    MovieCardWide(
                        movie = movie,
                        onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkBackground.copy(alpha = 0f)),
                    endY = 200f
                )
            )
            .padding(top = 52.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LayarDigi",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Bioskop Digital Indonesia 🎬",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)),
                        androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCard)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🔍", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextPrimary,
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(CinemaRed),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Cari film, genre, sutradara...",
                            color = TextDisabled,
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            )
            AnimatedVisibility(visible = searchQuery.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = "✕",
                    color = TextSecondary,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onClearSearch() }
                )
            }
        }
    }
}

@Composable
fun HeroBanner(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    if (movies.isEmpty()) return

    val firstMovie = movies.first()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickable { onMovieClick(firstMovie) }
    ) {
        AsyncImage(
            model = firstMovie.posterUrl,
            contentDescription = firstMovie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.5f),
                            DarkBackground.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            // Featured badge
            Box(
                modifier = Modifier
                    .background(CinemaRed, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔥 UNGGULAN",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = firstMovie.title,
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${firstMovie.rating}",
                    color = CinemaGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("•", color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = firstMovie.genre.take(2).joinToString(" • "),
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("•", color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${firstMovie.duration} min",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Pesan Tiket Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)))
                        .clickable { onMovieClick(firstMovie) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "🎟 Pesan Tiket",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // Detail Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant.copy(alpha = 0.8f))
                        .clickable { onMovieClick(firstMovie) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "ℹ Detail",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Dot indicators (future carousel)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            movies.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == 0) 20.dp else 6.dp, 6.dp)
                        .background(
                            if (index == 0) CinemaRed else TextDisabled,
                            RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SearchResultsSection(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${movies.size} Film Ditemukan",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (movies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎭", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Film tidak ditemukan",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Coba kata kunci lain",
                        color = TextDisabled,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            movies.forEach { movie ->
                MovieCardWide(
                    movie = movie,
                    onClick = { onMovieClick(movie) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}

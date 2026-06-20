package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Slideshow Hero Banner
            item {
                HeroBanner(
                    movies = uiState.nowShowingMovies,
                    onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it.id)) }
                )
            }

            // Sedang Tayang
            item { SectionHeader(title = "Sedang Tayang", subtitle = "${uiState.nowShowingMovies.size} film") }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.nowShowingMovies) { movie ->
                        MovieCard(movie = movie,
                            onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Akan Tayang
            if (uiState.classicMovies.isNotEmpty()) {
                item { SectionHeader(title = "Akan Tayang", subtitle = "Segera hadir di bioskop") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.classicMovies.take(6)) { movie ->
                            UpcomingMovieCard(movie = movie,
                                onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) })
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Semua Film
            item { SectionHeader(title = "Semua Film", subtitle = "Koleksi lengkap LayarDigi") }
            items(uiState.classicMovies) { movie ->
                MovieCardWide(
                    movie = movie,
                    onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) },
                    modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroBanner(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {
    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { movies.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % movies.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val film = movies[page]
            Box(modifier = Modifier.fillMaxSize().clickable { onMovieClick(film) }) {
                AsyncImage(model = film.posterUrl, contentDescription = film.title,
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, DarkBackground.copy(0.5f), DarkBackground.copy(0.97f)))
                ))

                Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp).padding(bottom = 20.dp)) {
                    Surface(color = CinemaRed, shape = RoundedCornerShape(6.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("UNGGULAN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(film.title, color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp, maxLines = 2)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = CinemaGold, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${film.rating}", color = CinemaGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("•", color = TextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(film.genre.take(2).joinToString(" • "), color = TextSecondary, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.Transparent,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(
                                Brush.linearGradient(listOf(CinemaRed, CinemaRedDark))
                            ).clickable { onMovieClick(film) }) {
                            Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pesan Tiket", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Surface(shape = RoundedCornerShape(12.dp), color = DarkSurfaceVariant.copy(0.85f),
                            modifier = Modifier.clickable { onMovieClick(film) }) {
                            Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Detail", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // Dot indicators
        Row(modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            movies.forEachIndexed { i, _ ->
                val isSelected = pagerState.currentPage == i
                val width by androidx.compose.animation.core.animateDpAsState(if (isSelected) 20.dp else 6.dp, label = "dotWidth")
                val color by androidx.compose.animation.animateColorAsState(if (isSelected) CinemaRed else TextDisabled, label = "dotColor")
                Box(modifier = Modifier.size(width, 6.dp).background(color, RoundedCornerShape(3.dp)))
            }
        }
    }
}

@Composable
fun UpcomingMovieCard(movie: Movie, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(14.dp))
        ) {
            AsyncImage(model = movie.posterUrl, contentDescription = movie.title,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            // "Akan Tayang" ribbon
            Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                .background(Brush.linearGradient(listOf(CinemaRed.copy(0.9f), CinemaRedDark.copy(0.9f))), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)) {
                Text("SEGERA", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(movie.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp)
        Text(movie.genre.firstOrNull() ?: "", color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun SearchResultsSection(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("${movies.size} Film Ditemukan", color = TextSecondary, fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 12.dp))
        if (movies.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.SearchOff, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Film tidak ditemukan", color = TextSecondary, fontSize = 15.sp, textAlign = TextAlign.Center)
                    Text("Coba kata kunci lain", color = TextDisabled, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            movies.forEach { movie ->
                MovieCardWide(movie = movie, onClick = { onMovieClick(movie) },
                    modifier = Modifier.padding(bottom = 12.dp))
            }
        }
    }
}

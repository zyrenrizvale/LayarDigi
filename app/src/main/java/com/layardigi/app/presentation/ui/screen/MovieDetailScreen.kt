package com.layardigi.app.presentation.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.layardigi.app.data.model.Cinema
import com.google.firebase.database.FirebaseDatabase
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.navigation.Screen
import com.layardigi.app.presentation.ui.component.CinemaCard
import com.layardigi.app.presentation.viewmodel.MovieDetailViewModel
import com.layardigi.app.utils.extractYoutubeVideoId
import com.layardigi.app.utils.findActivity
import com.layardigi.app.ui.theme.*
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay

@Composable
fun MovieDetailScreen(
    movieId: String,
    navController: NavController,
    viewModel: MovieDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCinema by remember { mutableStateOf<Cinema?>(null) }
    var selectedShowtime by remember { mutableStateOf("") }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.onLocationPermissionGranted() else viewModel.onLocationPermissionDenied()
    }

    LaunchedEffect(movieId) { viewModel.loadMovie(movieId) }

    val movie = uiState.movie ?: return
    val videoId = remember(movie.trailerUrl) { extractYoutubeVideoId(movie.trailerUrl) }
    val username by AuthRepository.currentUsername.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(movieId, username) {
        val u = username ?: return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("favorites").child(u).child(movieId).get()
            .addOnSuccessListener { isFavorite = it.exists() }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 100.dp)) {

            // Hero
            item {
                Box(modifier = Modifier.fillMaxWidth().height(440.dp)) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, DarkBackground.copy(0.55f), DarkBackground))
                    ))

                    // Back + Favorite buttons
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 12.dp, end = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(42.dp).background(DarkBackground.copy(0.6f), CircleShape)
                        ) {
                            Icon(Icons.Rounded.ArrowBackIos, "Kembali", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                        if (username != null) {
                            IconButton(
                                onClick = {
                                    val u = username ?: return@IconButton
                                    val ref = FirebaseDatabase.getInstance().getReference("favorites").child(u).child(movieId)
                                    if (isFavorite) ref.removeValue() else ref.setValue(true)
                                    isFavorite = !isFavorite
                                },
                                modifier = Modifier.size(42.dp).background(DarkBackground.copy(0.6f), CircleShape)
                            ) {
                                Icon(
                                    if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    "Favorit", tint = if (isFavorite) CinemaRed else TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Status Badge
                    if (movie.status != "NOW_SHOWING") {
                        val isComing = movie.status == "COMING_SOON"
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isComing) CinemaRed.copy(0.15f) else DarkSurfaceVariant,
                            modifier = Modifier.align(Alignment.TopEnd).padding(top = 52.dp, end = 16.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isComing) Icons.Rounded.CalendarMonth else Icons.Rounded.EventBusy,
                                    contentDescription = null,
                                    tint = if (isComing) CinemaRed else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isComing) "AKAN TAYANG" else "TIDAK TERSEDIA",
                                    color = if (isComing) CinemaRed else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    // Title section at bottom
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            movie.genre.take(3).forEach { genre ->
                                Surface(shape = RoundedCornerShape(6.dp), color = CinemaRed.copy(0.2f)) {
                                    Text(genre, color = CinemaRed, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(movie.title, color = TextPrimary, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp)
                    }
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(14.dp)).background(DarkCard).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoStatIcon(Icons.Rounded.Star, "${movie.rating}", "Rating", CinemaGold)
                    VerticalDivider()
                    InfoStatIcon(Icons.Rounded.Schedule, "${movie.duration}m", "Durasi", TextSecondary)
                    VerticalDivider()
                    InfoStatIcon(Icons.Rounded.CalendarMonth, "${movie.year}", "Tahun", TextSecondary)
                    VerticalDivider()
                    InfoStatIcon(Icons.Rounded.Shield, movie.ageRating, "Usia", TextSecondary)
                }
            }

            // Trailer button if available
            if (videoId != null && videoId.isNotEmpty()) {
                item {
                    var showTrailerPlayer by remember { mutableStateOf(false) }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CinemaRed.copy(0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                            .clickable { showTrailerPlayer = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tonton Trailer", color = CinemaRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (showTrailerPlayer) {
                        FullscreenLandscapePlayer(videoId = videoId, onClose = { showTrailerPlayer = false })
                    }
                }
            }

            // Synopsis
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Sinopsis", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(movie.synopsis, color = TextSecondary, fontSize = 14.sp, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Director
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Movie, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Sutradara", color = TextSecondary, fontSize = 11.sp)
                            Text(movie.director, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // Cast
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Rounded.Group, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Pemain", color = TextSecondary, fontSize = 11.sp)
                            Text(movie.cast.joinToString(", "), color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // Language
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Language, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Bahasa", color = TextSecondary, fontSize = 11.sp)
                            Text(movie.language, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Cinema section
            if (movie.status == "NOW_SHOWING") {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                        HorizontalDivider(color = DarkSurfaceVariant)
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Pilih Bioskop", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (uiState.locationGranted) SuccessGreen.copy(0.15f) else CinemaRed.copy(0.12f),
                                modifier = Modifier.clickable {
                                    if (!uiState.locationGranted) {
                                        locationPermissionLauncher.launch(arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        ))
                                    }
                                }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (uiState.isLocationLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = CinemaRed, strokeWidth = 2.dp)
                                    } else {
                                        Icon(
                                            if (uiState.locationGranted) Icons.Rounded.MyLocation else Icons.Rounded.LocationOn,
                                            contentDescription = null,
                                            tint = if (uiState.locationGranted) SuccessGreen else CinemaRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (uiState.locationGranted) "Terdekat" else "Aktifkan",
                                        color = if (uiState.locationGranted) SuccessGreen else CinemaRed,
                                        fontSize = 13.sp, fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        uiState.locationError?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(it, color = WarningAmber, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                items(uiState.cinemas) { cinema ->
                    CinemaCard(
                        cinema = cinema,
                        showDistance = uiState.locationGranted,
                        onShowtimeSelected = { c, time -> selectedCinema = c; selectedShowtime = time },
                        modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)
                    )
                }
            } else {
                val isComing = movie.status == "COMING_SOON"
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(16.dp)).background(DarkCard).padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isComing) Icons.Rounded.Schedule else Icons.Rounded.EventBusy,
                                contentDescription = null,
                                tint = if (isComing) CinemaRed else TextDisabled,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isComing) "Film Ini Segera Tayang" else "Film Ini Sudah Tidak Tayang",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isComing) {
                                    "Film ${movie.title} (${movie.year}) akan segera hadir di bioskop LayarDigi. Nantikan jadwal tayangnya!"
                                } else {
                                    "Film ${movie.title} (${movie.year}) sudah tidak tersedia di bioskop LayarDigi."
                                },
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom booking bar
        if (movie.status == "NOW_SHOWING") {
            val canBook = selectedCinema != null && selectedShowtime.isNotEmpty()
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, DarkBackground)))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (canBook) Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)) else Brush.linearGradient(listOf(DarkCard, DarkCard)))
                        .clickable(enabled = canBook) {
                            selectedCinema?.let { cinema ->
                                navController.navigate(Screen.Booking.createRoute(movie.id, cinema.id, selectedShowtime))
                            }
                        }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canBook) {
                        Icon(Icons.Rounded.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pesan Tiket  •  ${selectedCinema?.city}  |  $selectedShowtime",
                            color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Rounded.Theaters, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih bioskop & jadwal tayang", color = TextDisabled, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoStatIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, iconTint: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun VerticalDivider() {
    Box(modifier = Modifier.width(1.dp).height(40.dp).background(DarkSurfaceVariant))
}



@Composable
fun FullscreenLandscapePlayer(videoId: String, onClose: () -> Unit) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        var ytPlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
        var isPlaying by remember { mutableStateOf(true) }
        var currentSecond by remember { mutableStateOf(0f) }
        var totalDuration by remember { mutableStateOf(0f) }

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            val playerView = remember(videoId) {
                YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false
                    val options = IFramePlayerOptions.Builder()
                        .controls(0)
                        .autoplay(1)
                        .build()
                    initialize(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            ytPlayer = youTubePlayer
                            youTubePlayer.loadVideo(videoId, 0f)
                        }

                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                            currentSecond = second
                        }

                        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                            totalDuration = duration
                        }

                        override fun onStateChange(
                            youTubePlayer: YouTubePlayer,
                            state: PlayerConstants.PlayerState
                        ) {
                            if (state == PlayerConstants.PlayerState.ENDED) {
                                onClose()
                            } else if (state == PlayerConstants.PlayerState.PLAYING) {
                                isPlaying = true
                            } else if (state == PlayerConstants.PlayerState.PAUSED) {
                                isPlaying = false
                            }
                        }
                    }, options)
                }
            }

            val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
            DisposableEffect(playerView, lifecycleOwner) {
                lifecycleOwner.lifecycle.addObserver(playerView)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(playerView)
                    playerView.release()
                }
            }

            AndroidView(
                factory = { playerView },
                modifier = Modifier.fillMaxSize()
            )

            // Custom Player Overlay
            var showControls by remember { mutableStateOf(true) }
            LaunchedEffect(showControls) {
                if (showControls) {
                    delay(3000)
                    showControls = false
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showControls = !showControls }
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showControls,
                    enter = androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(0.4f))
                    ) {
                        // Top Header (Back Button)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.TopStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(0.5f), CircleShape)
                            ) {
                                Icon(Icons.Rounded.ArrowBack, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        // Center Controls (Prev 10s, Play/Pause, Skip 10s)
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            IconButton(
                                onClick = { ytPlayer?.seekTo(maxOf(0f, currentSecond - 10f)) },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.Black.copy(0.5f), CircleShape)
                            ) {
                                Icon(Icons.Rounded.FastRewind, contentDescription = "Rewind 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                            }

                            IconButton(
                                onClick = {
                                    if (isPlaying) {
                                        ytPlayer?.pause()
                                    } else {
                                        ytPlayer?.play()
                                    }
                                },
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(CinemaRed, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            IconButton(
                                onClick = { ytPlayer?.seekTo(minOf(totalDuration, currentSecond + 10f)) },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.Black.copy(0.5f), CircleShape)
                            ) {
                                Icon(Icons.Rounded.FastForward, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                        }

                        // Bottom Seekbar (Timeline Slider + Time Stamp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(0.8f))
                                    )
                                )
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            fun formatTime(seconds: Float): String {
                                val mins = (seconds / 60).toInt()
                                val secs = (seconds % 60).toInt()
                                return String.format("%02d:%02d", mins, secs)
                            }

                            Text(
                                text = formatTime(currentSecond),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Slider(
                                value = currentSecond,
                                onValueChange = { seekTime ->
                                    currentSecond = seekTime
                                    ytPlayer?.seekTo(seekTime)
                                },
                                valueRange = 0f..maxOf(1f, totalDuration),
                                colors = SliderDefaults.colors(
                                    thumbColor = CinemaRed,
                                    activeTrackColor = CinemaRed,
                                    inactiveTrackColor = Color.White.copy(0.3f)
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = formatTime(totalDuration),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

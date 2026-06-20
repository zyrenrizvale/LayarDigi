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
import androidx.compose.runtime.saveable.rememberSaveable
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
    val isDirectVideo = videoId == null && movie.trailerUrl.isNotBlank()
    val username by AuthRepository.currentUsername.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(movieId, username) {
        val u = username ?: return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("favorites").child(u).child(movieId).get()
            .addOnSuccessListener { isFavorite = it.exists() }
    }

    var showTrailerPlayer by rememberSaveable { mutableStateOf(false) }

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
            if (videoId != null || isDirectVideo) {
                item {
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
        
        if (showTrailerPlayer && (videoId != null || isDirectVideo)) {
            FullscreenLandscapePlayer(videoId = videoId, directUrl = if (isDirectVideo) movie.trailerUrl else null, onClose = { showTrailerPlayer = false })
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
fun FullscreenLandscapePlayer(videoId: String?, directUrl: String?, onClose: () -> Unit) {
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
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                factory = { ctx ->
                    if (directUrl != null) {
                        android.widget.VideoView(ctx).apply {
                            setVideoURI(android.net.Uri.parse(directUrl))
                            val mediaController = android.widget.MediaController(ctx)
                            mediaController.setAnchorView(this)
                            setMediaController(mediaController)
                            setOnPreparedListener { mp ->
                                start()
                            }
                        }
                    } else {
                        android.webkit.WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webViewClient = object : android.webkit.WebViewClient() {
                            override fun onPageFinished(view: android.webkit.WebView, url: String?) {
                                super.onPageFinished(view, url)
                                // Inject CSS to hide YouTube mobile web UI, creating a clean fullscreen player feel
                                // And inject JavaScript to FORCE autoplay and unmute the video
                                val js = """
                                    javascript:(function() {
                                        var style = document.createElement('style');
                                        style.innerHTML = 'ytm-header-bar, ytm-item-section-renderer, ytm-comment-section-renderer, ytm-companion-ad-renderer, .page-footer, .related-videos { display: none !important; } .player-container { position: fixed !important; top: 0 !important; left: 0 !important; width: 100vw !important; height: 100vh !important; z-index: 9999 !important; background: black !important; } video { object-fit: contain !important; }';
                                        document.head.appendChild(style);
                                        
                                        var playAttempt = setInterval(function() {
                                            var video = document.querySelector('video');
                                            if (video) {
                                                video.muted = false;
                                                video.play();
                                                
                                                var unMuteBtn = document.querySelector('.ytm-custom-control-unmute');
                                                if (unMuteBtn) unMuteBtn.click();
                                                
                                                if (!video.paused && !video.muted) {
                                                    clearInterval(playAttempt);
                                                }
                                            }
                                        }, 500);
                                        
                                        // Stop trying after 5 seconds to prevent infinite loops
                                        setTimeout(function() { clearInterval(playAttempt); }, 5000);
                                    })();
                                """.trimIndent().replace("\n", "")
                                view.evaluateJavascript(js, null)
                            }
                        }
                        webChromeClient = android.webkit.WebChromeClient()
                        loadUrl("https://m.youtube.com/watch?v=${videoId}")
                    }
                }
            },
            onRelease = { view ->
                    if (view is android.webkit.WebView) {
                        view.destroy()
                    } else if (view is android.widget.VideoView) {
                        view.stopPlayback()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

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
        }
    }
}

package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.layardigi.app.navigation.Screen
import com.layardigi.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseOut),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, delayMillis = 400),
        label = "tagline_alpha"
    )

    LaunchedEffect(true) {
        startAnimation = true
        delay(2800)
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Background radial glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(CinemaRed.copy(alpha = 0.25f), Color.Transparent),
                        radius = 600f
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(CinemaRed, CinemaRedDark),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(300f, 300f)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎬", fontSize = 56.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "LayarDigi",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "BIOSKOP DIGITAL INDONESIA",
                fontSize = 12.sp,
                color = CinemaGold,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }

        // Loading dots at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(taglineAlpha),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                val dotAlpha by animateFloatAsState(
                    targetValue = if (startAnimation) 1f else 0f,
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = 600 + index * 200
                    ),
                    label = "dot_$index"
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(dotAlpha)
                        .background(CinemaRed, androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
    }
}

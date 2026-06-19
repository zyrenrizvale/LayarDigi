package com.layardigi.app.presentation.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Theaters
import androidx.compose.material3.Icon
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
        animationSpec = tween(900, easing = EaseOut), label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), label = "scale"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 500), label = "tagline"
    )

    LaunchedEffect(true) {
        startAnimation = true
        delay(2800)
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Glow
        Box(modifier = Modifier.size(400.dp).background(
            Brush.radialGradient(listOf(CinemaRed.copy(0.2f), Color.Transparent))
        ))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale).alpha(alpha)
        ) {
            // Logo box with Material Icon
            Box(
                modifier = Modifier.size(110.dp).background(
                    Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)),
                    RoundedCornerShape(28.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Theaters,
                    contentDescription = "LayarDigi",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text("LayarDigi", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold,
                color = TextPrimary, letterSpacing = 1.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text("BIOSKOP DIGITAL INDONESIA", fontSize = 12.sp, color = CinemaGold,
                fontWeight = FontWeight.SemiBold, letterSpacing = 4.sp,
                textAlign = TextAlign.Center, modifier = Modifier.alpha(taglineAlpha))
        }

        // Loading dots
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp).alpha(taglineAlpha),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                val dotAlpha by animateFloatAsState(
                    targetValue = if (startAnimation) 1f else 0f,
                    animationSpec = tween(600, delayMillis = 700 + index * 200), label = "dot_$index"
                )
                Box(modifier = Modifier.size(6.dp).alpha(dotAlpha)
                    .background(CinemaRed, androidx.compose.foundation.shape.CircleShape))
            }
        }
    }
}

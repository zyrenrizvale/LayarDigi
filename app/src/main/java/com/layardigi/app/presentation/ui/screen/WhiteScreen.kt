package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.layardigi.app.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun WhiteScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(5000)
        navController.navigate(Screen.Splash.route) {
            popUpTo("white_screen") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    )
}

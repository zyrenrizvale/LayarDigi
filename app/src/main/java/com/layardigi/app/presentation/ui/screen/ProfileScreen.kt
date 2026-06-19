package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.data.repository.Role
import com.layardigi.app.navigation.Screen
import com.layardigi.app.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    val role by AuthRepository.currentUserRole.collectAsState()
    val username by AuthRepository.currentUsername.collectAsState()
    val context = LocalContext.current

    val isGuest = role == Role.GUEST
    val isAdmin = role == Role.ADMIN

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(DarkSurface, DarkBackground)))
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isGuest) "Tamu" else (username ?: "Pengguna"),
                        color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isGuest) "Belum masuk" else "Member Sejak 2026",
                        color = TextSecondary, fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    if (isAdmin) {
                        Box(
                            modifier = Modifier
                                .background(SuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("ADMIN", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    } else if (!isGuest) {
                        Box(
                            modifier = Modifier
                                .background(CinemaGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("PREMIUM", color = CinemaGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu items
        val menuItems = mutableListOf<Triple<androidx.compose.ui.graphics.vector.ImageVector, String, String>>()
        
        if (isAdmin) {
            menuItems.add(Triple(Icons.Rounded.DashboardCustomize, "Dashboard Admin", "Kelola daftar film"))
        }

        if (!isGuest) {
            menuItems.add(Triple(Icons.Rounded.ConfirmationNumber, "Tiket Saya", "Riwayat pembelian tiket"))
            menuItems.add(Triple(Icons.Rounded.FavoriteBorder, "Film Favorit", "Film yang kamu simpan"))
        }
        
        menuItems.addAll(listOf(
            Triple(Icons.Rounded.Notifications, "Notifikasi", "Pengaturan pemberitahuan"),
            Triple(Icons.Rounded.Lock, "Keamanan", "Password & privasi"),
            Triple(Icons.Rounded.HelpOutline, "Bantuan", "FAQ & hubungi kami"),
            Triple(Icons.Rounded.Info, "Tentang Aplikasi", "LayarDigi v1.0")
        ))

        menuItems.forEach { (icon, title, subtitle) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .clickable {
                        if (title == "Dashboard Admin") {
                            navController.navigate("admin_dashboard")
                        }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(DarkSurfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(subtitle, color = TextSecondary, fontSize = 12.sp)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isGuest) {
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Login / Daftar", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            OutlinedButton(
                onClick = { AuthRepository.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, CinemaRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Logout", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { (context as? Activity)?.finishAffinity() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, bottom = 20.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Keluar Aplikasi", color = TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

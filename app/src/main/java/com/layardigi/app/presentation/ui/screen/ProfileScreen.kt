package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.layardigi.app.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
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
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Pengguna LayarDigi", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Member Sejak 2026", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
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

        Spacer(modifier = Modifier.height(8.dp))

        // Menu items
        val menuItems = listOf(
            Triple(Icons.Rounded.ConfirmationNumber, "Tiket Saya", "Riwayat pembelian tiket"),
            Triple(Icons.Rounded.FavoriteBorder, "Film Favorit", "Film yang kamu simpan"),
            Triple(Icons.Rounded.Notifications, "Notifikasi", "Pengaturan pemberitahuan"),
            Triple(Icons.Rounded.Lock, "Keamanan", "Password & privasi"),
            Triple(Icons.Rounded.HelpOutline, "Bantuan", "FAQ & hubungi kami"),
            Triple(Icons.Rounded.Info, "Tentang Aplikasi", "LayarDigi v1.0")
        )

        menuItems.forEach { (icon, title, subtitle) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
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
    }
}

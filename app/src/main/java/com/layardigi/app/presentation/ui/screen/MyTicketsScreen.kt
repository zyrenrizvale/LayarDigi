package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.*
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.data.repository.Role
import com.layardigi.app.ui.theme.*

data class TicketHistory(
    val bookingCode: String = "",
    val movieTitle: String = "",
    val cinemaName: String = "",
    val date: String = "",
    val showtime: String = "",
    val seats: String = "",
    val total: Int = 0,
    val posterUrl: String = "",
    val timestamp: Long = 0L
)

@Composable
fun MyTicketsScreen() {
    val username by AuthRepository.currentUsername.collectAsState()
    val role by AuthRepository.currentUserRole.collectAsState()
    var tickets by remember { mutableStateOf<List<TicketHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(username) {
        if (username == null || role == Role.GUEST) {
            isLoading = false
            return@LaunchedEffect
        }
        val ref = FirebaseDatabase.getInstance().getReference("tickets").child(username!!)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TicketHistory>()
                for (child in snapshot.children) {
                    val ticket = TicketHistory(
                        bookingCode = child.child("bookingCode").getValue(String::class.java) ?: "",
                        movieTitle = child.child("movieTitle").getValue(String::class.java) ?: "",
                        cinemaName = child.child("cinemaName").getValue(String::class.java) ?: "",
                        date = child.child("date").getValue(String::class.java) ?: "",
                        showtime = child.child("showtime").getValue(String::class.java) ?: "",
                        seats = child.child("seats").getValue(String::class.java) ?: "",
                        total = child.child("total").getValue(Int::class.java) ?: 0,
                        posterUrl = child.child("posterUrl").getValue(String::class.java) ?: "",
                        timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    )
                    list.add(ticket)
                }
                tickets = list.sortedByDescending { it.timestamp }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        })
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBackground).statusBarsPadding()
    ) {
        // Top padding for status bar only
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tiket Saya", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 20.dp))
        Text("Riwayat pemesanan tiket kamu", color = TextSecondary, fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(16.dp))

        if (role == Role.GUEST) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.ConfirmationNumber, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Silakan login untuk melihat tiket", color = TextSecondary, fontSize = 15.sp)
                }
            }
        } else if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CinemaRed)
            }
        } else if (tickets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.ConfirmationNumber, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Belum ada tiket", color = TextSecondary, fontSize = 15.sp)
                    Text("Pesan tiket pertamamu sekarang!", color = TextDisabled, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                items(tickets) { ticket ->
                    TicketCard(ticket = ticket)
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: TicketHistory) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clip(RoundedCornerShape(20.dp)).background(DarkCard)
    ) {
        // Red header
        Box(modifier = Modifier.fillMaxWidth()
            .background(Brush.linearGradient(listOf(CinemaRed, CinemaRedDark))).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = ticket.posterUrl, contentDescription = null,
                    modifier = Modifier.size(50.dp, 70.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(ticket.movieTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(ticket.cinemaName, color = Color.White.copy(0.8f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text("${ticket.date} • ${ticket.showtime}", color = Color.White.copy(0.7f), fontSize = 12.sp)
                    }
                }
            }
        }
        // Perforated edge
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(20.dp).offset(x = (-10).dp).background(DarkBackground, RoundedCornerShape(50)))
            HorizontalDivider(modifier = Modifier.weight(1f), color = DarkSurfaceVariant, thickness = 1.dp)
            Box(modifier = Modifier.size(20.dp).offset(x = 10.dp).background(DarkBackground, RoundedCornerShape(50)))
        }
        // Ticket details
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("KURSI", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text(ticket.seats, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("KODE BOOKING", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text(ticket.bookingCode, color = CinemaGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LUNAS", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text("Rp ${"%,d".format(ticket.total)}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

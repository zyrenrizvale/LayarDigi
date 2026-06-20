package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.*
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val author: String = "",
    val timestamp: Long = 0L
)

data class AnnouncementComment(
    val username: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

@Composable
fun NotificationsScreen(navController: NavController) {
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("announcements")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Announcement>()
                for (child in snapshot.children) {
                    list.add(Announcement(
                        id = child.key ?: "",
                        title = child.child("title").getValue(String::class.java) ?: "",
                        body = child.child("body").getValue(String::class.java) ?: "",
                        author = child.child("author").getValue(String::class.java) ?: "",
                        timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    ))
                }
                announcements = list.sortedByDescending { it.timestamp }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        })
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBackground).statusBarsPadding()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Notifikasi", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 20.dp))
        Text("Pengumuman & pemberitahuan terbaru", color = TextSecondary, fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CinemaRed)
            }
        } else if (announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.NotificationsNone, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Belum ada notifikasi", color = TextSecondary, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
                items(announcements) { announcement ->
                    AnnouncementCard(announcement) {
                        navController.navigate("announcement_detail/${announcement.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement, onClick: () -> Unit) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp)).background(DarkCard)
            .clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.size(42.dp).background(CinemaRed.copy(0.15f), CircleShape),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Campaign, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(announcement.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(announcement.body, color = TextSecondary, fontSize = 13.sp, maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp))
            Text(sdf.format(Date(announcement.timestamp)), color = TextDisabled, fontSize = 11.sp)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(18.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDetailScreen(announcementId: String) {
    var announcement by remember { mutableStateOf<Announcement?>(null) }
    var comments by remember { mutableStateOf<List<AnnouncementComment>>(emptyList()) }
    var commentText by remember { mutableStateOf("") }
    val username by AuthRepository.currentUsername.collectAsState()
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }

    LaunchedEffect(announcementId) {
        val ref = FirebaseDatabase.getInstance().getReference("announcements").child(announcementId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                announcement = Announcement(
                    id = announcementId,
                    title = snapshot.child("title").getValue(String::class.java) ?: "",
                    body = snapshot.child("body").getValue(String::class.java) ?: "",
                    author = snapshot.child("author").getValue(String::class.java) ?: "",
                    timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                )
                val commentList = mutableListOf<AnnouncementComment>()
                for (child in snapshot.child("comments").children) {
                    commentList.add(AnnouncementComment(
                        username = child.child("username").getValue(String::class.java) ?: "",
                        text = child.child("text").getValue(String::class.java) ?: "",
                        timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    ))
                }
                comments = commentList.sortedBy { it.timestamp }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBackground).statusBarsPadding()
            .imePadding()
    ) {
        announcement?.let { ann ->
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                item {
                    // Announcement content
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(CinemaRed, CinemaRedDark))).padding(20.dp)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PENGUMUMAN", color = Color.White.copy(0.8f), fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(ann.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(ann.body, color = Color.White.copy(0.9f), fontSize = 14.sp, lineHeight = 20.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Oleh: ${ann.author} • ${sdf.format(Date(ann.timestamp))}",
                                color = Color.White.copy(0.6f), fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Komentar (${comments.size})", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (comments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada komentar. Jadilah yang pertama!", color = TextDisabled, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(comments) { comment ->
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Box(modifier = Modifier.size(36.dp).background(DarkSurfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center) {
                                Text(comment.username.take(1).uppercase(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(comment.username, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(sdf.format(Date(comment.timestamp)), color = TextDisabled, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(comment.text, color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Comment input bar
            if (username != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(DarkCard)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Tulis komentar...", color = TextDisabled) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CinemaRed,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkBackground,
                            focusedContainerColor = DarkBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                val ref = FirebaseDatabase.getInstance().getReference("announcements")
                                    .child(announcementId).child("comments").push()
                                ref.setValue(mapOf(
                                    "username" to (username ?: "Anonim"),
                                    "text" to commentText,
                                    "timestamp" to System.currentTimeMillis()
                                ))
                                commentText = ""
                            }
                        },
                        modifier = Modifier.size(48.dp).background(CinemaRed, CircleShape)
                    ) {
                        Icon(Icons.Rounded.Send, contentDescription = "Kirim", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

package com.layardigi.app

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.core.app.NotificationCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.layardigi.app.navigation.NavGraph
import com.layardigi.app.navigation.Screen
import com.layardigi.app.ui.theme.*
import kotlinx.coroutines.flow.MutableSharedFlow

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "Beranda", Icons.Rounded.Home),
    BottomNavItem("search_tab", "Cari", Icons.Rounded.Search),
    BottomNavItem("my_tickets_tab", "Tiket", Icons.Rounded.ConfirmationNumber),
    BottomNavItem("favorites_tab", "Favorit", Icons.Rounded.FavoriteBorder, Icons.Rounded.Favorite),
    BottomNavItem("profile_tab", "Profil", Icons.Rounded.Person)
)

// Routes yang MENAMPILKAN bottom nav
val routesWithBottomNav = setOf(
    Screen.Home.route,
    "search_tab",
    "my_tickets_tab",
    "favorites_tab",
    "profile_tab"
)

class MainActivity : ComponentActivity() {
    private val pendingNavigation = MutableSharedFlow<String>(extraBufferCapacity = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission if API level is 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Listen for new announcements to show local notification
        val appStartTime = System.currentTimeMillis()
        val announcementsRef = FirebaseDatabase.getInstance().getReference("announcements")
        announcementsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                if (timestamp > appStartTime) {
                    val id = snapshot.key ?: ""
                    val title = snapshot.child("title").getValue(String::class.java) ?: "Pengumuman Baru"
                    val body = snapshot.child("body").getValue(String::class.java) ?: ""
                    showAnnouncementNotification(id, title, body)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        // Check if app was opened from notification
        intent.getStringExtra("announcementId")?.let { announcementId ->
            pendingNavigation.tryEmit("announcement_detail/$announcementId")
        }

        enableEdgeToEdge()
        setContent {
            LayarDigiTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                LaunchedEffect(navController) {
                    pendingNavigation.collect { route ->
                        navController.navigate(route)
                    }
                }

                val showBottomBar = currentRoute != null && routesWithBottomNav.any { route ->
                    currentRoute == route || currentRoute.startsWith(route)
                }

                Scaffold(
                    containerColor = DarkBackground,
                    contentWindowInsets = WindowInsets(0),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically { it } + fadeIn(),
                            exit = slideOutVertically { it } + fadeOut()
                        ) {
                            LayarDigiBottomNav(navController = navController, currentRoute = currentRoute)
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra("announcementId")?.let { announcementId ->
            pendingNavigation.tryEmit("announcement_detail/$announcementId")
        }
    }

    private fun showAnnouncementNotification(id: String, title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("announcementId", id)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, LayarDigiApp.NOTIF_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id.hashCode(), notification)
    }
}

@Composable
fun LayarDigiBottomNav(navController: NavController, currentRoute: String?) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = item.title, fontSize = 11.sp, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CinemaRed,
                    selectedTextColor = CinemaRed,
                    indicatorColor = CinemaRed.copy(alpha = 0.15f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

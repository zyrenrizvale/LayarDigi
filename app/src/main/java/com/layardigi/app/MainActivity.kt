package com.layardigi.app

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
import com.layardigi.app.navigation.NavGraph
import com.layardigi.app.navigation.Screen
import com.layardigi.app.ui.theme.*

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayarDigiTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

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

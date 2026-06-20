package com.layardigi.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.layardigi.app.presentation.ui.screen.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: String) = "movie_detail/$movieId"
    }
    object Booking : Screen("booking/{movieId}/{cinemaId}/{showtime}") {
        fun createRoute(movieId: String, cinemaId: String, showtime: String) =
            "booking/$movieId/$cinemaId/${showtime.replace(":", "_")}"
    }
    object Checkout : Screen("checkout/{movieId}/{cinemaId}/{date}/{showtime}/{seats}/{total}") {
        fun createRoute(movieId: String, cinemaId: String, date: String, showtime: String, seats: String, total: Int) =
            "checkout/$movieId/$cinemaId/${date.replace(" ", "_").replace(",", "")}/${showtime.replace(":", "_")}/$seats/$total"
    }
    object TicketSuccess : Screen("ticket_success/{bookingCode}") {
        fun createRoute(bookingCode: String) = "ticket_success/$bookingCode"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // Main tabs
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable("search_tab") { SearchScreen(navController = navController) }
        composable("my_tickets_tab") { MyTicketsScreen() }
        composable("favorites_tab") { FavoritesScreen(navController = navController) }
        composable("profile_tab") { ProfileScreen(navController = navController) }

        // Auth
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen(navController = navController) }

        // Admin
        composable("admin_dashboard") { AdminDashboardScreen(navController = navController) }

        // Movie Form
        composable(
            route = "movie_form?movieId={movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            MovieFormScreen(navController = navController, movieId = backStackEntry.arguments?.getString("movieId"))
        }

        // Cinema Form
        composable(
            route = "cinema_form?cinemaId={cinemaId}",
            arguments = listOf(navArgument("cinemaId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            CinemaFormScreen(navController = navController, cinemaId = backStackEntry.arguments?.getString("cinemaId"))
        }

        // Movie Detail
        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            MovieDetailScreen(
                movieId = backStackEntry.arguments?.getString("movieId") ?: "",
                navController = navController
            )
        }

        // Booking
        composable(
            route = Screen.Booking.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType },
                navArgument("cinemaId") { type = NavType.StringType },
                navArgument("showtime") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            BookingScreen(
                movieId = backStackEntry.arguments?.getString("movieId") ?: "",
                cinemaId = backStackEntry.arguments?.getString("cinemaId") ?: "",
                showtime = backStackEntry.arguments?.getString("showtime")?.replace("_", ":") ?: "",
                navController = navController
            )
        }

        // Checkout
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType },
                navArgument("cinemaId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("showtime") { type = NavType.StringType },
                navArgument("seats") { type = NavType.StringType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            CheckoutScreen(
                movieId = backStackEntry.arguments?.getString("movieId") ?: "",
                cinemaId = backStackEntry.arguments?.getString("cinemaId") ?: "",
                date = backStackEntry.arguments?.getString("date") ?: "",
                showtime = backStackEntry.arguments?.getString("showtime")?.replace("_", ":") ?: "",
                seatIds = backStackEntry.arguments?.getString("seats") ?: "",
                total = backStackEntry.arguments?.getInt("total") ?: 0,
                navController = navController
            )
        }

        // Ticket Success
        composable(
            route = Screen.TicketSuccess.route,
            arguments = listOf(navArgument("bookingCode") { type = NavType.StringType })
        ) { backStackEntry ->
            TicketSuccessScreen(
                bookingCode = backStackEntry.arguments?.getString("bookingCode") ?: "",
                navController = navController
            )
        }

        // Notifications
        composable("notifications") { NotificationsScreen(navController = navController) }

        // Announcement Detail
        composable(
            route = "announcement_detail/{announcementId}",
            arguments = listOf(navArgument("announcementId") { type = NavType.StringType })
        ) { backStackEntry ->
            AnnouncementDetailScreen(announcementId = backStackEntry.arguments?.getString("announcementId") ?: "")
        }

        // Profile sub-pages (stubs)
        composable("security") { SimpleInfoScreen("Keamanan", "Fitur ini segera hadir") }
        composable("help") { SimpleInfoScreen("Bantuan", "Hubungi kami di support@layardigi.id") }
        composable("about") { SimpleInfoScreen("Tentang Aplikasi", "LayarDigi v2.0\nBioskop Digital Indonesia\n© 2026 LayarDigi") }
        composable("my_tickets") { MyTicketsScreen() }
        composable("favorites") { FavoritesScreen(navController = navController) }
    }
}

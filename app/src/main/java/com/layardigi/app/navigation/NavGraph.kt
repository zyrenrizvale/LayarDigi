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
    object Checkout : Screen("checkout/{movieId}/{cinemaId}/{showtime}/{seats}/{total}") {
        fun createRoute(movieId: String, cinemaId: String, showtime: String, seats: String, total: Int) =
            "checkout/$movieId/$cinemaId/${showtime.replace(":", "_")}/$seats/$total"
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
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            MovieDetailScreen(
                movieId = backStackEntry.arguments?.getString("movieId") ?: "",
                navController = navController
            )
        }

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

        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType },
                navArgument("cinemaId") { type = NavType.StringType },
                navArgument("showtime") { type = NavType.StringType },
                navArgument("seats") { type = NavType.StringType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            CheckoutScreen(
                movieId = backStackEntry.arguments?.getString("movieId") ?: "",
                cinemaId = backStackEntry.arguments?.getString("cinemaId") ?: "",
                showtime = backStackEntry.arguments?.getString("showtime")?.replace("_", ":") ?: "",
                seatIds = backStackEntry.arguments?.getString("seats") ?: "",
                total = backStackEntry.arguments?.getInt("total") ?: 0,
                navController = navController
            )
        }

        composable(
            route = Screen.TicketSuccess.route,
            arguments = listOf(navArgument("bookingCode") { type = NavType.StringType })
        ) { backStackEntry ->
            TicketSuccessScreen(
                bookingCode = backStackEntry.arguments?.getString("bookingCode") ?: "",
                navController = navController
            )
        }
    }
}

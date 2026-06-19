package com.layardigi.app.domain.usecase

import com.layardigi.app.data.model.Cinema
import com.layardigi.app.data.repository.CinemaRepository
import kotlin.math.*

class GetNearestCinemasUseCase {

    operator fun invoke(userLat: Double, userLon: Double): List<Cinema> {
        return CinemaRepository.cinemas
            .map { cinema ->
                val distance = haversineDistance(userLat, userLon, cinema.latitude, cinema.longitude)
                cinema.copy(distanceKm = distance)
            }
            .sortedBy { it.distanceKm }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * asin(sqrt(a))
        return earthRadiusKm * c
    }
}

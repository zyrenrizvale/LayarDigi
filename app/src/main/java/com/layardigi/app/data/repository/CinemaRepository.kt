package com.layardigi.app.data.repository

import com.layardigi.app.data.model.Cinema

object CinemaRepository {

    val cinemas = listOf(
        Cinema(
            id = "ld_grand_indo",
            name = "LayarDigi Grand Indonesia",
            address = "Grand Indonesia Shopping Town, Lt. 6, Jl. MH Thamrin No.1, Jakarta Pusat",
            city = "Jakarta Pusat",
            latitude = -6.1944,
            longitude = 106.8229,
            showtimes = listOf("10:00", "12:30", "15:00", "17:30", "20:00", "22:30")
        ),
        Cinema(
            id = "ld_pim",
            name = "LayarDigi Pondok Indah Mall",
            address = "Pondok Indah Mall 2, Lt. 3, Jl. Metro Pondok Indah, Jakarta Selatan",
            city = "Jakarta Selatan",
            latitude = -6.2686,
            longitude = 106.7836,
            showtimes = listOf("10:30", "13:00", "15:30", "18:00", "20:30", "23:00")
        ),
        Cinema(
            id = "ld_bekasi",
            name = "LayarDigi Bekasi Square",
            address = "Bekasi Square, Jl. Ahmad Yani No.1, Bekasi",
            city = "Bekasi",
            latitude = -6.2349,
            longitude = 106.9934,
            showtimes = listOf("11:00", "13:30", "16:00", "18:30", "21:00")
        ),
        Cinema(
            id = "ld_tangerang",
            name = "LayarDigi Tangerang City",
            address = "Tangerang City Mall, Lt. 4, Jl. Jend. Sudirman, Tangerang",
            city = "Tangerang",
            latitude = -6.1754,
            longitude = 106.6297,
            showtimes = listOf("10:00", "12:30", "15:00", "17:30", "20:00")
        ),
        Cinema(
            id = "ld_depok",
            name = "LayarDigi Depok Town Square",
            address = "Depok Town Square, Lt. 3, Jl. Margonda Raya, Depok",
            city = "Depok",
            latitude = -6.3836,
            longitude = 106.8324,
            showtimes = listOf("11:30", "14:00", "16:30", "19:00", "21:30")
        )
    )

    fun getCinemaById(id: String): Cinema? = cinemas.find { it.id == id }
}

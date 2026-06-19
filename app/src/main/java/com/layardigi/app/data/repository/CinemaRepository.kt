package com.layardigi.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.layardigi.app.data.model.Cinema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CinemaRepository {

    private val db = FirebaseDatabase.getInstance().getReference("cinemas")

    private val _cinemasFlow = MutableStateFlow<List<Cinema>>(emptyList())
    val cinemasFlow: StateFlow<List<Cinema>> = _cinemasFlow.asStateFlow()

    // Backward compatibility before migration
    val cinemas: List<Cinema> get() = _cinemasFlow.value

    init {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    seedInitialData()
                    return
                }

                val list = mutableListOf<Cinema>()
                for (child in snapshot.children) {
                    val id = child.child("id").getValue(String::class.java) ?: continue
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val address = child.child("address").getValue(String::class.java) ?: ""
                    val city = child.child("city").getValue(String::class.java) ?: ""
                    val latitude = child.child("latitude").getValue(Double::class.java) ?: 0.0
                    val longitude = child.child("longitude").getValue(Double::class.java) ?: 0.0
                    val basePrice = child.child("basePrice").getValue(Int::class.java) ?: 55000
                    
                    val showtimesList = mutableListOf<String>()
                    for (time in child.child("showtimes").children) {
                        time.getValue(String::class.java)?.let { showtimesList.add(it) }
                    }

                    list.add(
                        Cinema(id, name, address, city, latitude, longitude, showtimesList, basePrice, 0.0)
                    )
                }
                _cinemasFlow.value = list
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun seedInitialData() {
        val initialCinemas = listOf(
            mapOf("id" to "ld_grand_indo", "name" to "LayarDigi Grand Indonesia", "address" to "Grand Indonesia Shopping Town, Lt. 6, Jl. MH Thamrin No.1, Jakarta Pusat", "city" to "Jakarta Pusat", "latitude" to -6.1944, "longitude" to 106.8229, "basePrice" to 60000, "showtimes" to listOf("10:00", "12:30", "15:00", "17:30", "20:00", "22:30")),
            mapOf("id" to "ld_pim", "name" to "LayarDigi Pondok Indah Mall", "address" to "Pondok Indah Mall 2, Lt. 3, Jl. Metro Pondok Indah, Jakarta Selatan", "city" to "Jakarta Selatan", "latitude" to -6.2686, "longitude" to 106.7836, "basePrice" to 55000, "showtimes" to listOf("10:30", "13:00", "15:30", "18:00", "20:30", "23:00")),
            mapOf("id" to "ld_bekasi", "name" to "LayarDigi Bekasi Square", "address" to "Bekasi Square, Jl. Ahmad Yani No.1, Bekasi", "city" to "Bekasi", "latitude" to -6.2349, "longitude" to 106.9934, "basePrice" to 45000, "showtimes" to listOf("11:00", "13:30", "16:00", "18:30", "21:00")),
            mapOf("id" to "ld_tangerang", "name" to "LayarDigi Tangerang City", "address" to "Tangerang City Mall, Lt. 4, Jl. Jend. Sudirman, Tangerang", "city" to "Tangerang", "latitude" to -6.1754, "longitude" to 106.6297, "basePrice" to 45000, "showtimes" to listOf("10:00", "12:30", "15:00", "17:30", "20:00")),
            mapOf("id" to "ld_depok", "name" to "LayarDigi Depok Town Square", "address" to "Depok Town Square, Lt. 3, Jl. Margonda Raya, Depok", "city" to "Depok", "latitude" to -6.3836, "longitude" to 106.8324, "basePrice" to 40000, "showtimes" to listOf("11:30", "14:00", "16:30", "19:00", "21:30"))
        )
        initialCinemas.forEach { cinema ->
            db.child(cinema["id"] as String).setValue(cinema)
        }
    }

    fun getCinemaById(id: String): Cinema? = _cinemasFlow.value.find { it.id == id }

    fun addCinema(cinemaData: Map<String, Any>) {
        val id = cinemaData["id"] as String
        db.child(id).setValue(cinemaData)
    }

    fun updateCinema(cinemaData: Map<String, Any>) {
        val id = cinemaData["id"] as String
        db.child(id).setValue(cinemaData)
    }

    fun deleteCinema(id: String) {
        db.child(id).removeValue()
    }
}

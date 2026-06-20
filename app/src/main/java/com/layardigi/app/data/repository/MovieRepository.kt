package com.layardigi.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.layardigi.app.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MovieRepository {

    private val db = FirebaseDatabase.getInstance().getReference("movies")

    private val _moviesFlow = MutableStateFlow<List<Movie>>(emptyList())
    val moviesFlow: StateFlow<List<Movie>> = _moviesFlow.asStateFlow()

    val allMovies: List<Movie> get() = _moviesFlow.value
    val nowShowingMovies: List<Movie> get() = _moviesFlow.value.filter { it.status == "NOW_SHOWING" }
    val classicMovies: List<Movie> get() = _moviesFlow.value.filter { it.status == "COMING_SOON" }
    val finishedMovies: List<Movie> get() = _moviesFlow.value.filter { it.status == "FINISHED" }

    init {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    seedInitialData()
                    return
                }

                val list = mutableListOf<Movie>()
                for (child in snapshot.children) {
                    val id = child.child("id").getValue(String::class.java) ?: continue
                    val title = child.child("title").getValue(String::class.java) ?: ""
                    val rating = child.child("rating").getValue(Float::class.java) ?: 0f
                    val duration = child.child("duration").getValue(Int::class.java) ?: 0
                    val synopsis = child.child("synopsis").getValue(String::class.java) ?: ""
                    val posterUrl = child.child("posterUrl").getValue(String::class.java) ?: ""
                    val status = child.child("status").getValue(String::class.java)
                        ?: if (child.child("nowShowing").getValue(Boolean::class.java) == true) "NOW_SHOWING" else "COMING_SOON"
                    val year = child.child("year").getValue(Int::class.java) ?: 0
                    val director = child.child("director").getValue(String::class.java) ?: ""
                    val language = child.child("language").getValue(String::class.java) ?: ""
                    val ageRating = child.child("ageRating").getValue(String::class.java) ?: ""
                    val trailerUrl = child.child("trailerUrl").getValue(String::class.java) ?: ""

                    val genre = mutableListOf<String>()
                    child.child("genre").children.forEach { it.getValue(String::class.java)?.let { g -> genre.add(g) } }
                    
                    val cast = mutableListOf<String>()
                    child.child("cast").children.forEach { it.getValue(String::class.java)?.let { c -> cast.add(c) } }
                    
                    val availableCinemas = mutableListOf<String>()
                    child.child("availableCinemas").children.forEach { it.getValue(String::class.java)?.let { ac -> availableCinemas.add(ac) } }

                    list.add(
                        Movie(
                            id = id, title = title, genre = genre, rating = rating, duration = duration,
                            synopsis = synopsis, posterUrl = posterUrl, status = status,
                            year = year, director = director, cast = cast, language = language, ageRating = ageRating,
                            trailerUrl = trailerUrl, availableCinemas = availableCinemas
                        )
                    )
                }
                _moviesFlow.value = list
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun seedInitialData() {
        val initialMovies = listOf(
            Movie("colony", "Colony", listOf("Sci-Fi", "Thriller", "Drama"), 7.8f, 115, "Di tahun 2092, Bumi telah dikuasai...", "https://cdn.cgv.id/uploads_v2/movie/compressed/26020800.jpg?version=2", "NOW_SHOWING", 2026, "Marcus Webb", listOf("Chris Hemsworth", "Ana de Armas"), "English (Subtitle Indo)", "17+", "zSWdZAibgR4", listOf("ld_grand_indo", "ld_pim", "ld_bekasi", "ld_tangerang", "ld_depok")),
            Movie("peninsula", "Peninsula", listOf("Action", "Horror", "Thriller"), 7.5f, 116, "Empat tahun setelah wabah zombie...", "https://cdn.cgv.id/uploads/movie/pictures/20011700.jpg?version=2", "NOW_SHOWING", 2026, "Yeon Sang-ho", listOf("Gang Dong-won", "Lee Jung-hyun"), "Korea (Subtitle Indo)", "17+", "xsRstvdL6t8", listOf("ld_grand_indo", "ld_pim")),
            Movie("interstellar", "Interstellar", listOf("Sci-Fi", "Drama", "Adventure"), 8.7f, 169, "Ketika Bumi menghadapi kekeringan...", "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg", "COMING_SOON", 2014, "Christopher Nolan", listOf("Matthew McConaughey", "Anne Hathaway"), "English (Subtitle Indo)", "13+", "zSWdZAibgR4", listOf("ld_grand_indo", "ld_pim", "ld_bekasi", "ld_tangerang", "ld_depok"))
        )
        initialMovies.forEach { addMovie(it) }
    }

    fun getMovieById(id: String): Movie? = _moviesFlow.value.find { it.id == id }

    fun toggleNowShowing(id: String) {
        val movie = getMovieById(id) ?: return
        val nextStatus = when (movie.status) {
            "COMING_SOON" -> "NOW_SHOWING"
            "NOW_SHOWING" -> "FINISHED"
            else -> "COMING_SOON"
        }
        db.child(id).child("status").setValue(nextStatus)
    }

    fun addMovie(movie: Movie) {
        db.child(movie.id).setValue(movie)
    }

    fun updateMovie(movie: Movie) {
        db.child(movie.id).setValue(movie)
    }

    fun deleteMovie(id: String) {
        db.child(id).removeValue()
    }
}
